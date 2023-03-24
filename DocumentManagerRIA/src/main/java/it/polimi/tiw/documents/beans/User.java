package it.polimi.tiw.documents.beans;

import com.google.gson.JsonObject;

import it.polimi.tiw.documents.utils.JsonHandler;

public class User extends Bean {
	private String username;
	private String email;
	private byte[] password;

	public String getUsername() {
		return username;
	}

	public User setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public User setEmail(String email) {
		this.email = email;
		return this;
	}

	public byte[] getPassword() {
		return password;
	}

	public User setPassword(byte[] password) {
		this.password = password;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj.getClass() != User.class)
			return false;
		User user = (User) obj;
		return getId() == user.getId();
	}
	
	@Override
	public JsonObject serialize() {
		return JsonHandler.serialize(this);
	}
}
