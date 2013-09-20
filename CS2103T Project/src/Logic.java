import java.util.ArrayList;


public class Logic {
	private static Storage storage = null;
	private static ArrayList<String> previousState;
	
	protected Logic(){
		storage = new Storage();
	}
	
	static Feedback executeCommand(String userCommand) {
		if (userCommand.isEmpty()) {
			return displayInvalidCommand(userCommand);
		}

		Command command = Parser.parseCommand(userCommand);
		CommandType commandType = command.getCommandType();

		switch (commandType) {
		case ADD_LINE:
			return addLine(command);
		case DISPLAY:
			return displayFile();
		case DELETE:
			return deleteLine(command);
		case INVALID:
			return displayInvalidCommand(command);
		case CLEAR:
			return clearFile(command);
		case EXIT:
			exitProgram();
		case SORT:
			return sortFile();
		case SEARCH:
			return searchFile(command);
		default:
			throw new Error(Constants.MESSAGE_UNRECOGNISED_COMMAND);
		}
	}
	
	protected static addLine(Command command){
		HashMap<String, String> commandAttributes = command.getCommandAttributes();
		Task newTask = new Task(commandAttributes);
		currentState.add(newTask);
	}
}
