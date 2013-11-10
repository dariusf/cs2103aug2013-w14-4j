package parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import logic.Command;
import logic.Interval;
import org.joda.time.DateTime;

import common.ClearMode;
import common.CommandType;
import common.Constants;
import common.DisplayMode;
import common.InvalidCommandReason;

//@author A0097282W
public class Parser {

	private static final boolean PRINT_LEXER_TOKENS = false;
	private static final boolean PRINT_MATCHED_COMMAND_TYPE = false;
	private static final boolean PRINT_PARSED_COMMAND = true;

	@SuppressWarnings("resource")
	public static void main(String[] args) {

		// Mini REPL for testing
		java.util.Scanner scanner = new java.util.Scanner(System.in);
		while(true) {
			String input = scanner.nextLine();
			test(input);
		}
	}

	private static void test(String input) {
		Command command = Parser.parse(input);
		if (PRINT_PARSED_COMMAND) System.out.println(command.toString());
	}

	// Limit the exposed interface of the parser, to prevent users from getting
	// an explicit reference to a Parser object:

	private Parser() {}
	
	public static Command parse(String userInput) {
		return new Parser().parseInput(userInput);
	}

	// This way fields of default visibility will not be accessible by anything
	// other than implementations of State (which are passed an explicit reference
	// to a Parser object).

	StateStack parseStates = new StateStack();
	TokenCollection tokens = null;

	// Components of the command that will gradually be constructed
	String description = "";
	DateTime deadline = null;
	ArrayList<Interval> intervals = new ArrayList<>();
	ArrayList<String> tags = new ArrayList<>();
	int taskIndex = -1;
	int timeslotIndex = -1;

	private Command parseInput(String userInput) {

		if (isEmpty(userInput)) {
			return invalidCommand(InvalidCommandReason.EMPTY_COMMAND);
		}

		try {
			tokenize(userInput);
		} catch (IllegalDateException e) {
			return invalidCommand(InvalidCommandReason.INVALID_DATE);
		}
		return constructCommand();
	}

	private void tokenize(String userInput) throws IllegalDateException {
		try {
			Lexer lexer = new Lexer(new ByteArrayInputStream(userInput.getBytes("UTF-8")));

			Token next;
			if (PRINT_LEXER_TOKENS) System.out.println("\nTokens:");
			
			ArrayList<Token> tokens = new ArrayList<>();
			while ((next = lexer.nextToken()) != null) {
				tokens.add(next);
				if (PRINT_LEXER_TOKENS) System.out.println(next.toString());
			}

			this.tokens = new TokenCollection(tokens);

		} catch (IOException e) {
			System.out.println("Error getting next token!");
			e.printStackTrace();
		}
	}

	private Command constructCommand() {

		if (!tokens.hasTokensLeft()) {
			return invalidCommand(InvalidCommandReason.EMPTY_COMMAND);
		}

		Token firstToken = tokens.getCurrentToken();
		CommandType commandType;
		boolean exactMatch = true;

		if (isCommand(firstToken)) {
			commandType = CommandType.fromString(firstToken.contents);
		} else {
			exactMatch = false;
			commandType = tryFuzzyMatch(firstToken.contents);
		}

		if (PRINT_MATCHED_COMMAND_TYPE) System.out.println("Command (" + (exactMatch ? "exact" : "fuzzy") + "): " + commandType);

		tokens.nextToken();

		switch (commandType) {
		case ADD:
			return createNaturalCommand(CommandType.ADD);
		case EDIT:
			return createEditCommand();
		case DELETE:
		case DONE:
			return createTaskIndexCommand(commandType);
		case FINALISE:
			return createFinaliseCommand();
		case SEARCH:
			return createSearchCommand();
		case HELP:
			return createHelpCommand();
		case CLEAR:
			return createClearCommand();
		case DISPLAY:
			return createDisplayCommand();
		case EXIT:
		case SORT:
		case UNDO:
		case REDO:
			return createArgumentlessCommand(commandType);
		case GOTO:
			return createPageIndexCommand(commandType);
		case INVALID:
			return invalidCommand(InvalidCommandReason.UNRECOGNIZED_COMMAND);
		default:
			assert false : "No such command type";
			return null;
		}
	}

