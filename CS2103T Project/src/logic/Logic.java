package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;

import parser.Parser;
import storage.Storage;

public class Logic {

	protected static Storage storage = null;
	protected static HashMap<Integer, Integer> temporaryMapping = new HashMap<Integer, Integer>();
	protected static boolean isDynamicIndex = false;

	public Logic() throws IOException {
		storage = new Storage("default.txt");
	}

	public Feedback executeCommand(String userCommand) {
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

	public static Feedback addTask(Command command) {
		Task newTask = new Task(command);
		storage.add(newTask);
		isDynamicIndex = false;
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

	protected static Feedback editTask(Command command) {
		Feedback feedback = null;
		HashMap<String, String> commandAttributes = command
				.getCommandAttributes();
		int inputIndex = Integer.parseInt(commandAttributes
				.get(Constants.EDIT_ATT_LINE));
		int taskIndex = inputIndex;

		if (taskIndex > storage.size()
				|| (isDynamicIndex && !temporaryMapping.containsKey(taskIndex))) {
			return new Feedback(Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR,
					CommandType.EDIT_TASK);
		} else if (isDynamicIndex && temporaryMapping.containsKey(taskIndex)) {
			taskIndex = temporaryMapping.get(inputIndex);
		}
		Task taskToEdit = storage.get(taskIndex);
		String finalType = command.getTaskType();

		if (!command.getDescription().isEmpty()) {
			taskToEdit.setName(command.getDescription());
		}

		if (command.getTags() != null) {
			ArrayList<String> originalTags = taskToEdit.getTags();
			originalTags.addAll(command.getTags());
		}

		if (finalType == Constants.TASK_TYPE_DEADLINE) {
			taskToEdit.setType(Constants.TASK_TYPE_DEADLINE);
			taskToEdit.setDeadline(command.getDeadline());
		} else if (finalType == Constants.TASK_TYPE_TIMED) {
			taskToEdit.setType(Constants.TASK_TYPE_TIMED);
			taskToEdit.setInterval(command.getIntervals().get(0));
		} else if (finalType == Constants.TASK_TYPE_FLOATING) {
			taskToEdit.setType(Constants.TASK_TYPE_FLOATING);
			taskToEdit.setPossibleTime(command.getIntervals());
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
	
	public String displayOnWindow(){
		String feedback = null;
		StringBuilder output = new StringBuilder();
		if(!isDynamicIndex){
			if (storage.size() > 0) {
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
				feedback = output.toString();
			} else {
				feedback = "No Task";
			}
		} else {
			for (Integer index : temporaryMapping.keySet()) {
				Task task = storage.get(temporaryMapping.get(index));
				output.append(index + ". ");
				output.append(task.toString());
				if (index < storage.size()) {
					output.append("\n");
				}
				index++;
			}
			feedback = output.toString();
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

	protected static Feedback deleteTask(Command command) {
		HashMap<String, String> commandAttributes = command
				.getCommandAttributes();
		int lineNumber = Integer.parseInt(commandAttributes
				.get(Constants.DELETE_ATT_LINE));
		if (isDynamicIndex) {
			lineNumber = temporaryMapping.get(lineNumber);
		}

		Feedback feedback = null;
		if (lineNumber <= storage.size()) {
			String taskDescription = storage.get(lineNumber).getName();
			storage.remove(lineNumber);
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.DELETE,
					taskDescription);
			isDynamicIndex = false;
		} else {
			feedback = new Feedback(Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR,
					CommandType.DELETE);
		}

		return feedback;
	}

	protected static Feedback clearTasks(Command command) {
		Feedback feedback = null;
		HashMap<String, String> commandAttributes = command
				.getCommandAttributes();
		boolean isClearDone = Boolean.valueOf(commandAttributes
				.get(Constants.CLEAR_ATT_DONE));

		if (storage.size() > 0) {
			if (isClearDone) {
				for(int i = storage.size(); i > 0; i--){
					Task currentTask = storage.get(i);
					if(currentTask.getDone()){
						storage.remove(i);
					}
				}
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.CLEAR);
				isDynamicIndex = false;
			} else {
				storage.clear();
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.CLEAR);
				isDynamicIndex = false;
			}
		} else {
			feedback = new Feedback(Constants.SC_NO_TASK_ERROR,
					CommandType.CLEAR);
		}
		return feedback;
	}

	// Not working yet
	protected static Feedback finaliseTask(Command command) {
		Feedback feedback = null;
		HashMap<String, String> commandAttributes = command
				.getCommandAttributes();
		int inputIndex = Integer.parseInt(commandAttributes
				.get(Constants.FINALISE_ATT_LINE));
		int taskIndex = inputIndex;

		if (taskIndex > storage.size()
				|| (isDynamicIndex && !temporaryMapping.containsKey(taskIndex))) {
			return new Feedback(Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR,
					CommandType.FINALISE);
		}

		if (isDynamicIndex && temporaryMapping.containsKey(taskIndex)) {
			taskIndex = temporaryMapping.get(inputIndex);
		}

		Task taskToEdit = storage.get(taskIndex);
		if (!taskToEdit.isFloatingTask()) {
			return new Feedback(Constants.SC_FINALISE_TYPE_MISMATCH_ERROR,
					CommandType.FINALISE);
		}

		int taskSlotIndex = Integer.parseInt(commandAttributes
				.get(Constants.FINALISE_ATT_INDEX));
		List<Interval> oldIntervalList = taskToEdit.getPossibleTime();
		if (taskSlotIndex > oldIntervalList.size()) {
			return new Feedback(Constants.SC_INTEGER_OUT_OF_BOUNDS_TIME_ERROR,
					CommandType.FINALISE);
		}

		Interval newInterval = oldIntervalList.get(taskSlotIndex - 1);
		taskToEdit.setType(Constants.TASK_TYPE_TIMED);
		taskToEdit.setInterval(newInterval);
		
		storage.replace(taskIndex, taskToEdit);

		if (isTaskOver(taskToEdit)) {
			feedback = new Feedback(Constants.SC_SUCCESS_TASK_OVERDUE,
					CommandType.FINALISE, taskToEdit.toString());
		} else {
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.FINALISE,
					taskToEdit.toString());
		}

		return feedback;
	}

	protected static Feedback sortTask() {
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

	// Not working yet
	protected static Feedback markDone(Command command) {
		HashMap<String, String> commandAttributes = command
				.getCommandAttributes();
		int lineNumber = Integer.parseInt(commandAttributes
				.get(Constants.DONE_ATT_LINE));
		if (isDynamicIndex) {
			lineNumber = temporaryMapping.get(lineNumber);
		}

		Feedback feedback = null;

		if (lineNumber <= storage.size()) {
			Task doneTask = storage.get(lineNumber);
			doneTask.markDone();
			storage.replace(lineNumber, doneTask);
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.DONE,
					doneTask.getName());
			isDynamicIndex = false;
		} else {
			feedback = new Feedback(Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR,
					CommandType.DONE);
		}

		return feedback;
	}

	protected static Feedback showHelp(Command command) {
		Feedback feedback = null;
		HashMap<String, String> commandAttributes = command
				.getCommandAttributes();
		if (commandAttributes.containsKey("helpCommand")) {
			String commandString = commandAttributes.get("helpCommand");
			CommandType commandToGetHelp = Parser
					.determineCommandType(commandString);
			switch (commandToGetHelp) {
			case ADD_TASK:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_ADD_TASK);
				break;
			case EDIT_TASK:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_EDIT_TASK);
				break;
			case SORT:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_SORT);
				break;
			case DELETE:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_DELETE);
				break;
			case CLEAR:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_CLEAR);
				break;
			case UNDO:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_UNDO);
				break;
			case SEARCH:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_SEARCH);
				break;
			case HELP:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_HELP);
				break;
			case DONE:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_DONE);
				break;
			case FINALISE:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_FINALISE);
				break;
			case DISPLAY:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_DISPLAY);
				break;
			case EXIT:
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP,
						Constants.HELP_EXIT);
				break;
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

	protected static Feedback undoState() {
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

	protected static Feedback searchTasks(Command command) {
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

		temporaryMapping = new HashMap<Integer, Integer>();
		for (int i = 1; i <= validTasksAbsoluteIndices.size(); i++) {
			temporaryMapping.put(i, validTasksAbsoluteIndices.get(i - 1));
		}
		isDynamicIndex = true;

		if (validTasks.size() > 0) {
			StringBuilder output = new StringBuilder();
			int index = 1;
			for (Task task : validTasks) {
				output.append(index + ". ");
				output.append(task.toString());
				if (index < validTasks.size()) {
					output.append("\n");
				}
				index++;
			}
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.SEARCH,
					output.toString());
		} else {
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.SEARCH,
					Constants.MSG_NO_RESULT);
		}
		return feedback;
	}

	protected static void exitProgram() {
		try {
			storage.close();
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	protected static boolean isTaskOver(Task task) {
		if (task.isDeadlineTask()) {
			DateTime deadline = task.getDeadline();
			return isTimePastAlready(deadline);
		} else if (task.isTimedTask()) {
			DateTime endTime = task.getEndTime();
			return isTimePastAlready(endTime);
		} else if (task.isFloatingTask()) {
			List<Interval> possibleTime = task.getPossibleTime();
			return isFloatingTaskOver(possibleTime);
		} else {
			return false;
		}
	}

	protected static boolean isFloatingTaskOver(List<Interval> possibleTime) {
		boolean isAllSlotOver = true;
		for (Interval slot : possibleTime) {
			if (!isTimePastAlready(slot.getEnd())) {
				isAllSlotOver = false;
			}
		}
		return isAllSlotOver;
	}

	protected static boolean isTimePastAlready(DateTime time) {
		return time.compareTo(new DateTime()) < 0;
	}

	protected static boolean isWordInString(String word, String string) {
		String lowerCaseString = string.toLowerCase();
		String lowerCaseWord = word.toLowerCase();
		return lowerCaseString.indexOf(lowerCaseWord) != -1;
	}

	public static void main(String[] args) throws IOException {
		Logic logic = new Logic();

		// Display task test
		System.out.println(logic.displayTasks());

		// Add task test 1 (deadline task, success not overdue)
		Command command1 = new Parser()
				.parse("add submit v01 by 11:59 pm 30/10/2013");
		System.out.println(logic.addTask(command1) + "\n");

		// Add task test 2 (deadline task, success but overdue)
		Command command2 = new Command(CommandType.ADD_TASK);
		DateTime date2 = new DateTime(2012, 10, 14, 23, 59, 59);
		command2.setDeadline(date2);
		command2.setDescription("Submit overdue V0.1");
		System.out.println(logic.addTask(command2) + "\n");

		// Add task test 3 (deadline task, success not overdue)
		Command command3 = new Command(CommandType.ADD_TASK);
		DateTime startDate3 = new DateTime(2013, 10, 30, 15, 0, 0);
		DateTime endDate3 = new DateTime(2013, 10, 30, 16, 0, 0);
		Interval interval3 = new Interval();
		interval3.setStart(startDate3);
		interval3.setEnd(endDate3);
		ArrayList<Interval> intervalList3 = new ArrayList<Interval>();
		intervalList3.add(interval3);
		command3.setIntervals(intervalList3);
		command3.setDescription("CS2105 Test");
		System.out.println(logic.addTask(command3) + "\n");

		// Add task test 4 (deadline task, success but overdue)
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
		System.out.println(logic.addTask(command4) + "\n");

		// Add task test 5 (floating task, success not overdue)
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
		System.out.println(logic.addTask(command5) + "\n");

		// Add task test 6 (floating task, success but overdue)
		Command command6 = new Command(CommandType.ADD_TASK);
		DateTime startDate6a = new DateTime(2012, 10, 30, 15, 0, 0);
		DateTime endDate6a = new DateTime(2012, 10, 30, 16, 0, 0);
		Interval interval6a = new Interval();
		interval6a.setStart(startDate6a);
		interval6a.setEnd(endDate6a);
		DateTime startDate6b = new DateTime(2012, 10, 30, 16, 0, 0);
		DateTime endDate6b = new DateTime(2012, 10, 30, 17, 0, 0);
		Interval interval6b = new Interval();
		interval6b.setStart(startDate6b);
		interval6b.setEnd(endDate6b);
		DateTime startDate6c = new DateTime(2012, 10, 30, 17, 0, 0);
		DateTime endDate6c = new DateTime(2012, 10, 30, 18, 0, 0);
		Interval interval6c = new Interval();
		interval6c.setStart(startDate6c);
		interval6c.setEnd(endDate6c);
		ArrayList<Interval> intervalList6 = new ArrayList<Interval>();
		intervalList6.add(interval6a);
		intervalList6.add(interval6b);
		intervalList6.add(interval6c);
		command6.setIntervals(intervalList6);
		command6.setDescription("An overdue floating event!");
		System.out.println(logic.addTask(command6) + "\n");

		// Add task test 7 (Task with tags)
		Command command7 = new Command(CommandType.ADD_TASK);
		DateTime date7 = new DateTime(2013, 10, 11, 22, 00, 00);
		command7.setDeadline(date7);
		command7.setDescription("Party");
		ArrayList<String> tags7 = new ArrayList<String>();
		tags7.add("TGIF");
		tags7.add("forfun");
		command7.setTags(tags7);
		System.out.println(logic.addTask(command7) + "\n");

		System.out.println(logic.displayTasks() + "\n");

		// Delete task test 8
		Command command8 = new Command(CommandType.DELETE);
		command8.setValue("deleteIndex", "1");
		System.out.println(logic.deleteTask(command8) + "\n");

		System.out.println(logic.displayTasks() + "\n");

		// Help test 9 general
		Command command9 = new Command(CommandType.HELP);
		command9.setValue("helpCommand", "");
		System.out.println(logic.showHelp(command9) + "\n");

		// Help test 10 add command
		Command command10 = new Command(CommandType.HELP);
		command10.setValue("helpCommand", "add");
		System.out.println(logic.showHelp(command10) + "\n");

		// Done test 11
		Command command11 = new Command(CommandType.DONE);
		command11.setValue("doneIndex", "1");
		System.out.println(logic.markDone(command11) + "\n");

		System.out.println(logic.displayTasks() + "\n");

		// Edit test 12 change name
		Command command12 = new Command(CommandType.EDIT_TASK);
		command12.setDescription("hahahahah");
		command12.setValue(Constants.EDIT_ATT_LINE, "2");
		System.out.println(logic.editTask(command12) + "\n");

		System.out.println(logic.displayTasks() + "\n");

		// Edit test 13 change tags
		Command command13 = new Command(CommandType.EDIT_TASK);
		ArrayList<String> tags13 = new ArrayList<String>();
		tags13.add("Moretags");
		tags13.add("Evenmore");
		command13.setTags(tags13);
		command13.setValue(Constants.EDIT_ATT_LINE, "6");
		System.out.println(logic.editTask(command13) + "\n");

		System.out.println(logic.displayTasks() + "\n");

		// Edit test 14 change deadline (same type)
		Command command14 = new Command(CommandType.EDIT_TASK);
		command14.setDescription("Submit V0.5");
		DateTime date14 = new DateTime(2012, 11, 11, 23, 59, 59);
		command14.setDeadline(date14);
		command14.setValue(Constants.EDIT_ATT_LINE, "1");
		System.out.println(logic.editTask(command14) + "\n");

		System.out.println(logic.displayTasks() + "\n");

		// Edit test 15 change deadline (same type)
		Command command15 = new Command(CommandType.EDIT_TASK);
		command15.setDescription("Work on V0.5");
		DateTime startDate15 = new DateTime(2013, 10, 14, 0, 0, 0);
		DateTime endDate15 = new DateTime(2013, 11, 10, 23, 59, 59);
		Interval interval15 = new Interval();
		interval15.setStart(startDate15);
		interval15.setEnd(endDate15);
		ArrayList<Interval> intervalList15 = new ArrayList<Interval>();
		intervalList15.add(interval15);
		command15.setIntervals(intervalList15);
		ArrayList<String> tags15 = new ArrayList<String>();
		tags15.add("important");
		command15.setTags(tags15);
		command15.setValue(Constants.EDIT_ATT_LINE, "1");
		System.out.println(logic.editTask(command15) + "\n");

		System.out.println(logic.displayTasks() + "\n");

		// Edit test 16 finalise task
		Command command16 = new Command(CommandType.FINALISE);
		command16.setValue(Constants.FINALISE_ATT_LINE, "4");
		command16.setValue(Constants.FINALISE_ATT_INDEX, "3");
		System.out.println(logic.finaliseTask(command16) + "\n");

		System.out.println(logic.displayTasks() + "\n");

		// Edit test 17 search task
		Command command17 = new Command(CommandType.SEARCH);
		command17.setValue(Constants.TASK_ATT_NAME, "a");
		System.out.println(logic.searchTasks(command17) + "\n");

		// Delete task test 18
		Command command18 = new Command(CommandType.DELETE);
		command18.setValue("deleteIndex", "1");
		System.out.println(logic.deleteTask(command18) + "\n");

		System.out.println(logic.searchTasks(command17) + "\n");

		// Edit test 19 finalise task
		Command command19 = new Command(CommandType.FINALISE);
		command19.setValue(Constants.FINALISE_ATT_LINE, "2");
		command19.setValue(Constants.FINALISE_ATT_INDEX, "3");
		System.out.println(logic.finaliseTask(command19) + "\n");

		System.out.println(logic.displayTasks() + "\n");
		
		System.out.println(logic.searchTasks(command17) + "\n");
		
		// Done task test 20
		Command command20 = new Command(CommandType.DONE);
		command20.setValue(Constants.DONE_ATT_LINE, "1");
		System.out.println(logic.markDone(command20) + "\n");

		System.out.println(logic.displayTasks() + "\n");
		
		// Clear done test 21
		Command command21 = new Command(CommandType.CLEAR);
		command21.setValue(Constants.CLEAR_ATT_DONE, "true");
		System.out.println(logic.clearTasks(command21) + "\n");

		System.out.println(logic.displayTasks() + "\n");
		
		System.out.println(logic.displayOnWindow());
	}
}
