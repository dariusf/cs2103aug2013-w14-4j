package parser;

class StateInterval implements Parser.State {

	private final Parser parser;
	StateDefault parent;
	boolean added = false; // Will be set to true by a child state, if it succeeds

	public StateInterval(Parser parser, StateDefault parent) {
		this.parser = parser;
		this.parent = parent;
	}
	
	@Override
	public void processToken(Token t) {
		assert !popCondition(); // t is anything but a WordToken with contents 'or'
		
		Token token = this.parser.getCurrentToken();
		if (token instanceof TimeToken || token instanceof DateToken) {
			this.parser.pushState(new StateDateTime(this.parser, this));
		}
		else {
			this.parser.nextToken();
		}
	}

	@Override
	public boolean popCondition() {
		Token token = this.parser.getCurrentToken();
		return (token instanceof WordToken && !token.contents.equals("or"));
	}
	
	@Override
	public void onPop() {
		if (!added) {
			parent.words.append("from ");
		}
	}

	@Override
	public void onPush() {
	}
	
}