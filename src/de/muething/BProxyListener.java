package de.muething;

import java.net.Socket;

import org.parosproxy.paros.core.proxy.ProxyListener;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.PersistentConnectionListener;
import org.zaproxy.zap.ZapGetMethod;

public class BProxyListener implements ProxyListener, PersistentConnectionListener {

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

		
		
		return false;
	}

}
