package ui;

import logic.Command;
import common.Constants;

//@author A0097282W
public class ContextualHelp {
	Command command;
	
	public ContextualHelp(Command command) {
		setCommand(command);
	}

	private String determineFeedback() {
		switch (command.getCommandType()) {
		case ADD:
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
		case EDIT:
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
		case DISPLAY:
			if (command.hasDisplayMode()) {
				return constructFeedbackString(Constants.COMMAND_DISPLAY, Constants.CONTEXTUAL_HELP_DISPLAY_MODE, "\n", Constants.CONTEXTUAL_HELP_DISPLAY_MODE_TIP);
			} else {
				return constructFeedbackString(Constants.COMMAND_DISPLAY, highlight(Constants.CONTEXTUAL_HELP_DISPLAY_MODE), "\n", Constants.CONTEXTUAL_HELP_DISPLAY_MODE_TIP);
			}
		case CLEAR:
			String clearMode = command.hasClearMode() ? Constants.CONTEXTUAL_HELP_CLEAR_MODE : highlight(Constants.CONTEXTUAL_HELP_CLEAR_MODE);
			return constructFeedbackString(Constants.COMMAND_CLEAR, clearMode, "\n", Constants.CONTEXTUAL_HELP_CLEAR_MODE_TIP);
		case DELETE:
			String taskIndex = command.hasTaskIndex() ? Constants.CONTEXTUAL_HELP_TASK_INDEX : highlight(Constants.CONTEXTUAL_HELP_TASK_INDEX);
			return constructFeedbackString(Constants.COMMAND_DELETE, taskIndex);
		case SEARCH:
			String searchTerms = command.hasSearchTerms() ? Constants.CONTEXTUAL_HELP_SEARCH_TERMS : highlight(Constants.CONTEXTUAL_HELP_SEARCH_TERMS);
			return constructFeedbackString(Constants.COMMAND_SEARCH, searchTerms);
		case SORT:
			return Constants.CONTEXTUAL_HELP_SORT;
		case UNDO:
			return Constants.CONTEXTUAL_HELP_UNDO;
		case REDO:
			return Constants.CONTEXTUAL_HELP_REDO;
		case FINALISE:
			String finaliseTaskIndex = command.hasTaskIndex() ? Constants.CONTEXTUAL_HELP_TASK_INDEX : highlight(Constants.CONTEXTUAL_HELP_TASK_INDEX);
			String timeslotIndex = command.hasTaskIndex() ? Constants.CONTEXTUAL_HELP_TIMESLOT_INDEX : highlight(Constants.CONTEXTUAL_HELP_TIMESLOT_INDEX);
			return constructFeedbackString(Constants.COMMAND_FINALISE, finaliseTaskIndex, timeslotIndex);
		case HELP:
	        String helpCommand = command.hasHelpCommand() ? Constants.CONTEXTUAL_HELP_COMMAND : highlight(Constants.CONTEXTUAL_HELP_COMMAND);
	        return constructFeedbackString(Constants.COMMAND_HELP, helpCommand);
		case DONE:
			String doneTaskIndex = command.hasTaskIndex() ? Constants.CONTEXTUAL_HELP_TASK_INDEX : highlight(Constants.CONTEXTUAL_HELP_TASK_INDEX);
			return constructFeedbackString(Constants.COMMAND_DONE, doneTaskIndex);
		case GOTO:
			String pageIndex = command.hasPageIndex() ? Constants.CONTEXTUAL_HELP_PAGE_INDEX : highlight(Constants.CONTEXTUAL_HELP_PAGE_INDEX);
			return constructFeedbackString(Constants.COMMAND_GOTO, pageIndex);
		case EXIT:
			return Constants.CONTEXTUAL_HELP_EXIT;
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
