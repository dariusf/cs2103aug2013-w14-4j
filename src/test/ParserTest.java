
package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.Test;

import common.ClearMode;
import common.Command;
import common.CommandType;
import common.DisplayMode;
import common.Interval;
import common.InvalidCommandReason;

import parser.DateToken;
import parser.Parser;
import parser.StateDeadline;

//@author A0097282W
public class ParserTest {

//	public void assertEquals(Object one, Object two) {}
	
	@Test
	public void generalTests() {
		Command expected, actual;

		// Invalid commands
		// Empty string
		expected = new Command(CommandType.INVALID);
		expected.setInvalidCommandReason(InvalidCommandReason.EMPTY_COMMAND);
		actual = Parser.parse("");
		assertEquals(actual, expected);

		// Null string
		actual = Parser.parse(null);
		assertEquals(actual, expected);

		// Plain invalid commands
		// Gibberish
		actual = Parser.parse("kasdkajsklad aklsjdkals kajsld klajsd");
		expected.setInvalidCommandReason(InvalidCommandReason.UNRECOGNIZED_COMMAND);
		assertEquals(actual, expected);
		actual = Parser.parse("!@#$%^&*({}][]\\|';.,><;");
		expected.setInvalidCommandReason(InvalidCommandReason.UNRECOGNIZED_COMMAND);
		assertEquals(actual, expected);
	}
	
