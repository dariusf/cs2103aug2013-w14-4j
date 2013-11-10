package ui;

import common.Command;
import common.Constants;

//@author A0097282W
public class ContextualHelp {
	Command command;
	
	public ContextualHelp(Command command) {
		setCommand(command);
	}

	private String highlight (String text) {
		return "&" + text + "&";
	}

	private String constructHelpString() {
		switch (command.getCommandType()) {
		case ADD:
			return addHelp();
		case EDIT:
			return editHelp();
		case DISPLAY:
			return displayHelp();
		case CLEAR:
			return clearHelp();
		case DELETE:
			return deleteHelp();
		case SEARCH:
			return searchHelp();
		case SORT:
			return Constants.CONTEXTUAL_HELP_SORT;
		case UNDO:
			return Constants.CONTEXTUAL_HELP_UNDO;
		case REDO:
			return Constants.CONTEXTUAL_HELP_REDO;
		case FINALISE:
			return finaliseHelp();
		case HELP:
	        return helpHelp();
		case DONE:
			return doneHelp();
		case GOTO:
			return gotoHelp();
		case EXIT:
			return Constants.CONTEXTUAL_HELP_EXIT;
		default:
			return Constants.MSG_AVAILABLE_COMMANDS;
		}
	}

	private String gotoHelp() {
		String pageIndex = command.hasPageIndex() ? Constants.CONTEXTUAL_HELP_PAGE_INDEX : highlight(Constants.CONTEXTUAL_HELP_PAGE_INDEX);
		return constructFeedbackString(Constants.COMMAND_GOTO, pageIndex);
	}

	private String doneHelp() {
		String doneTaskIndex = command.hasTaskIndex() ? Constants.CONTEXTUAL_HELP_TASK_INDEX : highlight(Constants.CONTEXTUAL_HELP_TASK_INDEX);
		return constructFeedbackString(Constants.COMMAND_DONE, doneTaskIndex);
	}

	private String helpHelp() {
		String helpCommand = command.hasHelpCommand() ? Constants.CONTEXTUAL_HELP_COMMAND : highlight(Constants.CONTEXTUAL_HELP_COMMAND);
		return constructFeedbackString(Constants.COMMAND_HELP, helpCommand);
	}

	private String finaliseHelp() {
		if (!command.hasTaskIndex()) {
			return constructFeedbackString(Constants.COMMAND_FINALISE, highlight(Constants.CONTEXTUAL_HELP_TASK_INDEX), Constants.CONTEXTUAL_HELP_TIMESLOT_INDEX);
		} else {
			String timeslotIndex = command.hasTimeslotIndex() ? Constants.CONTEXTUAL_HELP_TIMESLOT_INDEX : highlight(Constants.CONTEXTUAL_HELP_TIMESLOT_INDEX);
			return constructFeedbackString(Constants.COMMAND_FINALISE, Constants.CONTEXTUAL_HELP_TASK_INDEX, timeslotIndex);
		}
	}

	private String searchHelp() {
		String searchTerms = command.hasSearchTerms() ? Constants.CONTEXTUAL_HELP_SEARCH_TERMS : highlight(Constants.CONTEXTUAL_HELP_SEARCH_TERMS);
		return constructFeedbackString(Constants.COMMAND_SEARCH, searchTerms);
	}

	private String deleteHelp() {
		String taskIndex = command.hasTaskIndex() ? Constants.CONTEXTUAL_HELP_TASK_INDEX : highlight(Constants.CONTEXTUAL_HELP_TASK_INDEX);
		return constructFeedbackString(Constants.COMMAND_DELETE, taskIndex);
	}

	private String clearHelp() {
		String clearMode = command.hasClearMode() ? Constants.CONTEXTUAL_HELP_CLEAR_MODE : highlight(Constants.CONTEXTUAL_HELP_CLEAR_MODE);
		return constructFeedbackString(Constants.COMMAND_CLEAR, clearMode, "\n", Constants.CONTEXTUAL_HELP_CLEAR_MODE_TIP);
	}

