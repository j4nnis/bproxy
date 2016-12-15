package de.muething.proxying;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.xml.sax.SAXException;

public class ProxyManager {
	public static final ProxyManager INSTANCE = new ProxyManager();
	
	private HashMap<String, ManagedProxy> proxies = new HashMap<>();
	
	synchronized ManagedProxy getManagedProxy() throws SAXException, IOException, Exception {
		String uniqueID;
		
		do {
			uniqueID = UUID.randomUUID().toString();
		} while (proxies.containsKey(uniqueID));
		
		
		ManagedProxy mProxy = new ManagedProxy(uniqueID);
		proxies.put(uniqueID, mProxy);
		
		return mProxy;
	}

	void newSessionWithProxy() {
		//set 
	}
	
}



