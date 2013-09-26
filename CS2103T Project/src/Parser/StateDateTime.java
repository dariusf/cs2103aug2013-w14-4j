package Parser;

import java.util.ArrayList;

import Logic.Interval;
import Parser.Parser.State;

class StateDateTime implements Parser.State {

	private final Parser parser;
	StateInterval parent;
	boolean foundTo = false;
	ArrayList<Token> from = new ArrayList<>();
	ArrayList<Token> to = new ArrayList<>();
	
	public StateDateTime (Parser parser, StateInterval parent) {
		this.parser = parser;
		this.parent = parent;
	}
	
	@Override
	public void processToken(Token t) {
		if (t instanceof TimeToken || t instanceof DateToken) {
			if (!foundTo) {
				from.add(t);
			}
			else {
				to.add(t);
			}
			this.parser.nextToken();
		}
		else if (t instanceof WordToken) {
			if (t.contents.equals("to")) {
				foundTo = true;
			}
			this.parser.nextToken();
		}
	}

	@Override
	public boolean popCondition() {
		Token t = this.parser.getCurrentToken();
		return foundTo && from.size() > 0 && to.size() > 0
				&& !(t instanceof TimeToken || t instanceof DateToken);
	}

	@Override
	public void onPop() {
		Interval that = new Interval();
//		System.out.println("From:");
		for (Token token : from) {
			that.from.add(token);
//			System.out.println(token.toString());
		}
//		System.out.println("To:");
		for (Token token : to) {
			that.to.add(token);
//			System.out.println(token.toString());
		}
		parser.intervals.add(that);
	}

	@Override
	public void onPush() {
		// TODO Auto-generated method stub
		
	}
	
}