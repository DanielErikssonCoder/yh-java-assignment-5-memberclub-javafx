package org.example.memberclubjavafx_assignment5.service;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class helps GSON to save and load dates correctly.
 * It converts between LocalDateTime objects and text strings.
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    // We use this format for the dates
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Method to save (Java to JSON)
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {

        // Turn the date into text
        String dateString = src.format(formatter);
        return new JsonPrimitive(dateString);
    }

    // Method to load (JSON to Java)
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        // Get the text from the JSON file
        String text = json.getAsString();
        return LocalDateTime.parse(text, formatter);
    }
}