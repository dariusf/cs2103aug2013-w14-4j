import java.util.ArrayList;
import java.util.HashMap;


public class Logic {
	private static Storage storage = null;
	
	protected Logic(){
		storage = new Storage();
	}
	
	static Feedback executeCommand(String userCommand) {
		Command command = Parser.parseCommand(userCommand);
		CommandType commandType = Command.getCommandType();

		switch (commandType) {
		case ADD_TASK:
			return addTask(command);
		case DISPLAY:
			return displayTasks();
		case DELETE:
			return deleteTask(command);
		case CLEAR:
			return clearTasks(command);
		case EXIT:
			exitProgram();
		case SEARCH:
			return searchTasks(command);
		case UNDO:
			return undoState();
		case EDIT_TASK:
			return editTask(command);
		case HELP:
			return showHelp(command);
		case SORT:
			return sortTask();
		default:
			throw new Error(Constants.MESSAGE_UNRECOGNISED_COMMAND);
		}
	}
	

	private static Feedback sortTask() {
		// TODO Auto-generated method stub
		return null;
	}

	private static Feedback showHelp(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Feedback editTask(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Feedback undoState() {
		// TODO Auto-generated method stub
		return null;
	}

	private static Feedback searchTasks(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	private static void exitProgram() {
		// TODO Auto-generated method stub
		
	}

	private static Feedback clearTasks(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Feedback deleteTask(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Feedback displayTasks() {
		// TODO Auto-generated method stub
		return null;
	}

	private static Feedback addTask(Command command) {
		HashMap<String, String> taskAttributes = Command.getCommandAttributes();
		Task newTask = new Task(taskAttributes);
		storage.add(newTask);
		
		Feedback feedback = 
		return null;
	}


}
