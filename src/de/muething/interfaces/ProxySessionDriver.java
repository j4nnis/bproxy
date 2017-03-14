package de.muething.interfaces;

import org.parosproxy.paros.control.Proxy;

public interface ProxySessionDriver {
	void prepareProxyForSession(Proxy currentProxy, int sessionNo);
}
