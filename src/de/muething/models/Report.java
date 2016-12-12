package de.muething.models;

import java.util.ArrayList;

public class Report {
	public ArrayList<ReportRecord> legend;
	public ArrayList<ArrayList<ReportRecord>> values;
	
	class ReportRecord {
		String value;
		String detail;
	}
	
}
