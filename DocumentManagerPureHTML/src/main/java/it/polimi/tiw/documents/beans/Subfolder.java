package it.polimi.tiw.documents.beans;

public class Subfolder extends Content {
	private Folder parentFolder;

	public Folder getParentFolder() {
		return parentFolder;
	}

	public Subfolder setParentFolder(Folder parentFolder) {
		this.parentFolder = parentFolder;
		return this;
	}

	@Override
	public User getOwner() {
		return parentFolder.getOwner();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj.getClass() != Subfolder.class)
			return false;
		Subfolder subfolder = (Subfolder) obj;
		return getId() == subfolder.getId();
	}
}
