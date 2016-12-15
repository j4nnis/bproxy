package de.muething.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ReportRecord {
	public String value;

	public String detail;
	
	public ReportRecord(String value, String detail) {
		this.value = value;
		this.detail = detail;
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
}