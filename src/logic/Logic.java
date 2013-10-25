package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;

import common.CommandType;
import common.Constants;
import common.DisplayMode;

import parser.Parser;
import storage.Storage;

public class Logic {

	protected Storage storage = null;
	protected HashMap<Integer, Integer> temporaryMapping = new HashMap<Integer, Integer>();
	protected boolean isDynamicIndex = false;
	protected boolean isDisplayHelp = false;
	protected Command currentHelpCommand = null;

	public Logic() throws IOException {
		storage = new Storage();
		this.executeCommand("display");
	}

	public ActiveFeedback activeFeedback(String userInput) {
		Command command = new Parser().parse(userInput);
		CommandType commandType = command.getCommandType();
		switch (commandType) {
		case ADD:
			return activeAddTask(command);
		case EDIT:
			return activeEditTask(command);
		case DELETE:
			return activeDeleteTask(command);
		case DONE:
			return activeMarkDone(userInput);
		case FINALISE:
			return activeFinalisaTask(command);
		case SEARCH:
			return activeSearchTasks(command);
		default:
			return null;
		}
	}

	private ActiveFeedback activeSearchTasks(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	private ActiveFeedback activeFinalisaTask(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	private ActiveFeedback activeMarkDone(String userInput) {
		// TODO Auto-generated method stub
		return null;
	}

	private ActiveFeedback activeDeleteTask(Command command) {
		return new ActiveFeedback(command);
	}

	private ActiveFeedback activeEditTask(Command command) {
		return new ActiveFeedback(command);
	}

	private ActiveFeedback activeAddTask(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	public Feedback executeCommand(String userCommand) {
		Command command = new Parser().parse(userCommand);
		CommandType commandType = command.getCommandType();

		switch (commandType) {
		case ADD:
			return addTask(command);
		case DISPLAY:
			return displayTasks(command);
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
		case REDO:
			return redoState();
		case EDIT:
			return editTask(command);
		case HELP:
			return showHelp(command);
		case DONE:
			return markDone(command);
		case FINALISE:
			return finaliseTask(command);
		case SORT:
			return sortTask();
		case GOTO:
			return gotoPage(command);
		default:
			return new Feedback(Constants.SC_INVALID_COMMAND_ERROR,
					CommandType.INVALID);
		}
	}

	public Feedback addTask(Command command) {
		assert (command.getCommandType() == CommandType.ADD);
		Task newTask = new Task(command);
		storage.add(newTask);
		isDynamicIndex = false;
		Feedback feedback = null;
		if (isTaskOver(newTask)) {
			feedback = new Feedback(Constants.SC_SUCCESS_TASK_OVERDUE,
					CommandType.ADD);
		} else {
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.ADD);
		}

		return feedback;
	}

	protected Feedback editTask(Command command) {
		assert (command.getCommandType() == CommandType.EDIT);
		Feedback feedback = null;
		int inputIndex = command.getTaskIndex();
		int taskIndex = inputIndex;

		if (taskIndex > storage.size()
				|| (isDynamicIndex && !temporaryMapping.containsKey(taskIndex))) {
			return new Feedback(Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR,
					CommandType.EDIT);
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
					CommandType.EDIT);
			feedback.setTaskIndex(taskIndex);
		} else {
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.EDIT);
			feedback.setTaskIndex(taskIndex);
		}

		return feedback;
	}

	public int getNumberOfTasks() {
		return storage.size();
	}

	public int getNumberOfRemainingTasks() {
		int count = 0;
		for (Task task : storage) {
			if (!task.isDone()) {
				count++;
			}
		}
		return count;
	}

	public int getNumberOfTasksToday() {
		int count = 0;
		DateTime today = new DateTime();
		for (Task task : storage) {
			if (!task.isDone() && task.isOnDate(today)) {
				count++;
			}
		}
		return count;

	}

	public ArrayList<Task> getTasksToDisplay() {
		ArrayList<Task> output = new ArrayList<Task>();
		System.out.println(temporaryMapping);
		if (!isDynamicIndex) {
			Iterator<Task> storageIterator = storage.iterator();
			while (storageIterator.hasNext()) {
				Task task = storageIterator.next();
				output.add(task);
			}
		} else {
			for (Integer index : temporaryMapping.keySet()) {
				Task task = storage.get(temporaryMapping.get(index));
				output.add(task);
			}
		}
		return output;
	}

