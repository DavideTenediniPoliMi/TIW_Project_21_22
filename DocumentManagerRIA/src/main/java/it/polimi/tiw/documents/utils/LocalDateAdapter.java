package it.polimi.tiw.documents.utils;

import java.io.IOException;
import java.time.LocalDate;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {
	@Override
	public LocalDate read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
            return null;
        } else {
            return LocalDate.parse(reader.nextString());
        }
	}
	
	@Override
	public void write(JsonWriter writer, LocalDate localDate) throws IOException {
		if (localDate == null) {
			writer.nullValue();
        } else {
        	writer.value(localDate.toString());
        }
	}
}