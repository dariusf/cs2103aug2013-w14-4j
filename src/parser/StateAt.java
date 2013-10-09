package parser;

import java.util.ArrayList;

class StateAt implements Parser.State {

	private final Parser parser;
	ArrayList<TimeToken> results = new ArrayList<>();
	StateDefault parent;

	public StateAt (Parser parser, StateDefault parent) {
		this.parser = parser;
		this.parent = parent;
	}

	@Override
	public void processToken(Token t) {
		assert !popCondition(); // t has to be a TimeToken
		results.add((TimeToken) t);
		this.parser.nextToken();
	}

	@Override
	public boolean popCondition() {
		Token t = this.parser.getCurrentToken();
		return !(t instanceof TimeToken);
	}

	@Override
	public void onPop() {
		if (results.size() == 0) {
			parent.words.append("at ");
		}
		else {
			for (TimeToken token : results) {
				parent.onAtUntilInterval.changeStartTime(token);
			}
		}
	}

	@Override
	public void onPush() {
	}
}