	public ArrayList<Task> getTasksToDisplay(DisplayMode displayMode, DateTime dateTime) {
		Command displayCommand = new Command(CommandType.DISPLAY);
		displayCommand.setDisplayMode(displayMode);
		if(displayMode == displayMode.DATE){
			displayCommand.setDisplayDateTime(dateTime);
		}
		displayTasks(displayCommand);
		return getTasksToDisplay();
	}
	

	public boolean displayCondition(Command command, Task task) {
		DisplayMode displayMode = command.getDisplayMode();
		DateTime displayDate = null;
		switch (displayMode) {
		case DATE:
			displayDate = command.getDisplayDateTime();
			return task.isOnDate(displayDate);
		case TODAY:
			displayDate = new DateTime();
			return task.isOnDate(displayDate);
		case TOMORROW:
			displayDate = new DateTime();
			displayDate = displayDate.plusDays(1);
			return task.isOnDate(displayDate);
		case ALL:
			return true;
		case TODO:
			return !task.isDone();
		case TIMED:
			return task.isTimedTask();
		case DEADLINE:
			return task.isDeadlineTask();
		case FLOATING:
			return task.isFloatingTask();
		case UNTIMED:
			return task.isUntimedTask();
		case OVERDUE:
		default:
			return !task.isDone();
		}
	}

	public Feedback displayTasks(Command command) {
		DisplayMode displayMode = command.getDisplayMode();
		Feedback feedback = null;

		ArrayList<Task> validTasks = new ArrayList<Task>();
		ArrayList<Integer> validTasksAbsoluteIndices = new ArrayList<Integer>();
		ArrayList<Task> allTasks = new ArrayList<Task>();
		Iterator<Task> storageIterator = storage.iterator();
		while (storageIterator.hasNext()) {
			allTasks.add(storageIterator.next());
		}
		if (displayMode != DisplayMode.SEARCH) {
			for (int i = 0; i < allTasks.size(); i++) {
				Task currentTask = allTasks.get(i);
				System.out.println(currentTask);
				if (displayCondition(command, currentTask)) {
					validTasks.add(currentTask);
					validTasksAbsoluteIndices.add(i + 1);
				}
			}

			temporaryMapping = new HashMap<Integer, Integer>();
			for (int i = 1; i <= validTasksAbsoluteIndices.size(); i++) {
				temporaryMapping.put(i, validTasksAbsoluteIndices.get(i - 1));
			}
			isDynamicIndex = true;

			if (validTasks.size() > 0) {
				feedback = new Feedback(Constants.SC_SUCCESS,
						CommandType.DISPLAY);

			} else {
				feedback = new Feedback(Constants.SC_NO_TASK_ERROR,
						CommandType.DISPLAY);

			}
		} else {
			feedback = new Feedback(Constants.SC_SUCCESS,
					CommandType.DISPLAY);
		}
		
		feedback.setDisplayMode(displayMode);
		if (displayMode == DisplayMode.DATE) {
			feedback.setDisplayDate(command.getDisplayDateTime());
		}
		return feedback;
	}

	protected Feedback deleteTask(Command command) {
		HashMap<String, String> commandAttributes = command
				.getCommandAttributes();
		int taskIndex = Integer.parseInt(commandAttributes
				.get(Constants.DELETE_ATT_LINE));
		if (isDynamicIndex) {
			taskIndex = temporaryMapping.get(taskIndex);
		}

		Feedback feedback = null;
		if (taskIndex <= storage.size()) {
			String taskDescription = storage.get(taskIndex).getName();
			storage.remove(taskIndex);
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.DELETE);
			feedback.setTaskIndex(taskIndex);
			isDynamicIndex = false;
		} else {
			feedback = new Feedback(Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR,
					CommandType.DELETE);
		}

