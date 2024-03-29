package parser;

import java.util.ArrayList;

import org.joda.time.DateTime;

//@author A0097282W
public class StateDeadline implements State {

	private static DateTime nowStub = null; // for testing purposes

	private final Parser parser;
	ArrayList<Token> results = new ArrayList<>();;
	StateDefault parent;
	String token;
	
	public StateDeadline (Parser parser, StateDefault parent, String token) {
		this.parser = parser;
		this.parent = parent;
		this.token = token;
	}
	
	public static void setNowStub(DateTime now) {
		nowStub = now;
	}
	
	@Override
	public void processToken(Token t) {
		assert !popCondition(); // t has to be a DateToken or TimeToken
		results.add(t);
		this.parser.tokens.nextToken();
	}

	@Override
	public boolean popCondition() {
		Token t = this.parser.tokens.getCurrentToken();
		return !(t instanceof DateToken || t instanceof TimeToken);
	}

	@Override
	public void onPop() {
		if (results.size() == 0) {
			parent.words.append(token + " ");
		}
		else {
			DateTime now = nowStub == null ? new DateTime() : nowStub;
			parser.deadline = now.withTime(23, 59, 0, 0);
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