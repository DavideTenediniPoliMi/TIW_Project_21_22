package it.polimi.tiw.documents.utils;

public class HttpVariable {
	private String name;
	private String value;
	
	public HttpVariable(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
}