	private CommandType tryFuzzyMatch(String probableCommand) {

		// This list is prioritised
		String[] keywords = new String[] {
				Constants.COMMAND_ADD,
				Constants.COMMAND_SEARCH,
				Constants.COMMAND_HELP,
				Constants.COMMAND_EDIT,
				Constants.COMMAND_DELETE,
				Constants.COMMAND_DISPLAY,
				Constants.COMMAND_SORT,
				Constants.COMMAND_FINALISE,
				Constants.COMMAND_EXIT,
				Constants.COMMAND_CLEAR,
				Constants.COMMAND_GOTO,
				Constants.COMMAND_UNDO,
				Constants.COMMAND_REDO,
				Constants.COMMAND_DONE
		};

		// Calculate length of longest subsequence and Levenshtein distance for each keyword
		// Also find the maximum subsequence length

		int[] longestSubsequenceLength = new int[keywords.length];
		int[] levDist = new int[keywords.length];
		int maximumSubsequenceLength = Integer.MIN_VALUE;

		for (int i=0; i<keywords.length; i++) {
			levDist[i] = Utility.levenshteinDistance(probableCommand, keywords[i]);
			longestSubsequenceLength[i] = Utility.longestCommonSubsequence(probableCommand, keywords[i]).length();
			if (longestSubsequenceLength[i] > maximumSubsequenceLength) {
				maximumSubsequenceLength = longestSubsequenceLength[i];
			}
		}

		// Calculate distance heuristic:

		// Levenshtein distance
		// + (maximum longest subsequence - longest subsequence)
		// -1 if the keyword starts with the same letter as the probable command
		// +1 if it doesn't start with the same letter

		// Inferior heuristics tried:
		// Absolute difference in string length

		int[] heuristic = new int[keywords.length];

		int threshold = 3;
		int minimum = Integer.MAX_VALUE;
		int minimumIndex = -1;

		// Find the smallest distance (for the best-matching keyword) while we're at it
		
		for (int i=0; i<keywords.length; i++) {
			heuristic[i] = (probableCommand.charAt(0) == keywords[i].charAt(0) ? -1 : 1) + (maximumSubsequenceLength - longestSubsequenceLength[i]) + levDist[i];

			if (heuristic[i] < minimum && heuristic[i] <= threshold) {
				minimum = heuristic[i];
				minimumIndex = i;
			}
		}

		CommandType commandType;

		if (minimumIndex == -1) {
			commandType = CommandType.INVALID;
		}
		else {
			commandType = CommandType.fromString(keywords[minimumIndex]);
		}
		return commandType;
	}
		
	private Command createFinaliseCommand() {
		int whichTask = -1, whichSlot = -1;
		
		Command command = new Command(CommandType.FINALISE);

		if (tokens.hasTokensLeft()) {
			try {
				whichTask = Integer.parseInt(tokens.getCurrentToken().contents);
				command.setTaskIndex(whichTask);
				tokens.nextToken();
				
				if (tokens.hasTokensLeft()) {
					try {
						whichSlot = Integer.parseInt(tokens.getCurrentToken().contents);
					} catch (NumberFormatException e) {
						return command;
					}
				}

			} catch (NumberFormatException e) {
				return command;
			}

			command.setTimeslotIndex(whichSlot);
			return command;

		}
		else {
			return new Command(CommandType.FINALISE);
		}
	}

	private Command createEditCommand() {

		if (tokens.hasTokensLeft()) {
			try {
				taskIndex = Integer.parseInt(tokens.getCurrentToken().contents);
			} catch (NumberFormatException e) {
				return new Command(CommandType.EDIT);
			}
			tokens.nextToken();
						
			if (tokens.hasTokensLeft()) {
				Token currentToken = tokens.getCurrentToken();
				try {
					// if the next token is numerical, edit is being applied
					// to a timeslot
					timeslotIndex = Integer.parseInt(currentToken.contents);
					tokens.nextToken();
					return createEditTimeslotCommand();
				} catch (NumberFormatException e) {
					return createNaturalCommand(CommandType.EDIT);
				}
			}
			else {
				Command command = new Command(CommandType.EDIT);
				command.setTaskIndex(taskIndex);
				return command;
			}
		}
		else {
			return new Command(CommandType.EDIT);
		}
	}

	private Command createEditTimeslotCommand() {
		parseStates.push(new StateEditInterval(this));

		while (tokens.hasTokensLeft()) {
			State currentState = parseStates.peek();

			if (currentState.popCondition()) {
				parseStates.pop();
			} else {
				Token token = tokens.getCurrentToken();
				currentState.processToken(token);
			}
		}

		// Pop the remaining states from the stack, in case
		// we ran out of tokens before they all were done
		
		while (parseStates.size() > 0) {
			parseStates.pop();
		}

		Command command = new Command(CommandType.EDIT);

		command.setTaskIndex(taskIndex);
		command.setTimeslotIndex(timeslotIndex);
		command.setIntervals(intervals);

		return command;
	}

