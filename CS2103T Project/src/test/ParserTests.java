
package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.junit.Test;
import logic.Command;
import logic.CommandType;
import parser.Interval;
import parser.Parser;

public class ParserTests {

//	public void assertEquals(Object one, Object two) {
//		
//	}
	
	@Test
	public void addCommandTests() {

		DateTime now = new DateTime();

		// Correct format
		Command command = new Command(CommandType.ADD_TASK);
		command.setDescription("go home");
		ArrayList<Interval> intervals = new ArrayList<>();
		DateTime start = new DateTime().withTime(22, 0, 0, 0);
		if (now.isAfter(start)) {
			start = start.plusDays(1);
		}
		DateTime end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		command.setIntervals(intervals);
		Command parsed = new Parser().parse("go home at 10:00pm");
		assertEquals(parsed, command);

		// Invalid 12-hour format
		command = new Command(CommandType.ADD_TASK);
		command.setDescription("go home pm");
		intervals = new ArrayList<>();
		start = new DateTime().withTime(13, 0, 0, 0);
		if (now.isAfter(start)) {
			start = start.plusDays(1);
		}
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		command.setIntervals(intervals);
		parsed = new Parser().parse("go home at 13:00 pm");
		assertEquals(parsed, command);
		
//		// Quote
//		command = new Command(CommandType.ADD_TASK);
//		command.setDescription("go home pm");
//		intervals = new ArrayList<>();
//		intervals.add(new Interval(new DateTime().withTime(13, 0, 0, 0), new DateTime().withTime(14, 0, 0, 0)));
//		command.setIntervals(intervals);
//		parsed = new Parser().parse("go home at 13:00 pm");
//		assertEquals(parsed, command);
//		
//		command = new Command(CommandType.ADD_TASK);
//		command.setValue(Constants.TASK_ATT_NAME, "'go home at 10:00 pm'");
//		assertEquals(new Parser().parse("add 'go home at 10:00 pm'"), command);
//
//		// Typo in pm
//		command = new Command(CommandType.ADD_TASK);
//		command.setValue(Constants.TASK_ATT_NAME, "go home p");
//		command.setValue(Constants.TASK_ATT_STARTTIME, "1:00 pm");
//		assertEquals(new Parser().parse("add go home at 13:00 p"), command);
//
//		// Symbols, day alias
//		command = new Command(CommandType.ADD_TASK);
//		command.setValue(Constants.TASK_ATT_NAME, "go home on tuesday yeah");
//		command.setValue(Constants.TASK_ATT_STARTTIME, "1:00 pm");
//		assertEquals(new Parser().parse("add go home at 13:00 on tuesday yeah!"), command);
//
//		// Proper date format
//		command = new Command(CommandType.ADD_TASK);
//		command.setValue(Constants.TASK_ATT_NAME, "go home");
//		command.setValue(Constants.TASK_ATT_DEADLINE, "1/1/13");
//		assertEquals(new Parser().parse("add go home on 1/1/13"), command);
//
//		// No year
//		command = new Command(CommandType.ADD_TASK);
//		command.setValue(Constants.TASK_ATT_NAME, "go home");
//		command.setValue(Constants.TASK_ATT_DEADLINE, "1/1/13");
//		assertEquals(new Parser().parse("add go home on 1/1"), command);
//
//		// Date and time
//		command = new Command(CommandType.ADD_TASK);
//		command.setValue(Constants.TASK_ATT_NAME, "go home");
//		command.setValue(Constants.TASK_ATT_STARTTIME, "10:00 pm");
//		command.setValue(Constants.TASK_ATT_DEADLINE, "1/2/13");
//		assertEquals(new Parser().parse("add go home at 10:00 pm on 1/2/13"), command);
//
//		// Fake on and at keywords
//		command = new Command(CommandType.ADD_TASK);
//		command.setValue(Constants.TASK_ATT_NAME, "go home at kitchen on top");
//		command.setValue(Constants.TASK_ATT_DEADLINE, "1/2/13");
//		command.setValue(Constants.TASK_ATT_STARTTIME, "10:00 pm");
//		assertEquals(new Parser().parse("add go home at kitchen on top at 10:00 pm on 1/2/13"), command);
//
//		command = new Command(CommandType.ADD_TASK);
//		command.setValue(Constants.TASK_ATT_NAME, "go home at at at at");
//		command.setValue(Constants.TASK_ATT_STARTTIME, "10:00 pm");
//		assertEquals(new Parser().parse("add go home at at at at at 10:00 pm"), command);
	}

	@Test
	public void addCommandFromTests() {
//		Command command = new Command(CommandType.ADD_TASK);
//		command.setValue(Constants.TASK_ATT_NAME, "go home");
//		command.setValue(Constants.TASK_ATT_STARTTIME, "10:00 pm");
//		assertEquals(new Parser().parse("go home at 10:00 pm"), command);
	}
}
