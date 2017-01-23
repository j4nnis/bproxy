package de.muething.modules;

import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.ZapGetMethod;

import de.muething.interfaces.ProxyJITAnalyzer;
import de.muething.interfaces.ProxyRequestResponseAnalyzer;
import de.muething.models.PersistedRequest;
import de.muething.models.ReportRecord;
import de.muething.proxying.ManagedProxy;

public class UsernamePasswordDetector extends ProxyRequestResponseAnalyzer implements ProxyJITAnalyzer {

	Pattern usernamePasswordPattern = Pattern.compile("((password|username|user|pass) *[:=])+", Pattern.CASE_INSENSITIVE);
	
	private HashMap<String, String> domainToUsernamePasswordLeakBody = new HashMap<>();
	private HashMap<String, String> domainToUsernamePasswordLeakURL = new HashMap<>();

	private static String identifier = "usernamePasswordDetector";

	@Override
	public PersistedRequest willPersistRequest(PersistedRequest request, HttpMessage httpMessage, Socket inSocket,
			ZapGetMethod method) {

		Matcher m = usernamePasswordPattern.matcher(request.getRequestBody());
		if (m.find()) {
			domainToUsernamePasswordLeakBody.put(request.getHostName(), "Possible leak (Body)");
			
			tag(request, 0);
		}
		
		m = usernamePasswordPattern.matcher(request.getRequestURL());
		if (m.find()) {
			domainToUsernamePasswordLeakURL.put(request.getHostName(), "Possible leak (URL)");
			
			tag(request, 0);
		}

		return request;
	}

	@Override
	public List<ReportRecord> createReportReportRowFor(ManagedProxy proxy, String domain) {
		String leakInfoString = "";
		
		if (domainToUsernamePasswordLeakBody.containsKey(domain)) {
			leakInfoString = domainToUsernamePasswordLeakBody.get(domain);
		}
		
		if (domainToUsernamePasswordLeakURL.containsKey(domain)) {
			leakInfoString += domainToUsernamePasswordLeakURL.get(domain);
		}
		
		ReportRecord record = new ReportRecord(leakInfoString, "", getIdentifier(), domain, 0);
		
		return  Arrays.asList(record);
	}

	
	public static final List<ReportRecord> titlesRow = Arrays.asList(new ReportRecord("Username/Password leak", "Possible username and/or password leak."));
	
	@Override
	public List<ReportRecord> getTitlesRowForResults() {
		return titlesRow;
	}


	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public Integer getOrderNumberForOutput() {
		return 5;
	}

}
