package de.muething.modules;

import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import org.parosproxy.paros.control.Proxy;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.security.CachedSslCertifificateServiceImpl;
import org.zaproxy.zap.ZapGetMethod;

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

public class SSLCertificateTestDriverAndAnalyzer extends ProxyRequestResponseAnalyzer implements ProxyJITAnalyzer, ProxyPreparator{

	@Override
	public PersistedRequest willPersistRequest(PersistedRequest request, HttpMessage httpMessage, Socket inSocket,
			ZapGetMethod method) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ReportRecord> createReportReportRowFor(ManagedProxy proxy, String domain) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static final List<ReportRecord> titlesRow = Arrays.asList(
			new ReportRecord("SSL Test 1", "dyn host, signed by untrusted"),
			new ReportRecord("SSL Test 2", "dynamic host, self signed certificate"),
			new ReportRecord("SSL Test 3", "static host, trusted ca"),
			new ReportRecord("SSL Test 4", "static host, self signed certificate"));
	

	@Override
	public List<ReportRecord> getTitlesRowForResults() {
		return titlesRow;
	}
	
	@Override
	public void prepareProxyForSession(Proxy currentProxy, int sessionNo) {
		switch (sessionNo) {
		case 0:
		case 1:
			currentProxy.setCertificateService(CachedSslCertifificateServiceImpl.getService());
			break;
		case 2: 
			currentProxy.setCertificateService(DynamicHostNameSignedByUntrustedCertificateService.getService());
			break;
		case 3: 
			currentProxy.setCertificateService(DynamicHostNameUntrustedCertificateService.getService());
			break;
		case 4: 
			currentProxy.setCertificateService(StaticHostNameCertficateService.getService());
			break;
		case 5: 
			currentProxy.setCertificateService(StaticHostNameUntrustedCertificateService.getService());
			break;
		}
		
	}

}
