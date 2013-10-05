package parser;

import java.util.ArrayList;

class StateUntil implements Parser.State {

	private final Parser parser;
	ArrayList<Token> results = new ArrayList<>();;
	StateDefault parent;

	public StateUntil (Parser parser, StateDefault parent) {
		this.parser = parser;
		this.parent = parent;
	}

	@Override
	public void processToken(Token t) {
		assert !popCondition(); // t has to be a DateToken or TimeToken
		results.add(t);
		this.parser.nextToken();
	}

	@Override
	public boolean popCondition() {
		Token t = this.parser.getCurrentToken();
		return !(t instanceof DateToken || t instanceof TimeToken);
	}

	@Override
	public void onPop() {
		if (results.size() == 0) {
			parent.words.append("until ");
		}
		else {
			for (Token token : results) {
				if (token instanceof DateToken) {
					parent.onAtUntilInterval.changeEndDate((DateToken) token);
				}
				else if (token instanceof TimeToken) {
					parent.onAtUntilInterval.changeEndTime((TimeToken) token);
				}
			}
		}
	}

	@Override
	public void onPush() {
	}
}