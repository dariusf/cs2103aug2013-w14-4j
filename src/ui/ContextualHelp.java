package ui;

import logic.Command;
import common.Constants;

public class ContextualHelp {
	private final static String DESC = "<description>";
	private final static String DESC_OPTIONAL = "{description}";
	private final static String DEADLINE = "{by}";
	private final static String INTERVAL_ALTERNATIVE = "{or}";
	private final static String INTERVAL = "{at|on|from..to}";
	private final static String HASHTAG = "{#hashtag}";
	private final static String TASK_INDEX = "<task index>";
	private final static String TIMESLOT_INDEX = "<timeslot index>";
	private final static String TIMESLOT_INDEX_OPTIONAL = "{timeslot index}";
	private final static String CLEAR_MODE = "{deadline|timed|<date>|done|overdue|...}";
	private final static String DISPLAY_MODE = "{done|today|tomorrow|deadline|timed|...}";
	private final static String DISPLAY_MODE_TIP = "Type 'help display' to see more options.";
	private final static String CLEAR_MODE_TIP = "Type 'help clear' to see more options.";
	private final static String SEARCH_TERMS = "{keyword|hashtag}+";
	private final static String COMMAND = "{command}";
	private final static String PAGE_INDEX = "{page number}";
	private final static String COMMAND_FORMAT_SORT = "Tasks will be sorted by date.";
	private final static String COMMAND_FORMAT_UNDO = "Undo reverts the last operation on the task list.";
	private final static String COMMAND_FORMAT_REDO = "Redo reverts the last undo.";
	private final static String COMMAND_FORMAT_EXIT = "Exit quits the application.";

	Command command;
	
	public ContextualHelp(Command command) {
		setCommand(command);
	}

	private String determineFeedback() {
		switch (command.getCommandType()) {
		case ADD:
			if (command.getDescription().isEmpty()) {
				return constructFeedbackString(Constants.COMMAND_ADD, highlight(DESC), DEADLINE, INTERVAL, INTERVAL_ALTERNATIVE, "\n", HASHTAG);
			}
			else {
				boolean hasIntervalsNorDeadline = command.hasDeadline() || command.hasIntervals();
				String interval = hasIntervalsNorDeadline ? INTERVAL : highlight(INTERVAL);
				String deadline = hasIntervalsNorDeadline ? DEADLINE : highlight(DEADLINE);
				String alternative = !command.hasDeadline() ? highlight(INTERVAL_ALTERNATIVE) : INTERVAL_ALTERNATIVE;
				return constructFeedbackString(Constants.COMMAND_ADD, DESC, deadline, interval, alternative, "\n", highlight(HASHTAG));
			}
		case EDIT:
			if (!command.hasTaskIndex()) {
				return constructFeedbackString(Constants.COMMAND_EDIT, highlight(TASK_INDEX), TIMESLOT_INDEX_OPTIONAL, DESC, DEADLINE, "\n", INTERVAL, INTERVAL_ALTERNATIVE, HASHTAG);
			}
			else {
				String timeslotIndex = command.hasDeadline() || command.hasIntervals() || command.hasTags() || !command.getDescription().isEmpty() ? TIMESLOT_INDEX_OPTIONAL : highlight(TIMESLOT_INDEX_OPTIONAL);
				String desc = command.getDescription().isEmpty() ? highlight(DESC_OPTIONAL) : DESC_OPTIONAL;
				boolean hasIntervalsNorDeadline = command.hasDeadline() || command.hasIntervals();
				String interval = hasIntervalsNorDeadline ? INTERVAL : highlight(INTERVAL);
				String deadline = hasIntervalsNorDeadline ? DEADLINE : highlight(DEADLINE);
				String alternative = !command.hasDeadline() ? highlight(INTERVAL_ALTERNATIVE) : INTERVAL_ALTERNATIVE;
				return constructFeedbackString(Constants.COMMAND_EDIT, TASK_INDEX, timeslotIndex, desc, deadline, "\n", interval, alternative, highlight(HASHTAG));
			}
		case DISPLAY:
			if (command.hasDisplayMode()) {
				return constructFeedbackString(Constants.COMMAND_DISPLAY, DISPLAY_MODE, "\n", DISPLAY_MODE_TIP);
			} else {
				return constructFeedbackString(Constants.COMMAND_DISPLAY, highlight(DISPLAY_MODE), "\n", DISPLAY_MODE_TIP);
			}
		case CLEAR:
			String clearMode = command.hasClearMode() ? CLEAR_MODE : highlight(CLEAR_MODE);
			return constructFeedbackString(Constants.COMMAND_CLEAR, clearMode, "\n", CLEAR_MODE_TIP);
		case DELETE:
			String taskIndex = command.hasTaskIndex() ? TASK_INDEX : highlight(TASK_INDEX);
			return constructFeedbackString(Constants.COMMAND_DELETE, taskIndex);
		case SEARCH:
			String searchTerms = command.hasSearchTerms() ? SEARCH_TERMS : highlight(SEARCH_TERMS);
			return constructFeedbackString(Constants.COMMAND_SEARCH, searchTerms);
		case SORT:
			return COMMAND_FORMAT_SORT;
		case UNDO:
			return COMMAND_FORMAT_UNDO;
		case REDO:
			return COMMAND_FORMAT_REDO;
		case FINALISE:
			String finaliseTaskIndex = command.hasTaskIndex() ? TASK_INDEX : highlight(TASK_INDEX);
			String timeslotIndex = command.hasTaskIndex() ? TIMESLOT_INDEX : highlight(TIMESLOT_INDEX);
			return constructFeedbackString(Constants.COMMAND_FINALISE, finaliseTaskIndex, timeslotIndex);
		case HELP:
	        String helpCommand = command.hasHelpCommand() ? COMMAND : highlight(COMMAND);
	        return constructFeedbackString(Constants.COMMAND_HELP, helpCommand);
		case DONE:
			String doneTaskIndex = command.hasTaskIndex() ? TASK_INDEX : highlight(TASK_INDEX);
			return constructFeedbackString(Constants.COMMAND_DONE, doneTaskIndex);
		case GOTO:
			String pageIndex = command.hasPageIndex() ? PAGE_INDEX : highlight(PAGE_INDEX);
			return constructFeedbackString(Constants.COMMAND_GOTO, pageIndex);
		case EXIT:
			return COMMAND_FORMAT_EXIT;
		default:
			return Constants.MSG_AVAILABLE_COMMANDS;
		}
	}

	private String constructFeedbackString(String ...portions) {
		StringBuilder s = new StringBuilder();
		for (int i=0; i<portions.length; i++) {
			s.append(portions[i]);
			s.append(" ");
		}
		return s.toString();
	}

	public void setCommand(Command command) {
		assert command != null;
		this.command = command;
	}

	private String highlight (String text) {
		return "&" + text + "&";
	}

	public String toString() {
		String feedback = determineFeedback();
		return feedback;
	}
}
