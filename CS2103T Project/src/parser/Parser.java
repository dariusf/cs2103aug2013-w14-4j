package parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import logic.Command;

import org.eclipse.ui.internal.operations.AdvancedValidationUserApprover;
import org.joda.time.DateTime;

import common.CommandType;
import common.Constants;

public class Parser {
	
	private static final boolean PRINT_LEXER_TOKENS = false;

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Command command = new Parser().parse("add task 3 from 10:00 pm 1/2/13 to 11:pm 5/5/15");
		command = new Parser().parse("add task at 2:00pm");
		command = new Parser().parse("add task at 10:00pm");
		command = new Parser().parse("add task on 1/2/13");
		command = new Parser().parse("add task on 5/10/13");
		command = new Parser().parse("add task on 6/10/13");
		command = new Parser().parse("add go home from 10:00 am to 11:00 am or 1/2/13 12:00 pm to 1:00 pm 2/3/14");
		command = new Parser().parse("add go home from 10:00 am to 11:00 am");
		command = new Parser().parse("add go home from 10:00 am to 11:00 am or 1:00 pm");
		command = new Parser().parse("add go home by 10:00 am");
		command = new Parser().parse("add go home at at 10:00 am on 1/1/12");
		command = new Parser().parse("add go home on 10:00 am at 1/1/12");
		command = new Parser().parse("add go home at 10:00 am on 1/1/12 until 10:00 pm 1/2/12");
		
		command = new Parser().parse("edit 1 go home at 10:00 am");
		command = new Parser().parse("delete 1");
		command = new Parser().parse("search hi there");
		command = new Parser().parse("clear");
		command = new Parser().parse("clear done");
		command = new Parser().parse("clear aklsjdksd");
		command = new Parser().parse("help done asjdlkasd");

		// Mini REPL for testing
		Scanner scanner = new Scanner(System.in);
		while(true){
			String message = scanner.nextLine();
			System.out.println(new Parser().parse(message));
		}
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
	
//	ArrayList<TimeToken> atTokens = new ArrayList<>();
//	ArrayList<DateToken> onTokens = new ArrayList<>();
//	ArrayList<Token> untilTokens = new ArrayList<>();
//	ArrayList<Token> byTokens = new ArrayList<>();
//	ArrayList<IntervalToken> intervalTokens = new ArrayList<>();
	
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
		
		if (string == null || string.isEmpty()) {
			// TODO where to put error fields in task?
			return new Command(CommandType.INVALID);
		}
		
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
			return createNumericalCommand(commandType);
		case FINALISE:
			return createFinaliseCommand(commandType);
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
		case REDO:
		case INVALID:
			return createArgumentlessCommand(commandType);
		default:
			return null;
		}
	}
	
	private Command createFinaliseCommand(CommandType commandType) {
		if (hasTokensLeft()) {
			int finaliseIndex;
			int slotIndex;
			try {
				finaliseIndex = Integer.parseInt(getCurrentToken().contents);

				if (nextToken()) {
					slotIndex = Integer.parseInt(getCurrentToken().contents);
				}
				else {
					// TODO reason
					return new Command(CommandType.INVALID);
				}
				
			} catch (NumberFormatException e) {
				// TODO reason
				return new Command(CommandType.INVALID);
			}
			
			Command command = new Command(commandType);
			command.setValue("finaliseIndex", Integer.toString(finaliseIndex));
			command.setValue("slotIndex", Integer.toString(slotIndex));
			return command;

		}
		else {
			return new Command(CommandType.INVALID);
		}
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
		if (hasTokensLeft()) {
			int index;
			try {
				index = Integer.parseInt(getCurrentToken().contents);

				Command command = new Command(commandType);
				command.setValue(commandType.toString().toLowerCase() + "Index", Integer.toString(index));
				return command;
			} catch (NumberFormatException e) {
				return new Command(CommandType.INVALID);
			}
		}
		else {
			return new Command(CommandType.INVALID);
		}
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
