package de.muething.models;

import java.util.Arrays;
import java.util.List;


public class Report {
	public String name = "Test App";
	
	public List<ReportRecord> legend;
	public List<Row> values;
	
	public static Report testReport;
	
	public Report() {
		
	}
	
	static {
		testReport = new Report();
		
		testReport.legend = Arrays.asList(new ReportRecord("Domain", ""), new ReportRecord("Sessions", "number of sessions"));
		testReport.values = Arrays.asList(
				new Row(Arrays.asList(new ReportRecord("exaple1.com", ""), new ReportRecord("5", "0: 5x"))),
				new Row(Arrays.asList(new ReportRecord("exaple2.com", ""), new ReportRecord("5", "0: 5x")))
				);

	}

	public List<ReportRecord> getLegend() {
		return legend;
	}

	public void setLegend(List<ReportRecord> legend) {
		this.legend = legend;
	}

	public List<Row> getValues() {
		return values;
	}

	public void setValues(List<Row> values) {
		this.values = values;
	}
	
	public static class Row {	
		public List<ReportRecord> row;
		
		public Row(){
			
		}
		
		public Row(List<ReportRecord> row) {
			this.row = row;
		}

		public List<ReportRecord> getRow() {
			return row;
		}

		public void setRow(List<ReportRecord> row) {
			this.row = row;
		}
	}
}