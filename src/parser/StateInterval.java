package parser;

import java.util.ArrayList;

import logic.Interval;

class StateInterval implements Parser.State {

	private final Parser parser;
	StateDefault parent;
	private boolean foundDelimiter = false;
	private ArrayList<Token> from = new ArrayList<>();
	private ArrayList<Token> to = new ArrayList<>();

	public StateInterval(Parser parser, StateDefault parent) {
		this.parser = parser;
		this.parent = parent;
	}
	
	@Override
	public void processToken(Token t) {
		assert !popCondition(); // t is either a TimeToken, a DateToken,
								// or an interval-delimiting WordToken,
								// or a WordToken containing 'or'

		if (t instanceof TimeToken || t instanceof DateToken) {
			if (!foundDelimiter) {
				from.add(t);
			}
			else {
				to.add(t);
			}
			this.parser.nextToken();
		}
		else if (t instanceof WordToken) {
			if (t.contents.equalsIgnoreCase("or")) {
				finaliseInterval();
				foundDelimiter = false;
			}
			else {
				assert tokenIsIntervalDelimiter(t);
				foundDelimiter = true;
			}
			this.parser.nextToken();
		}
	}

	@Override
	public boolean popCondition() {
		Token token = this.parser.getCurrentToken();
		
		return !(token instanceof DateToken
				|| token instanceof TimeToken
				|| (token instanceof WordToken && tokenIsIntervalDelimiter(token))
				|| (token instanceof WordToken && token.contents.equalsIgnoreCase("or")));
	}
		
	private boolean tokenIsIntervalDelimiter(Token token) {
		String contents = token.contents;
		return contents.equalsIgnoreCase("to") || contents.equalsIgnoreCase("till") || contents.equalsIgnoreCase("until");
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
		to.clear();
		from.clear();
		parser.intervals.add(newInterval);
	}

	@Override
	public void onPush() {
	}
	
}