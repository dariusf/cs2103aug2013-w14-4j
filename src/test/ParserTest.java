
package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.Test;

import common.CommandType;

import logic.Command;
import logic.Interval;
import parser.DateToken;
import parser.Parser;
import parser.StateDeadline;

public class ParserTest {

//	public void assertEquals(Object one, Object two) {}

	@Test
	public void addCommandTests() {

		DateTime now = new DateTime(2013, 10, 5, 20, 0); // saturday
		Interval.setNowStub(now);
		DateToken.setNowStub(now);
		StateDeadline.setNowStub(now);
		
		Command expected, gotten;
		ArrayList<Interval> intervals;
		DateTime start, end;
		
		// Invalid commands
		// Empty string
		expected = new Command(CommandType.INVALID);
		gotten = new Parser().parse("");
		assertEquals(gotten, expected);

		// Null string
		gotten = new Parser().parse(null);
		assertEquals(gotten, expected);

		// Plain invalid commands
		// Gibberish
		gotten = new Parser().parse("kasdkajsklad aklsjdkals kajsld klajsd");
		assertEquals(gotten, expected);
		// Invalid starting keyword
		gotten = new Parser().parse("hjkhjs task at 10:00 pm");
		assertEquals(gotten, expected);
		// Missing add keyword
		gotten = new Parser().parse("task at 10:00 pm");
		assertEquals(gotten, expected);

		// Correct format
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withTime(22, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home at 10:00 pm");
		assertEquals(gotten, expected);

		// No qualifier
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withTime(22, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home 10:00 pm");
		assertEquals(gotten, expected);
		
		// 24h format
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withTime(22, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home 22:00");
		assertEquals(gotten, expected);

		// 12h shorthand 
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withTime(22, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home 10pm");
		assertEquals(gotten, expected);
		
		// Invalid 12-hour format
		// Loose spaces
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home pm");
		intervals = new ArrayList<>();
		start = now.plusDays(1).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("  add  go  home   at  13:00 pm  ");
		assertEquals(gotten, expected);
		
		// Quotes
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("'  go home at 10:00 pm'");
		gotten = new Parser().parse("add'  go home at 10:00 pm'");
		assertEquals(gotten, expected);
		
		// Multiple quotes
		// Missing add keyword
		expected = new Command(CommandType.INVALID);
		gotten = new Parser().parse("''add task at ''10 pm");
		assertEquals(gotten, expected);
		// Valid
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("'task at '");
		intervals = new ArrayList<>();
		start = now.withTime(22, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add''task at ''10 pm");
		assertEquals(gotten, expected);
		expected.setDescription("'what a ' task weird at hello");
		gotten = new Parser().parse("add 'what a 'task weird at ' hello 10 pm");
		assertEquals(gotten, expected);

		// Typo in pm
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home p");
		intervals = new ArrayList<>();
		start = now.plusDays(1).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home at 13:00 p");
		assertEquals(gotten, expected);

		// Symbols
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home yeah");
		intervals = new ArrayList<>();
		start = now.plusDays(1).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home at 13:00 yeah!");
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
		start = new DateTime(2011, 10, 12, 12, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home at 12:00 on 12/10/11");
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
		
		// Mixed date
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = new DateTime(2015, 2, 12, 12, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home at 12:00 on 12 february 15");
		assertEquals(gotten, expected);
		
		// Mixed date (short)
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = new DateTime(2013, 2, 12, 12, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home on 12 feb at 12:00");
		assertEquals(gotten, expected);
		
		// Date aliases
		// today
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("event");
		intervals = new ArrayList<>();
		start = now.withTime(0, 0, 0, 0);
		end = now.withTime(23, 59, 0, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add event today");
		assertEquals(gotten, expected);
		
		// tomorrow
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withTime(0, 0, 0, 0).plusDays(1);
		end = now.withTime(23, 59, 0, 0).plusDays(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home tomorrow");
		assertEquals(gotten, expected);

		// Alias
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home for a scare");
		intervals = new ArrayList<>();
		start = now.withMonthOfYear(10).withDayOfMonth(31).withTime(0, 0, 0, 0);
		end = now.withMonthOfYear(10).withDayOfMonth(31).withTime(23, 59, 0, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home on halloween for a scare");
		assertEquals(gotten, expected);

		// Specifying dates and times in the middle
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("do schoolwork in school");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.MONDAY).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add do schoolwork at 13:00 on monday in school");
		assertEquals(gotten, expected);
		
		// Like the above, but with a trailing or keyword
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("do schoolwork or die");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.MONDAY).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add do schoolwork at 13:00 on monday or die");
		assertEquals(gotten, expected);

		// Like the above, but with a trailing delimiter keyword
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go to school to do homework");
		intervals = new ArrayList<>();
		start = now.plusDays(1).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go to school at 1pm to do homework");
		assertEquals(gotten, expected);

		// From, until, till, to
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("event");
		intervals = new ArrayList<>();
		start = now.withTime(10, 0, 0, 0).plusDays(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add event from 10:00 am until 11am");
		assertEquals(gotten, expected);
		gotten = new Parser().parse("add event at 10:00 am till 11am");
		assertEquals(gotten, expected);
		gotten = new Parser().parse("add event from 10:00 am to 11am");
		assertEquals(gotten, expected);

		// Wrong qualifier
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("event on");
		intervals = new ArrayList<>();
		start = now.withTime(10, 0, 0, 0).plusDays(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add event on 10:00 am");
		assertEquals(gotten, expected);

		// Interval literal
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("event");
		intervals = new ArrayList<>();
		start = now.withTime(10, 0, 0, 0).plusDays(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		start = new DateTime(2013, 2, 1, 12, 0);
		end = new DateTime(2014, 3, 2, 13, 0);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add event from 10:00 am until 11am or 1/2/13 12:00 pm to 13:00 2/3/14");
		assertEquals(gotten, expected);
		
		// Crossing am boundary
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("event");
		intervals = new ArrayList<>();
		start = now.withTime(23, 59, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add event from 11:59 pm");
		assertEquals(gotten, expected);

		// Flexible interval
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("'halloween' and also maybe");
		intervals = new ArrayList<>();
		start = now.withDayOfMonth(31).withMonthOfYear(10).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		start = now.plusDays(1).withTime(14, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add 'halloween' at 13:00 on 31/10 and also maybe at 2:00pm");
		assertEquals(gotten, expected);
		
		// Deadline
		// Date
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("finish assignment");
		expected.setDeadline(now.withDate(2015, 2, 12).withTime(23, 59, 0, 0));
		gotten = new Parser().parse("add finish assignment by 12/2/15");
		assertEquals(gotten, expected);
		
		// Time
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("finish assignment");
		expected.setDeadline(now.withTime(23, 59, 0, 0));
		gotten = new Parser().parse("add finish assignment by 23:59");
		assertEquals(gotten, expected);

		// Both
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("finish assignment");
		expected.setDeadline(now.withTime(23, 59, 0, 0).withDate(2013, 10, 10));
		gotten = new Parser().parse("add finish assignment by 23:59 10/10/13");
		assertEquals(gotten, expected);

		// Deadline and interval
		
		// Floating task
		// Multiple incomplete intervals
		
		// Ordering of date and time don't matter
		// Future date
		expected = new Parser().parse("add go home on 12/2/15 at 3:00");
		gotten = new Parser().parse("add go home at 3:00 on 12/2/15");
		assertEquals(gotten, expected);

		// Past date
		expected = new Parser().parse("add go home on 12/2/12 at 3:00");
		gotten = new Parser().parse("add go home at 3:00 on 12/2/12");
		assertEquals(gotten, expected);
		
		// Default intervals
		// Date only
		// Past
		expected = new Parser().parse("add go home from 12am on 11/2/12 to 23:59 on 11/2/12");
		gotten = new Parser().parse("add go home on 11/2/12");
		assertEquals(gotten, expected);
		// Future
		expected = new Parser().parse("add go home from 12am on 11/2/15 to 23:59 on 11/2/15");
		gotten = new Parser().parse("add go home on 11/2/15");
		assertEquals(gotten, expected);

		// Time only
		expected = new Parser().parse("add go home from 11pm on 5/10/13 to 12am on 6/10/13");
		gotten = new Parser().parse("add go home at 11pm");
		assertEquals(gotten, expected);

		// Date and time
		// Past
		expected = new Parser().parse("add go home from 11pm on 12/2/12 to 12am on 13/2/12");
		gotten = new Parser().parse("add go home at 11pm 12/2/12");
		assertEquals(gotten, expected);
		// Future
		expected = new Parser().parse("add go home from 11pm on 12/2/15 to 12am on 12/2/15");
		gotten = new Parser().parse("add go home at 11pm 12/2/15");
		assertEquals(gotten, expected);

		// Days of the week
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.TUESDAY).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home at 13:00 on tuesday");
		assertEquals(gotten, expected);
		
		// this keyword
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.TUESDAY).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home at 13:00 this tuesday");
		assertEquals(gotten, expected);
		
		// last keyword
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.TUESDAY).withTime(13, 0, 0, 0).minusWeeks(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home at 13:00 last tuesday");
		assertEquals(gotten, expected);
		
		// next keyword
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.MONDAY).withTime(13, 0, 0, 0).plusWeeks(1);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home at 13:00 next monday");
		assertEquals(gotten, expected);

		// Wacky case
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("gO hOmE");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.THURSDAY).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add gO hOmE aT 13:00 tHiS thursday");
		assertEquals(gotten, expected);
		
		// Day of week shorthand
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.THURSDAY).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home at 13:00 this thurs");
		assertEquals(gotten, expected);
		
		// Substring of day - does not match word boundary
		expected = new Command(CommandType.ADD_TASK);
		expected.setDescription("go home nes");
		intervals = new ArrayList<>();
		start = now.withDayOfWeek(DateTimeConstants.WEDNESDAY).withTime(13, 0, 0, 0);
		end = start.plusHours(1);
		intervals.add(new Interval(start, end));
		expected.setIntervals(intervals);
		gotten = new Parser().parse("add go home at 13:00 this wednes");
		assertEquals(gotten, expected);
	}

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
}