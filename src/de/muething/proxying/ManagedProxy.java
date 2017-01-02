package de.muething.proxying;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.parosproxy.paros.control.Proxy;
import org.parosproxy.paros.core.proxy.ConnectRequestProxyListener;
import org.parosproxy.paros.core.proxy.ProxyListener;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.network.HttpMessage;
import org.xml.sax.SAXException;
import org.zaproxy.zap.PersistentConnectionListener;
import org.zaproxy.zap.ZapGetMethod;
import org.zaproxy.zap.control.ControlOverrides;

import de.muething.interfaces.HandshakeListener;
import de.muething.interfaces.ProxyJITAnalyzer;
import de.muething.interfaces.ProxyPreparator;
import de.muething.interfaces.ProxyRequestResponseAnalyzer;
import de.muething.models.PersistedRequest;
import de.muething.modules.DomainCounter;

public class ManagedProxy implements PersistentConnectionListener, HandshakeListener{
	public static final int MIN_PORT_NUMBER = 1100;
	public static final int MAX_PORT_NUMBER = 49151;
	
	public Proxy proxy;
	
	int sessionNo = -1;
	
	private List<ProxyJITAnalyzer> analyzers = new LinkedList<ProxyJITAnalyzer>();
	private List<HandshakeListener> handshakeListeners = new LinkedList<HandshakeListener>();

	private List<ProxyPreparator> preparators = new LinkedList<ProxyPreparator>();
	private List<ProxyRequestResponseAnalyzer> requestResponseAnalyzers = new LinkedList<>();

	private DomainCounter domainCounter;
	
	public String appName = "undefinded";
	public String proxyIdentifier;
	public int port;

	public ManagedProxy(String identifier) throws SAXException, IOException, Exception {
		proxyIdentifier = identifier;
		
		Model model = new Model();
		ControlOverrides override = new ControlOverrides();
			
		int randomPort;
		do {
			randomPort = ThreadLocalRandom.current().nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER + 1);
		} while (!available(randomPort));
		
		
		port = randomPort;
		override.setProxyPort(randomPort);
		override.setProxyHost("jane.local");
		
		model.init(override);
		
		proxy = new Proxy(model, override);
		proxy.addPersistentConnectionListener(this);
		proxy.addHandshakeListener(this);
		
		proxy.startServer();
	}
	
	public void startSession() {
		for (ProxyPreparator preparator : preparators) {
			preparator.prepareProxyForSession(proxy, sessionNo + 1);
		}
		sessionNo += 1;
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
	
	public void addProxyPerparator(ProxyPreparator preparator) {
		preparators.add(preparator);
	}
	
	public void addProxyAnalyzer(ProxyJITAnalyzer analyzer) {
		analyzers.add(analyzer);
	}
	
	public void addProxyRequestResponseAnalyzer(ProxyRequestResponseAnalyzer analyzer) {
		requestResponseAnalyzers.add(analyzer);
	}
	
	public void removeProxyPerparator(ProxyPreparator preparator) {
		preparators.remove(preparator);
	}
	
	public void removeProxyAnalyzer(ProxyJITAnalyzer analyzer) {
		analyzers.remove(analyzer);
	}
	
	public void removeProxyRequestResponseAnalyzer(ProxyRequestResponseAnalyzer analyzer) {
		requestResponseAnalyzers.remove(analyzer);
	}
	
	public void addHandshakeListener(HandshakeListener handshakeListener) {
		handshakeListeners.add(handshakeListener);
	}
	
	public void removeHandshakeListener(HandshakeListener handshakeListener) {
		handshakeListeners.remove(handshakeListener);
	}
	
	public int getSessionNo() {
		return sessionNo;
	}

	public void setSessionNo(int sessionNo) {
		this.sessionNo = sessionNo;
	}

	public DomainCounter getDomainCounter() {
		return domainCounter;
	}

	public void setDomainCounter(DomainCounter domainCounter) {
		this.domainCounter = domainCounter;
	}
	
	public List<ProxyRequestResponseAnalyzer> getRequestResponseAnalyzers() {
		return requestResponseAnalyzers;
	}

	//PersistentConnectionListener

	@Override
	public int getArrangeableListenerOrder() {
		return 0;
	}
	
	
	

	@Override
	public boolean onHandshakeResponse(HttpMessage httpMessage, Socket inSocket, ZapGetMethod method) {
		PersistedRequest request = new PersistedRequest(proxyIdentifier, sessionNo, httpMessage, inSocket);
		
		for (ProxyJITAnalyzer jitAnalyzer : analyzers) {
			request = jitAnalyzer.willPersistRequest(request, httpMessage, inSocket, method);
		}
		
		//DatabaseDriver.INSTANCE.getDatastore().save(request);
		return false;
	}

	@Override
	public void handshake(String domain, boolean success, String info) {
		for (HandshakeListener handshakeListener : handshakeListeners) {
			handshakeListener.handshake(domain, success, info);
		}
	}
	
}
