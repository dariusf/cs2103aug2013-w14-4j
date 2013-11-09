package parser;

import java.util.ArrayList;

import logic.Interval;

//@author A0097282W
class StateEditInterval implements State {

	private final Parser parser;
	private boolean foundDelimiter = false;
	private ArrayList<Token> from = new ArrayList<>();
	private ArrayList<Token> to = new ArrayList<>();

	public StateEditInterval(Parser parser) {
		this.parser = parser;
	}
	
	@Override
	public void processToken(Token t) {
		
		if (t instanceof TimeToken || t instanceof DateToken) {
			if (!foundDelimiter) {
				from.add(t);
			}
			else {
				to.add(t);
			}
		}
		else if (t instanceof WordToken) {
			if (t.contents.equalsIgnoreCase("or")) {
				finaliseInterval();
				foundDelimiter = false;
			}
			else if (StateInterval.tokenIsIntervalDelimiter(t)) {
				foundDelimiter = true;
			}
			else {
				// Ignore the current token
			}
		}
		else {
			// Ignore the current token
		}
		this.parser.nextToken();
	}

	@Override
	public boolean popCondition() {
		// This is meant to be a top-level state and never pops
		return false;
	}

	@Override
	public void onPop() {
		finaliseInterval();
	}
	
	private void finaliseInterval() {
		Interval newInterval = new Interval();
		for (Token token : from) {
			if (token instanceof DateToken) {
				newInterval.changeStartDate((DateToken) token);
			}
			else if (token instanceof TimeToken) {
				newInterval.changeStartTime((TimeToken) token);
			}
		}
		for (Token token : to) {
			if (token instanceof DateToken) {
				newInterval.changeEndDate((DateToken) token);
			}
			else if (token instanceof TimeToken) {
				newInterval.changeEndTime((TimeToken) token);
			}
		}
		
		boolean intervalValid = from.size() > 0 || to.size() > 0;
		
		if (intervalValid) {
			parser.intervals.add(newInterval);
			to.clear();
			from.clear();
		}
	}

	@Override
	public void onPush() {
	}
	
}