package de.muething.interfaces;

import java.net.Socket;

import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.ZapGetMethod;

import de.muething.models.PersistedRequest;

/*
 * This interface gives the implementing class an opportunity to look at every request goiing through the proxy without having to perform any database queries. 
 */

public interface ProxyJITAnalyzer{
	PersistedRequest willPersistRequest(PersistedRequest request, HttpMessage httpMessage, Socket inSocket, ZapGetMethod method);
}
