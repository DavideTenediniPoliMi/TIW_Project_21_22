package it.polimi.tiw.documents.beans;

import com.google.gson.JsonObject;

import it.polimi.tiw.documents.utils.JsonHandler;

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
	
	@Override
	public JsonObject serialize() {
		JsonObject thisJson = JsonHandler.serialize(this);
		
		thisJson.remove("parentFolder");
		
		return thisJson;
	}
}
