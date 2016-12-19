package de.muething.modules;

import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.ZapGetMethod;

import de.muething.interfaces.ProxyJITAnalyzer;
import de.muething.interfaces.ProxyRequestResponseAnalyzer;
import de.muething.models.PersistedRequest;
import de.muething.models.ReportRecord;
import de.muething.proxying.ManagedProxy;

public class IPGeoLocationAnalyzer extends ProxyRequestResponseAnalyzer implements ProxyJITAnalyzer{

	private static Client client = ClientBuilder.newClient();
	private static WebTarget target = client.target("http://ip-api.com/json/");

	
	private HashMap<String, String> domainToNation = new HashMap<>();
	
	@Override
	public PersistedRequest willPersistRequest(PersistedRequest request, HttpMessage httpMessage, Socket inSocket,
			ZapGetMethod method) {

		if (domainToNation.get(request.getHostName()) == null) {
			GeoLocationServiceResponse response = target.path("{domain}")
        			.resolveTemplate("domain", request.getHostName())
                    .request()
                    .get(GeoLocationServiceResponse.class);
			domainToNation.put(request.getHostName(), response.toString());
		}
		
		return request;
	}

	@Override
	public List<ReportRecord> createReportReportRowFor(ManagedProxy proxy, String domain) {
		String nation = domainToNation.get(domain);
		nation = nation != null ? nation : "unknown";
			
		ReportRecord record = new ReportRecord(nation, "");
		
		return Arrays.asList(record);
	}

	
	public static final List<ReportRecord> titlesRow = Arrays.asList(new ReportRecord("Location", "The servers location according to '" + target.getUri().toString() + "'"));
	
	@Override
	public List<ReportRecord> getTitlesRowForResults() {
		return titlesRow;
	}

}

class GeoLocationServiceResponse {
	String country;

	public GeoLocationServiceResponse() {}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	@Override
	public String toString() {
		return country;
	}
}

