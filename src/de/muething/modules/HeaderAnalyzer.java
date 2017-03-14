package de.muething.modules;

import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.ZapGetMethod;

import de.muething.interfaces.ProxyAnalyzer;
import de.muething.models.PersistedRequest;
import de.muething.models.ReportRecord;
import de.muething.proxying.ManagedProxy;

public class HeaderAnalyzer extends ProxyAnalyzer{

	private HashMap<String, CredentialExchangeCharacteristics> domainToUnprotectedCookieLeakage = new HashMap<>();
	private HashMap<String, CredentialExchangeCharacteristics> domainToUnprotectedAuthorizationLeakage = new HashMap<>();

	private static String identifier = "headerAnalyzer";
	
	@Override
	public PersistedRequest willPersistRequest(PersistedRequest request, HttpMessage httpMessage, Socket inSocket,
			ZapGetMethod method) {
		if (request == null) {
			return null;
		}
		
		boolean connectionProtected = httpMessage.getRequestHeader().isSecure();
		
		CredentialExchangeCharacteristics characteristics = domainToUnprotectedCookieLeakage.get(request.getHostName());
		characteristics = characteristics != null ? characteristics : new CredentialExchangeCharacteristics();
	
		Vector<String> cookies = httpMessage.getRequestHeader().getHeaders("Cookie");
		if (cookies != null && !cookies.isEmpty()) {
			tag(request, 0);

			if (connectionProtected) {
				characteristics.setFirstEncryptedExchangeURLDescrString(urlDescrString(request.getRequestURL(), "Cookie", cookies));
			} else {
				characteristics.setFirstUnencryptedExchangeURLDescrString(urlDescrString(request.getRequestURL(), "Cookie", cookies));
			}
		}
		
		cookies = httpMessage.getResponseHeader().getHeaders("Set-Cookie");
		if (cookies != null && !cookies.isEmpty()) {
			tag(request, 0);

			if (connectionProtected) {
				characteristics.setFirstEncryptedExchangeURLDescrString(urlDescrString(request.getRequestURL(), "Set-Cookie", cookies));
			} else {
				characteristics.setFirstUnencryptedExchangeURLDescrString(urlDescrString(request.getRequestURL(), "Set-Cookie", cookies));
			}
		}
		
		domainToUnprotectedCookieLeakage.put(request.getHostName(), characteristics);
		
		//Authorization
		characteristics = domainToUnprotectedAuthorizationLeakage.get(request.getHostName());
		characteristics = characteristics != null ? characteristics : new CredentialExchangeCharacteristics();
		
		Vector<String> authorization = httpMessage.getRequestHeader().getHeaders("Authorization");
		if (authorization != null && !authorization.isEmpty()) {
			
			tag(request, 1);

			if (connectionProtected) {
				characteristics.setFirstEncryptedExchangeURLDescrString(urlDescrString(request.getRequestURL(), "Authorization", authorization));
			} else {
				characteristics.setFirstUnencryptedExchangeURLDescrString(urlDescrString(request.getRequestURL(), "Authorization", authorization));
				
				
				String bearer = checkBearerTokenUsage(authorization);
				if (bearer != null) {
					characteristics.setBearerTokenLeaked(urlDescrString(request.getRequestURL(), "Authorization/Bearer", Arrays.asList(bearer)));
					tag(request, 2);
				}
			}
			
		}
		
		domainToUnprotectedAuthorizationLeakage.put(request.getHostName(), characteristics);
		
		return request;
	}
	
	
	private String urlDescrString(String url, String headerField, List<String> headerContents) {
		return url+" ("+headerField+": " + startOfHeaders(headerContents) + ")";
	}

	@Override
	public List<ReportRecord> createReportReportRowFor(ManagedProxy proxy, String domain) {
		CredentialExchangeCharacteristics cookieCharacteristicsForDomain = domainToUnprotectedCookieLeakage.get(domain);
		cookieCharacteristicsForDomain = cookieCharacteristicsForDomain != null ? cookieCharacteristicsForDomain : new CredentialExchangeCharacteristics();
		
		
		ReportRecord cookie = new ReportRecord(cookieCharacteristicsForDomain.getResultNumberAsString(), cookieCharacteristicsForDomain.toString(), getIdentifier(), domain, 0 );
		
		CredentialExchangeCharacteristics authorizationCharacteristicsForDomain = domainToUnprotectedAuthorizationLeakage.get(domain);
		authorizationCharacteristicsForDomain = authorizationCharacteristicsForDomain != null ? authorizationCharacteristicsForDomain : new CredentialExchangeCharacteristics();
		ReportRecord authorization = new ReportRecord(authorizationCharacteristicsForDomain.getResultNumberAsString(), authorizationCharacteristicsForDomain.toString(),getIdentifier(), domain, 1);
		ReportRecord bearer = new ReportRecord(authorizationCharacteristicsForDomain.getBearerInfoString(), authorizationCharacteristicsForDomain.getBearerTokenLeaked(),getIdentifier(), domain, 2);

		return Arrays.asList(cookie, authorization, bearer);
	}

	
	public static final List<ReportRecord> titlesRow = Arrays.asList(new ReportRecord("Session hijacking", "Cookie header field"), new ReportRecord("Leaks credentials", "Authorization header field usage."), new ReportRecord("OAuth", ""));
	
