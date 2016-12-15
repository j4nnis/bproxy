package de.muething.interfaces;

import java.util.Iterator;
import java.util.List;

import org.mongodb.morphia.query.Query;

import de.muething.DatabaseDriver;
import de.muething.models.PersistedRequest;
import de.muething.models.ReportRecord;
import de.muething.proxying.ManagedProxy;

public abstract class ProxyRequestResponseAnalyzer {
	public abstract List<ReportRecord> createReportReportRowFor(ManagedProxy proxy, String domain);
	public abstract List<ReportRecord> getTitlesRowForResults();
	
	Iterator<PersistedRequest> fetchReports(String domain) {
		final Query<PersistedRequest> query = DatabaseDriver.INSTANCE.getDatastore().createQuery(PersistedRequest.class);
		query.or(
				query.criteria("requestURL").startsWith("http://"+domain),
				query.criteria("requestURL").startsWith("http://"+domain));
		Iterator<PersistedRequest> iterator = query.fetch();

		return iterator;
	}

}
