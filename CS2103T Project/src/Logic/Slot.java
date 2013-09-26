package Logic;

import java.util.Date;

public class Slot {
	private Date startTime = null;
	private Date endTime = null;

	public Slot(Date start, Date end) {
		startTime = start;
		endTime = end;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setStartTime(Date start) {
		startTime = start;
	}

	public void setEndTime(Date end) {
		endTime = end;
	}

	public String toString() {
		return startTime.toString() + " " + endTime.toString();
	}
}