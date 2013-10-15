package parser;

import java.util.ArrayList;

import org.joda.time.DateTime;

class StateDeadline implements Parser.State {

	private final Parser parser;
	ArrayList<Token> results = new ArrayList<>();;
	StateDefault parent;
	String token;
	
	public StateDeadline (Parser parser, StateDefault parent, String token) {
		this.parser = parser;
		this.parent = parent;
		this.token = token;
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
			parent.words.append(token + " ");
		}
		else {
			parser.deadline = new DateTime().withTime(23, 59, 0, 0);
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