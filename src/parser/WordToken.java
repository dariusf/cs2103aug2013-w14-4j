package parser;

import common.Constants;

//@author A0097282W
class WordToken extends Token {


	public WordToken(String name) {
		super(name);
	}

	public String toString() {
		return Constants.PARSER_TOKEN_PREFIX_WORD + super.toString();
	}

}