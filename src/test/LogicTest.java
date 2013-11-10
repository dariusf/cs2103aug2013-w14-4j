/**
 * To conduct this test, set the system clock to 11/11/2013. We compare the tasks which are returned by command logic to be displayed by UI with the expected tasks. To do so, we convert the tasks to JSON and compare the strings.
 */

package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Type;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import common.Constants;
import common.Interval;

import logic.CommandLogic;

/**
 * @author macbook
 * 
 */
public class LogicTest {
	static String formatString(String input) {
		return "\"" + input.replace("\n", "\\n").replace("\"", "\\\"") + "\""
				+ ";";
	}

	@Test
	public void test() throws IOException {

		final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
				.appendPattern(Constants.DATE_TIME_FORMAT).toFormatter();

		class IntervalSerializer implements JsonSerializer<Interval> {
			public JsonElement serialize(Interval src, Type typeOfSrc,
					JsonSerializationContext context) {
				return new JsonPrimitive(src.toString());
			}
		}

		class IntervalDeserializer implements JsonDeserializer<Interval> {
			public Interval deserialize(JsonElement json, Type typeOfT,
					JsonDeserializationContext context)
					throws JsonParseException {
				String[] dates = json.getAsString().split(" to ");
				return new Interval(dateTimeFormatter.parseDateTime(dates[0]),
						dateTimeFormatter.parseDateTime(dates[1]));
			}
		}

		class DateTimeSerializer implements JsonSerializer<DateTime> {
			public JsonElement serialize(DateTime src, Type typeOfSrc,
					JsonSerializationContext context) {
				return new JsonPrimitive(dateTimeFormatter.print(src));
			}
		}

		class DateTimeDeserializer implements JsonDeserializer<DateTime> {
			public DateTime deserialize(JsonElement json, Type typeOfT,
					JsonDeserializationContext context)
					throws JsonParseException {
				return dateTimeFormatter.parseDateTime(json
						.getAsJsonPrimitive().getAsString());
			}
		}

		CommandLogic logic = new CommandLogic();
		Gson jsonFormatter = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(DateTime.class, new DateTimeSerializer())
				.registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
				.registerTypeAdapter(Interval.class, new IntervalSerializer())
				.registerTypeAdapter(Interval.class, new IntervalDeserializer())
				.create();

		String expected = "[]";
		logic.executeCommand("clear");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Untimed task
		logic.executeCommand("add new task");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task\",\n    \"type\": \"UNTIMED\",\n    \"tags\": [],\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Overdue timed task
		logic.executeCommand("add new task yesterday");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task\",\n    \"type\": \"UNTIMED\",\n    \"tags\": [],\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Timed task
		logic.executeCommand("add new task 1pm to 2pm tomorrow");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task\",\n    \"type\": \"UNTIMED\",\n    \"tags\": [],\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Timed task
		logic.executeCommand("add new task 1pm to 2pm 11/11");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task\",\n    \"type\": \"UNTIMED\",\n    \"tags\": [],\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Tentative task
		logic.executeCommand("add new task 1pm to 2pm tmr or 3pm to 4pm tmr");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task\",\n    \"type\": \"UNTIMED\",\n    \"tags\": [],\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Tentative task
		logic.executeCommand("add new task 1pm to 2pm 11/11 or 3pm to 4pm 11/11");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task\",\n    \"type\": \"UNTIMED\",\n    \"tags\": [],\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Display today
		logic.executeCommand("display today");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Display tomorrow
		logic.executeCommand("display tmr");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Display overdue
		logic.executeCommand("display overdue");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Display timed
		logic.executeCommand("display timed");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Display untimed
		logic.executeCommand("display untimed");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task\",\n    \"type\": \"UNTIMED\",\n    \"tags\": [],\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Display tentative
		logic.executeCommand("display tentative");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Display deadline
		logic.executeCommand("add new task by tmr");
		logic.executeCommand("display deadline");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"12/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Display date
		logic.executeCommand("add new task by 11/11");
		logic.executeCommand("display 11/11");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Display all
		logic.executeCommand("display all");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task\",\n    \"type\": \"UNTIMED\",\n    \"tags\": [],\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"12/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Edit task name
		logic.executeCommand("edit 1 new task name");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task name\",\n    \"type\": \"UNTIMED\",\n    \"tags\": [],\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"12/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Edit task name wrong index (negative number)
		logic.executeCommand("edit -1 new task name");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task name\",\n    \"type\": \"UNTIMED\",\n    \"tags\": [],\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"12/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Edit task name wrong index (large number)
		logic.executeCommand("edit 10 new task name");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task name\",\n    \"type\": \"UNTIMED\",\n    \"tags\": [],\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"12/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Edit task time (to deadline)
		logic.executeCommand("edit 1 by 10pm");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task name\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 10:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"12/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Edit task time (to timed)
		logic.executeCommand("edit 1 from 10pm to 11pm");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task name\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 10:00 PM to 11/11/13 11:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"12/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Edit task time (to tentative)
		logic.executeCommand("edit 1 from 10pm to 11pm or 8pm to 9pm");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task name\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 10:00 PM to 11/11/13 11:00 PM\",\n      \"11/11/13 8:00 PM to 11/11/13 9:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"12/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Edit task time slot
		logic.executeCommand("edit 1 1 from 6pm to 7pm");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task name\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 6:00 PM to 11/11/13 7:00 PM\",\n      \"11/11/13 8:00 PM to 11/11/13 9:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"12/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Edit task time slot (negative number)
		logic.executeCommand("edit 1 -1 from 6pm to 7pm");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task name\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 6:00 PM to 11/11/13 7:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"12/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Edit task time slot (large number)
		logic.executeCommand("edit 1 3 from 6pm to 7pm");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task name\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 6:00 PM to 11/11/13 7:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"12/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Add task with tags
		logic.executeCommand("add a new task #tag");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task name\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 6:00 PM to 11/11/13 7:00 PM\",\n    \"possibleIntervals\": [\n      \"11/11/13 6:00 PM to 11/11/13 7:00 PM\",\n      \"11/11/13 8:00 PM to 11/11/13 9:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"12/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"a new task\",\n    \"type\": \"UNTIMED\",\n    \"tags\": [\n      \"#tag\"\n    ],\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Edit add new tag
		logic.executeCommand("edit 9 #newtag");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task name\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 6:00 PM to 11/11/13 7:00 PM\",\n    \"possibleIntervals\": [\n      \"11/11/13 6:00 PM to 11/11/13 7:00 PM\",\n      \"11/11/13 8:00 PM to 11/11/13 9:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"12/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"a new task\",\n    \"type\": \"UNTIMED\",\n    \"tags\": [\n      \"#tag\",\n      \"#newtag\"\n    ],\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Edit add multiple tags
		logic.executeCommand("edit 1 #newtag1 #newtag2");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task name\",\n    \"type\": \"TIMED\",\n    \"tags\": [\n      \"#newtag1\",\n      \"#newtag2\"\n    ],\n    \"interval\": \"11/11/13 6:00 PM to 11/11/13 7:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"12/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"a new task\",\n    \"type\": \"UNTIMED\",\n    \"tags\": [\n      \"#tag\",\n      \"#newtag\"\n    ],\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));

		// Edit multiple attributes
		logic.executeCommand("edit 1 another new name by 10pm");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"another new name\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [\n      \"#newtag1\",\n      \"#newtag2\"\n    ],\n    \"interval\": \"11/11/13 6:00 PM to 11/11/13 7:00 PM\",\n    \"deadline\": \"11/11/13 10:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"12/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"a new task\",\n    \"type\": \"UNTIMED\",\n    \"tags\": [\n      \"#tag\",\n      \"#newtag\"\n    ],\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));
		
		// Edit in different display mode
		logic.executeCommand("display today");
		logic.executeCommand("edit 1 yet another new name by 10pm");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"yet another new name\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [\n      \"#newtag1\",\n      \"#newtag2\"\n    ],\n    \"deadline\": \"11/11/13 10:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));
		
		// Delete
		logic.executeCommand("display all");
		logic.executeCommand("delete 1");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"12/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"a new task\",\n    \"type\": \"UNTIMED\",\n    \"tags\": [\n      \"#tag\",\n      \"#newtag\"\n    ],\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));
		
		// Delete (small number)
		logic.executeCommand("delete 0");
		System.out.println(formatString(jsonFormatter.toJson(logic
				.getTasksToDisplay())));
		expected = "[\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"10/11/13 12:00 AM to 10/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TIMED\",\n    \"tags\": [],\n    \"interval\": \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"12/11/13 1:00 PM to 12/11/13 2:00 PM\",\n      \"12/11/13 3:00 PM to 12/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"TENTATIVE\",\n    \"tags\": [],\n    \"possibleIntervals\": [\n      \"11/11/13 1:00 PM to 11/11/13 2:00 PM\",\n      \"11/11/13 3:00 PM to 11/11/13 4:00 PM\"\n    ],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"12/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"new task\",\n    \"type\": \"DEADLINE\",\n    \"tags\": [],\n    \"deadline\": \"11/11/13 11:59 PM\",\n    \"possibleIntervals\": [],\n    \"done\": false\n  },\n  {\n    \"name\": \"a new task\",\n    \"type\": \"UNTIMED\",\n    \"tags\": [\n      \"#tag\",\n      \"#newtag\"\n    ],\n    \"possibleIntervals\": [],\n    \"done\": false\n  }\n]";
		assertEquals(expected, jsonFormatter.toJson(logic.getTasksToDisplay()));
	}
}
