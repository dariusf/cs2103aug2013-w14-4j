package test;

import static org.junit.Assert.*;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.text.StyledEditorKit.ForegroundAction;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.junit.Test;

import common.Constants;
import common.undo.DoublyLinkedList;


import logic.Interval;
import logic.Task;
import storage.Json;
import storage.Storage;

public class StorageTest {
	
	private static String dateFormatString = "dd/MM/yy hh:mm a";
	
	static DateTimeFormatter dateTimeParser = new DateTimeFormatterBuilder().
			appendPattern(dateFormatString).toFormatter();	
	
	private static DateTime deadlineTime = dateTimeParser.parseDateTime("23/04/13 10:00 PM");
	private static DateTime startTime = dateTimeParser.parseDateTime("10/01/13 09:00 AM");
	private static DateTime endTime = dateTimeParser.parseDateTime("10/01/13 06:00 PM");
	private static Interval interval1 = new Interval(dateTimeParser.parseDateTime("01/06/13 09:00 AM"),
													dateTimeParser.parseDateTime("01/06/13 09:00 PM"));
	private static Interval interval2 = new Interval(dateTimeParser.parseDateTime("02/06/13 09:00 AM"),
			dateTimeParser.parseDateTime("05/06/13 06:00 PM"));
	private static Interval interval3 = new Interval(dateTimeParser.parseDateTime("04/06/13 09:00 AM"),
			dateTimeParser.parseDateTime("09/06/13 09:00 AM"));
	
	private static ArrayList<String> tagsListBuilder() {
		ArrayList<String> result = new ArrayList<>();
		for(String t : new String[]{"blah1", "blah2", "blah3"}) {
			result.add(t);
		}
		return result;
	}
	
	private static ArrayList<Interval> intervalListBuilder() {
		ArrayList<Interval> result = new ArrayList<>();
		for(Interval i : new Interval[]{interval1, interval2, interval3} ) {
			result.add(i);
		}
		return result;
	}
	
	private static Task deadlineTaskCreator() {
		Task result = new Task();
		result.setName("deadlineTask");
		result.setType(Constants.TASK_TYPE_DEADLINE);
		result.setDeadline(deadlineTime);
		
		return result;
	}
	
	private static Task timedTaskCreator() {
		Task result = new Task();
		result.setName("timedTask");
		result.setType(Constants.TASK_TYPE_TIMED);
		result.setInterval(new Interval(startTime, endTime));
		
		return result;
	}
	
	private static Task untimedTaskCreator() {
		Task result = new Task();
		result.setName("timedTask");
		result.setType(Constants.TASK_TYPE_UNTIMED);
		
		return result;
	}
	
	private static Task taggedTaskCreator() {
		Task result = new Task();
		result.setName("timedTask");
		result.setType(Constants.TASK_TYPE_UNTIMED);
		result.setTags(tags);
		
		return result;
	}
	
	private static Task floatingTaskCreator() {
		Task result = new Task();
		result.setName("floatingTask");
		result.setType(Constants.TASK_TYPE_TENTATIVE);
		result.setPossibleTime(intervals);
		
		return result;
	}
	
	private static boolean areTasksEqual(Task a, Task b) {
		String aString = a.toString();
		String bString = b.toString();
		
		return aString.equals(bString);
	}
	
	private static boolean areTasksEqual(ArrayList<Task> a, ArrayList<Task> b) {
		boolean result = true;
		Iterator<Task> aIterator = a.iterator();
		Iterator<Task> bIterator = b.iterator();
		
		if(aIterator.hasNext() && bIterator.hasNext()) {
			result = result && areTasksEqual(aIterator.next(), bIterator.next());
		}
		
		return result && !(aIterator.hasNext() || bIterator.hasNext());
	}
	
	private static ArrayList<String> tags = tagsListBuilder();
	private static ArrayList<Interval> intervals = intervalListBuilder();
	private static Task deadlineTask = deadlineTaskCreator();
	private static Task timedTask = timedTaskCreator();
	private static Task taggedTask = taggedTaskCreator();
	private static Task untimedTask = untimedTaskCreator();
	private static Task floatingTask = floatingTaskCreator();
	

	@Test
	public void stringOutputTest() throws IOException {
		ArrayList<Task> taskList = new ArrayList<>();
		taskList.add(deadlineTask);
		taskList.add(timedTask);
		System.out.println(Json.writeToString(taskList));
	}
	
	@Test
	public void stringInputTest() throws IOException {
		ArrayList<Task> testList = Json.readFromString("[\r\n  {\r\n    \"name\": \"deadlineTask\",\r\n    \"type\": \"deadline\",\r\n    \"location\": \"home\",\r\n    \"tags\": [],\r\n    \"deadline\": \"23/04/13 10:00 PM\",\r\n    \"possibleIntervals\": [],\r\n    \"done\": false\r\n  },\r\n  {\r\n    \"name\": \"timedTask\",\r\n    \"type\": \"timed\",\r\n    \"location\": \"office\",\r\n    \"tags\": [],\r\n    \"interval\": \"10/01/13 09:00 AM to 10/01/13 06:00 PM\",\r\n    \"possibleIntervals\": [],\r\n    \"done\": false\r\n  }\r\n]\r\n");
		for(Task task : testList) {
			System.out.println(task.toString());
		}
	}
	
	@Test
	public void taggedTaskTest() throws IOException {
		ArrayList<Task> taskList = new ArrayList<>();
		taskList.add(deadlineTask);
		taskList.add(taggedTask);
		System.out.println(Json.writeToString(taskList));
	}
	
	@Test
	public void stringInput_taggedTaskTest() throws IOException {
		ArrayList<Task> testList = Json.readFromString("[\r\n  {\r\n    \"name\": \"deadlineTask\",\r\n    \"type\": \"deadline\",\r\n    \"location\": \"home\",\r\n    \"tags\": [],\r\n    \"deadline\": \"23/04/13 10:00 PM\",\r\n    \"possibleIntervals\": [],\r\n    \"done\": false\r\n  },\r\n  {\r\n    \"name\": \"timedTask\",\r\n    \"type\": \"untimed\",\r\n    \"location\": \"office\",\r\n    \"tags\": [\r\n      \"blah1\",\r\n      \"blah2\",\r\n      \"blah3\"\r\n    ],\r\n    \"possibleIntervals\": [],\r\n    \"done\": false\r\n  }\r\n]\r\n");
		for(Task task : testList) {
			System.out.println(task.toString());
		}
	}
	
	@Test
	public void storageAddTest() throws Exception {
		Storage testStorage = new Storage("newtest.txt");
		testStorage.add(deadlineTask);
		assertTrue(areTasksEqual(deadlineTask, testStorage.get(1)));
//		testStorage.undo();
		assertTrue(testStorage.isEmpty());
		
		testStorage.close();
		File file = new File("newtest.txt");
		Files.delete(file.toPath());
	}
	
	@Test
	public void storageLinkedListTest () {
		DoublyLinkedList<Integer> testList = new DoublyLinkedList<>();
		
		testList.pushHere(1);
		assertTrue(testList.hasNext());
		assertEquals((Integer)1, testList.next());
		testList = new DoublyLinkedList<>();
		
		for (int i = 5; i > 0; i--) {
			testList.push(i);
		}
		Integer counter = 1;
		while (testList.hasNext()) {
			assertEquals(counter++, testList.next());
		}
		
		for (int i = 5; i > 0; i--) {
			testList.pushHere(i);
		}
		counter = 1;
		while (testList.hasNext()) {
			assertEquals(counter++, testList.next());
		}
	}
}
