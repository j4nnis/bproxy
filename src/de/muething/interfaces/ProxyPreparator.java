package de.muething.interfaces;

import org.parosproxy.paros.control.Proxy;

public interface ProxyPreparator {
	void prepareProxyForSession(Proxy currentProxy, int sessionNo);
}
