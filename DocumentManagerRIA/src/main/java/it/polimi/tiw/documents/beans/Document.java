package it.polimi.tiw.documents.beans;

import com.google.gson.JsonObject;

import it.polimi.tiw.documents.utils.JsonHandler;

public class Document extends Content {
	private Subfolder parentFolder;
	private String type;
	private String summary;

	public Subfolder getParentFolder() {
		return parentFolder;
	}

	public Document setParentFolder(Subfolder parentFolder) {
		this.parentFolder = parentFolder;
		return this;
	}

	public String getType() {
		return type;
	}

	public Document setType(String type) {
		this.type = type;
		return this;
	}

	public String getSummary() {
		return summary;
	}

	public Document setSummary(String summary) {
		this.summary = summary;
		return this;
	}

	@Override
	public User getOwner() {
		return parentFolder.getParentFolder().getOwner();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj.getClass() != Document.class)
			return false;
		Document document = (Document) obj;
		return getId() == document.getId();
	}
	
	@Override
	public JsonObject serialize() {
		JsonObject thisJson = JsonHandler.serialize(this);
		
		thisJson.remove("parentFolder");
		
		return thisJson;
	}
}
