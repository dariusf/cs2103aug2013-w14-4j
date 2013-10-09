package ui;

import java.util.ArrayList;
import java.util.List;

public class UserInputHistory {

	int index = -1;
	List<String> userInput = null;

	public UserInputHistory() {
		 userInput = new ArrayList<String>();
	}
	
	protected void addInput(String input) {
		userInput.add(input);
		index = userInput.size() - 1;
	}
	
	protected String getInput(int index) {
		String pastInput = userInput.get(index);
		return pastInput;
	}
	
	protected void setIndex(int newIndex) {
		index = newIndex;
	}

	protected int getIndex() {
		return index;
	}
	
	protected boolean isEndOfHistory() {
		if (userInput.size() == index + 1) {
			return true;
		} else {
			return false;
		}
	}
}
