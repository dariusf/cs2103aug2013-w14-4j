package parser;

import java.util.ArrayList;

import org.joda.time.DateTime;

class StateAt implements Parser.State {

	private final Parser parser;
	ArrayList<TimeToken> results;
	StateDefault parent;

	public StateAt (Parser parser, StateDefault parent) {
		this.parser = parser;
		this.parent = parent;
	}

	@Override
	public void processToken(Token t) {
		assert !popCondition();
		results.add((TimeToken) t);
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
			for (TimeToken token : results) {
				if (parent.tenuous.getStart() == null) {
					parent.tenuous.setStart(token.toDateTime());
				}
				else {
					DateTime t = token.toDateTime();
					parent.tenuous.setStart(parent.tenuous.getStart().withTime(t.getHourOfDay(), t.getMinuteOfHour(), 0, 0));
				}
			}
		}
	}

	@Override
	public void onPush() {
		results = new ArrayList<>();
	}
}