package storage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import logic.Constants;
import logic.Task;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import parser.Interval;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Contains two static methods, one for creating a JSON document out of a list of objects
 * and another for creating a list of objects out of a JSON document.
 * @author Matthew
 *
 */
public class Json {
	
	private static DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().
			appendPattern(Constants.DATE_TIME_FORMAT).toFormatter();
	
	static Gson jsonFormatter = new GsonBuilder().setPrettyPrinting().
			registerTypeAdapter(DateTime.class, new DateTimeSerializer()).
			registerTypeAdapter(DateTime.class, new DateTimeDeserializer()).
			registerTypeAdapter(Interval.class, new IntervalSerializer()).
			registerTypeAdapter(Interval.class, new IntervalDeserializer()).
			create();
	
	private static class IntervalSerializer implements JsonSerializer<Interval> {
		public JsonElement serialize(Interval src, Type typeOfSrc,
				JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}
	}
	
	private static class IntervalDeserializer implements JsonDeserializer<Interval> {
		public Interval deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			String[] dates = json.getAsString().split(" to ");
			return new Interval(dateTimeFormatter.parseDateTime(dates[0]), dateTimeFormatter.parseDateTime(dates[1]));
		}
	}
	
	private static class DateTimeSerializer implements JsonSerializer<DateTime> {
		public JsonElement serialize(DateTime src, Type typeOfSrc,
				JsonSerializationContext context) {
			return new JsonPrimitive(dateTimeFormatter.print(src));
		}
	}
	
	private static class DateTimeDeserializer implements JsonDeserializer<DateTime> {
		public DateTime deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context)
			  throws JsonParseException {
		  return dateTimeFormatter.parseDateTime(json.getAsJsonPrimitive().getAsString());
		}
	}
	
	public static ArrayList<Task> readFromFile (File file) throws IOException {
		if(!file.exists()) {
			return new ArrayList<>();
		}
		FileReader reader = new FileReader(file);
		Task[] tasksArray = jsonFormatter.fromJson(reader, Task[].class);
		reader.close();
		return arrayToArrayList(tasksArray);
	}
	
	public static void writeToFile (ArrayList<Task> tasks, File file) throws IOException {
		if(file.exists()) {
			file.delete();
			file.createNewFile();
		}
		FileWriter writer = new FileWriter(file);
		jsonFormatter.toJson(tasks, writer);
		writer.close();
	}
	
	public static ArrayList<Task> readFromString (String jsonString) {
		Task[] tasksArray = jsonFormatter.fromJson(jsonString, Task[].class);
		if(tasksArray == null) { 
			return new ArrayList<>();
		} else { 
			return arrayToArrayList(tasksArray);
		}
	}
	
	public static String writeToString (ArrayList<Task> tasks) {
		return jsonFormatter.toJson(tasks);
	}
	
	private static <E> ArrayList<E> arrayToArrayList(E[] array) {
		if(array == null || array.length == 0) {
			return new ArrayList<>();
		}
		ArrayList<E> result = new ArrayList<>();
		for(E e : array) {
			result.add(e);
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(dateTimeFormatter.parseDateTime("23/04/13 10:00 PM").toString());
	}

}