	private Command createNaturalCommand(CommandType commandType) {
		parseStates.push(new StateDefault(this));

		while (tokens.hasTokensLeft()) {
			State currentState = parseStates.peek();

			if (currentState.popCondition()) {
				parseStates.pop();
			} else {
				Token token = tokens.getCurrentToken();
				currentState.processToken(token);
			}
		}

		// Pop the remaining states from the stack, in case
		// we ran out of tokens before they all were done
		
		while (parseStates.size() > 0) {
			parseStates.pop();
		}

		Command command = new Command(commandType);

		command.setDeadline(deadline);
		command.setDescription(description);
		command.setIntervals(intervals);
		command.setTags(tags);
		command.setTaskIndex(taskIndex);

		return command;
	}

	private Command createClearCommand() {
		Command command = new Command(CommandType.CLEAR);
		
        if (tokens.hasTokensLeft()) {
            Token currentToken = tokens.getCurrentToken();
            
            ClearMode clearMode = ClearMode.fromString(currentToken.contents);
            
            if (clearMode != ClearMode.INVALID && clearMode != ClearMode.DATE) {
                command.setClearMode(clearMode);
            }
            else if (currentToken instanceof DateToken){
            	command.setClearMode(ClearMode.DATE);
                command.setClearDateTime(((DateToken) currentToken).toDateTime());
            } else {
                command.setClearMode(ClearMode.ALL);
            }
        } else {
            command.setClearMode(ClearMode.ALL);
        }

		return command;
	}

	
	private Command createDisplayCommand() {
		Command command = new Command(CommandType.DISPLAY);

		if (tokens.hasTokensLeft()) {
			Token currentToken = tokens.getCurrentToken();
			
			DisplayMode displayMode = DisplayMode.fromString(currentToken.contents);
			
			if (displayMode != DisplayMode.INVALID && displayMode != DisplayMode.DATE && displayMode != DisplayMode.SEARCH) {
				command.setDisplayMode(displayMode);
			}
			else if (currentToken instanceof DateToken){
				command.setDisplayMode(DisplayMode.DATE);
				command.setDisplayDateTime(((DateToken) currentToken).toDateTime());
			} else {
				command.setDisplayMode(DisplayMode.TODO);
			}
		} else {
			command.setDisplayMode(DisplayMode.TODO);
		}
		
		return command;
	}

	private Command createHelpCommand() {
		Command command = new Command(CommandType.HELP);

		if (tokens.hasTokensLeft()) {
			
			CommandType commandType;
			
			if (isCommand(tokens.getCurrentToken())) {
				commandType = CommandType.fromString(tokens.getCurrentToken().contents);
				command.setHelpCommand(commandType);
			} else {
				commandType = tryFuzzyMatch(tokens.getCurrentToken().contents);
				command.setHelpCommand(commandType);
			}
		}

		return command;
	}

	private Command createSearchCommand() {
		Command command = new Command(CommandType.SEARCH);
		ArrayList<String> tags = new ArrayList<String>();
		ArrayList<String> searchTerms = new ArrayList<String>();
		
		while (tokens.hasTokensLeft()) {
			Token currentToken = tokens.getCurrentToken();
			
			if (currentToken instanceof TagToken) {
				tags.add(currentToken.contents);
			}
			else {
				searchTerms.add(currentToken.contents);
			}
			
			tokens.nextToken();
		}
		
		command.setTags(tags);
		command.setSearchTerms(searchTerms);
		
		return command;
	}

	private Command createArgumentlessCommand(CommandType type) { 
		return new Command(type);
	}

	private Command createPageIndexCommand(CommandType commandType) {
		Command command = new Command(commandType);

		if (tokens.hasTokensLeft()) {
			int index;
			try {
				index = Integer.parseInt(tokens.getCurrentToken().contents);
			} catch (NumberFormatException e) {
				return command;
			}

			command.setPageIndex(index);
			return command;
		}
		else {
			return command;
		}
	}

	private Command createTaskIndexCommand(CommandType commandType) {
		Command command = new Command(commandType);
		
		if (tokens.hasTokensLeft()) {
			int index;
			try {
				index = Integer.parseInt(tokens.getCurrentToken().contents);
			} catch (NumberFormatException e) {
				return command;
			}

			command.setTaskIndex(index);
			return command;
		}
		else {
			return command;
		}
	}

	private static boolean isCommand(Token token) {
		return CommandType.fromString(token.contents) != CommandType.INVALID;
	}

	private boolean isEmpty(String userInput) {
		return userInput == null || userInput.isEmpty();
	}
	
	private Command invalidCommand(InvalidCommandReason reason) {
		Command command = new Command(CommandType.INVALID);
		command.setInvalidCommandReason(reason);
		return command;
	}
}
