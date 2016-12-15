package de.muething.modules;

import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.parosproxy.paros.control.Proxy;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.ZapGetMethod;

import de.muething.interfaces.ProxyJITAnalyzer;
import de.muething.interfaces.ProxyRequestResponseAnalyzer;
import de.muething.models.PersistedRequest;
import de.muething.models.ReportRecord;
import de.muething.proxying.ManagedProxy;

public class DomainCounter extends ProxyRequestResponseAnalyzer implements ProxyJITAnalyzer {
	//mapping from domain to mapping from session to count
	HashMap<String, HashMap<Integer, Integer>> counts = new HashMap<>(); 
	
	static public final String domainRegExp = "^(?:https?:\\/\\/)?(?:[^@\\/\\n]+@)?(?:www\\.)?([^:\\/\\n]+)";
	static public final Pattern domainRegExpPattern = Pattern.compile(domainRegExp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
	
	public Iterator<String> getDomains() {
		return counts.keySet().iterator();
	}
	
	@Override
	public PersistedRequest willPersistRequest(PersistedRequest request, HttpMessage httpMessage, Socket inSocket,
			ZapGetMethod method) {

		final Matcher matcher = domainRegExpPattern.matcher(request.getRequestURL());
		
		if (matcher.find() && matcher.groupCount() > 1) {
			String match = matcher.group(1);
			
			HashMap<Integer, Integer> sessionToCounts = counts.get(match) == null ? new HashMap<Integer, Integer>() : counts.get(match);
			Integer countForSession = sessionToCounts.get(request.getNoOfSession())  == null ? 1 : sessionToCounts.get(request.getNoOfSession()) + 1;
			
			sessionToCounts.put(request.getNoOfSession(), countForSession);
			
			counts.put(match, sessionToCounts);
		}

		return request;
	}
	

	@Override
	public List<ReportRecord> createReportReportRowFor(ManagedProxy proxy, String domain) {
		ReportRecord domainName = new ReportRecord(domain, "");
		
		HashMap<Integer, Integer> sessionToCounts = counts.get(domain) == null ? new HashMap<Integer, Integer>() : counts.get(domain);
		String countsPerSession = "Number of times the domain was called per session\n";
		
		Integer[] array = (Integer[]) sessionToCounts.keySet().toArray();
		Arrays.sort(array);
		for (int i = 0; i < array.length; i++) {
			countsPerSession += "SessionNo. " + array[i].toString() + " : " + sessionToCounts.get(array[i]) + "x \n"; 
		}
		
		String sessionCountString = Integer.toString(sessionToCounts.size());
		
		ReportRecord sessionCount = new ReportRecord(sessionCountString, countsPerSession);
		
		return Arrays.asList(domainName, sessionCount);
	}
	
	
	
	
	public static final List<ReportRecord> titlesRow = Arrays.asList(new ReportRecord("Domain", "total number of times this domain was called. Including all paths."), new ReportRecord("Sessions", "Number of session this domain was called in. A small number indicates that this domain was not called by the app under testing."));

	@Override
	public List<ReportRecord> getTitlesRowForResults() {
		return titlesRow;
	}
	
}
