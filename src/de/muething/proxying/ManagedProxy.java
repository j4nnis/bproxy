package de.muething.proxying;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

import org.parosproxy.paros.control.Proxy;
import org.parosproxy.paros.core.proxy.ProxyListener;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.network.HttpMessage;
import org.xml.sax.SAXException;
import org.zaproxy.zap.PersistentConnectionListener;
import org.zaproxy.zap.ZapGetMethod;
import org.zaproxy.zap.control.ControlOverrides;

import de.muething.models.PersistedRequest;

public class ManagedProxy{
	public static final int MIN_PORT_NUMBER = 1100;

	public static final int MAX_PORT_NUMBER = 49151;
	
	Proxy proxy;
	
	int sesisonNo;
	
	public ManagedProxy() throws SAXException, IOException, Exception {
		Model model = new Model();
		ControlOverrides override = new ControlOverrides();
			
		int randomPort;
		do {
			randomPort = ThreadLocalRandom.current().nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER + 1);
		} while (!available(randomPort));
		
		override.setProxyPort(randomPort);
		override.setProxyHost("localhost");
		
		model.init(override);
		
		proxy = new Proxy(model, override);
	}
	
	public static boolean available(int port) {
	    if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
	        throw new IllegalArgumentException("Invalid start port: " + port);
	    }

	    ServerSocket ss = null;
	    DatagramSocket ds = null;
	    try {
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        ds = new DatagramSocket(port);
	        ds.setReuseAddress(true);
	        return true;
	    } catch (IOException e) {
	    } finally {
	        if (ds != null) {
	            ds.close();
	        }

	        if (ss != null) {
	            try {
	                ss.close();
	            } catch (IOException e) {
	                /* should not be thrown */
	            }
	        }
	    }

	    return false;
	}
	
}
