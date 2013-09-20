public class Parser {
	protected static Command parseCommand (String commandString){
		CommandType determinedCommandType = null;
		String commandTypeString = getFirstWord(commandString);
		if (commandTypeString.equalsIgnoreCase(Constants.COMMAND_ADD)) {
			determinedCommandType = CommandType.ADD_LINE;
		} else if (commandTypeString.equalsIgnoreCase(Constants.COMMAND_DISPLAY)) {
			determinedCommandType = CommandType.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase(Constants.COMMAND_EXIT)) {
			determinedCommandType = CommandType.EXIT;
		} else if (commandTypeString.equalsIgnoreCase(Constants.COMMAND_CLEAR)) {
			determinedCommandType = CommandType.CLEAR;
		} else if (commandTypeString.equalsIgnoreCase(Constants.COMMAND_DELETE)) {
			determinedCommandType = CommandType.DELETE;
		} else if (commandTypeString.equalsIgnoreCase(Constants.COMMAND_SORT)) {
			determinedCommandType = CommandType.SORT;
		} else if (commandTypeString.equalsIgnoreCase(Constants.COMMAND_SEARCH)) {
			determinedCommandType = CommandType.SEARCH;
		} else {
			determinedCommandType = CommandType.INVALID;
		}
		String remainingCommand = removeFirstWord(commandString);
		Command determinedCommand = new Command(determinedCommandType);
		determinedCommand.setValue("instructions", remainingCommand);
		return determinedCommand;
	}
	
	private static String getFirstWord(String userCommand) {
		String commandTypeString = userCommand.trim().split("\\s+")[0];
		return commandTypeString;
	}
	
	private static String removeFirstWord(String userCommand) {
		String[] words = userCommand.split(" ", 2);
		String remainingCommand = Constants.EMPTY_STRING;
		if (words.length > 1) {
			remainingCommand = words[1];
		}
		return remainingCommand;
	}
}
