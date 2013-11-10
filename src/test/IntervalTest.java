package test;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;

import common.Interval;

import parser.DateToken;
import parser.TimeToken;

public class IntervalTest {

	@Test
	public void test() {
		Interval.setNowStub(new DateTime().withDate(2013, 10, 5).withTime(19, 0, 0, 0));
		
		// Start date
		Interval interval = new Interval();
		interval.changeStartDate(new DateToken("11/2/13"));
		assertEquals(interval.toString(), "11/2/13 12:00 AM to 11/2/13 11:59 PM");
		
		// Start time (after)
		interval = new Interval();
		interval.changeStartTime(new TimeToken("10:00pm"));
		assertEquals(interval.toString(), "5/10/13 10:00 PM to 5/10/13 11:00 PM");
		
		// Start time (before)
		interval = new Interval();
		interval.changeStartTime(new TimeToken("1:00pm"));
		assertEquals(interval.toString(), "6/10/13 1:00 PM to 6/10/13 2:00 PM");

		// Start time, then date
		interval = new Interval();
		interval.changeStartTime(new TimeToken("1:00pm"));
		interval.changeStartDate(new DateToken("1/2/13"));
		assertEquals(interval.toString(), "1/2/13 1:00 PM to 6/10/13 2:00 PM");

		// Start date, then time
		interval = new Interval();
		interval.changeStartDate(new DateToken("1/2/13"));
		interval.changeStartTime(new TimeToken("1:00pm"));
		assertEquals(interval.toString(), "1/2/13 1:00 PM to 1/2/13 2:00 PM");

		// Interval, then end time
		interval = new Interval();
		interval.changeStartTime(new TimeToken("1:00pm"));
		interval.changeStartDate(new DateToken("1/2/13"));
		interval.changeEndTime(new TimeToken("3:00pm"));
		assertEquals(interval.toString(), "1/2/13 1:00 PM to 6/10/13 3:00 PM");

		// Interval, then end date
		interval = new Interval();
		interval.changeStartTime(new TimeToken("1:00pm"));
		interval.changeStartDate(new DateToken("1/2/13"));
		interval.changeEndDate(new DateToken("5/2/13"));
		assertEquals(interval.toString(), "1/2/13 1:00 PM to 5/2/13 2:00 PM");

		// End date earlier than start
		interval = new Interval();
		interval.changeStartTime(new TimeToken("1:00pm"));
		interval.changeStartDate(new DateToken("1/2/13"));
		interval.changeEndDate(new DateToken("5/1/13"));
		assertEquals(interval.toString(), "1/2/13 1:00 PM to 1/2/13 2:00 PM");

		// 2 start times
		interval = new Interval();
		interval.changeStartTime(new TimeToken("1:00pm"));
		interval.changeStartTime(new TimeToken("5:00pm"));
		assertEquals(interval.toString(), "6/10/13 5:00 PM to 6/10/13 6:00 PM");
	}

}
