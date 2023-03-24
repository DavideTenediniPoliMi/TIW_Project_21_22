package it.polimi.tiw.documents.beans;

public class Folder extends Content {
	private User owner;

	@Override
	public User getOwner() {
		return owner;
	}

	public Folder setOwner(User owner) {
		this.owner = owner;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj.getClass() != Folder.class)
			return false;
		Folder folder = (Folder) obj;
		return getId() == folder.getId();
	}
}
