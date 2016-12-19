package de.muething.modules;

import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.ZapGetMethod;

import de.muething.interfaces.ProxyJITAnalyzer;
import de.muething.interfaces.ProxyRequestResponseAnalyzer;
import de.muething.models.PersistedRequest;
import de.muething.models.ReportRecord;
import de.muething.proxying.ManagedProxy;

public class SSLAnalyzer extends ProxyRequestResponseAnalyzer implements ProxyJITAnalyzer{

	HashMap<String, String> domainToTLSVersion = new HashMap<>();
	
	
	@Override
	public PersistedRequest willPersistRequest(PersistedRequest request, HttpMessage httpMessage, Socket inSocket,
			ZapGetMethod method) {

		String tlsVersion = request.getTlsVersion();
		tlsVersion = tlsVersion == null || tlsVersion == "" ? "-" : tlsVersion;
				
		domainToTLSVersion.put(request.getHostName(), tlsVersion);
		
		return request;
	}

	@Override
	public List<ReportRecord> createReportReportRowFor(ManagedProxy proxy, String domain) {
		ReportRecord version = new ReportRecord(domainToTLSVersion.get(domain), "");
		ReportRecord pinning = new ReportRecord("not implemented", "");

		return Arrays.asList(version, pinning);
	}

	
	public static final List<ReportRecord> titlesRow = Arrays.asList(new ReportRecord("TLS version", "the oldest version used by requests to a server behind this domain"), new ReportRecord("Cert pinning used", "If cert pinning is used, the certificate installed on the users phone will not suffice to estublish the server as secure."));
	
	@Override
	public List<ReportRecord> getTitlesRowForResults() {
		return titlesRow;
	}

}