		return feedback;
	}

	protected Feedback clearTasks(Command command) {
		Feedback feedback = null;
		HashMap<String, String> commandAttributes = command
				.getCommandAttributes();
		boolean isClearDone = Boolean.valueOf(commandAttributes
				.get(Constants.CLEAR_ATT_DONE));

		if (storage.size() > 0) {
			if (isClearDone) {
				Iterator<Task> tasksIterator = storage.iterator();
				ArrayList<Task> doneTasks = new ArrayList<>();

				while (tasksIterator.hasNext()) {
					Task currentTask = tasksIterator.next();
					if (currentTask.getDone()) {
						doneTasks.add(currentTask);
					}
				}

				storage.removeSet(doneTasks);
				feedback = new Feedback(Constants.SC_SUCCESS_CLEAR_DONE,
						CommandType.CLEAR);
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

	protected Feedback finaliseTask(Command command) {
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
		isDynamicIndex = false;

		if (isTaskOver(taskToEdit)) {
			feedback = new Feedback(Constants.SC_SUCCESS_TASK_OVERDUE,
					CommandType.FINALISE);
			feedback.setTaskIndex(taskIndex);
		} else {
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.FINALISE);
			feedback.setTaskIndex(taskIndex);
		}

		return feedback;
	}

	protected Feedback gotoPage(Command command) {
		Feedback feedback = null;
		int pageIndex = command.getPageIndex();

		if (pageIndex != 0) {
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.GOTO);
			feedback.setGotoPage(pageIndex);
		} else {
			feedback = new Feedback(Constants.SC_INVALID_PAGE_INDEX,
					CommandType.GOTO);
		}

		return feedback;
	}

	protected Feedback sortTask() {
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

	protected Feedback markDone(Command command) {
		Feedback feedback = null;
		int taskIndex = command.getTaskIndex();

		if ((isDynamicIndex && !temporaryMapping.containsKey(taskIndex))
				|| taskIndex > storage.size()) {
			feedback = new Feedback(Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR,
					CommandType.DONE);
		} else {
			if (isDynamicIndex) {
				taskIndex = temporaryMapping.get(taskIndex);
			}
			Task doneTask = storage.get(taskIndex);
			doneTask.markDone();
			storage.replace(taskIndex, doneTask);
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.DONE);
			feedback.setTaskIndex(taskIndex);
			isDynamicIndex = false;
		}

		return feedback;
	}

	protected Feedback showHelp(Command command) {
		Feedback feedback = null;
		currentHelpCommand = command;
		CommandType helpCommandType = currentHelpCommand.getHelpCommand();
		isDisplayHelp = true;
		feedback = new Feedback(Constants.SC_SUCCESS, CommandType.HELP);
		feedback.setHelpCommandType(helpCommandType);
		return feedback;
	}

	protected Feedback undoState() {
		Feedback feedback = null;
		try {
			storage.undo();
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.UNDO);
		} catch (Exception e) {
			feedback = new Feedback(Constants.SC_UNDO_NO_PRIOR_STATE_ERROR,
					CommandType.UNDO);
		}

		return feedback;
	}

	protected Feedback redoState() {
		Feedback feedback = null;
		try {
			storage.redo();
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.REDO);
		} catch (Exception e) {
			feedback = new Feedback(Constants.SC_REDO_NO_PRIOR_STATE_ERROR,
					CommandType.REDO);
		}

		return feedback;
	}

	protected Feedback searchTasks(Command command) {
		Feedback feedback = null;
		String searchString = command.getSearchString();
		ArrayList<String> searchTags = command.getTags();

		ArrayList<Task> validTasks = new ArrayList<Task>();
		ArrayList<Integer> validTasksAbsoluteIndices = new ArrayList<Integer>();

		Iterator<Task> storageIterator = storage.iterator();
		System.out.println(searchTags);
		if (searchTags.size() > 0) {
			boolean shouldAdd = true;
			for (int i = 0; i < storage.size(); i++) {
				Task currentTask = storageIterator.next();
				for (String tag : searchTags) {
					if (!isTagInTask(tag, currentTask)) {
						shouldAdd = false;
					}
				}
				if (shouldAdd) {
					validTasks.add(currentTask);
					validTasksAbsoluteIndices.add(i + 1);
				}
				shouldAdd = true;
			}
		} else {
			for (int i = 0; i < storage.size(); i++) {
				Task currentTask = storageIterator.next();
				if (isWordInString(searchString, currentTask.getName())) {
					validTasks.add(currentTask);
					validTasksAbsoluteIndices.add(i + 1);
				}
			}
		}

		temporaryMapping = new HashMap<Integer, Integer>();
		for (int i = 1; i <= validTasksAbsoluteIndices.size(); i++) {
			temporaryMapping.put(i, validTasksAbsoluteIndices.get(i - 1));
		}
		isDynamicIndex = true;
		System.out.println(temporaryMapping);

		feedback = new Feedback(Constants.SC_SUCCESS, CommandType.SEARCH);

		// TODO: add in checks that correspond to errors in Feedback
		feedback.setDisplayMode(DisplayMode.SEARCH);
		return feedback;
	}

	protected void exitProgram() {
		try {
			storage.close();
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected boolean isTaskOver(Task task) {
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

	protected boolean isFloatingTaskOver(List<Interval> possibleTime) {
		boolean isAllSlotOver = true;
		for (Interval slot : possibleTime) {
			if (!isTimePastAlready(slot.getEndDateTime())) {
				isAllSlotOver = false;
			}
		}
		return isAllSlotOver;
	}

	protected boolean isTimePastAlready(DateTime time) {
		return time.compareTo(new DateTime()) < 0;
	}

	protected boolean isWordInString(String word, String string) {
		String lowerCaseString = string.toLowerCase();
		String lowerCaseWord = word.toLowerCase();
		return lowerCaseString.indexOf(lowerCaseWord) != -1;
	}

	public boolean isTagInTask(String tag, Task task) {
		ArrayList<String> tags = task.getTags();
		boolean result = false;
		for(String string : tags){
			if(tag.equalsIgnoreCase(string)){
				result = true;
			}
		}
		return result;
	}

//	public static void main(String[] args) {
//		Logic logic;
//		try {
//			logic = new Logic();
//
//			Command displayCommand = new Command(CommandType.DISPLAY);
//			displayCommand.setDisplayMode(DisplayMode.ALL);
//
//			// Display task test
//			System.out.println(logic.displayTasks(displayCommand));
//
//			// Add task test 1 (deadline task, success not overdue)
//			Command command1 = new Parser()
//					.parse("add submit v01 by 11:59 pm 30/10/2013");
//			System.out.println(logic.addTask(command1) + "\n");
//
//			// Add task test 2 (deadline task, success but overdue)
//			Command command2 = new Command(CommandType.ADD);
//			DateTime date2 = new DateTime(2012, 10, 14, 23, 59, 59);
//			command2.setDeadline(date2);
//			command2.setDescription("Submit overdue V0.1");
//			System.out.println(logic.addTask(command2) + "\n");
//
//			// Add task test 3 (deadline task, success not overdue)
//			Command command3 = new Command(CommandType.ADD);
//			DateTime startDate3 = new DateTime(2013, 10, 30, 15, 0, 0);
//			DateTime endDate3 = new DateTime(2013, 10, 30, 16, 0, 0);
//			Interval interval3 = new Interval();
//			interval3.setStartDateTime(startDate3);
//			interval3.setEndDateTime(endDate3);
//			ArrayList<Interval> intervalList3 = new ArrayList<Interval>();
//			intervalList3.add(interval3);
//			command3.setIntervals(intervalList3);
//			command3.setDescription("CS2105 Test");
//			System.out.println(logic.addTask(command3) + "\n");
//
//			// Add task test 4 (deadline task, success but overdue)
//			Command command4 = new Command(CommandType.ADD);
//			DateTime startDate4 = new DateTime(2012, 9, 30, 15, 0, 0);
//			DateTime endDate4 = new DateTime(2012, 9, 30, 16, 0, 0);
//			Interval interval4 = new Interval();
//			interval4.setStartDateTime(startDate4);
//			interval4.setEndDateTime(endDate4);
//			ArrayList<Interval> intervalList4 = new ArrayList<Interval>();
//			intervalList4.add(interval4);
//			command4.setIntervals(intervalList4);
//			command4.setDescription("CS2105 Test 2012");
//			System.out.println(logic.addTask(command4) + "\n");
//
//			// Add task test 5 (floating task, success not overdue)
//			Command command5 = new Command(CommandType.ADD);
//			DateTime startDate5a = new DateTime(2013, 10, 30, 15, 0, 0);
//			DateTime endDate5a = new DateTime(2013, 10, 30, 16, 0, 0);
//			Interval interval5a = new Interval();
//			interval5a.setStartDateTime(startDate5a);
//			interval5a.setEndDateTime(endDate5a);
//			DateTime startDate5b = new DateTime(2013, 10, 30, 16, 0, 0);
//			DateTime endDate5b = new DateTime(2013, 10, 30, 17, 0, 0);
//			Interval interval5b = new Interval();
//			interval5b.setStartDateTime(startDate5b);
//			interval5b.setEndDateTime(endDate5b);
//			DateTime startDate5c = new DateTime(2013, 10, 30, 17, 0, 0);
//			DateTime endDate5c = new DateTime(2013, 10, 30, 18, 0, 0);
//			Interval interval5c = new Interval();
//			interval5c.setStartDateTime(startDate5c);
//			interval5c.setEndDateTime(endDate5c);
//			ArrayList<Interval> intervalList5 = new ArrayList<Interval>();
//			intervalList5.add(interval5a);
//			intervalList5.add(interval5b);
//			intervalList5.add(interval5c);
//			command5.setIntervals(intervalList5);
//			command5.setDescription("A floating event!");
//			System.out.println(logic.addTask(command5) + "\n");
//
//			// Add task test 6 (floating task, success but overdue)
//			Command command6 = new Command(CommandType.ADD);
//			DateTime startDate6a = new DateTime(2012, 10, 30, 15, 0, 0);
//			DateTime endDate6a = new DateTime(2012, 10, 30, 16, 0, 0);
//			Interval interval6a = new Interval();
//			interval6a.setStartDateTime(startDate6a);
//			interval6a.setEndDateTime(endDate6a);
//			DateTime startDate6b = new DateTime(2012, 10, 30, 16, 0, 0);
//			DateTime endDate6b = new DateTime(2012, 10, 30, 17, 0, 0);
//			Interval interval6b = new Interval();
//			interval6b.setStartDateTime(startDate6b);
//			interval6b.setEndDateTime(endDate6b);
//			DateTime startDate6c = new DateTime(2012, 10, 30, 17, 0, 0);
//			DateTime endDate6c = new DateTime(2012, 10, 30, 18, 0, 0);
//			Interval interval6c = new Interval();
//			interval6c.setStartDateTime(startDate6c);
//			interval6c.setEndDateTime(endDate6c);
//			ArrayList<Interval> intervalList6 = new ArrayList<Interval>();
//			intervalList6.add(interval6a);
//			intervalList6.add(interval6b);
//			intervalList6.add(interval6c);
//			command6.setIntervals(intervalList6);
//			command6.setDescription("An overdue floating event!");
//			System.out.println(logic.addTask(command6) + "\n");
//
//			// Add task test 7 (Task with tags)
//			Command command7 = new Command(CommandType.ADD);
//			DateTime date7 = new DateTime(2013, 10, 11, 22, 00, 00);
//			command7.setDeadline(date7);
//			command7.setDescription("Party");
//			ArrayList<String> tags7 = new ArrayList<String>();
//			tags7.add("TGIF");
//			tags7.add("forfun");
//			command7.setTags(tags7);
//			System.out.println(logic.addTask(command7) + "\n");
//
//			System.out.println(logic.displayTasks(displayCommand) + "\n");
//
//			// Delete task test 8
//			Command command8 = new Command(CommandType.DELETE);
//			command8.setValue("deleteIndex", "1");
//			System.out.println(logic.deleteTask(command8) + "\n");
//
//			System.out.println(logic.displayTasks(displayCommand) + "\n");
//
//			// Help test 9 general
//			Command command9 = new Command(CommandType.HELP);
//			command9.setValue("helpCommand", "");
//			System.out.println(logic.showHelp(command9) + "\n");
//
//			// Help test 10 add command
//			Command command10 = new Command(CommandType.HELP);
//			command10.setValue("helpCommand", "add");
//			System.out.println(logic.showHelp(command10) + "\n");
//
//			// Done test 11
//			Command command11 = new Command(CommandType.DONE);
//			command11.setValue("doneIndex", "1");
//			System.out.println(logic.markDone(command11) + "\n");
//
//			System.out.println(logic.displayTasks(displayCommand) + "\n");
//
//			// Edit test 12 change name
//			Command command12 = new Command(CommandType.EDIT);
//			command12.setDescription("hahahahah");
//			command12.setValue(Constants.EDIT_ATT_LINE, "2");
//			System.out.println(logic.editTask(command12) + "\n");
//
//			System.out.println(logic.displayTasks(displayCommand) + "\n");
//
//			// Edit test 13 change tags
//			Command command13 = new Command(CommandType.EDIT);
//			ArrayList<String> tags13 = new ArrayList<String>();
//			tags13.add("Moretags");
//			tags13.add("Evenmore");
//			command13.setTags(tags13);
//			command13.setValue(Constants.EDIT_ATT_LINE, "6");
//			System.out.println(logic.editTask(command13) + "\n");
//
//			System.out.println(logic.displayTasks(displayCommand) + "\n");
//
//			// Edit test 14 change deadline (same type)
//			Command command14 = new Command(CommandType.EDIT);
//			command14.setDescription("Submit V0.5");
//			DateTime date14 = new DateTime(2012, 11, 11, 23, 59, 59);
//			command14.setDeadline(date14);
//			command14.setValue(Constants.EDIT_ATT_LINE, "1");
//			System.out.println(logic.editTask(command14) + "\n");
//
//			System.out.println(logic.displayTasks(displayCommand) + "\n");
//
//			// Edit test 15 change deadline (same type)
//			Command command15 = new Command(CommandType.EDIT);
//			command15.setDescription("Work on V0.5");
//			DateTime startDate15 = new DateTime(2013, 10, 14, 0, 0, 0);
//			DateTime endDate15 = new DateTime(2013, 11, 10, 23, 59, 59);
//			Interval interval15 = new Interval();
//			interval15.setStartDateTime(startDate15);
//			interval15.setEndDateTime(endDate15);
//			ArrayList<Interval> intervalList15 = new ArrayList<Interval>();
//			intervalList15.add(interval15);
//			command15.setIntervals(intervalList15);
//			ArrayList<String> tags15 = new ArrayList<String>();
//			tags15.add("important");
//			command15.setTags(tags15);
//			command15.setValue(Constants.EDIT_ATT_LINE, "1");
//			System.out.println(logic.editTask(command15) + "\n");
//
//			System.out.println(logic.displayTasks(displayCommand) + "\n");
//
//			// Edit test 16 finalise task
//			Command command16 = new Command(CommandType.FINALISE);
//			command16.setValue(Constants.FINALISE_ATT_LINE, "4");
//			command16.setValue(Constants.FINALISE_ATT_INDEX, "3");
//			System.out.println(logic.finaliseTask(command16) + "\n");
//
//			System.out.println(logic.displayTasks(displayCommand) + "\n");
//
//			// Edit test 17 search task
//			Command command17 = new Command(CommandType.SEARCH);
//			command17.setValue(Constants.TASK_ATT_NAME, "a");
//			System.out.println(logic.searchTasks(command17) + "\n");
//
//			// Delete task test 18
//			Command command18 = new Command(CommandType.DELETE);
//			command18.setValue("deleteIndex", "1");
//			System.out.println(logic.deleteTask(command18) + "\n");
//
//			System.out.println(logic.searchTasks(command17) + "\n");
//
//			// Edit test 19 finalise task
//			Command command19 = new Command(CommandType.FINALISE);
//			command19.setValue(Constants.FINALISE_ATT_LINE, "2");
//			command19.setValue(Constants.FINALISE_ATT_INDEX, "3");
//			System.out.println(logic.finaliseTask(command19) + "\n");
//
//			System.out.println(logic.displayTasks(displayCommand) + "\n");
//
//			System.out.println(logic.searchTasks(command17) + "\n");
//
//			// Done task test 20
//			Command command20 = new Command(CommandType.DONE);
//			command20.setValue(Constants.DONE_ATT_LINE, "1");
//			System.out.println(logic.markDone(command20) + "\n");
//
//			System.out.println(logic.displayTasks(displayCommand) + "\n");
//
//			// Clear done test 21
//			Command command21 = new Command(CommandType.CLEAR);
//			command21.setValue(Constants.CLEAR_ATT_DONE, "true");
//			System.out.println(logic.clearTasks(command21) + "\n");
//
//			System.out.println(logic.displayTasks(displayCommand) + "\n");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
