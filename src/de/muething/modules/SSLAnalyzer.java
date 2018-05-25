package de.muething.modules;

import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.parosproxy.paros.control.Proxy;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.ZapGetMethod;

import de.muething.interfaces.HandshakeListener;
import de.muething.interfaces.ProxySessionDriver;
import de.muething.interfaces.ProxyAnalyzer;
import de.muething.models.PersistedRequest;
import de.muething.models.ReportRecord;
import de.muething.proxying.ManagedProxy;

public class SSLAnalyzer extends ProxyAnalyzer implements ProxySessionDriver, HandshakeListener{
	HashMap<String, String> domainToTLSVersion = new HashMap<>();
	HashMap<String, String> domainsWithHandshakeFailures = new HashMap<>();
	
	int session = -1; 

	private static String identifier = "sslAnalyzer";
	
	@Override
	public PersistedRequest willPersistRequest(PersistedRequest request, HttpMessage httpMessage, Socket inSocket,
			ZapGetMethod method) {

		String tlsVersion = request.getTlsVersion();
		tlsVersion = tlsVersion == null || tlsVersion.trim() == "" ? "-" : tlsVersion;
		
		String prevVersion = domainToTLSVersion.get(request.getHostName());
		if (prevVersion != null && !prevVersion.equals(tlsVersion)){
			tlsVersion = prevVersion + "\n" + tlsVersion;
		}
		domainToTLSVersion.put(request.getHostName(), tlsVersion);
		
		return request;
	}

	@Override
	public List<ReportRecord> createReportReportRowFor(ManagedProxy proxy, String domain) {
		String v = domainToTLSVersion.get(domain);
		ReportRecord version = null;
		if (v != null) {
			version = new ReportRecord(v, "");
		}else {
			version = new ReportRecord("n/a", "a valid TLS connection was not observed");
		}
		
		String handshakeFailureInfo = domainsWithHandshakeFailures.get(domain);
		
		String infoText = null;
		if (handshakeFailureInfo != null) {
			if (hadSuccessfullSecuredConnection(domain)) {
				infoText = "No (but handshakes did fail)";
			} else {
				infoText = "Yes";
			}
		} else {
			infoText = "No";
		}		
		
		ReportRecord pinning = new ReportRecord(infoText, handshakeFailureInfo != null ? ("At least one connection could not be watched due to the SSL handshake failing: '" + handshakeFailureInfo + "'") : "");

		return Arrays.asList(version, pinning);
	}
	
	private boolean hadSuccessfullSecuredConnection(String domain) {
		if (domainToTLSVersion.containsKey(domain) && (!domainToTLSVersion.get(domain).equalsIgnoreCase("-") || !domainToTLSVersion.get(domain).equalsIgnoreCase(""))) {
			return true;
		} else return false;
	}

	
	public static final List<ReportRecord> titlesRow = Arrays.asList(new ReportRecord("TLS versions", "TLS versions used by to connect to a domain"), new ReportRecord("Cert pinning used", "If cert pinning is used, the certificate installed on the users phone will not suffice to estublish the server as secure."));
	
	@Override
	public List<ReportRecord> getTitlesRowForResults() {
		return titlesRow;
	}

	@Override
	public void handshake(String domain, boolean success, String info) {
		
		System.err.println("domain: "+ domain + ": " + success + session + domainsWithHandshakeFailures.containsKey(domain) );
		
			if 		((session >= 0 && (session < 2 || session >= 5)) && 
					!success &&
					!domainsWithHandshakeFailures.containsKey(domain))
			{
				domainsWithHandshakeFailures.put(domain, info != null ? info : "No request was made. Possibly the SSL certificate was rejected.");
			}	
	}

	@Override
	public void prepareProxyForSession(Proxy currentProxy, int sessionNo) {
		session = sessionNo;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}
	
	@Override
	public Integer getOrderNumberForOutput() {
		return 1;
	}

}
