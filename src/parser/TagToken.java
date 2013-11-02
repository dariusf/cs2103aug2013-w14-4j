package parser;

class TagToken extends Token {
	public TagToken(String contents) {
		super(contents);
	}
	
	public String toString() {
		return "Tag " + contents;
	}
}