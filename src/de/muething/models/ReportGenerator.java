package de.muething.models;

import java.util.ArrayList;
import java.util.Iterator;

import de.muething.interfaces.ProxyRequestResponseAnalyzer;
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
		
		for (ProxyRequestResponseAnalyzer analyzer : proxy.getRequestResponseAnalyzers()) {
			legend.addAll(analyzer.getTitlesRowForResults());
		}
		
		report.legend = legend;
		
		
		ArrayList<Row> rows = new ArrayList<>();
		
		
		Iterator<String> iterator = proxy.getDomainCounter().getDomains();
		
		while (iterator.hasNext()) {
			String domain = iterator.next();
			ArrayList<ReportRecord> row = new ArrayList<ReportRecord>();
			
			for (ProxyRequestResponseAnalyzer analyzer : proxy.getRequestResponseAnalyzers()) {
				row.addAll(analyzer.createReportReportRowFor(proxy, domain));
			}
			
			rows.add(new Row(row));
		}
		
		report.values = rows;
		
		return report;
	}
	
	
}
