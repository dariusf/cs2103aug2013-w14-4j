package Storage;

import java.lang.reflect.Type;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import Logic.Interval;

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
	
	DateTimeFormatter formatter = new DateTimeFormatterBuilder().
			appendPattern("dd/MM/yy hh:mm a").toFormatter();
	
	private class IntervalSerializer implements JsonSerializer<Interval> {
		public JsonElement serialize(Interval src, Type typeOfSrc,
				JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}
	}
	
	private class IntervalDeserializer implements JsonDeserializer<Interval> {
		public Interval deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			String[] dates = json.getAsString().split(" to ");
			return new Interval(formatter.parseDateTime(dates[0]), formatter.parseDateTime(dates[1]));
		}
	}
	
	private class DateTimeSerializer implements JsonSerializer<DateTime> {
		public JsonElement serialize(DateTime src, Type typeOfSrc,
				JsonSerializationContext context) {
			return new JsonPrimitive(formatter.print(src));
		}
	}
	
	private class DateTimeDeserializer implements JsonDeserializer<DateTime> {
		public DateTime deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context)
			  throws JsonParseException {
		  return new DateTime(json.getAsJsonPrimitive().getAsString());
		}
	}
	
	GsonBuilder gson = new GsonBuilder().setPrettyPrinting().
			registerTypeAdapter(DateTime.class, new DateTimeSerializer()).
			registerTypeAdapter(DateTime.class, new DateTimeDeserializer()).
			registerTypeAdapter(Interval.class, new IntervalSerializer());

}
