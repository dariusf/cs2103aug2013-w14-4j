package Logic;

import Storage.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import Parser.Parser;
import Storage.Storage;

public class Logic {

	public static Storage storage = null;
	public static ArrayList<Integer> temporaryMapping = new ArrayList<Integer>();
	public static boolean isDynamicIndex = false;

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
		case DONE:
			return markDone(command);
		case FINALISE:
			return finaliseTask(command);
		case SORT:
			return sortTask();
		default:
			throw new Error(Constants.MSG_UNRECOGNISED_COMMAND);
		}
	}

	// Not working yet
	public static Feedback finaliseTask(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	// Not working yet
	public static Feedback markDone(Command command) {
		HashMap<String, String> commandAttributes = command
				.getCommandAttributes();
		int lineNumber = Integer.parseInt(commandAttributes
				.get(Constants.DELETE_ATT_LINE));
		if (isDynamicIndex) {
			lineNumber = temporaryMapping.get(lineNumber);
		}

		Feedback feedback = null;

		if (lineNumber <= storage.size()) {
			storage.remove(lineNumber);
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.DELETE);
			isDynamicIndex = false;
		} else {
			feedback = new Feedback(Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR,
					CommandType.DELETE);
		}

		return feedback;
	}

	public static Feedback sortTask() {
		Feedback feedback = null;
		if (storage.size() > 0) {
			storage.sort();
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.SORT);
		} else {
			feedback = new Feedback(Constants.SC_NO_TASK_ERROR,
					CommandType.SORT);
		}
		return feedback;
	}

	public static Feedback showHelp(Command command) {
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

	// Incomplete, need to figure out the command format for edit
	public static Feedback editTask(Command command) {
		Feedback feedback = null;
		HashMap<String, String> commandAttributes = command
				.getCommandAttributes();
		int inputIndex = Integer.parseInt(commandAttributes.get("index"));
		int taskIndex = inputIndex;

		if (isDynamicIndex) {
			taskIndex = temporaryMapping.get(inputIndex);
		}

		Task taskToEdit = storage.get(taskIndex);
		commandAttributes.remove("index");
		for (String key : commandAttributes.keySet()) {

		}
		storage.replace(taskIndex, taskToEdit);

		if (isTaskOver(taskToEdit)) {
			feedback = new Feedback(Constants.SC_SUCCESS_TASK_OVERDUE,
					CommandType.EDIT_TASK, taskToEdit.toString());
		} else {
			feedback = new Feedback(Constants.SC_SUCCESS,
					CommandType.EDIT_TASK, taskToEdit.toString());
		}

		return feedback;
	}

	public static Feedback addTask(Command command) {
		Task newTask = new Task(command);
		storage.add(newTask);

		Feedback feedback = null;
		if (isTaskOver(newTask)) {
			feedback = new Feedback(Constants.SC_SUCCESS_TASK_OVERDUE,
					CommandType.ADD_TASK, newTask.toString());
		} else {
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.ADD_TASK,
					newTask.toString());
		}

		return feedback;
	}

	public static Feedback undoState() {
		Feedback feedback = null;
		if (storage.size() > 0) {
			storage.sort();
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.UNDO);
		} else {
			feedback = new Feedback(Constants.SC_UNDO_NO_PRIOR_STATE_ERROR,
					CommandType.UNDO);
		}
		return feedback;
	}

	public static Feedback searchTasks(Command command) {
		Feedback feedback = null;
		HashMap<String, String> commandAttributes = command
				.getCommandAttributes();
		ArrayList<Task> validTasks = new ArrayList<Task>();
		ArrayList<Integer> validTasksAbsoluteIndices = new ArrayList<Integer>();
		ArrayList<Task> allTasks = new ArrayList<Task>();
		Iterator<Task> storageIterator = storage.iterator();
		while (storageIterator.hasNext()) {
			allTasks.add(storageIterator.next());
		}

		boolean shouldAdd = true;
		for (int i = 0; i < allTasks.size(); i++) {
			Task currentTask = allTasks.get(i);
			for (String attribute : commandAttributes.keySet()) {
				String keyword = commandAttributes.get(attribute);
				if (!isWordInString(keyword, currentTask.get(attribute))) {
					shouldAdd = false;
				}
			}
			if (shouldAdd) {
				validTasks.add(currentTask);
				validTasksAbsoluteIndices.add(i + 1);
			}
			shouldAdd = true;
		}

		for (int i = 1; i <= validTasksAbsoluteIndices.size(); i++) {
			temporaryMapping.set(i, validTasksAbsoluteIndices.get(i - 1));
		}
		isDynamicIndex = true;

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
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.SEARCH,
					output.toString());
		} else {
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.SEARCH,
					Constants.MSG_NO_RESULT);
		}
		return feedback;
	}

	public static void exitProgram() {
		System.exit(0);
	}

	public static Feedback clearTasks(Command command) {
		Feedback feedback = null;
		if (storage.size() > 0) {
			storage.clear();
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.CLEAR);
			isDynamicIndex = false;
		} else {
			feedback = new Feedback(Constants.SC_NO_TASK_ERROR,
					CommandType.CLEAR);
		}
		return feedback;
	}

	public static Feedback deleteTask(Command command) {
		HashMap<String, String> commandAttributes = command
				.getCommandAttributes();
		int lineNumber = Integer.parseInt(commandAttributes
				.get(Constants.DELETE_ATT_LINE));
		if (isDynamicIndex) {
			lineNumber = temporaryMapping.get(lineNumber);
		}

		Feedback feedback = null;
		if (lineNumber <= storage.size()) {
			storage.remove(lineNumber);
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.DELETE);
			isDynamicIndex = false;
		} else {
			feedback = new Feedback(Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR,
					CommandType.DELETE);
		}

		return feedback;
	}

	public static Feedback displayTasks() {
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
					output.append("\n");
				}
				index++;
			}
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.DISPLAY,
					output.toString());
			isDynamicIndex = false;
		} else {
			feedback = new Feedback(Constants.SC_NO_TASK_ERROR,
					CommandType.DISPLAY);
		}
		return feedback;
	}

	public static boolean isTaskOver(Task task) {
		if (task.isDeadlineTask()) {
			DateTime deadline = task.getDeadline();
			return isTimePastAlready(deadline);
		} else if (task.isTimedTask()) {
			DateTime endTime = task.getEndTime();
			return isTimePastAlready(endTime);
		} else if (task.isUntimedTask()) {
			List<Interval> possibleTime = task.getPossibleTime();
			return isUntimedTaskOver(possibleTime);
		} else {
			return false;
		}
	}

	public static boolean isUntimedTaskOver(List<Interval> possibleTime) {
		boolean isAllSlotOver = true;
		for (Interval slot : possibleTime) {
			if (!isTimePastAlready(slot.getEnd())) {
				isAllSlotOver = false;
			}
		}
		return isAllSlotOver;

	}

	public static boolean isTimePastAlready(DateTime time) {
		return time.compareTo(new DateTime()) < 0;
	}

	public static boolean isWordInString(String word, String string) {
		String lowerCaseString = string.toLowerCase();
		String lowerCaseWord = word.toLowerCase();
		return lowerCaseString.indexOf(lowerCaseWord) != -1;
	}

	public static void main(String[] args) {
		Logic logic = new Logic();

		// Display task test
		System.out.println(logic.displayTasks());

		// Add task test 1 (deadline task, success not overdue)
		Command command1 = new Command(CommandType.ADD_TASK);
		DateTime date1 = new DateTime(2013, 10, 14, 23, 59, 59);
		command1.setDeadline(date1);
		command1.setDescription("Submit V0.1");
		System.out.println(logic.addTask(command1));

		// Add task test 2 (deadline task, success but overdue)
		Command command2 = new Command(CommandType.ADD_TASK);
		DateTime date2 = new DateTime(2012, 10, 14, 23, 59, 59);
		command2.setDeadline(date2);
		command2.setDescription("Submit overdue V0.1");
		System.out.println(logic.addTask(command2));
		
		// Add task test 3 (deadline task, success not overdue)
		Command command3 = new Command(CommandType.ADD_TASK);
		DateTime startDate3 = new DateTime(2013, 9, 30, 15, 0, 0);
		DateTime endDate3 = new DateTime(2013, 9, 30, 16, 0, 0);
		Interval interval3 = new Interval();
		interval3.setStart(startDate3);
		interval3.setEnd(endDate3);
		ArrayList<Interval> intervalList3 = new ArrayList<Interval>();
		intervalList3.add(interval3);
		command3.setIntervals(intervalList3);
		command3.setDescription("CS2105 Test");
		System.out.println(logic.addTask(command3));
		
		Command command4 = new Command(CommandType.ADD_TASK);
		DateTime startDate4 = new DateTime(2012, 9, 30, 15, 0, 0);
		DateTime endDate4 = new DateTime(2012, 9, 30, 16, 0, 0);
		Interval interval4 = new Interval();
		interval4.setStart(startDate4);
		interval4.setEnd(endDate4);
		ArrayList<Interval> intervalList4 = new ArrayList<Interval>();
		intervalList4.add(interval4);
		command4.setIntervals(intervalList4);
		command4.setDescription("CS2105 Test 2012");
		System.out.println(logic.addTask(command4));
		
		Command command5 = new Command(CommandType.ADD_TASK);
		DateTime startDate5a = new DateTime(2013, 10, 30, 15, 0, 0);
		DateTime endDate5a = new DateTime(2013, 10, 30, 16, 0, 0);
		Interval interval5a = new Interval();
		interval5a.setStart(startDate5a);
		interval5a.setEnd(endDate5a);
		DateTime startDate5b = new DateTime(2013, 10, 30, 16, 0, 0);
		DateTime endDate5b = new DateTime(2013, 10, 30, 17, 0, 0);
		Interval interval5b = new Interval();
		interval5b.setStart(startDate5b);
		interval5b.setEnd(endDate5b);
		DateTime startDate5c = new DateTime(2013, 10, 30, 17, 0, 0);
		DateTime endDate5c = new DateTime(2013, 10, 30, 18, 0, 0);
		Interval interval5c = new Interval();
		interval5c.setStart(startDate5c);
		interval5c.setEnd(endDate5c);
		ArrayList<Interval> intervalList5 = new ArrayList<Interval>();
		intervalList5.add(interval5a);
		intervalList5.add(interval5b);
		intervalList5.add(interval5c);
		command5.setIntervals(intervalList5);
		command5.setDescription("A floating event!");
		System.out.println(logic.addTask(command5));
		
		Command command6 = new Command(CommandType.ADD_TASK);
		DateTime startDate6a = new DateTime(2013, 10, 30, 15, 0, 0);
		DateTime endDate6a = new DateTime(2013, 10, 30, 16, 0, 0);
		Interval interval6a = new Interval();
		interval6a.setStart(startDate6a);
		interval6a.setEnd(endDate6a);
		DateTime startDate6b = new DateTime(2013, 10, 30, 16, 0, 0);
		DateTime endDate6b = new DateTime(2013, 10, 30, 17, 0, 0);
		Interval interval6b = new Interval();
		interval6b.setStart(startDate6b);
		interval6b.setEnd(endDate6b);
		DateTime startDate6c = new DateTime(2013, 10, 30, 17, 0, 0);
		DateTime endDate6c = new DateTime(2013, 10, 30, 18, 0, 0);
		Interval interval6c = new Interval();
		interval6c.setStart(startDate6c);
		interval6c.setEnd(endDate6c);
		ArrayList<Interval> intervalList6 = new ArrayList<Interval>();
		intervalList6.add(interval6a);
		intervalList6.add(interval6b);
		intervalList6.add(interval6c);
		command6.setIntervals(intervalList6);
		command6.setDescription("An overdue floating event!");
		System.out.println(logic.addTask(command6));
		
		System.out.println(logic.displayTasks());
	}
}
