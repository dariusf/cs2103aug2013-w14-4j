package parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import logic.Command;
import logic.CommandType;
import logic.Constants;
import logic.Interval;

import org.joda.time.DateTime;


public class Parser {
	
	private static final boolean PRINT_LEXER_TOKENS = false;

	public static void main(String[] args) {
		Command command = new Parser().parse("edit 1 go home at 10:00 am");
		command = new Parser().parse("add task 3 by: 10:00pm");
		command = new Parser().parse("delete 1");
		command = new Parser().parse("search haha hi there");
		
		command = new Parser().parse("clear");
		command = new Parser().parse("clear done");
		command = new Parser().parse("clear aklsjdksd");
		command = new Parser().parse("help done asjdlkasd");
		command = new Parser().parse("add go home from 10:00 am to 11:00 am or 1/2/13 12:00 pm to 1:00 pm 2/3/14");
		command = new Parser().parse("add go home from 10:00 am to 11:00 am");
		
		Scanner scanner = new Scanner(System.in);
		while(true){
			String message = scanner.nextLine();
			System.out.println(new Parser().parse(message));
		}
//		command = new Parser().parse("add go home from 10:00 am to 11:00 am or 1:00 pm");
//		command = new Parser().parse("add go home by 10:00 am");
//		command = new Parser().parse("add go home at at 10:00 am on 1/1/12");
//		command = new Parser().parse("add go home on 10:00 am at 1/1/12");
//		command = new Parser().parse("add go home at 10:00 am on 1/1/12 until 10:00 pm 1/2/12");
	}

	// States

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
	int editIndex = -1;
	boolean clearDone = false;

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
			
			if (commandType == CommandType.EDIT_TASK) {
				try {
					editIndex = Integer.parseInt(getCurrentToken().contents);
					nextToken();
				} catch (NumberFormatException e) {
					commandType = CommandType.INVALID;
				}
			}
			else if (commandType == CommandType.CLEAR) {
				try {
					if (hasTokensLeft()) {
						clearDone = getCurrentToken().contents.equalsIgnoreCase("done");
						if (clearDone) nextToken();
					}
				} catch (NumberFormatException e) {
					commandType = CommandType.INVALID;
				}
			}
		} else {
			// default to the add command
			commandType = CommandType.ADD_TASK;
		}

		// TODO: factor this out
		switch (commandType) {
		case ADD_TASK:
		case EDIT_TASK:
			return createComplexCommand(commandType);
		case DELETE:
		case DONE:
		case FINALISE:
			return createNumericalCommand(commandType);
		case SEARCH:
			return createSearchStringCommand();
		case HELP:
			return createHelpCommand();
		case CLEAR:
			return createClearCommand();
		case EXIT:
		case SORT:
		case UNDO:
		case DISPLAY:
		case INVALID:
			return createArgumentlessCommand(commandType);
		default:
			return null;
		}
	}
	
	private Command createClearCommand() {
		Command command = createArgumentlessCommand(CommandType.CLEAR);
		command.setValue("clearDone", Boolean.toString(clearDone));
		return command;
	}

	private Command createHelpCommand() {
		Command command = new Command(CommandType.HELP);
		if (hasTokensLeft()) {
			command.setValue("helpCommand", getCurrentToken().contents);
		}
		return command;
	}

	private Command createSearchStringCommand() {
		StringBuilder searchString = new StringBuilder();
		while(hasTokensLeft()) {
			searchString.append(getCurrentToken().contents + " ");
			nextToken();
		}
		
		Command command = new Command(CommandType.SEARCH);
		command.setValue(Constants.TASK_ATT_NAME, searchString.toString().trim());
		
		return command;
	}

	private Command createArgumentlessCommand(CommandType type) { 
		return new Command(type);
	}

	private Command createNumericalCommand(CommandType commandType) {
		int index = Integer.parseInt(getCurrentToken().contents);
		
		Command command = new Command(commandType);
		command.setValue(commandType.toString().toLowerCase() + "Index", Integer.toString(index));
		return command;
	}
	
	private Command createComplexCommand(CommandType commandType) {
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

		Command command = new Command(commandType);
		command.setDeadline(deadline);
		command.setDescription(text);
		command.setIntervals(intervals);
		
		// TODO: factor this out
		if (commandType == CommandType.EDIT_TASK) {
			command.setValue("editIndex", Integer.toString(editIndex));
		}
		
		return command;
	}

	public static CommandType determineCommandType(String enumString) {
		if (enumString.equalsIgnoreCase("invalid")) {
			return CommandType.ADD_TASK;
		} else if (enumString.equalsIgnoreCase("add") || enumString.equalsIgnoreCase("edit")) {
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
