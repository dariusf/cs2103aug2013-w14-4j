package ui;

import logic.Command;
import common.CommandType;
import common.Constants;

public class ContextualHelp {
	final private String COMMAND_FORMAT_ADD = "add <task> {by|on|from..to|or} {#hashtag}";
	final private String COMMAND_FORMAT_EDIT = "edit <task index> {timeslot index} \n{task name|by|on|from..to|or|#hashtag}";
	final private String COMMAND_FORMAT_DISPLAY = "display {all|done|overdue|tentative|timed|untimed \ndeadline|date}";
	final private String COMMAND_FORMAT_DELETE = "delete <task index>";
	final private String COMMAND_FORMAT_CLEAR = "clear {done}";
	final private String COMMAND_FORMAT_SORT = "tasks will be sorted by date order";
	final private String COMMAND_FORMAT_SEARCH = "search <keyword|hashtag>";
	final private String COMMAND_FORMAT_UNDO = "undo reverses the last action performed";
	final private String COMMAND_FORMAT_REDO = "redo reverses the last 'undo' made";
	final private String COMMAND_FORMAT_FINALISE = "finalise <task index> <timeslot index>";
	final private String COMMAND_FORMAT_HELP = "help {command}";
	final private String COMMAND_FORMAT_DONE = "done <task index>";
	final private String COMMAND_FORMAT_GOTO = "goto <page number>\nYou can also use the page up and page down keys";
	final private String COMMAND_FORMAT_EXIT = "exit closes the application";
	
	Command command;
	
	public ContextualHelp(Command command) {
		setCommand(command);
	}

	public void setCommand(Command command) {
		assert command != null;
		this.command = command;
	}

	public String toString() {
		String feedback = determineFeedback();
		return feedback;
	}

	private String determineFeedback() {
		switch (command.getCommandType()) {
		case ADD:
			if (command.getDescription().isEmpty()) {
				return "add _<task>_ {by|on|from..to|or} {#hashtag}";
			}
			else if (!command.hasIntervals() && !command.hasDeadline()) {
				return "add <task> {_by_|_on|from..to_|_or_} _{#hashtag}_";
			}
			else if (command.hasIntervals()) {
				return "add <task> {by|on|from..to|_or_} _{#hashtag}_";
			}
			else {
				return "add <task> {by|on|from..to|or} _{#hashtag}_";
			}
		case EDIT:
			return COMMAND_FORMAT_EDIT;
		case DISPLAY:
			return COMMAND_FORMAT_DISPLAY;
		case DELETE:
			return COMMAND_FORMAT_DELETE;
		case CLEAR:
			return COMMAND_FORMAT_CLEAR;
		case SORT:
			return COMMAND_FORMAT_SORT;
		case SEARCH:
			return COMMAND_FORMAT_SEARCH;
		case UNDO:
			return COMMAND_FORMAT_UNDO;
		case REDO:
			return COMMAND_FORMAT_REDO;
		case FINALISE:
			return COMMAND_FORMAT_FINALISE;
		case HELP:
			return COMMAND_FORMAT_HELP;
		case DONE:
			return COMMAND_FORMAT_DONE;
		case GOTO:
			return COMMAND_FORMAT_GOTO;
		case EXIT:
			return COMMAND_FORMAT_EXIT;
		default:
			return Constants.MSG_AVAILABLE_COMMANDS;
		}
	}
}
