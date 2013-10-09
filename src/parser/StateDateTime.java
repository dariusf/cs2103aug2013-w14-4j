package parser;

import java.util.ArrayList;

import logic.Interval;

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
		assert !popCondition(); // until 'to' is found, t can be anything except a TagToken,
								// but it has to be a DateToken or TimeToken after
		
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
		return t instanceof TagToken || foundTo && from.size() > 0 && to.size() > 0
				&& !(t instanceof TimeToken || t instanceof DateToken);
	}

	@Override
	public void onPop() {
		Interval that = new Interval();
		for (Token token : from) {
			if (token instanceof DateToken) {
				that.changeStartDate((DateToken) token);
			}
			else if (token instanceof TimeToken) {
				that.changeStartTime((TimeToken) token);
			}
		}
		for (Token token : to) {
			if (token instanceof DateToken) {
				that.changeEndDate((DateToken) token);
			}
			else if (token instanceof TimeToken) {
				that.changeEndTime((TimeToken) token);
			}
		}
		parser.intervals.add(that);
		parent.added = from.size() > 0 || to.size() > 0;
	}

	@Override
	public void onPush() {
	}
	
}