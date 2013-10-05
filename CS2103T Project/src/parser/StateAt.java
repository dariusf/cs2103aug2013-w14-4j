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
			for (TimeToken token : results) { // TODO: only takes one token so why a list?
				
//				DateTime intervalStart = parent.onAtUntilInterval.getStart();
//
//				if (intervalStart == null) {
//					parent.onAtUntilInterval.setStart(token.toDateTime());
//				}
//				else {
//					parent.onAtUntilInterval.setStart(token.mergeInto(intervalStart));
//				}
//				
//				intervalStart = parent.onAtUntilInterval.getStart();
//				DateTime now = new DateTime();
//				if (intervalStart.isBefore(now)) {
//					parent.onAtUntilInterval.setStart(intervalStart.plusDays(1));
//				}
				parent.onAtUntilInterval.setStartTime(token);
			}
//			parser.atTokens.addAll(results);
		}
	}

	@Override
	public void onPush() {
		results = new ArrayList<>();
	}
}