	private String displayHelp() {
		if (command.hasDisplayMode()) {
			return constructFeedbackString(Constants.COMMAND_DISPLAY, Constants.CONTEXTUAL_HELP_DISPLAY_MODE, "\n", Constants.CONTEXTUAL_HELP_DISPLAY_MODE_TIP);
		} else {
			return constructFeedbackString(Constants.COMMAND_DISPLAY, highlight(Constants.CONTEXTUAL_HELP_DISPLAY_MODE), "\n", Constants.CONTEXTUAL_HELP_DISPLAY_MODE_TIP);
		}
	}

	private String editHelp() {
		if (!command.hasTaskIndex()) {
			return constructFeedbackString(Constants.COMMAND_EDIT, highlight(Constants.CONTEXTUAL_HELP_TASK_INDEX), Constants.CONTEXTUAL_HELP_TIMESLOT_INDEX_OPTIONAL, Constants.CONTEXTUAL_HELP_DESC, Constants.CONTEXTUAL_HELP_DEADLINE, "\n", Constants.CONTEXTUAL_HELP_INTERVAL, Constants.CONTEXTUAL_HELP_INTERVAL_ALTERNATIVE, Constants.CONTEXTUAL_HELP_HASHTAG);
		}
		else {
			String timeslotIndex = command.hasDeadline() || command.hasIntervals() || command.hasTags() || !command.getDescription().isEmpty() ? Constants.CONTEXTUAL_HELP_TIMESLOT_INDEX_OPTIONAL : highlight(Constants.CONTEXTUAL_HELP_TIMESLOT_INDEX_OPTIONAL);
			String desc = command.getDescription().isEmpty() ? highlight(Constants.CONTEXTUAL_HELP_DESC_OPTIONAL) : Constants.CONTEXTUAL_HELP_DESC_OPTIONAL;
			boolean hasIntervalsNorDeadline = command.hasDeadline() || command.hasIntervals();
			String interval = hasIntervalsNorDeadline ? Constants.CONTEXTUAL_HELP_INTERVAL : highlight(Constants.CONTEXTUAL_HELP_INTERVAL);
			String deadline = hasIntervalsNorDeadline ? Constants.CONTEXTUAL_HELP_DEADLINE : highlight(Constants.CONTEXTUAL_HELP_DEADLINE);
			String alternative = !command.hasDeadline() ? highlight(Constants.CONTEXTUAL_HELP_INTERVAL_ALTERNATIVE) : Constants.CONTEXTUAL_HELP_INTERVAL_ALTERNATIVE;
			return constructFeedbackString(Constants.COMMAND_EDIT, Constants.CONTEXTUAL_HELP_TASK_INDEX, timeslotIndex, desc, deadline, "\n", interval, alternative, highlight(Constants.CONTEXTUAL_HELP_HASHTAG));
		}
	}

	private String addHelp() {
		if (command.getDescription().isEmpty()) {
			return constructFeedbackString(Constants.COMMAND_ADD, highlight(Constants.CONTEXTUAL_HELP_DESC), Constants.CONTEXTUAL_HELP_DEADLINE, Constants.CONTEXTUAL_HELP_INTERVAL, Constants.CONTEXTUAL_HELP_INTERVAL_ALTERNATIVE, "\n", Constants.CONTEXTUAL_HELP_HASHTAG);
		}
		else {
			boolean hasIntervalsNorDeadline = command.hasDeadline() || command.hasIntervals();
			String interval = hasIntervalsNorDeadline ? Constants.CONTEXTUAL_HELP_INTERVAL : highlight(Constants.CONTEXTUAL_HELP_INTERVAL);
			String deadline = hasIntervalsNorDeadline ? Constants.CONTEXTUAL_HELP_DEADLINE : highlight(Constants.CONTEXTUAL_HELP_DEADLINE);
			String alternative = !command.hasDeadline() && command.hasIntervals() ? highlight(Constants.CONTEXTUAL_HELP_INTERVAL_ALTERNATIVE) : Constants.CONTEXTUAL_HELP_INTERVAL_ALTERNATIVE;
			return constructFeedbackString(Constants.COMMAND_ADD, Constants.CONTEXTUAL_HELP_DESC, deadline, interval, alternative, "\n", highlight(Constants.CONTEXTUAL_HELP_HASHTAG));
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

	public String toString() {
		String feedback = constructHelpString();
		return feedback;
	}
}
