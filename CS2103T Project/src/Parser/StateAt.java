package Parser;

import java.util.ArrayList;

class StateAt implements Parser.State {

	private final Parser parser;
	ArrayList<Token> results;
	StateDefault parent;

	public StateAt (Parser parser, StateDefault parent) {
		this.parser = parser;
		this.parent = parent;
	}

	@Override
	public void processToken(Token t) {
		assert !popCondition();
		results.add(t);
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
//			System.out.println("At:");
			for (Token token : results) {
//				System.out.println(token.toString());
				parent.tenuous.from.add(token);
			}
		}
	}

	@Override
	public void onPush() {
		results = new ArrayList<>();
	}
}