package Logic;

import org.joda.time.DateTime;

public class Interval {
	/**
	 * An interval is defined by two dates.
	 */
	
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

	public DateTime getStart() {
		return start;
	}

	public void setStart(DateTime start) {
		if (end == null || start.isAfter(end)) {
			end = start.plusHours(1);
		}
		else {
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
	
	public boolean hasStart() {
		return start != null;
	}
	
	public boolean hasEnd() {
		return end != null;
	}
	
	public String toString() {
		return start.toString("dd/MM/yy hh:mm a") + " to " + end.toString("dd/MM/yy hh:mm a");
	}
	
	public void normalizeEnd() {
		assert start != null : "Cannot normalize end of interval without start date";
		end = start.plusHours(1);
	}
	
}