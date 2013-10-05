package parser;

import java.util.ArrayList;

import org.joda.time.DateTime;

class StateOn implements Parser.State {

	private final Parser parser;
	ArrayList<DateToken> results;
	StateDefault parent;

	public StateOn (Parser parser, StateDefault parent) {
		this.parser = parser;
		this.parent = parent;
	}

	@Override
	public void processToken(Token t) {
		assert !popCondition();
		results.add((DateToken) t);
		this.parser.nextToken();
	}

	@Override
	public boolean popCondition() {
		Token t = this.parser.getCurrentToken();
		return !(t instanceof DateToken);
	}

	@Override
	public void onPop() {
		if (results.size() == 0) {
			parent.words.append("on ");
		}
		else {
//			System.out.println("On:");
			for (DateToken token : results) {
				parent.onAtUntilInterval.setStartDate(token);
//				System.out.println(token.toString());
//				if (parent.onAtUntilInterval.getStart() == null) {
//					parent.onAtUntilInterval.setStart(token.toDateTime(true));
//				}
//				else {
//					DateTime d = token.toDateTime(true);
//					parent.onAtUntilInterval.setStart(parent.onAtUntilInterval.getStart().withDate(d.getYear(), d.getMonthOfYear(), d.getDayOfMonth()));
//				}
			}
//			parser.onTokens.addAll(results);
		}
	}

	@Override
	public void onPush() {
		results = new ArrayList<>();
	}
}