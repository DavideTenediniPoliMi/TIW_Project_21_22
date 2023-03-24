package it.polimi.tiw.documents.utils;

import java.time.LocalDate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class JsonHandler {
	private static Gson gson;
	
	static {
		GsonBuilder builder = new GsonBuilder(); 
		builder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter()); 
		gson = builder.create();
	}
	
	public static JsonObject serialize(Object object) {
		return gson.toJsonTree(object).getAsJsonObject();
	}
}
