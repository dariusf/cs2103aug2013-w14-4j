package parser;

import java.util.ArrayList;

import logic.Interval;

import org.joda.time.DateTime;

import parser.Parser.State;


class StateDateTime implements Parser.State {

	private final Parser parser;
	StateInterval parent;
	boolean foundTo = false;
	ArrayList<Token> from = new ArrayList<>();
	ArrayList<Token> to = new ArrayList<>();
	
	public StateDateTime (Parser parser, StateInterval parent) {
		this.parser = parser;
		this.parent = parent;
	}
	
	@Override
	public void processToken(Token t) {
		if (t instanceof TimeToken || t instanceof DateToken) {
			if (!foundTo) {
				from.add(t);
			}
			else {
				to.add(t);
			}
			this.parser.nextToken();
		}
		else if (t instanceof WordToken) {
			if (t.contents.equals("to")) {
				foundTo = true;
			}
			this.parser.nextToken();
		}
	}

	@Override
	public boolean popCondition() {
		Token t = this.parser.getCurrentToken();
		return foundTo && from.size() > 0 && to.size() > 0
				&& !(t instanceof TimeToken || t instanceof DateToken);
	}

	@Override
	public void onPop() {
		Interval that = new Interval();
//		System.out.println("From:");
		for (Token token : from) {
			if (token instanceof DateToken) {
				if (that.getStart() == null) {
					that.setStart(((DateToken) token).toDateTime());
				}
				else {
					DateTime d = ((DateToken) token).toDateTime();
					that.setStart(that.getStart().withDate(d.getYear(), d.getMonthOfYear(), d.getDayOfMonth()));
				}
			}
			else if (token instanceof TimeToken) {
				
				if (that.getStart() == null) {
					that.setStart(((TimeToken) token).toDateTime());
				}
				else {
					DateTime t = ((TimeToken) token).toDateTime();
					that.setStart(that.getStart().withTime(t.getHourOfDay(), t.getMinuteOfHour(), 0, 0));
				}
			}
//			System.out.println(token.toString());
		}
//		System.out.println("To:");
		for (Token token : to) {
			if (token instanceof DateToken) {
				if (that.getEnd() == null) {
					that.setEnd(((DateToken) token).toDateTime());
				}
				else {
					DateTime d = ((DateToken) token).toDateTime();
					that.setEnd(that.getEnd().withDate(d.getYear(), d.getMonthOfYear(), d.getDayOfMonth()));
				}
			}
			else if (token instanceof TimeToken) {
				
				if (that.getEnd() == null) {
					that.setEnd(((TimeToken) token).toDateTime());
				}
				else {
					DateTime t = ((TimeToken) token).toDateTime();
					that.setEnd(that.getEnd().withTime(t.getHourOfDay(), t.getMinuteOfHour(), 0, 0));
				}
			}
//			System.out.println(token.toString());
		}
		parser.intervals.add(that);
	}

	@Override
	public void onPush() {
		// TODO Auto-generated method stub
		
	}
	
}