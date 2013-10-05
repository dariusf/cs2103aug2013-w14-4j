package parser;

class StateInterval implements Parser.State {

	private final Parser parser;
	StateDefault parent;
	
	public StateInterval(Parser parser, StateDefault parent) {
		this.parser = parser;
		this.parent = parent;
	}
	
	@Override
	public void processToken(Token t) {
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
		return (token instanceof WordToken && !token.contents.equals("or")) || !this.parser.hasTokensLeft();
	}

	@Override
	public void onPop() {
	}

	@Override
	public void onPush() {
	}
	
}