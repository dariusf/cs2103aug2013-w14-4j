package Storage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import Logic.Interval;
import Logic.Task;

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
public class FileOperations {
	
	static DateTimeFormatter formatter = new DateTimeFormatterBuilder().
			appendPattern("dd/MM/yy hh:mm a").toFormatter();
	
	static Gson JSONformatter = new GsonBuilder().setPrettyPrinting().
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
			return new Interval(formatter.parseDateTime(dates[0]), formatter.parseDateTime(dates[1]));
		}
	}
	
	private static class DateTimeSerializer implements JsonSerializer<DateTime> {
		public JsonElement serialize(DateTime src, Type typeOfSrc,
				JsonSerializationContext context) {
			return new JsonPrimitive(formatter.print(src));
		}
	}
	
	private static class DateTimeDeserializer implements JsonDeserializer<DateTime> {
		public DateTime deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context)
			  throws JsonParseException {
		  return formatter.parseDateTime(json.getAsJsonPrimitive().getAsString());
		}
	}
	
	public static String tasksToJSONString(ArrayList<Task> taskList) {
		return JSONformatter.toJson(taskList);
	}
	
	public static void tasksToJSONDocument(ArrayList<Task> taskList, File file) throws IOException {
		if(file.exists()) { file.delete(); } // for now i'll just delete the existing file
		FileWriter writer = new FileWriter(file);
		JSONformatter.toJson(taskList, writer);
	}
	
	public static ArrayList<Task> JSONFileToList(File file) throws IOException {
		FileReader reader = new FileReader(file);
		Task[] tasksArray = JSONformatter.fromJson(reader, Task[].class);
		return arrayToArrayList(tasksArray);
	}
	
	public static ArrayList<Task> JSONStringToList(String JSONString) {
		Task[] tasksArray = JSONformatter.fromJson(JSONString, Task[].class);
		if(tasksArray == null) { 
			return new ArrayList<>();
		} else { 
			return arrayToArrayList(tasksArray);
		}
	}
	
	private static <E> ArrayList<E> arrayToArrayList(E[] array) {
		if(array.length == 0) { return new ArrayList<>(); }
		ArrayList<E> result = new ArrayList<>();
		for(E e : array) {
			result.add(e);
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(formatter.parseDateTime("23/04/13 10:00 PM").toString());
	}

}