	@Test
	public void addCommandTests() {

		DateTime now = new DateTime(2013, 10, 5, 20, 0); // saturday 8pm 5/10/13
		Interval.setNowStub(now);
		DateToken.setNowStub(now);
		StateDeadline.setNowStub(now);
		
		Command expected, actual;
		ArrayList<Interval> intervals;
		DateTime start, end;

		// Adding a bunch of symbols
		actual = Parser.parse("add !@#$%^&*({}][]\\|';.,><;");
		expected = new Command(CommandType.ADD);
		expected.setDescription("'");
		assertEquals(actual, expected);

		// Correct format
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withTime(22, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 10:00 pm");
		assertEquals(actual, expected);

		// No qualifier
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withTime(22, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home 10:00 pm");
		assertEquals(actual, expected);
		
		// 24h format
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withTime(22, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home 22:00");
		assertEquals(actual, expected);

		// 24h format without colon
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withTime(22, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 2200");
		assertEquals(actual, expected);

		// 12h shorthand 
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withTime(22, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home 10pm");
		assertEquals(actual, expected);
		
		// Invalid 12-hour format
		// Loose spaces
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home pm");
		intervals = new ArrayList<>();
		start = now.plusDays(1).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("  add  go  home   at  13:00 pm  ");
		assertEquals(actual, expected);
		
		// Time aliases
		// morning
		expected = new Command(CommandType.ADD);
		expected.setDescription("go running");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.SUNDAY).withTime(8, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go running on sunday morning");
		assertEquals(actual, expected);
		
		// evening
		expected = new Command(CommandType.ADD);
		expected.setDescription("buy dinner in the");
		intervals = new ArrayList<>();
		start = now.plusDays(1).withTime(19, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add buy dinner in the evening");
		assertEquals(actual, expected);
		
		// afternoon
		expected = new Command(CommandType.ADD);
		expected.setDescription("take nap");
		intervals = new ArrayList<>();
		start = now.plusDays(1).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add take afternoon nap");
		assertEquals(actual, expected);
		
		// midnight
		expected = new Command(CommandType.ADD);
		expected.setDescription("submit assignment");
		intervals = new ArrayList<>();
		start = now.plusDays(1).withTime(0, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add submit assignment at midnight tomorrow");
		assertEquals(actual, expected);
		
		// noon
		expected = new Command(CommandType.ADD);
		expected.setDescription("go to school");
		expected.setDeadline(now.withTime(12, 0, 0, 0));
		actual = Parser.parse("add go to school by noon");
		assertEquals(actual, expected);
		
		// Quotes
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home at 10:00 pm");
		actual = Parser.parse("add\"  go home at 10:00 pm  \"");
		assertEquals(actual, expected);
		
		// Apostrophe
		expected = new Command(CommandType.ADD);
		expected.setDescription("don't go home");
		actual = Parser.parse("add don't go home");
		assertEquals(actual, expected);

		// Multiple quotes
		// Missing add keyword
		expected = new Command(CommandType.INVALID);
		expected.setInvalidCommandReason(InvalidCommandReason.UNRECOGNIZED_COMMAND);
		actual = Parser.parse("\"\"add task at \"\"10 pm");
		assertEquals(actual, expected);
		// Valid
		expected = new Command(CommandType.ADD);
		expected.setDescription("task at");
		intervals = new ArrayList<>();
		start = now.withTime(22, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add\"\"task at \"\"10 pm");
		assertEquals(actual, expected);
		expected.setDescription("what a  task weird at hello");
		actual = Parser.parse("add \"what a \"task weird at \" hello 10 pm");
		assertEquals(actual, expected);

		// Typo in pm
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home p");
		intervals = new ArrayList<>();
		start = now.plusDays(1).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 p");
		assertEquals(actual, expected);

		// Symbols
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home yeah");
		intervals = new ArrayList<>();
		start = now.plusDays(1).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 yeah!");
		assertEquals(actual, expected);
		
		// Proper date format
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = new DateTime(2015, 2, 12, 0, 0);
		end = new DateTime(2015, 2, 12, 23, 59);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home on 12/2/15");
		assertEquals(actual, expected);

		// No year
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = new DateTime(2011, 10, 12, 12, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 12:00 on 12/10/11");
		assertEquals(actual, expected);
		
		// Date and time
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = new DateTime(2015, 2, 12, 12, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 12:00 on 12/2/15");
		assertEquals(actual, expected);
		
		// Fake on and at keywords
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home at on at");
		intervals = new ArrayList<>();
		start = new DateTime(2013, 10, 6, 13, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at on at at 13:00");
		assertEquals(actual, expected);
		
		// Mixed date
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = new DateTime(2015, 2, 12, 12, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 12:00 on 12 february 15");
		assertEquals(actual, expected);
		
		// Mixed date (short)
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = new DateTime(2013, 2, 12, 12, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home on 12 feb at 12:00");
		assertEquals(actual, expected);
		
		// Date aliases
		// today
		expected = new Command(CommandType.ADD);
		expected.setDescription("event");
		intervals = new ArrayList<>();
		start = now.withTime(0, 0, 0, 0);
		end = now.withTime(23, 59, 0, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add event today");
		assertEquals(actual, expected);
		
		// yesterday (no longer a keyword)
		expected = new Command(CommandType.ADD);
		expected.setDescription("event yesterday");
		actual = Parser.parse("add event yesterday");
		assertEquals(actual, expected);

		// tonight
		expected = new Command(CommandType.ADD);
		expected.setDescription("event");
		intervals = new ArrayList<>();
		start = now.withTime(0, 0, 0, 0);
		end = now.withTime(23, 59, 0, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add event tonight");
		assertEquals(actual, expected);
		assertEquals(Parser.parse("add event tonight"), Parser.parse("add event today"));

		// tomorrow
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withTime(0, 0, 0, 0).plusDays(1);
		end = now.withTime(23, 59, 0, 0).plusDays(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home tomorrow");
		assertEquals(actual, expected);

		// Alias
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home for a scare");
		intervals = new ArrayList<>();
		start = now.withMonthOfYear(10).withDayOfMonth(31).withTime(0, 0, 0, 0);
		end = now.withMonthOfYear(10).withDayOfMonth(31).withTime(23, 59, 0, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home on halloween for a scare");
		assertEquals(actual, expected);

		expected = new Command(CommandType.ADD);
		expected.setDescription("go to school");
		intervals = new ArrayList<>();
		start = now.withMonthOfYear(12).withDayOfMonth(25).withTime(0, 0, 0, 0);
		end = start.withTime(23, 59, 0, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go to school on christmas");
		assertEquals(actual, expected);

		// Specifying dates and times in the middle
		expected = new Command(CommandType.ADD);
		expected.setDescription("do schoolwork in school");
		intervals = new ArrayList<>();
		start = now.plusDays(2).withTime(13, 0, 0, 0); // the coming monday
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add do schoolwork at 13:00 on monday in school");
		assertEquals(actual, expected);
		
		// Like the above, but with a trailing or keyword
		expected = new Command(CommandType.ADD);
		expected.setDescription("do schoolwork or die");
		intervals = new ArrayList<>();
		start = now.plusDays(2).withTime(13, 0, 0, 0); // the coming monday
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add do schoolwork at 13:00 on monday or die");
		assertEquals(actual, expected);

		// Like the above, but with a trailing delimiter keyword
		expected = new Command(CommandType.ADD);
		expected.setDescription("go to school to do homework");
		intervals = new ArrayList<>();
		start = now.plusDays(1).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go to school at 1pm to do homework");
		assertEquals(actual, expected);

		// From, until, till, to
		expected = new Command(CommandType.ADD);
		expected.setDescription("event");
		intervals = new ArrayList<>();
		start = now.withTime(10, 0, 0, 0).plusDays(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add event from 10:00 am until 11am");
		assertEquals(actual, expected);
		actual = Parser.parse("add event at 10:00 am till 11am");
		assertEquals(actual, expected);
		actual = Parser.parse("add event from 10:00 am to 11am");
		assertEquals(actual, expected);

		// Wrong qualifier
		expected = new Command(CommandType.ADD);
		expected.setDescription("event on");
		intervals = new ArrayList<>();
		start = now.withTime(10, 0, 0, 0).plusDays(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add event on 10:00 am");
		assertEquals(actual, expected);

		// Interval literal
		expected = new Command(CommandType.ADD);
		expected.setDescription("event");
		intervals = new ArrayList<>();
		start = now.withTime(10, 0, 0, 0).plusDays(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		start = new DateTime(2013, 2, 1, 12, 0);
		end = new DateTime(2014, 3, 2, 13, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add event from 10:00 am until 11am or 1/2/13 12:00 pm to 13:00 2/3/14");
		assertEquals(actual, expected);
		
		// Crossing am boundary
		expected = new Command(CommandType.ADD);
		expected.setDescription("event");
		intervals = new ArrayList<>();
		start = now.withTime(23, 59, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add event from 11:59 pm");
		assertEquals(actual, expected);

		// Flexible interval
		expected = new Command(CommandType.ADD);
		expected.setDescription("'halloween' and also maybe");
		intervals = new ArrayList<>();
		start = now.withDayOfMonth(31).withMonthOfYear(10).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		start = now.plusDays(1).withTime(14, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add 'halloween' at 13:00 on 31/10 and also maybe at 2:00pm");
		assertEquals(actual, expected);
		
		// Deadline
		// Date
		expected = new Command(CommandType.ADD);
		expected.setDescription("finish assignment");
		expected.setDeadline(now.withDate(2015, 2, 12).withTime(23, 59, 0, 0));
		actual = Parser.parse("add finish assignment by 12/2/15");
		assertEquals(actual, expected);
		
		// Time
		expected = new Command(CommandType.ADD);
		expected.setDescription("finish assignment");
		expected.setDeadline(now.withTime(23, 59, 0, 0));
		actual = Parser.parse("add finish assignment by 23:59");
		assertEquals(actual, expected);

		// Both
		expected = new Command(CommandType.ADD);
		expected.setDescription("finish assignment");
		expected.setDeadline(now.withTime(23, 59, 0, 0).withDate(2013, 10, 10));
		actual = Parser.parse("add finish assignment by 23:59 10/10/13");
		assertEquals(actual, expected);

		// Deadline and interval
		
		// Floating task
		// Multiple incomplete intervals
		
		// Ordering of date and time don't matter
		// Future date
		expected = Parser.parse("add go home on 12/2/15 at 3:00");
		actual = Parser.parse("add go home at 3:00 on 12/2/15");
		assertEquals(actual, expected);

		// Past date
		expected = Parser.parse("add go home on 12/2/12 at 3:00");
		actual = Parser.parse("add go home at 3:00 on 12/2/12");
		assertEquals(actual, expected);
		
		// Inferring start date if start date is unspecified and end date is
		expected = new Command(CommandType.ADD);
		expected.setDescription("task");
		intervals = new ArrayList<>();
		start = now.withTime(13, 0, 0, 0).plusDays(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add task 1pm to 2pm today");
		assertEquals(actual, expected);
		
		// Default intervals
		// Date only
		// Past
		expected = Parser.parse("add go home from 12am on 11/2/12 to 23:59 on 11/2/12");
		actual = Parser.parse("add go home on 11/2/12");
		assertEquals(actual, expected);
		// Future
		expected = Parser.parse("add go home from 12am on 11/2/15 to 23:59 on 11/2/15");
		actual = Parser.parse("add go home on 11/2/15");
		assertEquals(actual, expected);

		// Time only
		expected = Parser.parse("add go home from 11pm on 5/10/13 to 12am on 6/10/13");
		actual = Parser.parse("add go home at 11pm");
		assertEquals(actual, expected);

		// Date and time
		// Past
		expected = Parser.parse("add go home from 11pm on 12/2/12 to 12am on 13/2/12");
		actual = Parser.parse("add go home at 11pm 12/2/12");
		assertEquals(actual, expected);
		// Future
		expected = Parser.parse("add go home from 11pm on 12/2/15 to 12am on 12/2/15");
		actual = Parser.parse("add go home at 11pm 12/2/15");
		assertEquals(actual, expected);

		// Days of the week
		// (Note: it's Saturday today)
		// (This) Sunday
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusDays(1).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 on sunday");
		assertEquals(actual, expected);
		actual = Parser.parse("add go home at 13:00 this sunday");
		assertEquals(actual, expected);
		
		// (This) Monday
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusDays(2).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 on monday");
		assertEquals(actual, expected);
		actual = Parser.parse("add go home at 13:00 this monday");
		assertEquals(actual, expected);

		// (This) Tuesday
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusDays(3).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 on tuesday");
		assertEquals(actual, expected);
		actual = Parser.parse("add go home at 13:00 this tuesday");
		assertEquals(actual, expected);

		// (This) Wednesday
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusDays(4).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 on wednesday");
		assertEquals(actual, expected);
		actual = Parser.parse("add go home at 13:00 this wednesday");
		assertEquals(actual, expected);
		
		// (This) Thursday
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusDays(5).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 on thursday");
		assertEquals(actual, expected);
		actual = Parser.parse("add go home at 13:00 this thursday");
		assertEquals(actual, expected);
		
		// (This) Friday
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusDays(6).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 on friday");
		assertEquals(actual, expected);
		actual = Parser.parse("add go home at 13:00 this friday");
		assertEquals(actual, expected);
		
		// (This) Saturday
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusDays(7).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 on saturday");
		assertEquals(actual, expected);
		actual = Parser.parse("add go home at 13:00 this saturday");
		assertEquals(actual, expected);
		
		// Next Saturday
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusDays(7).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 next saturday");
		assertEquals(actual, expected);
		
		// Next Sunday
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusDays(8).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 next sunday");
		assertEquals(actual, expected);
		
		// Next Monday
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusDays(9).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 next monday");
		assertEquals(actual, expected);
		
		// Next Tuesday
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusDays(10).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 next tuesday");
		assertEquals(actual, expected);
		
		// Next Wednesday
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusDays(11).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 next wednesday");
		assertEquals(actual, expected);
		
		// Next Thursday
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusDays(12).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 next thursday");
		assertEquals(actual, expected);
		
		// Next Friday
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusDays(13).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 next friday");
		assertEquals(actual, expected);

        // Last Saturday
        expected = new Command(CommandType.ADD);
        expected.setDescription("go home");
        intervals = new ArrayList<>();
        start = now.minusDays(7).withTime(13, 0, 0, 0);
        end = start.plusHours(1);
        intervals.add(new Interval(start, end));
        expected.setIntervals(intervals);
        actual = Parser.parse("add go home at 13:00 last saturday");
        assertEquals(actual, expected);
        
        // Last Sunday
        expected = new Command(CommandType.ADD);
        expected.setDescription("go home");
        intervals = new ArrayList<>();
        start = now.minusDays(6).withTime(13, 0, 0, 0);
        end = start.plusHours(1);
        intervals.add(new Interval(start, end));
        expected.setIntervals(intervals);
        actual = Parser.parse("add go home at 13:00 last sunday");
        assertEquals(actual, expected);
        
        // Last Monday
        expected = new Command(CommandType.ADD);
        expected.setDescription("go home");
        intervals = new ArrayList<>();
        start = now.minusDays(5).withTime(13, 0, 0, 0);
        end = start.plusHours(1);
        intervals.add(new Interval(start, end));
        expected.setIntervals(intervals);
        actual = Parser.parse("add go home at 13:00 last monday");
        assertEquals(actual, expected);
        
        // Last Tuesday
        expected = new Command(CommandType.ADD);
        expected.setDescription("go home");
        intervals = new ArrayList<>();
        start = now.minusDays(4).withTime(13, 0, 0, 0);
        end = start.plusHours(1);
        intervals.add(new Interval(start, end));
        expected.setIntervals(intervals);
        actual = Parser.parse("add go home at 13:00 last tuesday");
        assertEquals(actual, expected);
        
        // Last Wednesday
        expected = new Command(CommandType.ADD);
        expected.setDescription("go home");
        intervals = new ArrayList<>();
        start = now.minusDays(3).withTime(13, 0, 0, 0);
        end = start.plusHours(1);
        intervals.add(new Interval(start, end));
        expected.setIntervals(intervals);
        actual = Parser.parse("add go home at 13:00 last wednesday");
        assertEquals(actual, expected);
        
        // Last Thursday
        expected = new Command(CommandType.ADD);
        expected.setDescription("go home");
        intervals = new ArrayList<>();
        start = now.minusDays(2).withTime(13, 0, 0, 0);
        end = start.plusHours(1);
        intervals.add(new Interval(start, end));
        expected.setIntervals(intervals);
        actual = Parser.parse("add go home at 13:00 last thursday");
        assertEquals(actual, expected);
        
        // Last Friday
        expected = new Command(CommandType.ADD);
        expected.setDescription("go home");
        intervals = new ArrayList<>();
        start = now.minusDays(1).withTime(13, 0, 0, 0);
        end = start.plusHours(1);
        intervals.add(new Interval(start, end));
        expected.setIntervals(intervals);
        actual = Parser.parse("add go home at 13:00 last friday");
        assertEquals(actual, expected);

		// Wacky capitalization
		expected = new Command(CommandType.ADD);
		expected.setDescription("gO hOmE");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.THURSDAY).plusWeeks(1).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add gO hOmE aT 13:00 tHiS thursday");
		assertEquals(actual, expected);
		
		// Day of week shorthand
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.THURSDAY).plusWeeks(1).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 this thurs");
		assertEquals(actual, expected);
		
		// Substring of day - does not match word boundary
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home nes");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.WEDNESDAY).plusWeeks(1).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home at 13:00 this wednes");
		assertEquals(actual, expected);
		
		// Relative dates
		// Year
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusYears(1).withTime(0, 0, 0, 0);
		end = start.withTime(23, 59, 0, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home next year");
		assertEquals(actual, expected);
		
		// Week
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusWeeks(1).withTime(0, 0, 0, 0);
		end = start.withTime(23, 59, 0, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home next week");
		assertEquals(actual, expected);
		
		// Month
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusMonths(1).withTime(0, 0, 0, 0);
		end = start.withTime(23, 59, 0, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home next month");
		assertEquals(actual, expected);
		
		// Missing next keyword
		expected = new Command(CommandType.ADD);
		expected.setDescription("month");
		actual = Parser.parse("add month");
		assertEquals(actual, expected);
		expected = new Command(CommandType.ADD);
		expected.setDescription("week");
		actual = Parser.parse("add week");
		assertEquals(actual, expected);
		expected = new Command(CommandType.ADD);
		expected.setDescription("year");
		actual = Parser.parse("add year");
		assertEquals(actual, expected);

		// Fortnight
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusWeeks(2).withTime(0, 0, 0, 0);
		end = start.withTime(23, 59, 0, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home next fortnight");
		assertEquals(actual, expected);
		
		// Treating relative dates as dates
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusWeeks(2).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add go home next fortnight 1pm");
		assertEquals(actual, expected);

		// Invalid dates (30th Feb, 33th Jan, 31 June, etc.)
		expected = new Command(CommandType.INVALID);
		expected.setInvalidCommandReason(InvalidCommandReason.INVALID_DATE);
		actual = Parser.parse("add task 30 feb");
		assertEquals(actual, expected);

		expected = new Command(CommandType.INVALID);
		expected.setInvalidCommandReason(InvalidCommandReason.INVALID_DATE);
		actual = Parser.parse("add task 29 feb"); // year is implicitly 2013
		assertEquals(actual, expected);
		
		expected = new Command(CommandType.INVALID);
		expected.setInvalidCommandReason(InvalidCommandReason.INVALID_DATE);
		actual = Parser.parse("add task 31 june");
		assertEquals(actual, expected);

		expected = new Command(CommandType.ADD);
		expected.setDescription("task 33 jan");
		actual = Parser.parse("add task 33 jan");
		assertEquals(actual, expected);

		// Leap year
		expected = new Command(CommandType.ADD);
		expected.setDescription("task");
		intervals = new ArrayList<>();
		start = now.withDate(2012, 2, 29).withTime(0, 0, 0, 0);
		end = start.withTime(23, 59, 0, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add task 29 feb 12");
		assertEquals(actual, expected);
		
		// Invalid date range
		expected = new Command(CommandType.ADD);
		expected.setDescription("task");
		intervals = new ArrayList<>();
		start = now.withDate(2013, 10, 6).withTime(0, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("add task 6 oct to 4 oct");
		assertEquals(actual, expected);
	}

	@Test
	public void editCommandTests() {
		Command expected, actual;
		ArrayList<Interval> intervals;
		DateTime start, end;
		
		DateTime now = new DateTime(2013, 10, 5, 20, 0); // saturday 8pm 5/10/13
		Interval.setNowStub(now);
		DateToken.setNowStub(now);
		StateDeadline.setNowStub(now);
		
		// Insufficient parameters
		expected = new Command(CommandType.EDIT);
		actual = Parser.parse("edit");
		assertEquals(actual, expected);
		
		// Index only
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		actual = Parser.parse("edit 1");
		assertEquals(actual, expected);

		// Description only
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		expected.setDescription("hello");
		actual = Parser.parse("edit 1 hello");
		assertEquals(actual, expected);

		// Interval only
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		intervals = new ArrayList<Interval>();
		start = now.withTime(13, 0, 0, 0).plusDays(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("edit 1 1pm");
		assertEquals(actual, expected);
		
		// Description and interval
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		expected.setDescription("hello");
		intervals = new ArrayList<Interval>();
		start = now.withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("edit 1 1pm today hello");
		assertEquals(actual, expected);

		// Index and timeslot index only
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		expected.setTimeslotIndex(1);
		actual = Parser.parse("edit 1 1");
		assertEquals(actual, expected);
		
		// Making sure hashtags aren't mistakenly considered indices
		expected = new Command(CommandType.EDIT);
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("#1234");
		expected.setTags(tags);
		expected.setDescription("sup");
		expected.setTaskIndex(1);
		actual = Parser.parse("edit 1 #1234 sup");
		assertEquals(actual, expected);

		// Empty timeslot
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		expected.setTimeslotIndex(1);
		actual = Parser.parse("edit 1 1 kajsld");
		assertEquals(actual, expected);

		// Garbage before timeslot
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		expected.setTimeslotIndex(1);
		intervals = new ArrayList<Interval>();
		start = now.withTime(13, 0, 0, 0).plusDays(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("edit 1 1 kajsld 1pm");
		assertEquals(actual, expected);
		
		// Comma does not delimit as you would expect
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		expected.setTimeslotIndex(1);
		intervals = new ArrayList<Interval>();
		start = now.withTime(15, 0, 0, 0).plusDays(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("edit 1 1 1pm, 3pm");
		assertEquals(actual, expected);
		
		// Proper interval format
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		expected.setTimeslotIndex(1);
		intervals = new ArrayList<Interval>();
		start = now.withTime(13, 0, 0, 0).plusDays(1);
		end = start.plusHours(2);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("edit 1 1 1pm to 3pm");
		assertEquals(actual, expected);
		
		// Proper interval format 2
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		expected.setTimeslotIndex(1);
		intervals = new ArrayList<Interval>();
		start = now.withTime(13, 0, 0, 0).plusDays(1);
		end = start.plusHours(2);
		intervals.add(new Interval(start, end));
		start = now.withTime(14, 0, 0, 0).plusDays(1);
		end = start.plusHours(2);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("edit 1 1 1pm to 3pm or 2pm to 4pm");
		assertEquals(actual, expected);
		
		// Random gibberish inserted in the middle of intervals
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		expected.setTimeslotIndex(1);
		intervals = new ArrayList<Interval>();
		start = now.withTime(13, 0, 0, 0).plusDays(1);
		end = start.plusHours(2);
		intervals.add(new Interval(start, end));
		start = now.withTime(14, 0, 0, 0).plusDays(1);
		end = start.plusHours(2);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = Parser.parse("edit 1 1 1pm asda to jashdk 3pm !!! or asdn 2pm asjd to asdasd 4pm");
		assertEquals(actual, expected);
		
		// Deadline
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		expected.setDeadline(now.withTime(22, 0, 0, 0));
		actual = Parser.parse("edit 1 by 10pm");
		assertEquals(actual, expected);
	}

	@Test
	public void doneCommandTests() {
		Command expected, actual;
		
		// Correct format
		expected = new Command(CommandType.DONE);
		expected.setTaskIndex(1);
		actual = Parser.parse("done 1");
		assertEquals(actual, expected);

		// Negative index
		expected = new Command(CommandType.DONE);
		expected.setTaskIndex(-2);
		actual = Parser.parse("done -2");
		assertEquals(actual, expected);

		// Missing index
		expected = new Command(CommandType.DONE);
		actual = Parser.parse("done");
		assertEquals(actual, expected);

		// Invalid format
		expected = new Command(CommandType.DONE);
		actual = Parser.parse("done askldjas");
		assertEquals(actual, expected);
	}

	@Test
	public void deleteCommandTests() {
		Command expected, actual;
		
		// Correct format
		expected = new Command(CommandType.DELETE);
		expected.setTaskIndex(1);
		actual = Parser.parse("delete 1");
		assertEquals(actual, expected);

		// Negative index
		expected = new Command(CommandType.DELETE);
		expected.setTaskIndex(-2);
		actual = Parser.parse("delete -2");
		assertEquals(actual, expected);
		
		// Missing index
		expected = new Command(CommandType.DELETE);
		actual = Parser.parse("delete");
		assertEquals(actual, expected);

		// Invalid format
		expected = new Command(CommandType.DELETE);
		actual = Parser.parse("delete askldjas");
		assertEquals(actual, expected);
	}
	
	@Test
	public void gotoCommandTests() {
		Command expected, actual;
		
		expected = new Command(CommandType.GOTO);
		actual = Parser.parse("goto");
		assertEquals(expected, actual);

		expected = new Command(CommandType.GOTO);
		actual = Parser.parse("goto #1234");
		assertEquals(expected, actual);

		expected = new Command(CommandType.GOTO);
		actual = Parser.parse("goto what");
		assertEquals(expected, actual);

		expected = new Command(CommandType.GOTO);
		expected.setPageIndex(1);
		actual = Parser.parse("goto 1");
		assertEquals(expected, actual);
	}
	
	@Test
	public void searchCommandTests() {
		Command expected, actual;
		ArrayList<String> tags, searchTerms;
		
		expected = new Command(CommandType.SEARCH);
		actual = Parser.parse("search");
		assertEquals(expected, actual);
		
		expected = new Command(CommandType.SEARCH);
		searchTerms = new ArrayList<String>();
		searchTerms.add("hello");
		searchTerms.add("there");
		searchTerms.add("12/2");
		expected.setSearchTerms(searchTerms);
		actual = Parser.parse("search hello there 12/2");
		assertEquals(expected, actual);

		expected = new Command(CommandType.SEARCH);
		searchTerms = new ArrayList<String>();
		searchTerms.add("hello");
		searchTerms.add("there");
		expected.setSearchTerms(searchTerms);
		tags = new ArrayList<String>();
		tags.add("#yellow");
		expected.setTags(tags);
		actual = Parser.parse("search hello there #yellow");
		assertEquals(expected, actual);

		expected = Parser.parse("search #yellow hello there");
		actual = Parser.parse("search hello there #yellow");
		assertEquals(expected, actual);

		expected = Parser.parse("search there hello #yellow");
		actual = Parser.parse("search hello there #yellow");
		assertNotSame(expected, actual);

		expected = new Command(CommandType.SEARCH);
		searchTerms = new ArrayList<String>();
		searchTerms.add("whatever");
		expected.setSearchTerms(searchTerms);
		tags = new ArrayList<String>();
		tags.add("#yellow");
		tags.add("#blue");
		expected.setTags(tags);
		actual = Parser.parse("search #yellow #blue whatever");
		assertEquals(expected, actual);
	}
	
	@Test
	public void finaliseCommandTests() {
		Command expected, actual;
		
		// Too few arguments
		expected = new Command(CommandType.FINALISE);
		expected.setTaskIndex(1);
		actual = Parser.parse("finalise 1");
		assertEquals(actual, expected);

		expected = new Command(CommandType.FINALISE);
		actual = Parser.parse("finalise");
		assertEquals(actual, expected);

		// Proper format
		expected = new Command(CommandType.FINALISE);
		expected.setTaskIndex(12);
		expected.setTimeslotIndex(13);
		actual = Parser.parse("finalise 12 13");
		assertEquals(actual, expected);

		// Gibberish at the end is ignored
		expected = new Command(CommandType.FINALISE);
		expected.setTaskIndex(12);
		expected.setTimeslotIndex(13);
		actual = Parser.parse("finalise 12 13 asdkj");
		assertEquals(actual, expected);

		// Invalid number formats
		expected = new Command(CommandType.FINALISE);
		actual = Parser.parse("finalise 1askdj 1");
		assertEquals(actual, expected);

		expected = new Command(CommandType.FINALISE);
		expected.setTaskIndex(1);
		actual = Parser.parse("finalise 1 1askdj");
		assertEquals(actual, expected);
	}
	
	@Test
	public void fuzzyMatchingTests() {
		
		assertEquals(Parser.parse("qwe").getCommandType(), CommandType.INVALID);
		assertEquals(Parser.parse("zxc").getCommandType(), CommandType.INVALID);

		assertEquals(Parser.parse("a").getCommandType(), CommandType.ADD);
		assertEquals(Parser.parse("ad").getCommandType(), CommandType.ADD);
		assertEquals(Parser.parse("sad").getCommandType(), CommandType.ADD);
		assertEquals(Parser.parse("asd").getCommandType(), CommandType.ADD);
		assertEquals(Parser.parse("ass").getCommandType(), CommandType.ADD);
		assertEquals(Parser.parse("aww").getCommandType(), CommandType.ADD);
		assertEquals(Parser.parse("dad").getCommandType(), CommandType.ADD);
		assertEquals(Parser.parse("addd").getCommandType(), CommandType.ADD);

		assertEquals(Parser.parse("e").getCommandType(), CommandType.EDIT);
		assertEquals(Parser.parse("ed").getCommandType(), CommandType.EDIT);
		assertEquals(Parser.parse("edot").getCommandType(), CommandType.EDIT);
		assertEquals(Parser.parse("rdit").getCommandType(), CommandType.EDIT);
		assertEquals(Parser.parse("fdot").getCommandType(), CommandType.EDIT);

		assertEquals(Parser.parse("ex").getCommandType(), CommandType.EXIT);
		assertEquals(Parser.parse("ext").getCommandType(), CommandType.EXIT);
		assertEquals(Parser.parse("exat").getCommandType(), CommandType.EXIT);
		assertEquals(Parser.parse("exoo").getCommandType(), CommandType.EXIT);

		assertEquals(Parser.parse("dis").getCommandType(), CommandType.DISPLAY);
		assertEquals(Parser.parse("dipslay").getCommandType(), CommandType.DISPLAY);
		assertEquals(Parser.parse("dpisly").getCommandType(), CommandType.DISPLAY);

		assertEquals(Parser.parse("g").getCommandType(), CommandType.GOTO);
		assertEquals(Parser.parse("got").getCommandType(), CommandType.GOTO);
		assertEquals(Parser.parse("goo").getCommandType(), CommandType.GOTO);

		assertEquals(Parser.parse("s").getCommandType(), CommandType.SORT);
		assertEquals(Parser.parse("sor").getCommandType(), CommandType.SORT);
		assertEquals(Parser.parse("srot").getCommandType(), CommandType.SORT);

		assertEquals(Parser.parse("se").getCommandType(), CommandType.SEARCH);
		assertEquals(Parser.parse("srch").getCommandType(), CommandType.SEARCH);
		assertEquals(Parser.parse("sarrch").getCommandType(), CommandType.SEARCH);

		assertEquals(Parser.parse("u").getCommandType(), CommandType.UNDO);
		assertEquals(Parser.parse("uond").getCommandType(), CommandType.UNDO);
		assertEquals(Parser.parse("uni").getCommandType(), CommandType.UNDO);

		assertEquals(Parser.parse("fina").getCommandType(), CommandType.FINALISE);
		assertEquals(Parser.parse("finalize").getCommandType(), CommandType.FINALISE);
		assertEquals(Parser.parse("nalise").getCommandType(), CommandType.FINALISE);

		assertEquals(Parser.parse("h").getCommandType(), CommandType.HELP);
		assertEquals(Parser.parse("hlp").getCommandType(), CommandType.HELP);
		assertEquals(Parser.parse("hello").getCommandType(), CommandType.HELP);

		assertEquals(Parser.parse("reod").getCommandType(), CommandType.REDO);
		assertEquals(Parser.parse("r").getCommandType(), CommandType.REDO);
		assertEquals(Parser.parse("rod").getCommandType(), CommandType.REDO);

		assertEquals(Parser.parse("c").getCommandType(), CommandType.CLEAR);
		assertEquals(Parser.parse("clr").getCommandType(), CommandType.CLEAR);
		assertEquals(Parser.parse("lear").getCommandType(), CommandType.CLEAR);
		assertEquals(Parser.parse("clarr").getCommandType(), CommandType.CLEAR);
		assertEquals(Parser.parse("clrer").getCommandType(), CommandType.CLEAR);

		assertEquals(Parser.parse("del 1").getCommandType(), CommandType.DELETE);
		assertEquals(Parser.parse("dele 1").getCommandType(), CommandType.DELETE);
		assertEquals(Parser.parse("delet 1").getCommandType(), CommandType.DELETE);
		assertEquals(Parser.parse("deleet 1").getCommandType(), CommandType.DELETE);
		assertEquals(Parser.parse("deeelt 1").getCommandType(), CommandType.DELETE);
		assertEquals(Parser.parse("deleeete 1").getCommandType(), CommandType.DELETE);

		assertEquals(Parser.parse("d 1").getCommandType(), CommandType.DONE);
		assertEquals(Parser.parse("daa").getCommandType(), CommandType.DONE);
		assertEquals(Parser.parse("de 1").getCommandType(), CommandType.DONE);
		assertEquals(Parser.parse("dne 1").getCommandType(), CommandType.DONE);
		assertEquals(Parser.parse("don 1").getCommandType(), CommandType.DONE);
		assertEquals(Parser.parse("doone 1").getCommandType(), CommandType.DONE);
		assertEquals(Parser.parse("doon 1").getCommandType(), CommandType.DONE);

	}
	
	@Test
	public void sortCommandTests() {
		Command expected, actual;
	
	    // Proper command
	    expected = new Command(CommandType.SORT);
	    actual = Parser.parse("sort");
	    assertEquals(actual, expected);
	
	    // Random index
	    expected = new Command(CommandType.SORT);
	    actual = Parser.parse("sort 1");
	    assertEquals(actual, expected);
	
	    // Random symbols
	    expected = new Command(CommandType.SORT);
	    actual = Parser.parse("sort akjsdkljas!@#$%^&*()_+~{}|;'<>?;");
	    assertEquals(actual, expected);
	}

	@Test
	public void undoCommandTests() {
		Command expected, actual;
		
		// Proper command
		expected = new Command(CommandType.UNDO);
		actual = Parser.parse("undo");
		assertEquals(actual, expected);
	
		// Random index
		expected = new Command(CommandType.UNDO);
		actual = Parser.parse("undo 1");
		assertEquals(actual, expected);
	
		// Random symbols
		expected = new Command(CommandType.UNDO);
		actual = Parser.parse("undo akjsdkljas!@#$%^&*()_+~{}|;'<>?;");
		assertEquals(actual, expected);
	}

	@Test
	public void redoCommandTests() {
	    Command expected, actual;
	    
	    // Proper command
	    expected = new Command(CommandType.REDO);
	    actual = Parser.parse("redo");
	    assertEquals(actual, expected);
	
	    // Random index
	    expected = new Command(CommandType.REDO);
	    actual = Parser.parse("redo 1");
	    assertEquals(actual, expected);
	
	    // Random symbols
	    expected = new Command(CommandType.REDO);
	    actual = Parser.parse("redo akjsdkljas!@#$%^&*()_+~{}|;'<>?;");
	    assertEquals(actual, expected);
	}

	@Test
	public void exitCommandTests() {
        Command expected, actual;
        
        // Proper command
        expected = new Command(CommandType.EXIT);
        actual = Parser.parse("exit");
        assertEquals(actual, expected);
    
        // Random index
        expected = new Command(CommandType.EXIT);
        actual = Parser.parse("exit 1");
        assertEquals(actual, expected);
    
        // Random symbols
        expected = new Command(CommandType.EXIT);
        actual = Parser.parse("exit akjsdkljas!@#$%^&*()_+~{}|;'<>?;");
        assertEquals(actual, expected);
    }

	@Test
	public void displayCommandTests() {
		
		DateTime now = new DateTime(2013, 10, 5, 20, 0); // saturday 8pm 5/10/13
		Interval.setNowStub(now);
		DateToken.setNowStub(now);
		StateDeadline.setNowStub(now);

        Command expected, actual;
        
        // Typos
        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.TODO);
        actual = Parser.parse("display untmed");
        assertEquals(actual, expected);

        // No mode
        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.TODO);
        actual = Parser.parse("display");
        assertEquals(actual, expected);

        // Proper commands
        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.TODAY);
        actual = Parser.parse("display today");
        assertEquals(actual, expected);

        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.TOMORROW);
        actual = Parser.parse("display tomorrow");
        assertEquals(actual, expected);

        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.DEADLINE);
        actual = Parser.parse("display deadline");
        assertEquals(actual, expected);

        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.TIMED);
        actual = Parser.parse("display timed");
        assertEquals(actual, expected);

        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.TENTATIVE);
        actual = Parser.parse("display tentative");
        assertEquals(actual, expected);

        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.UNTIMED);
        actual = Parser.parse("display untimed");
        assertEquals(actual, expected);

        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.ALL);
        actual = Parser.parse("display all");
        assertEquals(actual, expected);

        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.OVERDUE);
        actual = Parser.parse("display overdue");
        assertEquals(actual, expected);

        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.TODO);
        actual = Parser.parse("display todo");
        assertEquals(actual, expected);

        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.DONE);
        actual = Parser.parse("display done");
        assertEquals(actual, expected);
        
        // Display date
        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.DATE);
        expected.setDisplayDateTime(now.withTime(0, 0, 0, 0).withDate(2013, 11, 14));
        actual = Parser.parse("display 14/11");
        assertEquals(actual, expected);
        
        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.DATE);
        expected.setDisplayDateTime(now.withTime(0, 0, 0, 0).plusDays(9));
        actual = Parser.parse("display next monday");
        assertEquals(actual, expected);

        // Invalid display modes
        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.TODO);
        actual = Parser.parse("display search");
        assertEquals(actual, expected);

        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.TODO);
        actual = Parser.parse("display invalid");
        assertEquals(actual, expected);

        // Random index
        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.TODO);
        actual = Parser.parse("display 1");
        assertEquals(actual, expected);
    
        // Random symbols
        expected = new Command(CommandType.DISPLAY);
        expected.setDisplayMode(DisplayMode.TODO);
        actual = Parser.parse("display akjsdkljas!@#$%^&*()_+~{}|;'<>?;");
        assertEquals(actual, expected);
	}
	@Test
	public void clearCommandTests() {

        DateTime now = new DateTime(2013, 10, 5, 20, 0); // saturday 8pm 5/10/13
        Interval.setNowStub(now);
        DateToken.setNowStub(now);
        StateDeadline.setNowStub(now);

		Command expected, actual;
		
		// Typos
		expected = new Command(CommandType.CLEAR);
		expected.setClearMode(ClearMode.ALL);
		actual = Parser.parse("clear untmed");
		assertEquals(actual, expected);
		
		// No mode
		expected = new Command(CommandType.CLEAR);
		expected.setClearMode(ClearMode.ALL);
		actual = Parser.parse("clear");
		assertEquals(actual, expected);
		
		// Proper commands
		expected = new Command(CommandType.CLEAR);
		expected.setClearMode(ClearMode.DEADLINE);
		actual = Parser.parse("clear deadline");
		assertEquals(actual, expected);
		
		expected = new Command(CommandType.CLEAR);
		expected.setClearMode(ClearMode.TIMED);
		actual = Parser.parse("clear timed");
		assertEquals(actual, expected);
		
		expected = new Command(CommandType.CLEAR);
		expected.setClearMode(ClearMode.TENTATIVE);
		actual = Parser.parse("clear tentative");
		assertEquals(actual, expected);
		
		expected = new Command(CommandType.CLEAR);
		expected.setClearMode(ClearMode.UNTIMED);
		actual = Parser.parse("clear untimed");
		assertEquals(actual, expected);
		
		expected = new Command(CommandType.CLEAR);
		expected.setClearMode(ClearMode.ALL);
		actual = Parser.parse("clear all");
		assertEquals(actual, expected);
		
		expected = new Command(CommandType.CLEAR);
		expected.setClearMode(ClearMode.OVERDUE);
		actual = Parser.parse("clear overdue");
		assertEquals(actual, expected);
		
		expected = new Command(CommandType.CLEAR);
		expected.setClearMode(ClearMode.DONE);
		actual = Parser.parse("clear done");
		assertEquals(actual, expected);
		
		// Display date
		expected = new Command(CommandType.CLEAR);
		expected.setClearMode(ClearMode.DATE);
		expected.setClearDateTime(now.withTime(0, 0, 0, 0).withDate(2013, 11, 14));
		actual = Parser.parse("clear 14/11");
		assertEquals(actual, expected);
		
		expected = new Command(CommandType.CLEAR);
		expected.setClearMode(ClearMode.DATE);
		expected.setClearDateTime(now.withTime(0, 0, 0, 0).plusDays(9));
		actual = Parser.parse("clear next monday");
		assertEquals(actual, expected);
		
		// Invalid clear modes
		expected = new Command(CommandType.CLEAR);
		expected.setClearMode(ClearMode.ALL);
		actual = Parser.parse("clear invalid");
		assertEquals(actual, expected);
		
		// Random index
		expected = new Command(CommandType.CLEAR);
		expected.setClearMode(ClearMode.ALL);
		actual = Parser.parse("clear 1");
		assertEquals(actual, expected);
		
		// Random symbols
		expected = new Command(CommandType.CLEAR);
		expected.setClearMode(ClearMode.ALL);
		actual = Parser.parse("clear akjsdkljas!@#$%^&*()_+~{}|;'<>?;");
		assertEquals(actual, expected);
	}
	@Test
	public void helpCommandTests() {
		Command expected, actual;
		
		// Invalid types
		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.INVALID);
		actual = Parser.parse("help invalid");
		assertEquals(actual, expected);

		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.INVALID);
		actual = Parser.parse("help werwwqeeqweqweqweq");
		assertEquals(actual, expected);

		// Typo correction
		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.ADD);
		actual = Parser.parse("help aww");
		assertEquals(actual, expected);

		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.DELETE);
		actual = Parser.parse("help del");
		assertEquals(actual, expected);
		
		// Proper commands
		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.ADD);
		actual = Parser.parse("help add");
		assertEquals(actual, expected);

		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.EDIT);
		actual = Parser.parse("help edit");
		assertEquals(actual, expected);

		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.DISPLAY);
		actual = Parser.parse("help display");
		assertEquals(actual, expected);

		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.DELETE);
		actual = Parser.parse("help delete");
		assertEquals(actual, expected);

		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.CLEAR);
		actual = Parser.parse("help clear");
		assertEquals(actual, expected);

		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.EXIT);
		actual = Parser.parse("help exit");
		assertEquals(actual, expected);

		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.GOTO);
		actual = Parser.parse("help goto");
		assertEquals(actual, expected);

		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.SORT);
		actual = Parser.parse("help sort");
		assertEquals(actual, expected);

		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.SEARCH);
		actual = Parser.parse("help search");
		assertEquals(actual, expected);

		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.UNDO);
		actual = Parser.parse("help undo");
		assertEquals(actual, expected);

		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.FINALISE);
		actual = Parser.parse("help finalise");
		assertEquals(actual, expected);

		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.HELP);
		actual = Parser.parse("help help");
		assertEquals(actual, expected);

		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.DONE);
		actual = Parser.parse("help done");
		assertEquals(actual, expected);

		expected = new Command(CommandType.HELP);
		expected.setHelpCommand(CommandType.REDO);
		actual = Parser.parse("help redo");
		assertEquals(actual, expected);
	}
}
