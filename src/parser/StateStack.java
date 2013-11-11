package parser;

import java.util.Stack;

//@author A0097282W
public class StateStack {

	private Stack<State> states = new Stack<>();

	public void push(State s) {
		s.onPush();
		states.push(s);
	}

	public State pop() {
		State state = states.pop();
		state.onPop();
		return state;
	}

	public State peek() {
		return states.peek();
	}

	public int size() {
		return states.size();
	}
}
