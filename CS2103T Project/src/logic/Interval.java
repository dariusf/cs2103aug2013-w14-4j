package logic;

import org.joda.time.DateTime;
import parser.DateToken;
import parser.TimeToken;

public class Interval {
	/**
	 * An interval is defined by two dates.
	 */
	
	private static DateTime nowStub = null; // for testing purposes

	private DateTime start = null;
	private DateTime end = null;
	
	
	public Interval() {
	}

	public Interval(DateTime start) {
		this.start = start;
		this.end = start.plusHours(1);
	}
	
	public Interval(DateTime start, DateTime end) {
		this.start = start;
		this.end = end;
	}
	
	public static void setNowStub(DateTime now) {
		Interval.nowStub = now;
	}

	/**
	 * The start functions incrementally build up an one half
	 * of the interval. They also set a tentative end. They
	 * must be called before an end function can be called.
	 * @param startToken
	 */
	public void setStartDate(DateToken startToken) {
		DateTime now = new DateTime();
		if (Interval.nowStub != null) now = Interval.nowStub;
		
		if (this.start == null) {
			DateTime start = startToken.mergeInto(now);
			start = start.withTime(0, 0, 0, 0);
			end = start.withTime(23, 59, 0, 0);
			this.start = start;
		}
		else {
			// a time has already been set
			DateTime start = startToken.mergeInto(this.start);
			this.start = start;
			if (this.end.isBefore(this.start)) {
				this.end = this.start.plusHours(1);
			}
		}
	}
	
	public void setStartTime(TimeToken startToken) {
		DateTime now = new DateTime();
		if (Interval.nowStub != null) now = Interval.nowStub;
		
		if (start == null) {
			DateTime start = startToken.mergeInto(now);
			
			if (start.isBefore(now)) {
				start = start.plusDays(1);
			}
			
			this.start = start;
			end = start.plusHours(1);
		}
		else {
			// a date has already been set
			DateTime start = startToken.mergeInto(this.start);
			this.start = start;
			end = start.plusHours(1);
		}
	}
	
	public void setEndDate(DateToken startToken) {
		if (this.start == null) throw new IllegalArgumentException("Start date has to be set before setEndDate can be called");
		
		this.end = startToken.mergeInto(this.end);
		
		if (this.end.isBefore(this.start)) {
			this.end = this.start.plusHours(1);
		}
	}
	
	public void setEndTime(TimeToken startToken) {
		if (this.start == null) throw new IllegalArgumentException("Start date has to be set before setEndTime can be called");

		this.end = startToken.mergeInto(this.end);
		
		if (this.end.isBefore(this.start)) {
			this.end = this.start.plusHours(1);
		}
	}
	
	public DateTime getStart() {
		return start;
	}

	public void setStart(DateTime start) {
		if (end == null || start.isAfter(end)) {
			end = start.plusHours(1);
		}
		else {
			// TODO replace with exceptions
			assert start.isBefore(end) : "The start of an interval cannot occur after its end date!";
		}
		this.start = start;
	}

	public DateTime getEnd() {
		return end;
	}

	public void setEnd(DateTime end) {
		assert start != null : "Can't have end date without start date!";
		assert end.isAfter(start) : "The end of an interval cannot occur before its start date!";
		this.end = end;
	}
	
	public boolean isSet() {
		return start != null;
	}
	
	public boolean hasEnd() {
		return end != null;
	}
	
	public String toString() {
		return start.toString(Constants.DATE_TIME_FORMAT) + " to " + end.toString(Constants.DATE_TIME_FORMAT);
	}
	
	public void normalizeEnd() {
		assert start != null : "Cannot normalize end of interval without start date";
		end = start.plusHours(1);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Interval other = (Interval) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}
	
	
	
}