/**
 * COMMAND LOGIC
 * 
 * This class processes the user's input after the hit "enter". It speaks to the storage of the application. Its main responsibilities are:
 * 1) Call the parser to interpret the entered command
 * 2) Maintain a mapping of index that maps the index which is currently displayed on the UI to the index in the storage
 * 3) Modify the task storage based on input
 * 4) Return display logic the tasks to be displayed based on the temporary mapping
 * 
 */

//@author A0102332A
package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.joda.time.DateTime;

import common.ClearMode;
import common.Command;
import common.CommandType;
import common.Constants;
import common.DisplayMode;
import common.Feedback;
import common.Interval;
import common.InvalidCommandReason;
import common.Task;
import common.TaskType;
import common.undo.ActionStack;

import parser.Parser;
import storage.Storage;

public class CommandLogic {

	private final int FINALISE_INDEX_OFFSET = 1;
	
	protected Storage storage = null;
	protected TreeMap<Integer, Integer> temporaryMapping = new TreeMap<Integer, Integer>();
	protected boolean isDynamicIndex = false;
	protected boolean isDisplayHelp = false;
	protected Command currentHelpCommand = null;
	protected ActionStack actionStack = ActionStack.getInstance();
	
	public CommandLogic() throws IOException {
		storage = new Storage(Constants.DEFAULT_FILENAME);
		this.executeCommand(Constants.COMMAND_DISPLAY);
	}

