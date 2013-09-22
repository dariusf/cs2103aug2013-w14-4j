
public class Token {
	String thing = "";
	
	public Token(String name) {
		thing = name;
	}
	
	public String toString() {
		return thing;
	}
	
}

class DateToken extends Token {

	public DateToken(String name) {
		super(name);
	}

	public String toString() {
		return "Date " + super.toString();
	}
	
}

class TimeToken extends Token {

	public TimeToken(String name) {
		super(name);
	}

	public String toString() {
		return "Time " + super.toString();
	}

}

class WordToken extends Token {

	public WordToken(String name) {
		super(name);
	}
	
	public String toString() {
		return "Word " + super.toString();
	}
	
}

class KeywordToken extends Token {

	public KeywordToken(String name) {
		super(name);
	}
	

	public String toString() {
		return "Keyword " + super.toString();
	}
	
	
}