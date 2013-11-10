package parser;

import common.Constants;

//@author A0097282W
class KeywordToken extends Token {

	public KeywordToken(String name) {
		super(name);
	}

	public String toString() {
		return Constants.PARSER_TOKEN_PREFIX_KEYWORD + super.toString();
	}
}