package de.muething.models;

import de.muething.proxying.ManagedProxy;

public class ExternalProxyDescription {
	public String domain = "jane.local";
	public int port;
	public String appName;
	public int session;
	public String proxyID;
	public String state = "running";
	public String caCertDownloadLink = "/root_ca.cer";

	public ExternalProxyDescription(){}
	
	public ExternalProxyDescription(ManagedProxy proxy) {
		port = proxy.port;
		appName = proxy.appName;
		session = proxy.getSessionNo();
		proxyID = proxy.proxyIdentifier;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public int getSession() {
		return session;
	}

	public void setSession(int sessionNo) {
		this.session = sessionNo;
	}

	public String getProxyID() {
		return proxyID;
	}

	public void setProxyID(String proxyID) {
		this.proxyID = proxyID;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCaCertDownloadLink() {
		return caCertDownloadLink;
	}

	public void setCaCertDownloadLink(String caCertDownloadLink) {
		this.caCertDownloadLink = caCertDownloadLink;
	}
}
