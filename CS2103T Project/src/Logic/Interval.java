package Logic;

public class Interval {
	/**
	 * An interval is defined by two moments.
	 */
	
	public Moment from;
	public Moment to;
	
	public Interval() {
		from = new Moment();
		to = new Moment();
	}
	
	public Interval(Moment from, Moment to) {
		this.from = from;
		this.to = to;
	}
}