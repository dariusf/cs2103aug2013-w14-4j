package common;

public enum CommandType {
	INVALID,
	ADD, EDIT, DISPLAY, DELETE, CLEAR, EXIT, GOTO,
	SORT, SEARCH, UNDO, FINALISE, HELP, DONE, REDO;

	public static CommandType fromString(String commandString) {
		try {
			return CommandType.valueOf(commandString.toUpperCase());
		} catch (IllegalArgumentException e) {
			return CommandType.INVALID;
		}
	}
}
