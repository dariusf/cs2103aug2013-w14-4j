package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.junit.Test;

import common.CommandType;


import logic.Command;
import logic.Interval;
import logic.Logic;

public class LogicTest {

	@Test
	public void test() throws IOException {
		Logic logic = new Logic();
		Command displayCommand = new Command(CommandType.DISPLAY);
		// Display task test
		assertEquals("You have no tasks :)", logic.displayTasks(displayCommand).toString());

		// Add task test 1 (deadline task, success not overdue)
		Command command1 = new Command(CommandType.ADD);
		DateTime date1 = new DateTime(2013, 10, 14, 23, 59, 59);
		command1.setDeadline(date1);
		command1.setDescription("Submit V0.1");
		assertEquals(
				"Task added successfully! \nSubmit V0.1 before 11:59 PM on Mon, 14/10/2013",
				logic.addTask(command1).toString());

		// Add task test 2 (deadline task, success but overdue)
		Command command2 = new Command(CommandType.ADD);
		DateTime date2 = new DateTime(2012, 10, 14, 23, 59, 59);
		command2.setDeadline(date2);
		command2.setDescription("Submit overdue V0.1");
		assertEquals(
				"Task added successfully! Task is overdue \nSubmit overdue V0.1 before 11:59 PM on Sun, 14/10/2012",
				logic.addTask(command2).toString());

		// Add task test 3 (deadline task, success not overdue)
		Command command3 = new Command(CommandType.ADD);
		DateTime startDate3 = new DateTime(2013, 9, 30, 15, 0, 0);
		DateTime endDate3 = new DateTime(2013, 9, 30, 16, 0, 0);
		Interval interval3 = new Interval();
		interval3.setStartDateTime(startDate3);
		interval3.setEndDateTime(endDate3);
		ArrayList<Interval> intervalList3 = new ArrayList<Interval>();
		intervalList3.add(interval3);
		command3.setIntervals(intervalList3);
		command3.setDescription("CS2105 Test");
		assertEquals(
				"Task added successfully! \nCS2105 Test from 3:00 PM on Mon, 30/9/2013 to 4:00 PM on Mon, 30/9/2013",
				logic.addTask(command3).toString());
		
		// Add task test 4 (deadline task, success but overdue)
		Command command4 = new Command(CommandType.ADD);
		DateTime startDate4 = new DateTime(2012, 9, 30, 15, 0, 0);
		DateTime endDate4 = new DateTime(2012, 9, 30, 16, 0, 0);
		Interval interval4 = new Interval();
		interval4.setStartDateTime(startDate4);
		interval4.setEndDateTime(endDate4);
		ArrayList<Interval> intervalList4 = new ArrayList<Interval>();
		intervalList4.add(interval4);
		command4.setIntervals(intervalList4);
		command4.setDescription("CS2105 Test 2012");
		assertEquals(
				"Task added successfully! Task is overdue \nCS2105 Test 2012 from 3:00 PM on Sun, 30/9/2012 to 4:00 PM on Sun, 30/9/2012",
				logic.addTask(command4).toString());
		
		// Add task test 5 (floating task, success not overdue)
		Command command5 = new Command(CommandType.ADD);
		DateTime startDate5a = new DateTime(2013, 10, 30, 15, 0, 0);
		DateTime endDate5a = new DateTime(2013, 10, 30, 16, 0, 0);
		Interval interval5a = new Interval();
		interval5a.setStartDateTime(startDate5a);
		interval5a.setEndDateTime(endDate5a);
		DateTime startDate5b = new DateTime(2013, 10, 30, 16, 0, 0);
		DateTime endDate5b = new DateTime(2013, 10, 30, 17, 0, 0);
		Interval interval5b = new Interval();
		interval5b.setStartDateTime(startDate5b);
		interval5b.setEndDateTime(endDate5b);
		DateTime startDate5c = new DateTime(2013, 10, 30, 17, 0, 0);
		DateTime endDate5c = new DateTime(2013, 10, 30, 18, 0, 0);
		Interval interval5c = new Interval();
		interval5c.setStartDateTime(startDate5c);
		interval5c.setEndDateTime(endDate5c);
		ArrayList<Interval> intervalList5 = new ArrayList<Interval>();
		intervalList5.add(interval5a);
		intervalList5.add(interval5b);
		intervalList5.add(interval5c);
		command5.setIntervals(intervalList5);
		command5.setDescription("A floating event!");
		assertEquals(
				"Task added successfully! \nA floating event! on (1) 3:00 PM on Wed, 30/10/2013 to 4:00 PM on Wed, 30/10/2013 or (2) 4:00 PM on Wed, 30/10/2013 to 5:00 PM on Wed, 30/10/2013 or (3) 5:00 PM on Wed, 30/10/2013 to 6:00 PM on Wed, 30/10/2013",
				logic.addTask(command5).toString());
		
		// Add task test 5 (floating task, success but overdue)
		Command command6 = new Command(CommandType.ADD);
		DateTime startDate6a = new DateTime(2013, 10, 30, 15, 0, 0);
		DateTime endDate6a = new DateTime(2013, 10, 30, 16, 0, 0);
		Interval interval6a = new Interval();
		interval6a.setStartDateTime(startDate6a);
		interval6a.setEndDateTime(endDate6a);
		DateTime startDate6b = new DateTime(2013, 10, 30, 16, 0, 0);
		DateTime endDate6b = new DateTime(2013, 10, 30, 17, 0, 0);
		Interval interval6b = new Interval();
		interval6b.setStartDateTime(startDate6b);
		interval6b.setEndDateTime(endDate6b);
		DateTime startDate6c = new DateTime(2013, 10, 30, 17, 0, 0);
		DateTime endDate6c = new DateTime(2013, 10, 30, 18, 0, 0);
		Interval interval6c = new Interval();
		interval6c.setStartDateTime(startDate6c);
		interval6c.setEndDateTime(endDate6c);
		ArrayList<Interval> intervalList6 = new ArrayList<Interval>();
		intervalList6.add(interval6a);
		intervalList6.add(interval6b);
		intervalList6.add(interval6c);
		command6.setIntervals(intervalList6);
		command6.setDescription("An overdue floating event!");
		assertEquals(
				"Task added successfully! \nAn overdue floating event! on (1) 3:00 PM on Wed, 30/10/2013 to 4:00 PM on Wed, 30/10/2013 or (2) 4:00 PM on Wed, 30/10/2013 to 5:00 PM on Wed, 30/10/2013 or (3) 5:00 PM on Wed, 30/10/2013 to 6:00 PM on Wed, 30/10/2013", logic.addTask(command6).toString());
		
		// Add task test 7 (task with tags)
		Command command7 = new Command(CommandType.ADD);
		DateTime date7 = new DateTime(2013, 10, 11, 22, 00, 00);
		command7.setDeadline(date7);
		command7.setDescription("Party");
		ArrayList<String> tags7 = new ArrayList<String>();
		tags7.add("TGIF");
		tags7.add("forfun");
		command7.setTags(tags7);
		assertEquals(
				"Task added successfully! \nParty before 10:00 PM on Fri, 11/10/2013 #TGIF #forfun",
				logic.addTask(command7).toString());
	}
}
