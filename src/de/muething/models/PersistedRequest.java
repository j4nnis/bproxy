package de.muething.models;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLSocket;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.parosproxy.paros.network.HttpHeaderField;
import org.parosproxy.paros.network.HttpMessage;

@Entity("requests")
@Indexes(@Index(fields = @Field("timestamp")))
public class PersistedRequest {
	@Id
	private ObjectId id;
	
	private String uniqueProxyIdentifier;
	
	private Integer noOfSession;

	private Date timestamp;

	private String httpMethod;

	@Embedded("requestHeaders")
	private List<Headerfield> requestHeaders = new LinkedList<>();

	@Embedded("responseHeaders")
	private List<Headerfield> responseHeaders = new LinkedList<>();

	private String requestBody;
	
	private String responseBody;
	
	private String requestURL;
		
	private String hostName;

	private String tlsVersion;
	
	private Integer responseStatusCode;

	private Map<String, Set<Integer>> tags;
	
	public PersistedRequest() {
		
	}
	
	public PersistedRequest(String proxyIdentifier, Integer sessionNo, HttpMessage message, Socket inSocket) {
		this.uniqueProxyIdentifier = proxyIdentifier;
		this.noOfSession = sessionNo;
		
		this.timestamp = new Date();
		this.httpMethod = message.getRequestHeader().getMethod();
		
		this.requestHeaders = this.convertHeaderfields(message.getRequestHeader().getHeaders());
		this.responseHeaders = this.convertHeaderfields(message.getResponseHeader().getHeaders());

		this.requestBody = message.getRequestBody().toString();
		this.responseBody = message.getResponseBody().toString();
		
		this.requestURL = message.getRequestHeader().getURI().toString();
		this.hostName = message.getRequestHeader().getHostName();
		
		this.responseStatusCode = message.getResponseHeader().getStatusCode();
		
		this.tags = new HashMap<>();
		
		if (inSocket instanceof SSLSocket) {
			this.tlsVersion = ((SSLSocket)inSocket).getSession().getProtocol();
		}
	}
	
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public String getUniqueProxyIdentifier() {
		return uniqueProxyIdentifier;
	}

	public void setUniqueProxyIdentifier(String uniqueProxyIdentifier) {
		this.uniqueProxyIdentifier = uniqueProxyIdentifier;
	}

	public String getTlsVersion() {
		return tlsVersion;
	}

	public void setTlsVersion(String tlsVersion) {
		this.tlsVersion = tlsVersion;
	}

	public Integer getNoOfSession() {
		return noOfSession;
	}

	public void setNoOfSession(Integer noOfSession) {
		this.noOfSession = noOfSession;
	}
	
	
	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	// convenience methods
	private List<Headerfield> convertHeaderfields(List<HttpHeaderField> headerfields) {
		List<Headerfield> fields = new ArrayList<Headerfield>();
		for (HttpHeaderField hf : headerfields) {
			fields.add(new Headerfield(hf.getName(), hf.getValue()));
		}
		return fields;
	}

	public Integer getResponseStatusCode() {
		return responseStatusCode;
	}

	public void setResponseStatusCode(Integer responseStatusCode) {
		this.responseStatusCode = responseStatusCode;
	}

	public Map<String, Set<Integer>> getTags() {
		return tags;
	}

	public void setTags(Map<String, Set<Integer>> tags) {
		this.tags = tags;
	}

	public List<Headerfield> getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(List<Headerfield> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public List<Headerfield> getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(List<Headerfield> responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}
}

class Headerfield {
	private String key = "";
	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private String value = "";
	
	public Headerfield() {
		
	}
	
	public Headerfield(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
