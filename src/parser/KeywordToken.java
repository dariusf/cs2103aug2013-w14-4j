package parser;

//@author A0097282W
class KeywordToken extends Token {

	public KeywordToken(String name) {
		super(name);
	}

	public String toString() {
		return "Keyword " + super.toString();
	}
}