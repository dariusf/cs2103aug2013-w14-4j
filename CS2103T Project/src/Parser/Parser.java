package Parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import Logic.Command;
import Logic.CommandType;
import Logic.Constants;

public class Parser {

//	public static void main(String[] args) {
//	}
	
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
	private AddState addState;
	private DateTimeState dateTimeState;
	
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
		
		addState = new AddState();
		dateTimeState = new DateTimeState();
		parseState = addState;
		
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
			parseState = determineStartingState(commandType);
			advanceToken();
		} else {
			// default to add command
			commandType = CommandType.ADD_TASK;
			parseState = addState;
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
	
	private State determineStartingState(CommandType type) {
		switch (type) {
		case ADD_TASK:
			return addState;
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
		if (token.thing.equals("add")) {
			return CommandType.ADD_TASK;
		}
		return CommandType.EXIT; // not really
	}

	private boolean isCommand(Token token) {
		return token.thing.equals("add");
	}

	private class AddState implements State {

		private boolean shouldChangeToDateTimeState() {
			Token currentToken = getToken();
			return currentToken instanceof KeywordToken &&
					(currentToken.thing.equals("at") || currentToken.thing.equals("on"));
		}

		private void changeToDateTimeState() {
			parseState = dateTimeState;
			previousToken = getToken();
			advanceToken();
		}
		
		@Override
		public boolean willChangeState() {
			return shouldChangeToDateTimeState();
		}

		@Override
		public void changeState() {
			if (shouldChangeToDateTimeState()) {
				changeToDateTimeState();
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
	
	// TODO: should be 2 different states!
	private class DateTimeState implements State {

		private boolean shouldChangeToNormalState() {
			Token currentToken = getToken();
			return !(currentToken instanceof TimeToken || currentToken instanceof DateToken);
		}

		private void changeToNormalState() {
			if (previousToken != null) {
				tokenContent.append(previousToken.thing + " ");
				// do not advance
			}
			parseState = addState;
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
			if (t instanceof TimeToken) {
				// TODO: add to current date and get most specific match, right now it just writes over
				command.setValue(Constants.TASK_ATT_STARTTIME, ((TimeToken) t).timeString());
				previousToken = null;
				advanceToken();
			}
			else if (t instanceof DateToken) {
				command.setValue(Constants.TASK_ATT_DEADLINE, ((DateToken) t).dateString());
				previousToken = null;
				advanceToken();
			}
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
