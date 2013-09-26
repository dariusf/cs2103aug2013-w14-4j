package Parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import Logic.Command;
import Logic.Command2;
import Logic.CommandType;
import Logic.Interval;
import Logic.Moment;

public class Parser {

	public static void main(String[] args) {
		Command2 command2 = new Parser().parse("add go home at 10:00 am");
		command2 = new Parser().parse("add go home from 10:00 am to 11:00 am or 1/2/13 12:00 pm to 1:00 pm 2/3/14");
		command2 = new Parser().parse("add go home from 10:00 am to 11:00 am");

		// should default to 1 hour interval
		command2 = new Parser().parse("add go home from 10:00 am to 11:00 am or 1:00 pm");
		
		command2 = new Parser().parse("add go home by 10:00 am");
		command2 = new Parser().parse("add go home at at 10:00 am on 1/1/12");
		
		command2 = new Parser().parse("add go home on 10:00 am at 1/1/12");
		
		command2 = new Parser().parse("add go home at 10:00 am on 1/1/12 until 10:00 pm 1/2/12");
	}
	
	private static final boolean PRINT_LEXER_TOKENS = false;
	
	// States
	
	// TODO: draw proper state diagram
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
	
	private Stack<State> parseStates;
	
	void pushState(State s) {
		s.onPush();
		parseStates.push(s);
	}
	void popState() {
		parseStates.pop().onPop();
	}
	
	// Tokens
	
	private ArrayList<Token> tokens;
	private int tokenCount = 0;
	private int tokenPointer = 0;
	
	boolean nextToken() {
		int temp = tokenPointer;
		tokenPointer = Math.min(tokenPointer+1, tokenCount);
		return temp != tokenPointer;
	}
	
	boolean previousToken() {
		int temp = tokenPointer;
		tokenPointer = Math.max(tokenPointer-1, 0);
		return temp != tokenPointer;
	}
	
	Token getCurrentToken() {
		return tokens.get(tokenPointer);
	}
	
	boolean hasTokensLeft() {
		return tokenPointer < tokenCount;
	}
	
	// The resulting command that will be built up gradually
	private Command2 command = null;
	Moment deadline = null;
	ArrayList<Interval> intervals = new ArrayList<>();
	String text = "";
	
	public Parser() {
		parseStates = new Stack<>();
		tokens = new ArrayList<>();
	}
	
	public Command2 parse(String string) {
		
		// TODO: preliminary processing of string
		tokenizeInput(string);
		buildResult();
		
		return command;
	}
	
	private void tokenizeInput(String string) {
		try {
			Lexer lexer = new Lexer(new ByteArrayInputStream(string.getBytes("UTF-8")));
			
			Token next;
			if (PRINT_LEXER_TOKENS) System.out.println("Tokens:");
			while ((next = lexer.nextToken()) != null) {
				tokens.add(next);
				if (PRINT_LEXER_TOKENS) System.out.println(next.toString());
			}
			tokenCount = tokens.size();

		} catch (IOException e) {
			System.out.println("Error getting next token!\nCurrent command: " + command.toString());
			e.printStackTrace();
		}
	}

	private void buildResult() {
		
		if (!hasTokensLeft()) return;
		
		Token firstToken;
		CommandType commandType;
		
		if (isCommand((firstToken = getCurrentToken()))) {
			// take the first token to be a command
			commandType = determineCommandType(firstToken.contents);
			nextToken();
		} else {
			// default to the add command
			commandType = CommandType.ADD_TASK;
		}
		
		// TODO: handle other commands here
		
		pushState(new StateDefault(this));
		
		while (hasTokensLeft()) {
			State currentState = parseStates.peek();
			
			if (currentState.popCondition()) {
				popState();
			}
			else {
				Token token = getCurrentToken();
				currentState.processToken(token);
			}
		}
		
		// pop the remaining states from the stack, in case we ran out of tokens
		// before they all were done
		while (parseStates.size() > 0) {
			popState();
		}
		
		command = new Command2(commandType);
		command.deadline = deadline;
		command.text = text;
		command.intervals = intervals;

//		command.setValue(Constants.TASK_ATT_NAME, tokenContent.toString().trim());
	}

	public static CommandType determineCommandType(String enumString) {
		if (enumString.equals("invalid")) {
			return CommandType.ADD_TASK;
		}
		else if (enumString.equals("add") || enumString.equals("edit")) {
			enumString = enumString.toUpperCase() + "_TASK";
		}
		else {
			enumString = enumString.toUpperCase();
		}
		
		try {
			return CommandType.valueOf(enumString);
		} catch (IllegalArgumentException e) {
			return CommandType.INVALID;
		}
	}

	private static boolean isCommand(Token token) {
		return determineCommandType(token.contents) != CommandType.INVALID;
	}
}
