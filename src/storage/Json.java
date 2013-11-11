// @author: A0097556M

package storage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import common.Constants;
import common.Interval;
import common.Task;

/**
 * Contains two static methods, one for creating a JSON document out of a list of objects
 * and another for creating a list of objects out of a JSON document.
 * @author Matthew
 *
 */
public class Json {
	
	private static DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().
			appendPattern(Constants.DATE_TIME_FORMAT).toFormatter();
	
	private static Gson jsonFormatter = new GsonBuilder().setPrettyPrinting().
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
			return new Interval(dateTimeFormatter.parseDateTime(dates[0]),
					dateTimeFormatter.parseDateTime(dates[1]));
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
	
	private static ArrayList<Task> fromReader(Reader reader) throws IOException {
		try {
			Task[] tasksArray = jsonFormatter.fromJson(reader, Task[].class);
			ArrayList<Task> tasks = arrayToArrayList(tasksArray);
			reader.close();
			return tasks;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	public static ArrayList<Task> readFromFile (File file) throws IOException {
		if(!file.exists()) {
			return new ArrayList<>();
		}
		return fromReader(new FileReader(file));
	}
	
	public static ArrayList<Task> readFromString (String jsonString) throws IOException {
		if(jsonString == "") {
			return new ArrayList<>(); // uses the ArrayList implementation
		}
		return fromReader(new StringReader(jsonString));
	}

	private static void toWriter(ArrayList<Task> tasks, Writer writer) throws IOException {
		try {
			jsonFormatter.toJson(tasks, writer);
			writer.close();
		} catch (JsonIOException e) {
			throw new IOException(e);
		}
	}
	
	public static void writeToFile (ArrayList<Task> tasks, File file) throws IOException {
		if(file.exists()) {
			file.delete();
			file.createNewFile();
		}
		toWriter(tasks, new FileWriter(file));
	}
	
	public static String writeToString (ArrayList<Task> tasks) throws IOException {
		StringWriter writer = new StringWriter();
		toWriter(tasks, writer);
		return writer.toString();
	}
	
	public static void writeToFile (Iterator<Task> tasksIterator, File file) throws IOException {
		writeToFile(iteratorToList(tasksIterator), file);
	}
	
	public static void writeToString (Iterator<Task> tasksIterator) throws IOException {
		writeToString(iteratorToList(tasksIterator));
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
	
	private static <E> ArrayList<E> iteratorToList (Iterator<E> iter) {
		ArrayList<E> result = new ArrayList<>();
		while (iter.hasNext()) {
			result.add(iter.next());
		}
		return result;
	}
}
