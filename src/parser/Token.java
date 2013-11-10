package parser;

//@author A0097282W
abstract class Token {
	String contents = "";

	public Token(String contents) {
		this.contents = contents;
	}

	public String toString() {
		return contents;
	}	
}