	public Feedback executeCommand(String userCommand) {
		Command command = Parser.parse(userCommand);
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
			return exit();
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
			return invalidCommand(command);
		}
	}
	
	/**
	 * Main Command Operations
	 */
	
	/**
	 * Invalid is used to disambiguate errors from parser
	 */
	public Feedback invalidCommand(Command command) {
		InvalidCommandReason error = command.getInvalidCommandReason();
		Feedback feedback = null;
		switch (error) {
		case EMPTY_COMMAND:
			feedback = new Feedback(Constants.SC_EMPTY_COMMAND_ERROR,
					CommandType.INVALID);
			break;
		case INVALID_DATE:
			feedback = new Feedback(Constants.SC_INVALID_DATE_ERROR,
					CommandType.INVALID);
			break;
		case INVALID_PAGE_INDEX:
			feedback = new Feedback(Constants.SC_INVALID_PAGE_INDEX_ERROR,
					CommandType.INVALID);
			break;
		case INVALID_SEARCH_PARAMETERS:
			feedback = new Feedback(
					Constants.SC_INVALID_SEARCH_PARAMETERS_ERROR,
					CommandType.INVALID);
			break;
		case INVALID_TASK_INDEX:
			feedback = new Feedback(Constants.SC_INVALID_TASK_INDEX_ERROR,
					CommandType.INVALID);
			break;
		case INVALID_TIMESLOT_INDEX:
			feedback = new Feedback(Constants.SC_INVALID_TIMESLOT_INDEX_ERROR,
					CommandType.INVALID);
			break;
		case TOO_FEW_ARGUMENTS:
			feedback = new Feedback(Constants.SC_TOO_FEW_ARGUMENTS_ERROR,
					CommandType.INVALID);
			break;
		case UNRECOGNIZED_COMMAND:
			feedback = new Feedback(Constants.SC_UNRECOGNIZED_COMMAND_ERROR,
					CommandType.INVALID);
			break;
		default:
			feedback = new Feedback(Constants.SC_INVALID_COMMAND_ERROR,
					CommandType.INVALID);
			break;
		}
		return feedback;
	}

	public Feedback addTask(Command command) {
		Task newTask = new Task(command);
		storage.add(newTask);
		isDynamicIndex = false;
		Feedback feedback = null;
		if (isTaskOver(newTask)) {
			feedback = new Feedback(Constants.SC_SUCCESS_TASK_OVERDUE,
					CommandType.ADD);
			feedback.setAddedTask(newTask);
		} else {
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.ADD);
			feedback.setAddedTask(newTask);
		}
		return feedback;
	}
	
	/**
	 * Operations related to task index
	 * 
	 * The following operations takes in a task index that refers to a specific task in the storage. They follow a consistent format in checking the validity of the task index as well as obtaining the correct mapping 
	 * 
	 */

	protected Feedback editTask(Command command) {
		Feedback feedback = null;
		int taskIndex = command.getTaskIndex();

		if (checkTaskIndexValidity(taskIndex)) {
			feedback = new Feedback(Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR,
					CommandType.DONE);
		} else {
			if (isDynamicIndex) {
				taskIndex = temporaryMapping.get(taskIndex);
			}
			
			Task taskToEdit = storage.get(taskIndex);
			int finaliseIndex = command.getTimeslotIndex();
			
			// Check if it edits a specific time slot for tentative task
			if (finaliseIndex > 0) {
				List<Interval> possibleIntervals = taskToEdit.getPossibleTime();
				if (finaliseIndex > possibleIntervals.size()) {
					feedback = new Feedback(
							Constants.SC_INTEGER_OUT_OF_BOUNDS_TIME_ERROR,
							CommandType.EDIT);
				} else if (command.getIntervals().isEmpty()) {
					feedback = new Feedback(Constants.SC_EMPTY_DESCRIPTION_ERROR,
							CommandType.EDIT);
				} else {
					possibleIntervals.remove(finaliseIndex - FINALISE_INDEX_OFFSET);
					possibleIntervals.add(finaliseIndex - FINALISE_INDEX_OFFSET, command.getIntervals()
							.get(0));
					taskToEdit.setPossibleTime(possibleIntervals);
					storage.replace(taskIndex, taskToEdit);
					if (isTaskOver(taskToEdit)) {
						feedback = new Feedback(Constants.SC_SUCCESS_TASK_OVERDUE,
								CommandType.EDIT);
						feedback.setTaskIndex(taskIndex);
					} else {
						feedback = new Feedback(Constants.SC_SUCCESS,
								CommandType.EDIT);
						feedback.setTaskIndex(taskIndex);
					}
				}
			} else {
				TaskType finalType = command.getTaskType();

				if (!command.getDescription().isEmpty()) {
					taskToEdit.setName(command.getDescription());
				}

				if (command.getTags() != null) {
					ArrayList<String> originalTags = taskToEdit.getTags();
					originalTags.addAll(command.getTags());
				}
				if (finalType != TaskType.UNTIMED) {
					taskToEdit.setType(finalType);

					if (finalType == TaskType.DEADLINE) {
						taskToEdit.setDeadline(command.getDeadline());
					} else if (finalType == TaskType.TIMED) {
						taskToEdit.setInterval(command.getIntervals().get(0));
					} else if (finalType == TaskType.TENTATIVE) {
						taskToEdit.setPossibleTime(command.getIntervals());
					}
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
			}
		}

		return feedback;
	}



	protected Feedback deleteTask(Command command) {
		Feedback feedback = null;
		int taskIndex = command.getTaskIndex();

		if (checkTaskIndexValidity(taskIndex)) {
			feedback = new Feedback(Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR,
					CommandType.DONE);
		} else {
			if (isDynamicIndex) {
				taskIndex = temporaryMapping.get(taskIndex);
			}
			storage.remove(taskIndex);
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.DELETE);
			feedback.setTaskIndex(taskIndex);
			isDynamicIndex = false;
		}

		return feedback;
	}

	protected Feedback finaliseTask(Command command) {
		Feedback feedback = null;
		int taskIndex = command.getTaskIndex();

		if (checkTaskIndexValidity(taskIndex)) {
			feedback = new Feedback(Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR,
					CommandType.DONE);
		} else {
			if (isDynamicIndex) {
				taskIndex = temporaryMapping.get(taskIndex);
			}
			Task taskToEdit = storage.get(taskIndex);
			if (!taskToEdit.isFloatingTask()) {
				return new Feedback(Constants.SC_FINALISE_TYPE_MISMATCH_ERROR,
						CommandType.FINALISE);
			}

			int taskSlotIndex = command.getTimeslotIndex();
			List<Interval> oldIntervalList = taskToEdit.getPossibleTime();
			if (taskSlotIndex > oldIntervalList.size() || taskSlotIndex < 1) {
				return new Feedback(
						Constants.SC_INTEGER_OUT_OF_BOUNDS_TIME_ERROR,
						CommandType.FINALISE);
			}

			Interval newInterval = oldIntervalList.get(taskSlotIndex - 1);
			taskToEdit.setType(TaskType.TIMED);
			taskToEdit.setInterval(newInterval);

			storage.replace(taskIndex, taskToEdit);
			isDynamicIndex = false;

			if (isTaskOver(taskToEdit)) {
				feedback = new Feedback(Constants.SC_SUCCESS_TASK_OVERDUE,
						CommandType.FINALISE);
				feedback.setTaskIndex(taskIndex);
			} else {
				feedback = new Feedback(Constants.SC_SUCCESS,
						CommandType.FINALISE);
				feedback.setTaskIndex(taskIndex);
			}
		}

		return feedback;
	}

	protected Feedback markDone(Command command) {
		Feedback feedback = null;
		int taskIndex = command.getTaskIndex();

		if (checkTaskIndexValidity(taskIndex)) {
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
	
	/**
	 * Other main operations
	 */

	protected Feedback clearTasks(Command command) {
		Feedback feedback = null;
		ClearMode clearMode = command.getClearMode();

		if (storage.size() > 0) {
			if (clearMode == ClearMode.ALL) {
				storage.clear();
				feedback = new Feedback(Constants.SC_SUCCESS, CommandType.CLEAR);
				isDynamicIndex = false;
			} else {
				Iterator<Task> tasksIterator = storage.iterator();
				ArrayList<Task> doneTasks = new ArrayList<>();

				while (tasksIterator.hasNext()) {

					Task currentTask = tasksIterator.next();
					boolean condition = false;

					switch (clearMode) {
					case DEADLINE:
						condition = currentTask.isDeadlineTask();
						break;
					case TIMED:
						condition = currentTask.isTimedTask();
						break;
					case TENTATIVE:
						condition = currentTask.isFloatingTask();
						break;
					case UNTIMED:
						condition = currentTask.isUntimedTask();
						break;
					case OVERDUE:
						condition = currentTask.isOverdue();
						break;
					case DATE:
						condition = currentTask.isOnDate(command
								.getClearDateTime());
						break;
					case DONE:
						condition = currentTask.isDone();
						break;
					case INVALID:
						assert false : "Invalid clear mode, either an error in above or parser logic";
					default:
						assert false : "Error in clear mode logic";
					}

					if (condition) {
						doneTasks.add(currentTask);
					}
				}

				storage.removeSet(doneTasks);
				feedback = new Feedback(Constants.SC_SUCCESS_CLEAR_DONE,
						CommandType.CLEAR);
				isDynamicIndex = false;
			}
		} else {
			feedback = new Feedback(Constants.SC_NO_TASK_ERROR,
					CommandType.CLEAR);
		}
		return feedback;
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
	
				if (displayCondition(command, currentTask)) {
					validTasks.add(currentTask);
					validTasksAbsoluteIndices.add(i + 1);
				}
			}
	
			temporaryMapping = new TreeMap<Integer, Integer>();
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
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.DISPLAY);
		}
	
		feedback.setDisplayMode(displayMode);
		if (displayMode == DisplayMode.DATE) {
			feedback.setDisplayDate(command.getDisplayDateTime());
		}
		return feedback;
	}

	protected Feedback searchTasks(Command command) {
		Feedback feedback = null;
	
		ArrayList<String> searchTags = command.getTags();
		ArrayList<String> searchTerms = command.getSearchTerms();
	
		ArrayList<Task> validTasks = new ArrayList<Task>();
		ArrayList<Integer> validTasksAbsoluteIndices = new ArrayList<Integer>();
	
		Iterator<Task> storageIterator = storage.iterator();
	
		if (!searchTags.isEmpty()) {
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
				if (areAllWordsInString(searchTerms, currentTask.getName())) {
					validTasks.add(currentTask);
					validTasksAbsoluteIndices.add(i + 1);
				}
			}
		}
	
		temporaryMapping = new TreeMap<Integer, Integer>();
		for (int i = 1; i <= validTasksAbsoluteIndices.size(); i++) {
			temporaryMapping.put(i, validTasksAbsoluteIndices.get(i - 1));
		}
		isDynamicIndex = true;
	
		feedback = new Feedback(Constants.SC_SUCCESS, CommandType.SEARCH);
		feedback.setDisplayMode(DisplayMode.SEARCH);
		return feedback;
	}

	protected Feedback gotoPage(Command command) {
		Feedback feedback = null;
		int pageIndex = command.getPageIndex();

		if (pageIndex > 0) {
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.GOTO);
			feedback.setPageNumber(pageIndex);
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
			actionStack.flushCurrentActionSet();
			actionStack.undo();
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
			actionStack.flushCurrentActionSet();
			actionStack.redo();
			feedback = new Feedback(Constants.SC_SUCCESS, CommandType.REDO);
		} catch (Exception e) {
			feedback = new Feedback(Constants.SC_REDO_NO_PRIOR_STATE_ERROR,
					CommandType.REDO);
		}

		return feedback;
	}

	protected Feedback exit() {
		Feedback feedback = new Feedback(Constants.SC_SUCCESS, CommandType.EXIT);

		try {
			storage.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return feedback;
	}
	
	/**
	 * Methods used to obtain task information to be displayed
	 */

	public int getNumberOfTasks() {
		if (isDynamicIndex) {
			return temporaryMapping.keySet().size();
		} else {
			return storage.size();
		}
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

	public ArrayList<Task> getTasksToDisplay(DisplayMode displayMode,
			DateTime dateTime) {
		Command displayCommand = new Command(CommandType.DISPLAY);
		displayCommand.setDisplayMode(displayMode);
		if (displayMode == displayMode.DATE) {
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
		case DONE:
			return task.isDone();
		case TIMED:
			return task.isTimedTask();
		case DEADLINE:
			return task.isDeadlineTask();
		case TENTATIVE:
			return task.isFloatingTask();
		case UNTIMED:
			return task.isUntimedTask();
		case OVERDUE:
			return task.isOverdue();
		default:
			return !task.isDone();
		}
	}

	
	/**
	 * Helper methods
	 */
	private boolean checkTaskIndexValidity(int taskIndex) {
		return (isDynamicIndex && !temporaryMapping.containsKey(taskIndex))
				|| (taskIndex > storage.size() || taskIndex < 1);
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

	protected boolean areAllWordsInString(ArrayList<String> words, String string) {
		for (String word : words) {
			if (!isWordInString(word, string)) {
				return false;
			}
		}
		return true;
	}

	protected boolean isWordInString(String word, String string) {
		String lowerCaseString = string.toLowerCase();
		String lowerCaseWord = word.toLowerCase();
		return lowerCaseString.indexOf(lowerCaseWord) != -1;
	}

	public boolean isTagInTask(String tagInQuestion, Task task) {
		ArrayList<String> tags = task.getTags();
		boolean result = false;
		for (String tag : tags) {
			if (isWordInString(tagInQuestion, tag)) {
				result = true;
			}
		}
		return result;
	}

	public void notifyStorage() {
		storage.fileWriteNotify();
	}

	public void forceFileWrite() {
		try {
			storage.writeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