	@Override
	public List<ReportRecord> getTitlesRowForResults() {
		return titlesRow;
	}
	
	@SuppressWarnings("unused")
	private String headerContentsAs(Vector<String> vec) {
		StringBuffer stringBuffer = new StringBuffer();
		
		for (String str : vec) {
			stringBuffer.append(str);
			stringBuffer.append("; ");
		}
		return stringBuffer.toString();
	}
	
	private String checkBearerTokenUsage(Vector<String> authorizationHeaders) {
		for (String header : authorizationHeaders) {
			if (header.startsWith("Bearer")) {
				return header.substring(0, 13) + "...";
			}
		}
		
		return null;
	}
	
	
	private String startOfHeaders(List<String> vec) {
		StringBuffer stringBuffer = new StringBuffer();
		
		for (String str : vec) {
			stringBuffer.append("[");
			stringBuffer.append(str.substring(0, 13));
			stringBuffer.append("]...; ");
		}
		return stringBuffer.toString();
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}


	@Override
	public Integer getOrderNumberForOutput() {
		return 2;
	}

}

class CredentialExchangeCharacteristics {
	String firstEncryptedPinnedExchangeURLDescrString = null;
	String firstEncryptedExchangeURLDescrString = null;
	String firstUnencryptedExchangeURLDescrString = null;
	
	String bearerTokenLeaked = null;
	
	public String getBearerTokenLeaked() {
		
		return bearerTokenLeaked;
	}
	public void setBearerTokenLeaked(String bearerTokenLeaked) {
		if (this.bearerTokenLeaked != null) {
			return;
		}
		
		this.bearerTokenLeaked = bearerTokenLeaked;
	}
	
	public String getBearerInfoString() {
		if (bearerTokenLeaked != null) {
			return "Bearer token leaked.";
		} else {
			return "-";
		}
	}
	
	public String getFirstEncryptedPinnedExchangeURLDescrString() {
		return firstEncryptedPinnedExchangeURLDescrString;
	}
	public void setFirstEncryptedPinnedExchangeURLDescrString(String firstEncryptedPinnedExchangeURLDescrString) {
		if (this.firstEncryptedPinnedExchangeURLDescrString != null) {
			return;
		}
		
		this.firstEncryptedPinnedExchangeURLDescrString = firstEncryptedPinnedExchangeURLDescrString;
	}
	public String getFirstEncryptedExchangeURLDescrString() {
		return firstEncryptedExchangeURLDescrString;
	}
	public void setFirstEncryptedExchangeURLDescrString(String firstEncryptedExchangeURLDescrString) {
		
		if (this.firstEncryptedExchangeURLDescrString != null) {
			return;
		}
		
		this.firstEncryptedExchangeURLDescrString = firstEncryptedExchangeURLDescrString;
	}
	public String getFirstUnencryptedExchangeURLDescrString() {
		return firstUnencryptedExchangeURLDescrString;
	}
	public void setFirstUnencryptedExchangeURLDescrString(String firstUnencryptedExchangeURLDescrString) {
		if (this.firstUnencryptedExchangeURLDescrString != null) {
			return;
		}
		
		this.firstUnencryptedExchangeURLDescrString = firstUnencryptedExchangeURLDescrString;
	}
	
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		if (firstEncryptedExchangeURLDescrString != null) {
			buffer.append("First prot. exchange: ");
			buffer.append(firstEncryptedExchangeURLDescrString);
		}
		
		if (firstUnencryptedExchangeURLDescrString != null) {
			buffer.append("\nFirst unprot. exchange: ");
			buffer.append(firstUnencryptedExchangeURLDescrString);
		}
		
		
		return buffer.toString();
	}
	
	public String getResultNumberAsString() {
		if (firstUnencryptedExchangeURLDescrString == null && firstEncryptedExchangeURLDescrString == null && firstEncryptedPinnedExchangeURLDescrString == null) {
			return "-1";
		}
			
		if (firstEncryptedPinnedExchangeURLDescrString != null && firstUnencryptedExchangeURLDescrString == null && firstEncryptedExchangeURLDescrString == null) {
			return "0";
		}
		
		if (firstUnencryptedExchangeURLDescrString != null && firstEncryptedExchangeURLDescrString == null && firstEncryptedPinnedExchangeURLDescrString == null) {
			return "3";
		}
		
		if (firstUnencryptedExchangeURLDescrString == null && firstEncryptedExchangeURLDescrString != null) {
			return "1";
		}
		
		if (firstUnencryptedExchangeURLDescrString != null && firstEncryptedExchangeURLDescrString != null) {
			return "2";
		}
		
		return "invalid result";
		
		
	}
	
	
	
}
