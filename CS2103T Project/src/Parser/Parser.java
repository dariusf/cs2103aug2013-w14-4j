package Parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import Logic.Command;
import Logic.CommandType;
import Logic.Constants;

public class Parser {

	public static void main(String[] args) {
//		new Parser().parse("add go home at 10:00 am");
	}
	
	// States
	
	// TODO: draw proper state diagram
	private interface State {
		
		// This method determines if the parser will change state
		// at all from the current state.
		public boolean willChangeState();

		// This is called to effect the change of state.
		public void changeState();

		// This is called only if the parser will NOT change state.
		// (in which case it always processes the current token)
		public void processToken(Token t);
	}

	private State parseState;
	private NaturalState naturalState;
	private OnDateState onDateState;
	private AtTimeState atTimeState;
	
	// Tokens
	
	private ArrayList<Token> tokens;
	private int tokenCount = 0;
	private int tokenPointer = 0;
	
	// The resulting command that will be built up gradually
	private Command command = null;
	private StringBuilder tokenContent;
	
	// A temporary field for the previous token
	// Used when transitioning between states
	// GLOBAL STATE: EXERCISE CAUTION
	private Token previousToken = null;
	
	public Parser() {
		naturalState = new NaturalState();
		onDateState = new OnDateState();
		atTimeState = new AtTimeState();
		parseState = naturalState;
		
		tokens = new ArrayList<>();
		tokenContent = new StringBuilder();
	}
	
	public Command parse(String string) {
		
		// TODO: preliminary processing of string
		tokenizeInput(string);
		buildResult();
		
		return command;
	}
	
	private void tokenizeInput(String string) {
		try {
			Lexer lexer = new Lexer(new ByteArrayInputStream(string.getBytes("UTF-8")));
			
			Token next;
			while ((next = lexer.nextToken()) != null) {
				tokens.add(next);
//				System.out.println(">>>>>" + next.toString() + "<<<<<<");
			}
			tokenCount = tokens.size();

		} catch (IOException e) {
			System.out.println("Error getting next token!\nCurrent command: " + command.toString());
			e.printStackTrace();
		}
	}

	private void buildResult() {
		
		if (!hasTokensLeft()) return;
		
		// the first token is always a command
		Token firstToken;
		CommandType commandType;
		
		if (isCommand((firstToken = getToken()))) {
			commandType = determineCommandType(firstToken);
			parseState = getStartingState(commandType);
			advanceToken();
		} else {
			// default to add command
			commandType = CommandType.ADD_TASK;
			parseState = naturalState;
		}

		command = new Command(commandType);
		
		while (hasTokensLeft()) {
			if (parseState.willChangeState()) {
				parseState.changeState();
			}
			else {
				parseState.processToken(getToken());
			}
		}
		
		command.setValue(Constants.TASK_ATT_NAME, tokenContent.toString().trim());
	}
	
	private State getStartingState(CommandType type) {
		switch (type) {
		case ADD_TASK:
			return naturalState;
		case DELETE: // arguments
		case EDIT_TASK:
		case FINALISE:
		case HELP:
		case SEARCH:
			return null;
		case EXIT: // no arguments
		case CLEAR:
		case DISPLAY:
		case SORT:
		case UNDO:
			return null;
		default:
			return null;
		}
	}

	public CommandType determineCommandType(Token token) {
		String enumString = token.thing;
		
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

	private boolean isCommand(Token token) {
		return determineCommandType(token) != CommandType.INVALID;
	}

	private class NaturalState implements State {

		// TODO: store the previous keyword token here
		
		private boolean shouldChangeToAtTimeState() {
			Token currentToken = getToken();
			return currentToken instanceof KeywordToken && currentToken.thing.equals("at");
		}

		private boolean shouldChangeToOnDateState() {
			Token currentToken = getToken();
			return currentToken instanceof KeywordToken && currentToken.thing.equals("on");
		}

		private void changeToOnDateState() {
			parseState = onDateState;
			previousToken = getToken();
			advanceToken();
		}

		private void changeToAtTimeState() {
			parseState = atTimeState;
			previousToken = getToken();
			advanceToken();
		}
		
		@Override
		public boolean willChangeState() {
			
			return shouldChangeToAtTimeState() || shouldChangeToOnDateState();
		}

		@Override
		public void changeState() {
			if (shouldChangeToAtTimeState()) {
				changeToAtTimeState();
			}
			else {
				assert shouldChangeToOnDateState();
				changeToOnDateState();
			}
		}

		@Override
		public void processToken(Token t) {
			if (t instanceof WordToken) {
				tokenContent.append(t.thing + " ");
				advanceToken();
			}
			else {
				// if we encounter a date here, it might be invalid. depends
				System.out.println("encountered a date without preceding qualifier");
			}
		}
		
	}
	
	private class AtTimeState implements State {
		
		public boolean untilEncountered = false;
		
		private boolean shouldChangeToNormalState() {
			Token currentToken = getToken();
			return !(currentToken instanceof TimeToken);
		}

		private void changeToNormalState() {
			if (previousToken != null) {
				tokenContent.append(previousToken.thing + " ");
				// do not advance
			}
			parseState = naturalState;
		}

		@Override
		public boolean willChangeState() {
			return shouldChangeToNormalState();
		}

		@Override
		public void changeState() {
			if (shouldChangeToNormalState()) {
				changeToNormalState();
			}
		}

		@Override
		public void processToken(Token t) {
			assert t instanceof TimeToken;
			// TODO: add to current date and get most specific match, right now it just writes over
			command.setValue(Constants.TASK_ATT_STARTTIME, ((TimeToken) t).timeString());
//			command.setValue(Constants.TASK_ATT_ENDTIME, null);
			previousToken = null;
			advanceToken();
		}
		
	}
	
	private class OnDateState implements State {

		private boolean shouldChangeToNormalState() {
			Token currentToken = getToken();
			return !(currentToken instanceof DateToken);
		}

		private void changeToNormalState() {
			if (previousToken != null) {
				tokenContent.append(previousToken.thing + " ");
				// do not advance
			}
			parseState = naturalState;
		}

		@Override
		public boolean willChangeState() {
			return shouldChangeToNormalState();
		}

		@Override
		public void changeState() {
			if (shouldChangeToNormalState()) {
				changeToNormalState();
			}
		}

		@Override
		public void processToken(Token t) {
			assert t instanceof DateToken;
			command.setValue(Constants.TASK_ATT_DEADLINE, ((DateToken) t).dateString());
			previousToken = null;
			advanceToken();
		}
		
	}

	private boolean advanceToken() {
		int temp = tokenPointer;
		tokenPointer = Math.min(tokenPointer+1, tokenCount);
		return temp != tokenPointer;
	}
	
//	private boolean backtrackToken() {
//		int temp = tokenPointer;
//		tokenPointer = Math.max(tokenPointer-1, 0);
//		return temp != tokenPointer;
//	}
	
	private Token getToken() {
		return tokens.get(tokenPointer);
	}
	
	private boolean hasTokensLeft() {
		return tokenPointer < tokenCount;
	}
}
