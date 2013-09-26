package Parser;

import java.util.ArrayList;

import Logic.Moment;

class StateBy implements Parser.State {

	private final Parser parser;
	ArrayList<Token> results;
	StateDefault parent;
	
	public StateBy (Parser parser, StateDefault parent) {
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
			parent.words.append("by ");
		}
		else {
//			System.out.println("By:");
			parser.deadline = new Moment();
			for (Token token : results) {
//				System.out.println(token.toString());
				parser.deadline.add(token);
			}
		}
	}

	@Override
	public void onPush() {
		results = new ArrayList<>();
	}
}