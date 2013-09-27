package Parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;

import org.joda.time.DateTime;

import Logic.Command;
import Logic.Command;
import Logic.CommandType;
import Logic.Interval;

public class Parser {
	
	private static final boolean PRINT_LEXER_TOKENS = false;

	public static void main(String[] args) {
		Command command = new Parser().parse("add go home at 10:00 am");		
		command = new Parser().parse("delete 1");
		command = new Parser().parse("add go home from 10:00 am to 11:00 am or 1/2/13 12:00 pm to 1:00 pm 2/3/14");
//		command = new Parser().parse("add go home from 10:00 am to 11:00 am");
//		command = new Parser().parse("add go home from 10:00 am to 11:00 am or 1:00 pm");
//		command = new Parser().parse("add go home by 10:00 am");
//		command = new Parser().parse("add go home at at 10:00 am on 1/1/12");
//		command = new Parser().parse("add go home on 10:00 am at 1/1/12");
//		command = new Parser().parse("add go home at 10:00 am on 1/1/12 until 10:00 pm 1/2/12");
	}

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
		tokenPointer = Math.min(tokenPointer + 1, tokenCount);
		return temp != tokenPointer;
	}

	boolean previousToken() {
		int temp = tokenPointer;
		tokenPointer = Math.max(tokenPointer - 1, 0);
		return temp != tokenPointer;
	}

	Token getCurrentToken() {
		return tokens.get(tokenPointer);
	}

	boolean hasTokensLeft() {
		return tokenPointer < tokenCount;
	}

	// Components of the command that will be built up gradually
	DateTime deadline = null;
	ArrayList<Interval> intervals = new ArrayList<>();
	String text = "";

	public Parser() {
		parseStates = new Stack<>();
		tokens = new ArrayList<>();
	}

	public Command parse(String string) {

		// TODO: preliminary processing of string
		tokenizeInput(string);
		return buildResult();
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
			System.out.println("Error getting next token!");
			e.printStackTrace();
		}
	}

	private Command buildResult() {

		assert hasTokensLeft() : "No tokens left, cannot build command";

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
		switch (commandType) {
		case ADD_TASK:
			return createAddCommand();
		case DELETE:
			return createDeleteCommand();
		}
		return null;
	}
	
	private Command createDeleteCommand() {

		int deletionIndex = Integer.parseInt(getCurrentToken().contents);
		
		Command command = new Command(CommandType.DELETE);
		command.setValue("deletionIndex", Integer.toString(deletionIndex));
		return command;
	}
	
	private Command createAddCommand() {
		pushState(new StateDefault(this));

		while (hasTokensLeft()) {
			State currentState = parseStates.peek();

			if (currentState.popCondition()) {
				popState();
			} else {
				Token token = getCurrentToken();
				currentState.processToken(token);
			}
		}

		// pop the remaining states from the stack, in case we ran out of tokens
		// before they all were done
		while (parseStates.size() > 0) {
			popState();
		}

		Command command = new Command(CommandType.ADD_TASK);
		command.setDeadline(deadline);
		command.setDescription(text);
		command.setIntervals(intervals);
		return command;
	}

	public static CommandType determineCommandType(String enumString) {
		if (enumString.equals("invalid")) {
			return CommandType.ADD_TASK;
		} else if (enumString.equals("add") || enumString.equals("edit")) {
			enumString = enumString.toUpperCase() + "_TASK";
		} else {
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
