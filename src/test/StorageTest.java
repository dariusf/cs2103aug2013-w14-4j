package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.junit.Test;

import common.Constants;
import common.Interval;
import common.Task;
import common.TaskType;
import common.undo.ActionStack;

import storage.ActionCapturer;
import storage.Cloner;
import storage.RealStorage;
import storage.Storage;

//@author A0097556M
public class StorageTest {
	
	private static String dateFormatString = Constants.DATE_TIME_FORMAT;
	
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
		result.setType(TaskType.DEADLINE);
		result.setDeadline(deadlineTime);
		
		return result;
	}
	
	private static Task timedTaskCreator() {
		Task result = new Task();
		result.setName("timedTask");
		result.setType(TaskType.TIMED);
		result.setInterval(new Interval(startTime, endTime));
		
		return result;
	}
	
	private static Task untimedTaskCreator() {
		Task result = new Task();
		result.setName("untimedTask");
		result.setType(TaskType.UNTIMED);
		
		return result;
	}
	
	private static Task taggedTaskCreator() {
		Task result = new Task();
		result.setName("taggedTask");
		result.setType(TaskType.UNTIMED);
		result.setTags(tags);
		
		return result;
	}
	
	private static Task floatingTaskCreator() {
		Task result = new Task();
		result.setName("floatingTask");
		result.setType(TaskType.TENTATIVE);
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
	
	//====================
	// RealStorage Tests
	//====================
	
	@Test
	public void test_RealStorage () {
		RealStorage<Integer> testStorage = new RealStorage<>();
		assertEquals(0, testStorage.size());
		ArrayList<Integer> testList = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			testList.add(i);
		}
		testStorage.setState(testList);
		Iterator<Integer> storageIterator = testStorage.iterator();
		Iterator<Integer> listIterator = testList.iterator();
		while(storageIterator.hasNext() && listIterator.hasNext()) {
			assertEquals(listIterator.next(), storageIterator.next());
		}
		testStorage.remove(0);
		assertEquals((Integer) 1, testStorage.get(0));
		testStorage.insert(0, 0);
		storageIterator = testStorage.iterator();
		listIterator = testList.iterator();
		while(storageIterator.hasNext() && listIterator.hasNext()) {
			assertEquals(listIterator.next(), storageIterator.next());
		}
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void pastArrayBoundsTest_emptyList_RealStorage () {
		RealStorage<Integer> testStorage = new RealStorage<>();
		testStorage.get(0);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void pastArrayBoundsTest_nonEmptyList_RealStorage () {
		RealStorage<Integer> testStorage = new RealStorage<>();
		testStorage.insert(0, 7);
		testStorage.get(2);
	}
	
	@Test
	public void nullInsertionTest_RealStorage () {
		RealStorage<Integer> testStorage = new RealStorage<>();
		testStorage.insert(0, null);
		assertEquals(null, testStorage.get(0));
	}
	
	@Test
	public void nullSetStateTest_RealStorage () {
		RealStorage<Integer> testStorage = new RealStorage<>();
		testStorage.setState(null);
		assertEquals(0, testStorage.size());
	}
	
	//========================
	// RealStorage Tests Done
	//========================
	
	//======================
	// ActionCapturer Tests
	//======================
	
	class IntegerCloner implements Cloner<Integer> {

		@Override
		public Integer clone(Integer originalItem) {
			return (Integer) originalItem.intValue();
		}
		
	}
	
	class StorageCloner implements Cloner<RealStorage<Integer>> {

		@Override
		public RealStorage<Integer> clone(RealStorage<Integer> originalItem) {
			return new RealStorage<>(originalItem);
		}
		
	}
	
	@Test
	public void test_ActionCapturer () throws Exception {
		ActionStack.resetActionStack();
		ActionStack testStack = ActionStack.getInstance();
		testStack.flushCurrentActionSet();
		ActionCapturer<Integer, RealStorage<Integer>> testStorage =
				new ActionCapturer<Integer, RealStorage<Integer>>(new RealStorage<Integer>(), new IntegerCloner(), new StorageCloner());
		assertEquals(0, testStorage.size());
		
		ArrayList<Integer> testList = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			testList.add(i);
		}
		testStorage.setState(testList);
		testStack.finaliseActions();
		
		Iterator<Integer> storageIterator = testStorage.iterator();
		Iterator<Integer> listIterator = testList.iterator();
		while(storageIterator.hasNext() && listIterator.hasNext()) {
			assertEquals(listIterator.next(), storageIterator.next());
		}
		testStack.undo();
		assertEquals(0, testStorage.size());
		testStack.redo();
		storageIterator = testStorage.iterator();
		listIterator = testList.iterator();
		while(storageIterator.hasNext() && listIterator.hasNext()) {
			assertEquals(listIterator.next(), storageIterator.next());
		}
		
		testStorage.remove(0);
		testStack.finaliseActions();
		assertEquals((Integer) 1, testStorage.get(0));
		testStorage.insert(0, 0);
		testStack.finaliseActions();
		storageIterator = testStorage.iterator();
		listIterator = testList.iterator();
		while(storageIterator.hasNext() && listIterator.hasNext()) {
			assertEquals(listIterator.next(), storageIterator.next());
		}
		testStack.undo();
		assertEquals((Integer) 1, testStorage.get(0));
		testStack.undo();
		storageIterator = testStorage.iterator();
		listIterator = testList.iterator();
		while(storageIterator.hasNext() && listIterator.hasNext()) {
			assertEquals(listIterator.next(), storageIterator.next());
		}
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void pastArrayBoundsTest_emptyList_ActionCapturer () {
		ActionCapturer<Integer, RealStorage<Integer>> testStorage =
				new ActionCapturer<Integer, RealStorage<Integer>>(new RealStorage<Integer>(), new IntegerCloner(), new StorageCloner());
		testStorage.get(0);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void pastArrayBoundsTest_nonEmptyList_ActionCapturer () {
		ActionCapturer<Integer, RealStorage<Integer>> testStorage =
				new ActionCapturer<Integer, RealStorage<Integer>>(new RealStorage<Integer>(), new IntegerCloner(), new StorageCloner());
		testStorage.insert(0, 7);
		testStorage.get(2);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void nullInsertionTest_ActionCapturer () {
		ActionCapturer<Integer, RealStorage<Integer>> testStorage =
				new ActionCapturer<Integer, RealStorage<Integer>>(new RealStorage<Integer>(), new IntegerCloner(), new StorageCloner());
		testStorage.insert(0, null);
	}
	
	//==========================
	// ActionCapturer Tests Done
	//==========================
	
	//===============
	// Storage Tests
	//===============
	
	@Test
	public void test_Storage() throws Exception {
		ActionStack.resetActionStack();
		ActionStack testStack = ActionStack.getInstance();
		testStack.flushCurrentActionSet();
		
		Storage testStorage = new Storage("newtest.txt");
		
		testStorage.add(deadlineTask);
		testStack.finaliseActions();
		assertTrue(areTasksEqual(deadlineTask, testStorage.get(1)));
		
		testStorage.remove(1);
		testStack.finaliseActions();
		assertTrue(testStorage.isEmpty());
		testStack.undo();
		assertTrue(areTasksEqual(deadlineTask, testStorage.get(1)));
		
		testStorage.add(timedTask);
		testStorage.add(taggedTask);
		testStack.finaliseActions();
		assertTrue(areTasksEqual(timedTask, testStorage.get(2)));
		
		testStack.undo();
		assertEquals(1, testStorage.size());
		
		testStack.redo();
		assertEquals(3, testStorage.size());
		
		testStorage.clear();
		testStack.finaliseActions();
		assertTrue(testStorage.isEmpty());
		
		testStorage.close();
		File file = new File("newtest.txt");
		Files.delete(file.toPath());
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void pastArrayBoundsTest_emptyList_Storage () throws IOException {
		Storage testStorage = new Storage("newtest.txt");
		testStorage.get(0);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void pastArrayBoundsTest_nonEmptyList_Storage () throws IOException {
		Storage testStorage = new Storage("newtest.txt");
		testStorage.add(deadlineTask);
		testStorage.get(2);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void nullInsertionTest_Storage () throws IOException {
		Storage testStorage = new Storage("newtest.txt");
		testStorage.add(null);
	}
	
	//==========================
	// Storage Tests Done
	//==========================

}
