package parser;

import java.util.ArrayList;

import org.joda.time.DateTime;

class StateBy implements Parser.State {

	private final Parser parser;
	ArrayList<Token> results = new ArrayList<>();;
	StateDefault parent;
	
	public StateBy (Parser parser, StateDefault parent) {
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
			parent.words.append("by ");
		}
		else {
			parser.deadline = new DateTime();
			for (Token token : results) {
				if (token instanceof DateToken) {
					parser.deadline = ((DateToken) token).mergeInto(parser.deadline);
				}
				else {
					parser.deadline = ((TimeToken) token).mergeInto(parser.deadline);
				}
			}
		}
	}
	
	@Override
	public void onPush() {
	}
}