package parser;

//@author A0097282W
public interface State {

	// This method determines if the current state will be popped from
	// the state stack
	public boolean popCondition();

	// This is called only if the current state remains on the state stack,
	// in which case it will processe the current token
	// (you can assert !popCondition(); in here)
	public void processToken(Token t);

	// These are called when the pop or push happen
	public void onPop();

	public void onPush();
}