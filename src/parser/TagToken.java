package parser;

import common.Constants;

//@author A0097282W
class TagToken extends Token {
	public TagToken(String contents) {
		super(contents);
	}
	
	public String toString() {
		return Constants.PARSER_TOKEN_PREFIX_TAG + contents;
	}
}