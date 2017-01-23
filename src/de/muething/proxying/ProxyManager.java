package de.muething.proxying;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.xml.sax.SAXException;

import de.muething.modules.DomainCounter;
import de.muething.modules.HeaderAnalyzer;
import de.muething.modules.IPGeoLocationAnalyzer;
import de.muething.modules.SSLAnalyzer;
import de.muething.modules.SSLCertificateTestDriverAndAnalyzer;
import de.muething.modules.UsernamePasswordDetector;

public class ProxyManager {
	public static final ProxyManager INSTANCE = new ProxyManager();	
	private HashMap<String, ManagedProxy> proxies = new HashMap<>();

	public synchronized ManagedProxy getNewManagedProxy() throws SAXException, IOException, Exception {
		String uniqueID;

		do {
			uniqueID = UUID.randomUUID().toString();
		} while (proxies.containsKey(uniqueID));

		ManagedProxy mProxy = new ManagedProxy(uniqueID);
		proxies.put(uniqueID, mProxy);

		DomainCounter dCounter = new DomainCounter();
		SSLAnalyzer sslAnalyzer = new SSLAnalyzer();
		HeaderAnalyzer headerAnalyzer = new HeaderAnalyzer();
		IPGeoLocationAnalyzer ipAnalyzer = new IPGeoLocationAnalyzer();
		SSLCertificateTestDriverAndAnalyzer sslTestDriver = new SSLCertificateTestDriverAndAnalyzer();
		UsernamePasswordDetector usernamePasswordD = new UsernamePasswordDetector();

		mProxy.add(dCounter);
		mProxy.add(sslAnalyzer);
		mProxy.add(headerAnalyzer);
		mProxy.add(ipAnalyzer);
		mProxy.add(sslTestDriver);
		mProxy.add(usernamePasswordD);

//		mProxy.addProxyAnalyzer(dCounter);
//		mProxy.addProxyRequestResponseAnalyzer(dCounter);
//		
//		mProxy.addProxyAnalyzer(sslAnalyzer);
//		mProxy.addProxyRequestResponseAnalyzer(sslAnalyzer);
//		mProxy.addProxyPerparator(sslAnalyzer);
//		
//		mProxy.addProxyAnalyzer(headerAnalyzer);
//		mProxy.addProxyRequestResponseAnalyzer(headerAnalyzer);
//		
//		mProxy.addProxyAnalyzer(ipAnalyzer);
//		mProxy.addProxyRequestResponseAnalyzer(ipAnalyzer);
//		
//		mProxy.addHandshakeListener(sslTestDriver);
//		mProxy.addProxyRequestResponseAnalyzer(sslTestDriver);
//		mProxy.addProxyPerparator(sslTestDriver);
		
		mProxy.setDomainCounter(dCounter);
		
		return mProxy;
	}

	public ManagedProxy getManagedProxy(String identifier) {
		return proxies.get(identifier);
	}

	public ManagedProxy newSessionWithProxy(String identifier) {
		ManagedProxy proxy = proxies.get(identifier);

		if (proxy != null) {
			proxy.startSession();
		}

		return proxy;

	}

}
