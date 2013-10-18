package parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import logic.Command;
import logic.Interval;
import org.joda.time.DateTime;
import common.CommandType;
import common.Constants;
import common.InvalidCommandReason;

public class Parser {

	private static final boolean PRINT_LEXER_TOKENS = false;
	private static final boolean PRINT_MATCHED_COMMAND_TYPE = false;
	private static final boolean PRINT_PARSED_COMMAND = true;

	@SuppressWarnings("resource")
	public static void main(String[] args) {

		//		test("add event from 10:00 am until 11am");
		//        test("edit 1 go home at 10:00 am");
		//        test("delete 1");
		//        test("search hi there");
		//        test("clear");
		//        test("clear done");
		//        test("clear aklsjdksd");
		//        test("help done asjdlkasd");

		// Mini REPL for testing
		java.util.Scanner scanner = new java.util.Scanner(System.in);
		while(true) {
			String input = scanner.nextLine();
			test(input);
		}
	}

	private static void test(String input) {
		Command command = new Parser().parse(input);
		if (PRINT_PARSED_COMMAND) System.out.println(command);
	}

	// State stack

	private Stack<State> parseStates = new Stack<>();

	void pushState(State s) {
		s.onPush();
		parseStates.push(s);
	}

	State popState() {
		State state = parseStates.pop();
		state.onPop();
		return state;
	}

	// Tokens

