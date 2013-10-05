package parser;

import logic.Interval;




class StateDefault implements Parser.State {

	StringBuilder words;
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
		// this is the starting state so this never pops
		return false;
	}

	@Override
	public void onPop() {
//		System.out.println("Words: " + words.toString().trim());
		parser.text = words.toString().trim();
		
		if (onAtUntilInterval.isSet()) {
//			if (!onAtUntilInterval.hasEnd()) {
//				onAtUntilInterval.normalizeEnd();
//			}
			parser.intervals.add(onAtUntilInterval);
		}
	}

	@Override
	public void onPush() {
		words = new StringBuilder();
	}
}