package Parser;

public class Token {
	String thing = "";

	public Token(String name) {
		thing = name;
	}

	public String toString() {
		return thing;
	}
}