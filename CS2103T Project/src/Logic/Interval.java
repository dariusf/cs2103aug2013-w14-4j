package Logic;

public class Interval {
	/**
	 * An interval is defined by two moments.
	 */
	
	public Moment startMoment;
	public Moment endMoment;
	
	public Interval() {
		startMoment = new Moment();
		endMoment = new Moment();
	}
	
	public Interval(Moment from, Moment to) {
		this.startMoment = from;
		this.endMoment = to;
	}

	public Moment getStartMoment() {
		return startMoment;
	}

	public void setStartMoment(Moment startMoment) {
		this.startMoment = startMoment;
	}

	public Moment getEndMoment() {
		return endMoment;
	}

	public void setEndMoment(Moment endMoment) {
		this.endMoment = endMoment;
	}
	
	
}