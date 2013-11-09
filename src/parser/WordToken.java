package parser;

//@author A0097282W
class WordToken extends Token {

	public WordToken(String name) {
		super(name);
	}

	public String toString() {
		return "Word " + super.toString();
	}

}