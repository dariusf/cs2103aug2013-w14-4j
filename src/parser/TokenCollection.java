package parser;

import java.util.ArrayList;

import common.Constants;

//@author A0097282W
public class TokenCollection {
	private ArrayList<Token> tokens = new ArrayList<>();
	private int tokenCount = 0;
	private int tokenPointer = 0;
	
	public TokenCollection(ArrayList<Token> tokens) {
		assert tokens != null;
		this.tokens = tokens;
		tokenCount = tokens.size();
	}

	void nextToken() {
		tokenPointer = Math.min(tokenPointer + 1, tokenCount);
	}

	void previousToken() {
		tokenPointer = Math.max(tokenPointer - 1, 0);
	}

	Token getCurrentToken() {
		assert tokenPointer < tokenCount : Constants.PARSER_ASSERTION_ERROR_NO_TOKENS_REMAINING;
		return tokens.get(tokenPointer);
	}

	boolean hasTokensLeft() {
		return tokenPointer < tokenCount;
	}
}
