package de.muething.proxying;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.xml.sax.SAXException;

import de.muething.modules.DomainCounter;

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
		
		mProxy.addProxyAnalyzer(dCounter);
		mProxy.addProxyRequestResponseAnalyzer(dCounter);
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
