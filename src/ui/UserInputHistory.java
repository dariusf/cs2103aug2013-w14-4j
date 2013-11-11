package ui;

import java.util.ArrayList;
import java.util.List;

//@author A0101048X
public class UserInputHistory {

	int index = -1;
	List<String> userInput = null;

	public UserInputHistory() {
		 userInput = new ArrayList<String>();
	}
	
	public void addInput(String input) {
		userInput.add(input);
		index = userInput.size() - 1;
	}
	
	public String getInput(int index) {
		String pastInput = userInput.get(index);
		return pastInput;
	}
	
	public void setIndex(int newIndex) {
		index = newIndex;
	}

	public int getIndex() {
		return index;
	}
	
	public boolean isEndOfHistory() {
		if (userInput.size() == index + 1) {
			return true;
		} else {
			return false;
		}
	}
}
