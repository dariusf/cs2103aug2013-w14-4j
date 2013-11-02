package parser;

class WordToken extends Token {

	public WordToken(String name) {
		super(name);
	}

	public String toString() {
		return "Word " + super.toString();
	}

}