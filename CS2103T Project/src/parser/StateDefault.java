package parser;

class StateDefault implements Parser.State {

	StringBuilder words = new StringBuilder();
	private final Parser parser;
	
	Interval onAtUntilInterval = new Interval();
	
	public StateDefault(Parser parser) {
		this.parser = parser;
	}
	
	@Override
	public void processToken(Token t) {
		if (t instanceof WordToken) {
			if (t.contents.equals("from") || t.contents.equals("from:")) {
				parser.pushState(new StateInterval(parser, this));
			}
			else if (t.contents.equals("by") || t.contents.equals("by:")) {
				parser.pushState(new StateBy(parser, this));
			}
			else if (t.contents.equals("on") || t.contents.equals("on:")) {
				parser.pushState(new StateOn(parser, this));
			}
			else if (t.contents.equals("at") || t.contents.equals("at:")) {
				parser.pushState(new StateAt(parser, this));
			}
			else if (t.contents.equals("until") || t.contents.equals("until:")) {
				parser.pushState(new StateUntil(parser, this));
			}
			else {
				words.append(t.contents + " ");
			}
		}
		else {
			words.append(t.contents + " ");
		}
		parser.nextToken();
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