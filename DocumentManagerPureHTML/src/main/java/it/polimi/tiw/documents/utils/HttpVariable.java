package it.polimi.tiw.documents.utils;

public class HttpVariable {
	private String name;
	private Object value;
	
	public HttpVariable(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public Object getValue() {
		return value;
	}
}
