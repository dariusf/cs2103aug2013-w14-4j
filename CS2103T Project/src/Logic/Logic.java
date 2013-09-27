package Logic;

import Storage.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import Parser.Parser;
import Storage.Storage;

public class Logic {
	
	private static Storage storage = null;

	public Logic() {
		storage = new Storage();
	}

	public static Feedback executeCommand(String userCommand) {
		Command command = new Parser().parse(userCommand);
		CommandType commandType = command.getCommandType();

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
			throw new Error(Constants.MSG_UNRECOGNISED_COMMAND);
		}
	}

	private static Feedback sortTask() {
		Feedback feedback = null;
		if (storage.size() > 0) {
			storage.sort();
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.SORT);
		} else {
			feedback = new Feedback(Constants.SC_NO_TASK_ERROR, CommandType.SORT);
		}
		return feedback;
	}

	private static Feedback showHelp(Command command) {
		Feedback feedback = null;
		HashMap<String, String> commandAttributes = command
				.getCommandAttributes();
		if (commandAttributes.containsKey("command")) {
			String commandString = commandAttributes.get("command");
			CommandType commandToGetHelp = Parser
					.determineCommandType(commandString);
			switch (commandToGetHelp) {
			case ADD_TASK:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_ADD_TASK);
			case EDIT_TASK:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_EDIT_TASK);
			case SORT:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_SORT);
			case DELETE:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_DELETE);
			case CLEAR:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_CLEAR);
			case UNDO:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_UNDO);
			case SEARCH:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_SEARCH);
			case HELP:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_HELP);
			case DONE:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_DONE);
			case FINALISE:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_FINALISE);
			case DISPLAY:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_DISPLAY);
			case EXIT:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_EXIT);
			default:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_GENERAL);
				;
			}
		} else {
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
					Constants.HELP_GENERAL);
		}
		return feedback;
	}

	private static Feedback editTask(Command command) {
		Feedback feedback = null;
		HashMap<String, String> commandAttributes = command.getCommandAttributes();
		int taskIndex = Integer.parseInt(commandAttributes.get("index"));
		Task taskToEdit = storage.get(taskIndex);
		commandAttributes.remove("index");
		for(String key : commandAttributes.keySet()){
			taskToEdit.set(key, commandAttributes.get(key));
		}
		storage.replace(taskIndex, taskToEdit);
		
		if (isTaskOver(taskToEdit)) {
			feedback = new Feedback(Constants.SC_SUCCESS_TASK_OVERDUE, CommandType.EDIT_TASK, taskToEdit.toString());
		} else {
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.EDIT_TASK, taskToEdit.toString());
		}
		
		return feedback;
	}

	private static Feedback addTask(Command command) {
		HashMap<String, String> taskAttributes = command.getCommandAttributes();
		Task newTask = new Task(taskAttributes);
		storage.add(newTask);
	
		Feedback feedback = null;
		if (isTaskOver(newTask)) {
			feedback = new Feedback(Constants.SC_SUCCESS_TASK_OVERDUE, CommandType.ADD_TASK, newTask.toString());
		} else {
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.ADD_TASK, newTask.toString());
		}
	
		return feedback;
	}

	private static Feedback undoState() {
		Feedback feedback = null;
		if (storage.size() > 0) {
			storage.sort();
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.UNDO);
		} else {
			feedback = new Feedback(Constants.SC_UNDO_NO_PRIOR_STATE_ERROR, CommandType.UNDO);
		}
		return feedback;
	}

	private static Feedback searchTasks(Command command) {
		Feedback feedback = null;
		HashMap<String, String> commandAttributes = command.getCommandAttributes();
		ArrayList<Task> validTasks = new ArrayList<>();
		Iterator<Task> storageIterator = storage.iterator();
		while(storageIterator.hasNext()){
			validTasks.add(storageIterator.next());
		}
		for(String attribute : commandAttributes.keySet()){
			String keyword = commandAttributes.get(attribute);
			for(int i = validTasks.size()-1; i >= 0; i--){
				Task currentTask = validTasks.get(i);
				if(!isWordInString(keyword, currentTask.get(attribute))){
					validTasks.remove(i);
				}
			}
		}
		
		if (validTasks.size() > 0) {
			StringBuilder output = new StringBuilder();
			int index = 1;
			for (Task task : validTasks) {
				output.append(index + ". ");
				output.append(task.toString());
				if (index < validTasks.size()) {
					output.append("/n");
				}
			}
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.SEARCH, output.toString());
		} else {
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.SEARCH, Constants.MSG_NO_RESULT);
		}
		return feedback;
	}

	private static void exitProgram() {
		System.exit(0);
	}

	private static Feedback clearTasks(Command command) {
		Feedback feedback = null;
		if (storage.size() > 0) {
			storage.clear();
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.CLEAR);
		} else {
			feedback = new Feedback(Constants.SC_NO_TASK_ERROR, CommandType.CLEAR);
		}
		return feedback;
	}

	private static Feedback deleteTask(Command command) {
		HashMap<String, String> commandAttributes = command
				.getCommandAttributes();
		int lineNumber = Integer.parseInt(commandAttributes
				.get(Constants.DELETE_ATT_LINE));

		Feedback feedback = null;
		if (lineNumber <= storage.size()) {
			storage.remove(lineNumber);
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.DELETE);
		} else {
			feedback = new Feedback(Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR, CommandType.DELETE);
		}

		return feedback;
	}

	private static Feedback displayTasks() {
		Feedback feedback = null;
		if (storage.size() > 0) {
			StringBuilder output = new StringBuilder();
			int index = 1;
			Iterator<Task> storageIterator = storage.iterator();
			while (storageIterator.hasNext()) {
				Task task = storageIterator.next();
				output.append(index + ". ");
				output.append(task.toString());
				if (index < storage.size()) {
					output.append("/n");
				}
			}
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.DISPLAY, output.toString());
		} else {
			feedback = new Feedback(Constants.SC_NO_TASK_ERROR, CommandType.DISPLAY);
		}
		return feedback;
	}

	private static boolean isTaskOver(Task task) {
		if (task.isDeadlineTask()) {
			Date deadline = task.getDeadline();
			return isTimePastAlready(deadline);
		} else if (task.isTimedTask()) {
			Date endTime = task.getEndTime();
			return isTimePastAlready(endTime);
		} else if (task.isUntimedTask()) {
			List<Task.Slot> possibleTime = task.getPossibleTime();
			return isUntimedTaskOver(possibleTime);
		} else {
			return false;
		}
	}

	private static boolean isUntimedTaskOver(List<Task.Slot> possibleTime) {
		boolean isAllSlotOver = true;
		for (Task.Slot slot : possibleTime) {
			if (!isTimePastAlready(slot.getEndTime())) {
				isAllSlotOver = false;
			}
		}
		return isAllSlotOver;

	}

	private static boolean isTimePastAlready(Date time) {
		return time.compareTo(new Date()) < 0;
	}
	
	private static boolean isWordInString(String word, String string) {
		String lowerCaseString = string.toLowerCase();
		String lowerCaseWord = word.toLowerCase();
		return lowerCaseString.indexOf(lowerCaseWord) != -1;
	}
	
	public static void main(String[] args) {
		Logic logic = new Logic();
		
		// Display task test
		System.out.println(logic.displayTasks());
		
		// Add task test 1 (timed task)
		HashMap<String, String> testMap1 = new HashMap<String, String>();
		testMap1.put("name", "April Fool's Prank");
		testMap1.put("type", "timed");
		testMap1.put("startTime", "10:00 am");
		testMap1.put("endTime", "11:00 am");
		testMap1.put("tags", "forfun maygetmefired");
		Command testCommand1 = new Command(CommandType.ADD_TASK, testMap1);
		System.out.println(logic.addTask(testCommand1));
		
		// Add task test 2 (deadline task)
		HashMap<String, String> testMap2 = new HashMap<String, String>();
		testMap2.put("name", "April Fool's Prank");
		testMap2.put("type", "deadline");
		testMap2.put("deadline", "10:00 am");
		testMap2.put("tags", "forfun maygetmefired");
		Command testCommand2 = new Command(CommandType.ADD_TASK, testMap2);
		System.out.println(logic.addTask(testCommand2));
		
	}
}
