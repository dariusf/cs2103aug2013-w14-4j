package parser;

import java.util.ArrayList;

class StateOn implements Parser.State {

	private final Parser parser;
	ArrayList<DateToken> results = new ArrayList<>();;
	StateDefault parent;

	public StateOn (Parser parser, StateDefault parent) {
		this.parser = parser;
		this.parent = parent;
	}

	@Override
	public void processToken(Token t) {
		assert !popCondition(); // t must be a DateToken
		results.add((DateToken) t);
		this.parser.nextToken();
	}

	@Override
	public boolean popCondition() {
		Token t = this.parser.getCurrentToken();
		return !(t instanceof DateToken);
	}

	@Override
	public void onPop() {
		if (results.size() == 0) {
			parent.words.append("on ");
		}
		else {
			for (DateToken token : results) {
				parent.onAtUntilInterval.changeStartDate(token);
			}
		}
	}

	@Override
	public void onPush() {
	}
}