package parser;

import java.util.ArrayList;

class StateUntil implements Parser.State {

	private final Parser parser;
	ArrayList<Token> results;
	StateDefault parent;

	public StateUntil (Parser parser, StateDefault parent) {
		this.parser = parser;
		this.parent = parent;
	}

	@Override
	public void processToken(Token t) {
		assert !popCondition();
		// t has to be date or time
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
//			System.out.println("Until:");
			for (Token token : results) {
//				System.out.println(token.toString());
				if (token instanceof DateToken) {
//					parent.onAtUntilInterval.setEnd(((DateToken) token).toDateTime(false));
					parent.onAtUntilInterval.setEndDate((DateToken) token);
				}
				else if (token instanceof TimeToken) {
					parent.onAtUntilInterval.setEndTime((TimeToken) token);
//					parent.onAtUntilInterval.setEnd(((TimeToken) token).toDateTime());
				}
			}
		}
	}

	@Override
	public void onPush() {
		results = new ArrayList<>();
	}
}