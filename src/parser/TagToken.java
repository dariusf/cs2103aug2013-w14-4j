package parser;

class TagToken extends Token {
	public TagToken(String contents) {
		super(contents.substring(1));
	}
	
	public String toString() {
		return "Tag " + contents;
	}
}