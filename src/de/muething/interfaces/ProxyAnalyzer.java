package de.muething.interfaces;

import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mongodb.morphia.query.Query;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.ZapGetMethod;

import de.muething.DatabaseDriver;
import de.muething.models.PersistedRequest;
import de.muething.models.ReportRecord;
import de.muething.proxying.ManagedProxy;

public abstract class ProxyAnalyzer implements Comparable<ProxyAnalyzer> {
	public abstract List<ReportRecord> createReportReportRowFor(ManagedProxy proxy, String domain);
	public abstract List<ReportRecord> getTitlesRowForResults();
	
	public abstract String getIdentifier();
	
	public List<PersistedRequest> getProofForResultsFor(String proxyIdentifier, int column, String domain) {
		
		final Query<PersistedRequest> query = DatabaseDriver.INSTANCE.getDatastore().createQuery(PersistedRequest.class);
		query.disableValidation();
		query.and(
				query.criteria("uniqueProxyIdentifier").equal(proxyIdentifier),
				query.criteria("hostName").equalIgnoreCase(domain),
				query.criteria("tags."+getIdentifier()).equal(column));
		List<PersistedRequest> list = query.order("timestamp").asList();
		return list;
	}
	
	Iterator<PersistedRequest> fetchReports(String domain) {
		final Query<PersistedRequest> query = DatabaseDriver.INSTANCE.getDatastore().createQuery(PersistedRequest.class);
		query.or(
				query.criteria("requestURL").startsWith("http://"+domain),
				query.criteria("requestURL").startsWith("https://"+domain));
		Iterator<PersistedRequest> iterator = query.fetch();

		return iterator;
	}
	
	public void tag(PersistedRequest request, int column) {
		Set<Integer> tags = request.getTags().get(getIdentifier());
		if (tags == null) {
			tags = new HashSet<>();
			request.getTags().put(getIdentifier(), tags);
		}
		
		tags.add(column);
	}
	
	public abstract Integer getOrderNumberForOutput();

	@Override
	public int compareTo(ProxyAnalyzer o) {
		return this.getOrderNumberForOutput().compareTo(o.getOrderNumberForOutput());
	}
	
	public abstract PersistedRequest willPersistRequest(PersistedRequest request, HttpMessage httpMessage, Socket inSocket, ZapGetMethod method);
	
}
	