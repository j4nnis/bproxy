package de.muething.modules;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.parosproxy.paros.control.Proxy;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.security.CachedSslCertifificateServiceImpl;
import org.zaproxy.zap.ZapGetMethod;

import de.muething.interfaces.HandshakeListener;
import de.muething.interfaces.ProxyJITAnalyzer;
import de.muething.interfaces.ProxyPreparator;
import de.muething.interfaces.ProxyRequestResponseAnalyzer;
import de.muething.models.PersistedRequest;
import de.muething.models.ReportRecord;
import de.muething.modules.ssl.DynamicHostNameSignedByUntrustedCertificateService;
import de.muething.modules.ssl.DynamicHostNameUntrustedCertificateService;
import de.muething.modules.ssl.StaticHostNameCertficateService;
import de.muething.modules.ssl.StaticHostNameUntrustedCertificateService;
import de.muething.proxying.ManagedProxy;

public class SSLCertificateTestDriverAndAnalyzer extends ProxyRequestResponseAnalyzer implements HandshakeListener, ProxyPreparator, ProxyJITAnalyzer{
	int sessionNo = -1;	
	List<HashMap<String, List<Result>>> testResultsForDomains = new LinkedList<>();
	
	private static String identifier = "sslCertificateTestDriverAndAnalyzer";
	
	@Override
	public void handshake(String domain, boolean success, String info) {
		int size = testResultsForDomains.size();
		if (size > 0) {
			HashMap<String, List<Result>> map = testResultsForDomains.get(size-1);
			
			if (!map.containsKey(domain)) {
				map.put(domain,new ArrayList<Result>(Arrays.asList(new Result(success, info))));
			} else {
				map.get(domain).add(new Result(success, info));
			}
		}
	}
	
	@Override
	public List<ReportRecord> createReportReportRowFor(ManagedProxy proxy, String domain) {
		
		ArrayList<ReportRecord> records = new ArrayList<>();
		
		int currSession = -1;
		
		for (HashMap<String, List<Result>> session : testResultsForDomains) {
			currSession++;

			if (currSession < 2) {
				continue;
			}
			
			if (currSession > 5) {
				break;
			}

			
			if (session.containsKey(domain)) {
				List<Result> domainResults = session.get(domain);
				Result result = succeedingResultOrFirst(domainResults);
				
				records.add(new ReportRecord(result.success ? "failed" : "passed", resultsAsString(domainResults)));
			} else {
				records.add(new ReportRecord("Nothing observed", "A connection to this domain was not observed during this test's session"));
			}
		}
		
		return records;
	}
	
	public static final List<ReportRecord> titlesRow = Arrays.asList(
			new ReportRecord("SSL Test 1", "dyn host, signed by untrusted"),
			new ReportRecord("SSL Test 2", "dynamic host, self signed certificate"),
			new ReportRecord("SSL Test 3", "static host, trusted ca"),
			new ReportRecord("SSL Test 4", "static host, self signed certificate"));
	

	@Override
	public List<ReportRecord> getTitlesRowForResults() {
		
		int endIndex =  testResultsForDomains.size()-2;
		endIndex = endIndex >= 0 ? endIndex : 0;
		endIndex = endIndex <= 4 ? endIndex : 4;
		
		return titlesRow.subList(0,endIndex);
	}
	
	@Override
	public void prepareProxyForSession(Proxy currentProxy, int sessionNo) {
		System.out.println("preparing for session: "+ sessionNo);
		this.sessionNo = sessionNo;
		
		switch (sessionNo) {
		case 0:
		case 1:
			currentProxy.setCertificateService(CachedSslCertifificateServiceImpl.getService());
			break;
		case 2:
			currentProxy.setCertificateService(DynamicHostNameSignedByUntrustedCertificateService.getCachedService());
			break;
		case 3: 
			currentProxy.setCertificateService(DynamicHostNameUntrustedCertificateService.getCachedService());
			break;
		case 4: 
			currentProxy.setCertificateService(StaticHostNameCertficateService.getCachedService());
			break;
		case 5: 
			currentProxy.setCertificateService(StaticHostNameUntrustedCertificateService.getCachedService());
			break;
		default:
			currentProxy.setCertificateService(CachedSslCertifificateServiceImpl.getService());
			break;
		}
		
		
		testResultsForDomains.add(new HashMap<>());
		
	}

	private class Result {
		boolean success;
		String info;
		
		Result(boolean success, String info) {
			this.success = success;
			this.info = info;
		}
		
		@Override
		public String toString() {
			return (success ? "Successful" : "Failed") + " Handshake" + (success ? "" :  (" with info" + info)) + "." ;
		}
	}
	
	private static final Result succeedingResultOrFirst(List<Result> results) {
		for (Result result : results) {
			if (result.success) {
				return result;
			}
		}
		
		return results.get(0);
		
	}
	
	private static final String resultsAsString(List<Result> results) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Observed handshakes to this domain: \n");
		for (Result result : results) {
			buffer.append(result.toString());
			buffer.append("\n");
		}
		
		return buffer.toString();
		
	}

	@Override
	public PersistedRequest willPersistRequest(PersistedRequest request, HttpMessage httpMessage, Socket inSocket,
			ZapGetMethod method) {
		return request;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}
	
	@Override
	public Integer getOrderNumberForOutput() {
		return 4;
	}
}
