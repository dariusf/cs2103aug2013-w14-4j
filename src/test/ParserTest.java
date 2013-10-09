
package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.junit.Test;

import common.CommandType;

import logic.Command;
import parser.Interval;
import parser.Parser;

public class ParserTest {

//	public void assertEquals(Object one, Object two) {
//		
//	}
	
	@Test
	public void deleteCommandTests() {
		Command expected, gotten;
		
		// Correct format
		expected = new Command(CommandType.DELETE);
		expected.setValue("deleteIndex", Integer.toString(1));
		gotten = new Parser().parse("delete 1");
		assertEquals(gotten, expected);

		// Missing index
		expected = new Command(CommandType.INVALID);
		// TODO add error info
		gotten = new Parser().parse("delete");
		assertEquals(gotten, expected);

		// Invalid format
		expected = new Command(CommandType.INVALID);
		// TODO add error info
		gotten = new Parser().parse("delete askldjas");
		assertEquals(gotten, expected);
	}

	@Test
	public void addCommandTests() {

		Interval.setNowStub(new DateTime(2013, 10, 5, 20, 0));
		
		Command expected, gotten;
		ArrayList<Interval> intervals;
		DateTime start, end;
		
		// Correct format
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = new DateTime(2013, 10, 5, 22, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home at 10:00 pm");
		assertEquals(gotten, expected);

		// Invalid 12-hour format
		// Loose spaces
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home pm");
		intervals = new ArrayList<>();
		start = new DateTime(2013, 10, 6, 13, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("  go  home   at  13:00 pm  ");
		assertEquals(gotten, expected);
		
		// Quote
		// TODO remove quotes?
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("'add  go home at 10:00 pm'");
		gotten = new Parser().parse("'add  go home at 10:00 pm'");
		assertEquals(gotten, expected);
		
		// Typo in pm
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home p");
		intervals = new ArrayList<>();
		start = new DateTime(2013, 10, 6, 13, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home at 13:00 p");
		assertEquals(gotten, expected);

		// Symbols, day alias
		// TODO: symbols are dropped for now
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home on tuesday yeah");
		intervals = new ArrayList<>();
		start = new DateTime(2013, 10, 6, 13, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home at 13:00 on tuesday yeah!");
		assertEquals(gotten, expected);

		// Proper date format
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = new DateTime(2015, 2, 12, 0, 0);
		end = new DateTime(2015, 2, 12, 23, 59);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home on 12/2/15");
		assertEquals(gotten, expected);

		// No year
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = new DateTime(2013, 10, 12, 12, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home at 12:00 on 12/10");
		assertEquals(gotten, expected);
		
		// Date and time
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = new DateTime(2015, 2, 12, 12, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home at 12:00 on 12/2/15");
		assertEquals(gotten, expected);
		
		// Fake on and at keywords
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home at on at");
		intervals = new ArrayList<>();
		start = new DateTime(2013, 10, 6, 13, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home at on at at 13:00");
		assertEquals(gotten, expected);
	}
}
