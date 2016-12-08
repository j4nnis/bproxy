package de.muething.models;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
	private List<Headerfield> requestHeaders;

	@Embedded("responseHeaders")
	private List<Headerfield> responseHeaders;

	private String requestBody;
	
	private String resonseBody;
	
	private String requestURL;

	private String tlsVersion;

	public PersistedRequest(String proxyIdentifier, Integer sessionNo, HttpMessage message, Socket inSocket) {
		this.uniqueProxyIdentifier = proxyIdentifier;
		this.noOfSession = sessionNo;
		
		this.timestamp = new Date();
		this.httpMethod = message.getRequestHeader().getMethod();
		
		this.requestHeaders = this.convertHeaderfields(message.getRequestHeader().getHeaders());
		this.responseHeaders = this.convertHeaderfields(message.getResponseHeader().getHeaders());

		this.requestBody = message.getRequestBody().toString();
		this.resonseBody = message.getResponseBody().toString();
		
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

	public String getResonseBody() {
		return resonseBody;
	}

	public void setResonseBody(String resonseBody) {
		this.resonseBody = resonseBody;
	}

	// convenience methods
	private List<Headerfield> convertHeaderfields(List<HttpHeaderField> headerfields) {
		List<Headerfield> fields = new ArrayList<Headerfield>();
		for (HttpHeaderField hf : headerfields) {
			fields.add(new Headerfield(hf.getName(), hf.getValue()));
		}
		return fields;
	}
}

class Headerfield {
	private String key;
	private String value;
	
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
