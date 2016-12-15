package de.muething.interfaces;

import java.util.Iterator;

import org.mongodb.morphia.query.Query;
import org.parosproxy.paros.control.Proxy;

import de.muething.DatabaseDriver;
import de.muething.models.PersistedRequest;
import de.muething.models.Report;

public abstract class ProxyRequestResponseAnalyzer {
	abstract Report.ReportRecord createReportReportRowFor(Proxy proxy, String domain);
	
	Iterator<PersistedRequest> fetchReports(String domain) {
		final Query<PersistedRequest> query = DatabaseDriver.INSTANCE.getDatastore().createQuery(PersistedRequest.class);
		query.or(
				query.criteria("requestURL").startsWith("http://"+domain),
				query.criteria("requestURL").startsWith("http://"+domain));
		Iterator<PersistedRequest> iterator = query.fetch();

		return iterator;
	}
}
