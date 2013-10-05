package parser;

import java.util.ArrayList;

import org.joda.time.DateTime;

class StateOn implements Parser.State {

	/**
	 * 
	 */
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
//				System.out.println(token.toString());
				if (parent.tenuous.getStart() == null) {
					parent.tenuous.setStart(token.toDateTime());
				}
				else {
					DateTime d = token.toDateTime();
					parent.tenuous.setStart(parent.tenuous.getStart().withDate(d.getYear(), d.getMonthOfYear(), d.getDayOfMonth()));
				}
			}
		}
	}

	@Override
	public void onPush() {
		results = new ArrayList<>();
	}
}