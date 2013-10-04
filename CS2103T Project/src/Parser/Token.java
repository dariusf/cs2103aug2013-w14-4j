package parser;

public class Token {
	String contents = "";

	public Token(String contents) {
		this.contents = contents;
	}

	public String toString() {
		return contents;
	}
}