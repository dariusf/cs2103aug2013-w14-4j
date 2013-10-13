package parser;

import logic.Interval;

class StateDefault implements Parser.State {

	StringBuilder words = new StringBuilder();
	private final Parser parser;
	
	Interval onAtUntilInterval = new Interval();
	
	public StateDefault(Parser parser) {
		this.parser = parser;
	}
	
	@Override
	public void processToken(Token t) {
		if (t instanceof TimeToken || t instanceof DateToken) {
			parser.pushState(new StateInterval(parser, this));
			// Do not advance
		}
		else if (t instanceof WordToken) {
			if (t.contents.equals("by")) {
				parser.pushState(new StateBy(parser, this));
			}
			else {
				words.append(t.contents + " ");
			}
			parser.nextToken();
		}
		else if (t instanceof TagToken) {
			parser.tags.add(((TagToken) t).contents);
			parser.nextToken();
		}
		else {
			words.append(t.contents + " ");
			parser.nextToken();
		}
	}

	@Override
	public boolean popCondition() {
		// This is the starting state, so it never pops until the very end
		return false;
	}

	@Override
	public void onPop() {
		parser.text = words.toString().trim();
		if (onAtUntilInterval.isSet()) {
			parser.intervals.add(onAtUntilInterval);
		}
	}

	@Override
	public void onPush() {
	}
}