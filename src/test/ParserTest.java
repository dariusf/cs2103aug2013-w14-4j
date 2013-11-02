
package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.Test;

import common.CommandType;
import common.InvalidCommandReason;

import logic.Command;
import logic.Interval;
import parser.DateToken;
import parser.Parser;
import parser.StateDeadline;

public class ParserTest {

//	public void assertEquals(Object one, Object two) {}
	
	@Test
	public void addCommandTests() {

		DateTime now = new DateTime(2013, 10, 5, 20, 0); // saturday 8pm 5/10/13
		Interval.setNowStub(now);
		DateToken.setNowStub(now);
		StateDeadline.setNowStub(now);
		
		Command expected, actual;
		ArrayList<Interval> intervals;
		DateTime start, end;
		
		// Invalid commands
		// Empty string
		expected = new Command(CommandType.INVALID);
		expected.setInvalidCommandReason(InvalidCommandReason.EMPTY_COMMAND);
		actual = new Parser().parse("");
		assertEquals(actual, expected);

		// Null string
		actual = new Parser().parse(null);
		assertEquals(actual, expected);

		// Plain invalid commands
		// Gibberish
		actual = new Parser().parse("kasdkajsklad aklsjdkals kajsld klajsd");
		expected.setInvalidCommandReason(InvalidCommandReason.UNRECOGNIZED_COMMAND);
		assertEquals(actual, expected);
		actual = new Parser().parse("!@#$%^&*({}][]\\|';.,><;");
		expected.setInvalidCommandReason(InvalidCommandReason.UNRECOGNIZED_COMMAND);
		assertEquals(actual, expected);
		// Invalid starting keyword
		actual = new Parser().parse("hjkhjs task at 10:00 pm");
		assertEquals(actual, expected);
		// Missing add keyword
		actual = new Parser().parse("task at 10:00 pm");
		assertEquals(actual, expected);

		// Adding a bunch of symbols
		actual = new Parser().parse("add !@#$%^&*({}][]\\|';.,><;");
		expected = new Command(CommandType.ADD);
		expected.setDescription("! ' .");
		assertEquals(actual, expected);

		// Correct format
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withTime(22, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home at 10:00 pm");
		assertEquals(actual, expected);

		// No qualifier
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withTime(22, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home 10:00 pm");
		assertEquals(actual, expected);
		
		// 24h format
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withTime(22, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home 22:00");
		assertEquals(actual, expected);

		// 24h format without colon
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withTime(22, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home at 2200");
		assertEquals(actual, expected);

		// 12h shorthand 
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withTime(22, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home 10pm");
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
		actual = new Parser().parse("  add  go  home   at  13:00 pm  ");
		assertEquals(actual, expected);
		
		// Quotes
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home at 10:00 pm");
		actual = new Parser().parse("add\"  go home at 10:00 pm  \"");
		assertEquals(actual, expected);
		
		// Apostrophe
		expected = new Command(CommandType.ADD);
		expected.setDescription("don't go home");
		actual = new Parser().parse("add don't go home");
		assertEquals(actual, expected);

		// Multiple quotes
		// Missing add keyword
		expected = new Command(CommandType.INVALID);
		expected.setInvalidCommandReason(InvalidCommandReason.UNRECOGNIZED_COMMAND);
		actual = new Parser().parse("\"\"add task at \"\"10 pm");
		assertEquals(actual, expected);
		// Valid
		expected = new Command(CommandType.ADD);
		expected.setDescription("task at");
		intervals = new ArrayList<>();
		start = now.withTime(22, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add\"\"task at \"\"10 pm");
		assertEquals(actual, expected);
		expected.setDescription("what a  task weird at hello");
		actual = new Parser().parse("add \"what a \"task weird at \" hello 10 pm");
		assertEquals(actual, expected);

		// Typo in pm
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home p");
		intervals = new ArrayList<>();
		start = now.plusDays(1).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home at 13:00 p");
		assertEquals(actual, expected);

		// Symbols
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home yeah!");
		intervals = new ArrayList<>();
		start = now.plusDays(1).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home at 13:00 yeah!");
		assertEquals(actual, expected);
		
		// Proper date format
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = new DateTime(2015, 2, 12, 0, 0);
		end = new DateTime(2015, 2, 12, 23, 59);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home on 12/2/15");
		assertEquals(actual, expected);

		// No year
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = new DateTime(2011, 10, 12, 12, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home at 12:00 on 12/10/11");
		assertEquals(actual, expected);
		
		// Date and time
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = new DateTime(2015, 2, 12, 12, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home at 12:00 on 12/2/15");
		assertEquals(actual, expected);
		
		// Fake on and at keywords
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home at on at");
		intervals = new ArrayList<>();
		start = new DateTime(2013, 10, 6, 13, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home at on at at 13:00");
		assertEquals(actual, expected);
		
		// Mixed date
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = new DateTime(2015, 2, 12, 12, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home at 12:00 on 12 february 15");
		assertEquals(actual, expected);
		
		// Mixed date (short)
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = new DateTime(2013, 2, 12, 12, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home on 12 feb at 12:00");
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
		actual = new Parser().parse("add event today");
		assertEquals(actual, expected);
		
		// tonight
		expected = new Command(CommandType.ADD);
		expected.setDescription("event");
		intervals = new ArrayList<>();
		start = now.withTime(0, 0, 0, 0);
		end = now.withTime(23, 59, 0, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add event tonight");
		assertEquals(actual, expected);
		assertEquals(new Parser().parse("add event tonight"), new Parser().parse("add event today"));

		// tomorrow
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withTime(0, 0, 0, 0).plusDays(1);
		end = now.withTime(23, 59, 0, 0).plusDays(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home tomorrow");
		assertEquals(actual, expected);

		// Alias
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home for a scare");
		intervals = new ArrayList<>();
		start = now.withMonthOfYear(10).withDayOfMonth(31).withTime(0, 0, 0, 0);
		end = now.withMonthOfYear(10).withDayOfMonth(31).withTime(23, 59, 0, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home on halloween for a scare");
		assertEquals(actual, expected);

		// Specifying dates and times in the middle
		expected = new Command(CommandType.ADD);
		expected.setDescription("do schoolwork in school");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.MONDAY).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add do schoolwork at 13:00 on monday in school");
		assertEquals(actual, expected);
		
		// Like the above, but with a trailing or keyword
		expected = new Command(CommandType.ADD);
		expected.setDescription("do schoolwork or die");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.MONDAY).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add do schoolwork at 13:00 on monday or die");
		assertEquals(actual, expected);

		// Like the above, but with a trailing delimiter keyword
		expected = new Command(CommandType.ADD);
		expected.setDescription("go to school to do homework");
		intervals = new ArrayList<>();
		start = now.plusDays(1).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go to school at 1pm to do homework");
		assertEquals(actual, expected);

		// From, until, till, to
		expected = new Command(CommandType.ADD);
		expected.setDescription("event");
		intervals = new ArrayList<>();
		start = now.withTime(10, 0, 0, 0).plusDays(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add event from 10:00 am until 11am");
		assertEquals(actual, expected);
		actual = new Parser().parse("add event at 10:00 am till 11am");
		assertEquals(actual, expected);
		actual = new Parser().parse("add event from 10:00 am to 11am");
		assertEquals(actual, expected);

		// Wrong qualifier
		expected = new Command(CommandType.ADD);
		expected.setDescription("event on");
		intervals = new ArrayList<>();
		start = now.withTime(10, 0, 0, 0).plusDays(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add event on 10:00 am");
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
		actual = new Parser().parse("add event from 10:00 am until 11am or 1/2/13 12:00 pm to 13:00 2/3/14");
		assertEquals(actual, expected);
		
		// Crossing am boundary
		expected = new Command(CommandType.ADD);
		expected.setDescription("event");
		intervals = new ArrayList<>();
		start = now.withTime(23, 59, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add event from 11:59 pm");
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
		actual = new Parser().parse("add 'halloween' at 13:00 on 31/10 and also maybe at 2:00pm");
		assertEquals(actual, expected);
		
		// Deadline
		// Date
		expected = new Command(CommandType.ADD);
		expected.setDescription("finish assignment");
		expected.setDeadline(now.withDate(2015, 2, 12).withTime(23, 59, 0, 0));
		actual = new Parser().parse("add finish assignment by 12/2/15");
		assertEquals(actual, expected);
		
		// Time
		expected = new Command(CommandType.ADD);
		expected.setDescription("finish assignment");
		expected.setDeadline(now.withTime(23, 59, 0, 0));
		actual = new Parser().parse("add finish assignment by 23:59");
		assertEquals(actual, expected);

		// Both
		expected = new Command(CommandType.ADD);
		expected.setDescription("finish assignment");
		expected.setDeadline(now.withTime(23, 59, 0, 0).withDate(2013, 10, 10));
		actual = new Parser().parse("add finish assignment by 23:59 10/10/13");
		assertEquals(actual, expected);

		// Deadline and interval
		
		// Floating task
		// Multiple incomplete intervals
		
		// Ordering of date and time don't matter
		// Future date
		expected = new Parser().parse("add go home on 12/2/15 at 3:00");
		actual = new Parser().parse("add go home at 3:00 on 12/2/15");
		assertEquals(actual, expected);

		// Past date
		expected = new Parser().parse("add go home on 12/2/12 at 3:00");
		actual = new Parser().parse("add go home at 3:00 on 12/2/12");
		assertEquals(actual, expected);
		
		// Inferring start date if start date is unspecified and end date is
		expected = new Command(CommandType.ADD);
		expected.setDescription("task");
		intervals = new ArrayList<>();
		start = now.withTime(13, 0, 0, 0).plusDays(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add task 1pm to 2pm today");
		assertEquals(actual, expected);
		
		// Default intervals
		// Date only
		// Past
		expected = new Parser().parse("add go home from 12am on 11/2/12 to 23:59 on 11/2/12");
		actual = new Parser().parse("add go home on 11/2/12");
		assertEquals(actual, expected);
		// Future
		expected = new Parser().parse("add go home from 12am on 11/2/15 to 23:59 on 11/2/15");
		actual = new Parser().parse("add go home on 11/2/15");
		assertEquals(actual, expected);

		// Time only
		expected = new Parser().parse("add go home from 11pm on 5/10/13 to 12am on 6/10/13");
		actual = new Parser().parse("add go home at 11pm");
		assertEquals(actual, expected);

		// Date and time
		// Past
		expected = new Parser().parse("add go home from 11pm on 12/2/12 to 12am on 13/2/12");
		actual = new Parser().parse("add go home at 11pm 12/2/12");
		assertEquals(actual, expected);
		// Future
		expected = new Parser().parse("add go home from 11pm on 12/2/15 to 12am on 12/2/15");
		actual = new Parser().parse("add go home at 11pm 12/2/15");
		assertEquals(actual, expected);

		// Days of the week
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.TUESDAY).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home at 13:00 on tuesday");
		assertEquals(actual, expected);
		
		// this keyword
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.TUESDAY).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home at 13:00 this tuesday");
		assertEquals(actual, expected);
		
		// last keyword
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.TUESDAY).withTime(13, 0, 0, 0).minusWeeks(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home at 13:00 last tuesday");
		assertEquals(actual, expected);
		
		// next keyword
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.MONDAY).withTime(13, 0, 0, 0).plusWeeks(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home at 13:00 next monday");
		assertEquals(actual, expected);

		// Wacky case
		expected = new Command(CommandType.ADD);
		expected.setDescription("gO hOmE");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.THURSDAY).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add gO hOmE aT 13:00 tHiS thursday");
		assertEquals(actual, expected);
		
		// Day of week shorthand
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.THURSDAY).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home at 13:00 this thurs");
		assertEquals(actual, expected);
		
		// Substring of day - does not match word boundary
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home nes");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.WEDNESDAY).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home at 13:00 this wednes");
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
		actual = new Parser().parse("add go home next year");
		assertEquals(actual, expected);
		
		// Week
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusWeeks(1).withTime(0, 0, 0, 0);
		end = start.withTime(23, 59, 0, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home next week");
		assertEquals(actual, expected);
		
		// Month
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusMonths(1).withTime(0, 0, 0, 0);
		end = start.withTime(23, 59, 0, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home next month");
		assertEquals(actual, expected);
		
		// Missing next keyword
		expected = new Command(CommandType.ADD);
		expected.setDescription("month");
		actual = new Parser().parse("add month");
		assertEquals(actual, expected);
		expected = new Command(CommandType.ADD);
		expected.setDescription("week");
		actual = new Parser().parse("add week");
		assertEquals(actual, expected);
		expected = new Command(CommandType.ADD);
		expected.setDescription("year");
		actual = new Parser().parse("add year");
		assertEquals(actual, expected);

		// Fortnight
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusWeeks(2).withTime(0, 0, 0, 0);
		end = start.withTime(23, 59, 0, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home next fortnight");
		assertEquals(actual, expected);
		
		// Treating relative dates as dates
		expected = new Command(CommandType.ADD);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.plusWeeks(2).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add go home next fortnight 1pm");
		assertEquals(actual, expected);

		// Invalid dates (30th Feb, 33th Jan, 31 June, etc.)
		expected = new Command(CommandType.INVALID);
		expected.setInvalidCommandReason(InvalidCommandReason.INVALID_DATE);
		actual = new Parser().parse("add task 30 feb");
		assertEquals(actual, expected);

		expected = new Command(CommandType.INVALID);
		expected.setInvalidCommandReason(InvalidCommandReason.INVALID_DATE);
		actual = new Parser().parse("add task 29 feb"); // year is implicitly 2013
		assertEquals(actual, expected);
		
		expected = new Command(CommandType.INVALID);
		expected.setInvalidCommandReason(InvalidCommandReason.INVALID_DATE);
		actual = new Parser().parse("add task 31 june");
		assertEquals(actual, expected);

		expected = new Command(CommandType.ADD);
		expected.setDescription("task 33 jan");
		actual = new Parser().parse("add task 33 jan");
		assertEquals(actual, expected);

		// Leap year
		expected = new Command(CommandType.ADD);
		expected.setDescription("task");
		intervals = new ArrayList<>();
		start = now.withDate(2012, 2, 29).withTime(0, 0, 0, 0);
		end = start.withTime(23, 59, 0, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("add task 29 feb 12");
		assertEquals(actual, expected);
		
//		expected = new Command(CommandType.INVALID);
//		expected.setInvalidCommandReason(InvalidCommandReason.INVALID_DATE);
//		
//		expected.setDescription("go home");
//		intervals = new ArrayList<>();
//		start = now.plusWeeks(2).withTime(13, 0, 0, 0);
//		end = start.plusHours(1);
//		intervals.add(new Interval(start, end));
//		expected.setIntervals(intervals);
//		actual = new Parser().parse("add go home next fortnight 1pm");
//		assertEquals(actual, expected);
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
		actual = new Parser().parse("edit");
		assertEquals(actual, expected);
		
		// Index only
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		actual = new Parser().parse("edit 1");
		assertEquals(actual, expected);

		// Description only
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		expected.setDescription("hello");
		actual = new Parser().parse("edit 1 hello");
		assertEquals(actual, expected);

		// Interval only
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		intervals = new ArrayList<Interval>();
		start = now.withTime(13, 0, 0, 0).plusDays(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		actual = new Parser().parse("edit 1 1pm");
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
		actual = new Parser().parse("edit 1 1pm today hello");
		assertEquals(actual, expected);

		// Index and timeslot index only
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		expected.setTimeslotIndex(1);
		actual = new Parser().parse("edit 1 1");
		assertEquals(actual, expected);
		
		// Making sure hashtags aren't mistakenly considered indices
		expected = new Command(CommandType.EDIT);
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("#1234");
		expected.setTags(tags);
		expected.setDescription("sup");
		expected.setTaskIndex(1);
		actual = new Parser().parse("edit 1 #1234 sup");
		assertEquals(actual, expected);

		// Empty timeslot
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		expected.setTimeslotIndex(1);
		actual = new Parser().parse("edit 1 1 kajsld");
		assertEquals(actual, expected);

		// Empty timeslot
		expected = new Command(CommandType.EDIT);
		expected.setTaskIndex(1);
		expected.setTimeslotIndex(1);
		actual = new Parser().parse("edit 1 1 kajsld");
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
		actual = new Parser().parse("edit 1 1 kajsld 1pm");
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
		actual = new Parser().parse("edit 1 1 1pm, 3pm");
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
		actual = new Parser().parse("edit 1 1 1pm to 3pm");
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
		actual = new Parser().parse("edit 1 1 1pm to 3pm or 2pm to 4pm");
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
		actual = new Parser().parse("edit 1 1 1pm asda to jashdk 3pm !!! or asdn 2pm asjd to asdasd 4pm");
		assertEquals(actual, expected);
	}

	@Test
	public void deleteCommandTests() {
		Command expected, actual;
		
		// Correct format
		expected = new Command(CommandType.DELETE);
		expected.setTaskIndex(1);
		actual = new Parser().parse("delete 1");
		assertEquals(actual, expected);

		// Missing index
		expected = new Command(CommandType.DELETE);
		actual = new Parser().parse("delete");
		assertEquals(actual, expected);

		// Invalid format
		expected = new Command(CommandType.DELETE);
		actual = new Parser().parse("delete askldjas");
		assertEquals(actual, expected);
	}
	
	@Test
	public void searchCommandTests() {
		Command expected, actual;
		ArrayList<String> tags;
		
		expected = new Command(CommandType.SEARCH);
		actual = new Parser().parse("search");
		assertEquals(expected, actual);
		
		expected = new Command(CommandType.SEARCH);
		expected.setSearchString("hello there 12/2");
		actual = new Parser().parse("search hello there 12/2");
		assertEquals(expected, actual);

		expected = new Command(CommandType.SEARCH);
		expected.setSearchString("hello there #yellow");
		actual = new Parser().parse("search hello there #yellow");
		assertEquals(expected, actual);

		expected = new Command(CommandType.SEARCH);
		tags = new ArrayList<String>();
		tags.add("#yellow");
		tags.add("#blue");
		expected.setTags(tags);
		actual = new Parser().parse("search #yellow #blue whatever");
		assertEquals(expected, actual);
	}
	
	@Test
	public void finaliseCommandTests() {
		Command expected, actual;
		
		// Too few arguments
		expected = new Command(CommandType.FINALISE);
		expected.setTaskIndex(1);
		actual = new Parser().parse("finalise 1");
		assertEquals(actual, expected);

		expected = new Command(CommandType.FINALISE);
		actual = new Parser().parse("finalise");
		assertEquals(actual, expected);

		// Proper format
		expected = new Command(CommandType.FINALISE);
		expected.setTaskIndex(12);
		expected.setTimeslotIndex(13);
		actual = new Parser().parse("finalise 12 13");
		assertEquals(actual, expected);

		// Gibberish at the end is ignored
		expected = new Command(CommandType.FINALISE);
		expected.setTaskIndex(12);
		expected.setTimeslotIndex(13);
		actual = new Parser().parse("finalise 12 13 asdkj");
		assertEquals(actual, expected);

		// Invalid number formats
		expected = new Command(CommandType.FINALISE);
		actual = new Parser().parse("finalise 1askdj 1");
		assertEquals(actual, expected);

		expected = new Command(CommandType.FINALISE);
		expected.setTaskIndex(1);
		actual = new Parser().parse("finalise 1 1askdj");
		assertEquals(actual, expected);
	}
	
	@Test
	public void fuzzyMatchingTests() {
		
//		INVALID,
//		ADD, EDIT, DISPLAY, DELETE, CLEAR, EXIT, GOTO,
//		SORT, SEARCH, UNDO, FINALISE, HELP, DONE, REDO;

//		Command expected, actual;
		
		assertEquals(new Parser().parse("qwe").getCommandType(), CommandType.INVALID);
		assertEquals(new Parser().parse("zxc").getCommandType(), CommandType.INVALID);

		assertEquals(new Parser().parse("ad").getCommandType(), CommandType.ADD);
		assertEquals(new Parser().parse("sad").getCommandType(), CommandType.ADD);
		assertEquals(new Parser().parse("asd").getCommandType(), CommandType.ADD);
		assertEquals(new Parser().parse("ass").getCommandType(), CommandType.ADD);
		assertEquals(new Parser().parse("dad").getCommandType(), CommandType.ADD);
		assertEquals(new Parser().parse("addd").getCommandType(), CommandType.ADD);

		assertEquals(new Parser().parse("del 1").getCommandType(), CommandType.DELETE);
		assertEquals(new Parser().parse("dele 1").getCommandType(), CommandType.DELETE);
		assertEquals(new Parser().parse("delet 1").getCommandType(), CommandType.DELETE);
		assertEquals(new Parser().parse("deleet 1").getCommandType(), CommandType.DELETE);
		assertEquals(new Parser().parse("deeelt 1").getCommandType(), CommandType.DELETE);
		assertEquals(new Parser().parse("deleeete 1").getCommandType(), CommandType.DELETE);

		assertEquals(new Parser().parse("daa").getCommandType(), CommandType.DONE);
		assertEquals(new Parser().parse("de 1").getCommandType(), CommandType.DONE);
		assertEquals(new Parser().parse("dne 1").getCommandType(), CommandType.DONE);
		assertEquals(new Parser().parse("don 1").getCommandType(), CommandType.DONE);
		assertEquals(new Parser().parse("doone 1").getCommandType(), CommandType.DONE);
		assertEquals(new Parser().parse("doon 1").getCommandType(), CommandType.DONE);

	}
}
