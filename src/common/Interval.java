package common;


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

	private boolean endExplicitlySet = false;
	private boolean startDateExplicitlySet = false;

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
	
	public Interval(Interval interval){
		this.start = new DateTime(interval.start);
		this.end = new DateTime(interval.end);
		this.endExplicitlySet = interval.endExplicitlySet;
		this.startDateExplicitlySet = interval.startDateExplicitlySet;	
	}
	
	public static void setNowStub(DateTime now) {
		Interval.nowStub = now;
	}
	
	public boolean isSet() {
		return start != null;
	}

	/**
	 * The start functions incrementally build up an one half
	 * of the interval. They also set a tentative end. They
	 * must be called before an end function can be called.
	 * 
	 * The idea is that changeStartDate and changeStartTime
	 * can be called in any order, and will produce equivalent
	 * results based on the latest calls.
	 * 
	 * @param startToken
	 */
	public void changeStartDate(DateToken startToken) {
		DateTime now = new DateTime();
		if (Interval.nowStub != null) now = Interval.nowStub;
		
		startDateExplicitlySet = true;
		
		if (this.start == null) {
			DateTime start = startToken.mergeInto(now).withTime(0, 0, 0, 0);

			// not leaping forward a year here; date should be taken as is
			
			this.start = start;
			end = start.withTime(23, 59, 0, 0);
		}
		else {
			// a time has already been set
			DateTime start = startToken.mergeInto(this.start);
			this.start = start;
			if (end.isBefore(this.start)) {
				end = this.start.plusHours(1);
			}
			else {
				end = startToken.mergeInto(end);
				if (end.isBefore(start)) {
					end = start.plusHours(1);
				}
			}
		}
	}
	
	public void changeStartTime(TimeToken startToken) {
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

	public void changeEndDate(DateToken startToken) {
		if (this.start == null) throw new IllegalArgumentException("Start date has to be set before setEndDate can be called");
		
		this.end = startToken.mergeInto(this.end);
		
		if (!endExplicitlySet) {
			this.end = this.end.withTime(23, 59, 0, 0);
			endExplicitlySet = true;
		}
		
		if (this.end.isBefore(this.start)) {
			this.end = this.start.plusHours(1);
		}
		
		if (!startDateExplicitlySet) {
			this.start = this.start.withDate(this.end.getYear(), this.end.getMonthOfYear(), this.end.getDayOfMonth());
		}
	}
	
	public void changeEndTime(TimeToken startToken) {
		if (this.start == null) throw new IllegalArgumentException("Start date has to be set before setEndTime can be called");

		this.end = startToken.mergeInto(this.end);
		endExplicitlySet = true;
		
		if (this.end.isBefore(this.start)) {
			this.end = this.start.plusHours(1);
		}
	}
	
	/**
	 * The following getters and setters do exactly what they say they do.
	 * No checks on whether start < end, and so on.
	 */
	
	public DateTime getStartDateTime() {
		return start;
	}
	public void setStartDateTime(DateTime start) {
		this.start = start;
	}
	public DateTime getEndDateTime() {
		return end;
	}
	public void setEndDateTime(DateTime end) {
		this.end = end;
	}
		
	public String toString() {
		return start.toString(Constants.DATE_TIME_FORMAT) + " to " + end.toString(Constants.DATE_TIME_FORMAT);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + (endExplicitlySet ? 1231 : 1237);
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
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