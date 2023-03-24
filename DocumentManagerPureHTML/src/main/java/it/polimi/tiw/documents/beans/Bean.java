package it.polimi.tiw.documents.beans;

public abstract class Bean {
	private int id;
	
	public int getId() {
		return id;
	}

	public Bean setId(int id) {
		this.id = id;
		return this;
	}
}
