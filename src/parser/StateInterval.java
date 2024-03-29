package parser;

import java.util.ArrayList;

import common.Constants;
import common.Interval;


//@author A0097282W
class StateInterval implements State {

	private final Parser parser;
	StateDefault parent;
	private boolean foundDelimiter = false;
	private String delimiter;
	private ArrayList<Token> from = new ArrayList<>();
	private ArrayList<Token> to = new ArrayList<>();
	private boolean trailingOr = false;
	private boolean trailingDelimiter = false;

	public StateInterval(Parser parser, StateDefault parent) {
		this.parser = parser;
		this.parent = parent;
	}
	
	@Override
	public void processToken(Token t) {
		assert !popCondition(); // t is either a TimeToken, a DateToken,
								// or an interval-delimiting WordToken,
								// or a WordToken containing 'or'

		if (t instanceof TimeToken || t instanceof DateToken) {
			if (!foundDelimiter) {
				from.add(t);
			}
			else {
				to.add(t);
			}
			trailingOr = false;
			trailingDelimiter = false;
			this.parser.tokens.nextToken();
		}
		else if (t instanceof WordToken) {
			if (t.contents.equalsIgnoreCase(Constants.PARSER_KEYWORD_OR)) {
				finaliseInterval();
				foundDelimiter = false;
				trailingOr = true;
			}
			else {
				assert tokenIsIntervalDelimiter(t);
				delimiter = t.contents;
				foundDelimiter = true;
				trailingDelimiter = true;
			}
			this.parser.tokens.nextToken();
		}
	}

	@Override
	public boolean popCondition() {
		Token token = this.parser.tokens.getCurrentToken();
		
		return !(token instanceof DateToken
				|| token instanceof TimeToken
				|| (token instanceof WordToken && tokenIsIntervalDelimiter(token))
				|| (token instanceof WordToken && token.contents.equalsIgnoreCase(Constants.PARSER_KEYWORD_OR)));
	}
		
	public static boolean tokenIsIntervalDelimiter(Token token) {
		String contents = token.contents;
		return contents.equalsIgnoreCase(Constants.PARSER_KEYWORD_TO) || contents.equalsIgnoreCase(Constants.PARSER_KEYWORD_TILL) || contents.equalsIgnoreCase(Constants.PARSER_KEYWORD_UNTIL);
	}

	@Override
	public void onPop() {
		finaliseInterval();
		if (parent != null) { // this state may be used standalone
			if (trailingOr) {
				parent.words.append(Constants.PARSER_KEYWORD_OR + " ");
			}
			else if (trailingDelimiter) {
				parent.words.append(delimiter + " ");
			}
		}
	}
	
	private void finaliseInterval() {
		Interval newInterval = new Interval();
		for (Token token : from) {
			if (token instanceof DateToken) {
				newInterval.changeStartDate((DateToken) token);
			}
			else if (token instanceof TimeToken) {
				newInterval.changeStartTime((TimeToken) token);
			}
		}
		for (Token token : to) {
			if (token instanceof DateToken) {
				newInterval.changeEndDate((DateToken) token);
			}
			else if (token instanceof TimeToken) {
				newInterval.changeEndTime((TimeToken) token);
			}
		}
		
		boolean intervalValid = from.size() > 0 || to.size() > 0;
		
		if (intervalValid) {
			parser.intervals.add(newInterval);
			to.clear();
			from.clear();
		}
	}

	@Override
	public void onPush() {
	}
	
}