package de.muething.modules;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.parosproxy.paros.control.Proxy;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.ZapGetMethod;

import de.muething.interfaces.HandshakeListener;
import de.muething.interfaces.ProxyJITAnalyzer;
import de.muething.interfaces.ProxyPreparator;
import de.muething.interfaces.ProxyRequestResponseAnalyzer;
import de.muething.models.PersistedRequest;
import de.muething.models.ReportRecord;
import de.muething.proxying.ManagedProxy;

public class DomainCounter extends ProxyRequestResponseAnalyzer implements ProxyJITAnalyzer, HandshakeListener, ProxyPreparator {
	// mapping from domain to mapping from session to count
	HashMap<String, HashMap<Integer, Integer>> counts = new HashMap<>();

	private String identifier = "domainCounter";

	private int currentSession = -1;
	
	public Iterator<String> getDomains() {
		return counts.keySet().iterator();
	}

	@Override
	public PersistedRequest willPersistRequest(PersistedRequest request, HttpMessage httpMessage, Socket inSocket,
			ZapGetMethod method) {
		String match = request.getHostName();

		HashMap<Integer, Integer> sessionToCounts = counts.get(match) == null ? new HashMap<Integer, Integer>()
				: counts.get(match);
		Integer countForSession = sessionToCounts.get(request.getNoOfSession()) == null ? 1
				: sessionToCounts.get(request.getNoOfSession()) + 1;

		sessionToCounts.put(request.getNoOfSession(), countForSession);

		counts.put(match, sessionToCounts);

		tag(request, 0);
		
		return request;
	}

	@Override
	public List<ReportRecord> createReportReportRowFor(ManagedProxy proxy, String domain) {
		ReportRecord domainName = new ReportRecord(domain, "", getIdentifier(), domain, 0);

		HashMap<Integer, Integer> sessionToCounts = counts.get(domain) == null ? new HashMap<Integer, Integer>()
				: counts.get(domain);
		String countsPerSession = "Number of times the domain was called per session\n";

		ArrayList<Integer> array = new ArrayList<>(sessionToCounts.keySet());
		array.sort(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o1.compareTo(o2);
			}
		});

		for (Integer i : array) {
			countsPerSession += "SessionNo. " + i.toString() + " : " + sessionToCounts.get(i) + "x \n";
		}

		String sessionCountString = Integer.toString(sessionToCounts.size());

		ReportRecord sessionCount = new ReportRecord(sessionCountString, countsPerSession, getIdentifier(), domain, 1);

		return Arrays.asList(domainName, sessionCount);
	}

	public static final List<ReportRecord> titlesRow = Arrays.asList(
			new ReportRecord("Domain", "total number of times this domain was called. Including all paths."),
			new ReportRecord("Sessions",
					"Number of session this domain was called in. A small number indicates that this domain was not called by the app under testing."));

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
		return 0;
	}
	
	
	@Override
	public void handshake(String domain, boolean success, String info) {
		//this will count domains more often per successful connection within the same session. It is not that relevant to know exactly how many times a domain was called. Just if it was called in all the sessions.
		
		HashMap<Integer, Integer> sessionToCounts = counts.get(domain) == null ? new HashMap<Integer, Integer>()
				: counts.get(domain);
		Integer countForSession = sessionToCounts.get(currentSession) == null ? 1
				: sessionToCounts.get(currentSession) + 1;

		sessionToCounts.put(currentSession, countForSession);

		counts.put(domain, sessionToCounts);
	}

	@Override
	public void prepareProxyForSession(Proxy currentProxy, int sessionNo) {
		currentSession = sessionNo;
	}

}
