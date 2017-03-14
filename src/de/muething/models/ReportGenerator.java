package de.muething.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.muething.interfaces.ProxyAnalyzer;
import de.muething.models.Report.Row;
import de.muething.proxying.ManagedProxy;
import de.muething.proxying.ProxyManager;

public class ReportGenerator {

	public static Report getReportFor(String proxyIdentifier) {
	
		ManagedProxy proxy = ProxyManager.INSTANCE.getManagedProxy(proxyIdentifier);
		if (proxy == null) {
			return null;
		}
		
		Report report = new Report();
		
		report.name = proxy.appName;
		
		ArrayList<ReportRecord> legend = new ArrayList<ReportRecord>();
		
		
		
		Object[] orderedOut = proxy.getRequestResponseAnalyzers().values().toArray(); 
		Arrays.sort(orderedOut);
		
		for (int i = 0; i < orderedOut.length; i++) {
			ProxyAnalyzer analyzer = (ProxyAnalyzer) orderedOut[i];
			legend.addAll(analyzer.getTitlesRowForResults());

		}
		
		report.legend = legend;
		
		
		ArrayList<Row> rows = new ArrayList<>();
		
		
		Iterator<String> iterator = proxy.getDomainCounter().getDomains();
		while (iterator.hasNext()) {
			String domain = iterator.next();
			ArrayList<ReportRecord> row = new ArrayList<ReportRecord>();
			
			for (int i = 0; i < orderedOut.length; i++) {
				ProxyAnalyzer analyzer = (ProxyAnalyzer) orderedOut[i];
				row.addAll(analyzer.createReportReportRowFor(proxy, domain));				
			}
			
			rows.add(new Row(row));
		}
		
		report.values = rows;
		
		return report;
	}
	
	public static List<PersistedRequest> getProofFor(String proxyIdentifier, String domain, String analyzerIdentifier, int column) {
		ManagedProxy proxy = ProxyManager.INSTANCE.getManagedProxy(proxyIdentifier);
		if (proxy == null) {
			return null;
		}
		 
		ProxyAnalyzer analyzer = proxy.getAnalyzerForIdentifier(analyzerIdentifier);
		if (analyzer != null) {
			return analyzer.getProofForResultsFor(proxyIdentifier ,column, domain);
		}
		
		return null;
	}
	
	
}
