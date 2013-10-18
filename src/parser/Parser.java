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

public class Parser {
	
	private static final boolean PRINT_LEXER_TOKENS = false;
	private static final boolean PRINT_MATCHED_COMMAND_TYPE = true;
	private static final boolean PRINT_PARSED_COMMAND = false;

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
//		Command command;
//		command = new Parser().parse("add event from 10:00 am until 11am");
//		command = new Parser().parse("add 10pm or die");
//		command = new Parser().parse("add task 3 last mOnDaY 1:00pm");
//		command = new Parser().parse("add task 3 from 10:00 pm 1/2/13 to 11:pm 5/5/15");
//		command = new Parser().parse("add Halloween Party on 31/10 #YOLO #Party");
//		command = new Parser().parse("add task at 2:00pm");
//		command = new Parser().parse("add task at 10:00pm");
//		command = new Parser().parse("add task on 1/2/13");
//		command = new Parser().parse("add task on 5/10/13");
//		command = new Parser().parse("add task on 6/10/13");
//		command = new Parser().parse("add go home from 10:00 am to 11:00 am or 1/2/13 12:00 pm to 1:00 pm 2/3/14");
//		command = new Parser().parse("add go home from 10:00 am to 11:00 am");
//		command = new Parser().parse("add go home from 10:00 am to 11:00 am or 1:00 pm");
//		command = new Parser().parse("add go home by 10:00 am");
//		command = new Parser().parse("add go home at at 10:00 am on 1/1/12");
//		command = new Parser().parse("add go home on 10:00 am at 1/1/12");
//		command = new Parser().parse("add go home at 10:00 am on 1/1/12 until 10:00 pm 1/2/12");
//		
//		command = new Parser().parse("edit 1 go home at 10:00 am");
//		command = new Parser().parse("delete 1");
//		command = new Parser().parse("search hi there");
//		command = new Parser().parse("clear");
//		command = new Parser().parse("clear done");
//		command = new Parser().parse("clear aklsjdksd");
//		command = new Parser().parse("help done asjdlkasd");

		// Mini REPL for testing
		java.util.Scanner scanner = new java.util.Scanner(System.in);
		while(true){
			String message = scanner.nextLine();
			Command result = new Parser().parse(message);
			if (PRINT_PARSED_COMMAND) System.out.println(result);
		}
	}

	// State stack

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
	ArrayList<String> tags = new ArrayList<>();

	public Parser() {
		parseStates = new Stack<>();
		tokens = new ArrayList<>();
	}

	public Command parse(String string) {
		
		if (string == null || string.isEmpty()) {
			// TODO where to put error fields in task?
			return new Command(CommandType.INVALID);
		}
		
		// TODO: deal with this via return values, not global state
		tokenizeInput(string);
		return buildResult();
	}

	private void tokenizeInput(String string) {
		try {
			Lexer lexer = new Lexer(new ByteArrayInputStream(string.getBytes("UTF-8")));

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

	private Command buildResult() {

		assert hasTokensLeft() : "No tokens left, cannot build command";

		Token firstToken;
		CommandType commandType;

		if (isCommand((firstToken = getCurrentToken()))) {
			// take the first token to be a command
			commandType = determineCommandType(firstToken.contents);
			nextToken();
			if (PRINT_MATCHED_COMMAND_TYPE) System.out.println("Command (exact): " + commandType);
			
			if (commandType == CommandType.EDIT_TASK && hasTokensLeft()) {
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
			
						
			// This list is prioritized
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

			// try longest subsequence first
			
			int[] subseq = new int[keywords.length];
			
//			System.out.println("Subsequence:");
			for (int i=0; i<keywords.length; i++) {
				subseq[i] = longestCommonSubsequence(firstToken.contents, keywords[i]).length();
//				System.out.println("    " + keywords[i] + ": " + subseq[i]);
			}
			
			int maximum = Integer.MIN_VALUE;
			int maximumIndex = -1;
			for (int i=0; i<keywords.length; i++) {
				if (subseq[i] > maximum) {
					maximum = subseq[i];
					maximumIndex = i;
				}
			}
			
//			if (maximumIndex != -1) {
//				commandType = determineCommandType(keywords[maximumIndex]);
//				if (PRINT_MATCHED_COMMAND_TYPE) System.out.println("Command (subsequence): " + commandType);
//			}
//			else {

				// try to do a fuzzy match

				int[] distances = new int[keywords.length];
				
				// Metric is string distance + difference in string length + priority
				System.out.println("Metric:");
				for (int i=0; i<keywords.length; i++) {
					distances[i] = (firstToken.contents.charAt(0) == keywords[i].charAt(0) ? -1 : 1)+(maximum - subseq[i]) + levenshteinDistance(firstToken.contents, keywords[i]);// + Math.abs(firstToken.contents.length() - keywords[i].length());
					System.out.println("  " +keywords[i]+": "+ distances[i]);
				}
				
				int threshold = 3;
				int minimum = Integer.MAX_VALUE;
				int minimumIndex = -1;
				for (int i=0; i<keywords.length; i++) {
					if (distances[i] < minimum && distances[i] <= threshold) {
						minimum = distances[i];
						minimumIndex = i;
					}
				}
				
				if (minimumIndex == -1) {
					// default to search?
					commandType = CommandType.INVALID;
				}
				else {
					commandType = determineCommandType(keywords[minimumIndex]);
				}
				if (PRINT_MATCHED_COMMAND_TYPE) System.out.println("Command (fuzzy): " + commandType);
//			}
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
		case DISPLAY:
			return createDisplayCommand();
		case EXIT:
		case SORT:
		case UNDO:
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
		command.setTags(tags);
		
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
	
	private Command createDisplayCommand() {
		Command command = createArgumentlessCommand(CommandType.DISPLAY);
		if(hasTokensLeft()){
			
		}
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
}
