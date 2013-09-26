package Logic;

import java.util.ArrayList;

import Parser.Token;

public class Moment {
	/**
	 * A moment is a date, a time, or both.
	 */
	
	private ArrayList<Token> tokens = new ArrayList<>();
	public void add(Token token) {
		tokens.add(token);
	}
	public boolean isEmpty() {
		return tokens.size() == 0;
	}
}