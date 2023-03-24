package it.polimi.tiw.documents.beans;

import java.time.LocalDate;

public abstract class Content extends Bean {
	private String name;
	private LocalDate creationDate;

	public String getName() {
		return name;
	}

	public Content setName(String name) {
		this.name = name;
		return this;
	}

	public LocalDate getCreationDate() {
		return creationDate;
	}

	public Content setCreationDate(LocalDate creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public abstract User getOwner();
}