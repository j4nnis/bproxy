package de.muething.models;

import org.mongodb.morphia.query.Query;

import de.muething.DatabaseDriver;

public class ReportGenerator {
	
	
	
	
	public Report getReportFor(String proxyIdentifier) {
		
		final Query<PersistedRequest> query = DatabaseDriver.INSTANCE.getDatastore().createQuery(PersistedRequest.class);

		
		
		return null;
	}
	
	
}
