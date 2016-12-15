package de.muething.proxying;

import java.net.Socket;

import org.parosproxy.paros.core.proxy.ProxyListener;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.PersistentConnectionListener;
import org.zaproxy.zap.ZapGetMethod;

import de.muething.DatabaseDriver;
import de.muething.models.PersistedRequest;

public class BProxyListener implements ProxyListener, PersistentConnectionListener {
	
	ManagedProxy proxy;
	
	private String proxyIdentifier;
	private int sessionNo;
	
	public BProxyListener(String proxyIdentifier, int sessionNo) {
		this.proxyIdentifier = proxyIdentifier;
		this.sessionNo = sessionNo;
	}
	
	@Override
	public int getArrangeableListenerOrder() {
		return 0;
	}

	@Override
	public boolean onHttpRequestSend(HttpMessage msg) {		
		return true;
	}

	@Override
	public boolean onHttpResponseReceive(HttpMessage msg) {

		System.out.println(msg.toString());
		
		return true;
	}
	
	//PersistentConnectionListener implementation
	@Override
	public boolean onHandshakeResponse(HttpMessage httpMessage, Socket inSocket, ZapGetMethod method) {
		PersistedRequest request = new PersistedRequest(proxyIdentifier, sessionNo, httpMessage, inSocket);
		DatabaseDriver.INSTANCE.getDatastore().save(request);
		return false;
	}

}