	private ArrayList<Token> tokens = new ArrayList<>();
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
		assert tokenPointer < tokenCount;
		return tokens.get(tokenPointer);
	}

	boolean hasTokensLeft() {
		return tokenPointer < tokenCount;
	}

	// Components of the command that will be built up gradually

	String description = "";
	DateTime deadline = null;
	ArrayList<Interval> intervals = new ArrayList<>();
	ArrayList<String> tags = new ArrayList<>();

	int taskIndex = -1;
	boolean clearDone = false;

	public Command parse(String userInput) {

		if (userInput == null || userInput.isEmpty()) {
			return invalidCommand(InvalidCommandReason.EMPTY_COMMAND);
		}

		tokenize(userInput);
		return constructCommand();
	}

	private void tokenize(String userInput) {
		try {
			Lexer lexer = new Lexer(new ByteArrayInputStream(userInput.getBytes("UTF-8")));

			Token next;
			if (PRINT_LEXER_TOKENS) System.out.println("\nTokens:");

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

	private Command constructCommand() {

		if (!hasTokensLeft()) {
			return invalidCommand(InvalidCommandReason.EMPTY_COMMAND);
		}

		Token firstToken = getCurrentToken();
		CommandType commandType;
		boolean exact = true;

		if (isCommand(firstToken)) {
			commandType = CommandType.fromString(firstToken.contents);
		} else {
			commandType = tryFuzzyMatch(firstToken.contents);
			exact = false;
		}

		if (PRINT_MATCHED_COMMAND_TYPE) System.out.println("Command (" + (exact ? "exact" : "fuzzy") + "): " + commandType);

		nextToken();

		switch (commandType) {
		case ADD:
			return createNaturalCommand(CommandType.ADD);
		case EDIT:
			return createEditCommand();
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
		case DISPLAY:
			return createDisplayCommand();
		case EXIT:
		case SORT:
		case UNDO:
		case REDO:
		case INVALID:
			return createArgumentlessCommand(commandType);
		default:
			assert false : "No such command type";
			return null;
		}
	}

	private CommandType tryFuzzyMatch(String probableCommand) {

		// This list is prioritized
		// TODO: not complete
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
				Constants.COMMAND_CLEAR
		};

		// Calculate length of longest subsequence and Levenshtein distance for each keyword
		// Also find the maximum subsequence length

		int[] longestSubsequenceLength = new int[keywords.length];
		int[] levDist = new int[keywords.length];
		int maximumSubsequenceLength = Integer.MIN_VALUE;

		for (int i=0; i<keywords.length; i++) {
			levDist[i] = levenshteinDistance(probableCommand, keywords[i]);
			longestSubsequenceLength[i] = longestCommonSubsequence(probableCommand, keywords[i]).length();
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

		// Find the smallest distance (and best-matching keyword) while we're at it

		int[] heuristic = new int[keywords.length];

		int threshold = 3;
		int minimum = Integer.MAX_VALUE;
		int minimumIndex = -1;

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
	
	// TODO: refactoring done up till here
	
	private Command createFinaliseCommand(CommandType commandType) {
		if (hasTokensLeft()) {
			int whichTask;
			int whichSlot;
			try {
				whichTask = Integer.parseInt(getCurrentToken().contents);

				if (nextToken()) {
					whichSlot = Integer.parseInt(getCurrentToken().contents);
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
			command.setTaskIndex(whichTask);
			command.setFinaliseIndex(whichSlot);
			command.setValue("finaliseIndex", Integer.toString(whichTask));
			command.setValue("slotIndex", Integer.toString(whichSlot));
			return command;

		}
		else {
			return new Command(CommandType.INVALID);
		}
	}

	private Command createEditCommand() {

		if (hasTokensLeft()) {
			try {
				taskIndex = Integer.parseInt(getCurrentToken().contents);
			} catch (NumberFormatException e) {
				return invalidCommand(InvalidCommandReason.INVALID_TASK_INDEX);
			}
			nextToken();
			return createNaturalCommand(CommandType.EDIT);
		}
		else {
			return invalidCommand(InvalidCommandReason.TOO_FEW_ARGUMENTS);
		}
	}

	private Command createNaturalCommand(CommandType commandType) {
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
		command.setDescription(description);
		command.setIntervals(intervals);
		command.setTags(tags);
		command.setTaskIndex(taskIndex);
		command.setValue("editIndex", Integer.toString(taskIndex));

		return command;
	}

	private Command createClearCommand() {
		Command command = createArgumentlessCommand(CommandType.CLEAR);

		if (hasTokensLeft()) {
			clearDone = getCurrentToken().contents.equalsIgnoreCase("done");
			command.setClearDone(clearDone);
			command.setValue("clearDone", Boolean.toString(clearDone));
		}

		return command;
	}

	private Command createDisplayCommand() {
		Command command = createArgumentlessCommand(CommandType.DISPLAY);
		if(hasTokensLeft()){

		}
		return command;
	}

	private Command createHelpCommand() {
		Command command = new Command(CommandType.HELP);
		if (hasTokensLeft()) {
			command.setHelpCommand(CommandType.fromString(getCurrentToken().contents));
			command.setValue("helpCommand", getCurrentToken().contents);
		}
		return command;
	}

	private Command createSearchStringCommand() {
		StringBuilder toSearch = new StringBuilder();
		while(hasTokensLeft()) {
			toSearch.append(getCurrentToken().contents + " ");
			nextToken();
		}

		Command command = new Command(CommandType.SEARCH);
		command.setSearchString(toSearch.toString().trim());
		command.setValue(Constants.TASK_ATT_NAME, toSearch.toString().trim());

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
				command.setTaskIndex(index);
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

	private static boolean isCommand(Token token) {
		return CommandType.fromString(token.contents) != CommandType.INVALID;
	}

	private static int[] listOfNumbers(int lower, int upper) {
		int[] result = new int[upper-lower+1];
		for (int i=lower; i<=upper; i++) {
			result[i-lower] = i;
		}
		return result;
	}

	// Source: https://github.com/threedaymonk/text/blob/master/lib/text/levenshtein.rb
	private static int levenshteinDistance(String s, String t) {
		int n = s.length();
		int m = t.length();

		if (n == 0) return m;
		if (m == 0) return n;

		int[] d = listOfNumbers(0, m);
		int x = 0;

		for (int i=0; i<n; i++) {
			int e = i + 1;
			for (int j=0; j<m; j++) {
				int cost = s.charAt(i) == t.charAt(j) ? 0 : 1;
				x = Math.min(
						d[j+1] + 1, Math.min(// insertion
								e + 1, // deletion
								d[j] + cost // substitution
								));
				d[j] = e;
				e = x;
			}
			d[m] = x;
		}
		return x;
	}

	// Source: http://rosettacode.org/wiki/Longest_common_subsequence
	private static String longestCommonSubsequence(String a, String b) {
		int[][] lengths = new int[a.length()+1][b.length()+1];

		// row 0 and column 0 are initialized to 0 already

		for (int i = 0; i < a.length(); i++)
			for (int j = 0; j < b.length(); j++)
				if (a.charAt(i) == b.charAt(j))
					lengths[i+1][j+1] = lengths[i][j] + 1;
				else
					lengths[i+1][j+1] =
					Math.max(lengths[i+1][j], lengths[i][j+1]);

		// read the substring out from the matrix
		StringBuffer sb = new StringBuffer();
		for (int x = a.length(), y = b.length();
				x != 0 && y != 0; ) {
			if (lengths[x][y] == lengths[x-1][y])
				x--;
			else if (lengths[x][y] == lengths[x][y-1])
				y--;
			else {
				assert a.charAt(x-1) == b.charAt(y-1);
				sb.append(a.charAt(x-1));
				x--;
				y--;
			}
		}

		return sb.reverse().toString();
	}

	private Command invalidCommand(InvalidCommandReason reason) {
		Command command = new Command(CommandType.INVALID);
		command.setInvalidCommandReason(reason);
		return command;
	}
}
