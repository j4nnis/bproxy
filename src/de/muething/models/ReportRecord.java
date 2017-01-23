package de.muething.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ReportRecord {
	public String value;

	public String detail;
	
	public String analyzerDomain;
	public String analyzerIdentifier;
	public Integer column;
	
	public ReportRecord(String value, String detail, String analyzer, String analyzerDomain, Integer column) {
		this.value = value;
		this.detail = detail;
		
		this.analyzerDomain = analyzerDomain;
		this.analyzerIdentifier = analyzer;
		this.column = column;
	}
	
	public ReportRecord(String value, String detail) {
		this(value, detail, "", "", -1);
	}
	
	public ReportRecord(){
		
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getAnalyzerIdentifier() {
		return analyzerIdentifier;
	}

	public void setAnalyzerIdentifier(String analyzerIdentifier) {
		this.analyzerIdentifier = analyzerIdentifier;
	}

	public Integer getColumn() {
		return column;
	}

	public void setColumn(Integer column) {
		this.column = column;
	}
}