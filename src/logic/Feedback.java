package logic;

import org.joda.time.DateTime;

import common.CommandType;
import common.Constants;
import common.DisplayMode;

public class Feedback {
	private CommandType feedbackCommand = null;
	private int taskIndex = 0; 
	private DisplayMode displayMode; 
	private DateTime displayDate;

	private int statusCode = 0;
	private String statusMessage = null;
	private boolean isError = false;
	private int gotoPage = 0;
	private CommandType helpCommandType = null;
	private Task addedTask = null;

	// @author A0101048X
	public Feedback(int status, CommandType command) {
		setStatusCode(status);
		setCommand(command);
		setIsError(statusCode);
	}
	
	public DisplayMode getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(DisplayMode displayMode) {
		this.displayMode = displayMode;
	}

	
	public void setTaskIndex(int index){
		taskIndex = index;
	}
	
	public int getTaskIndex(){
		return taskIndex;
	}

	public void setStatusCode(int status) {
		assert (status <= 120) && (status >= 10);
		statusCode = status;
		setIsError(statusCode);
	}

	public int getStatusCode() {
		return statusCode;
	}
	
	public void setHelpCommandType(CommandType command) {
		// TODO check that it's a valid command here.
		helpCommandType = command;
	}
	
	public CommandType getHelpCommandType() {
		return helpCommandType;
	}

	public void setCommand(CommandType command) {
		assert command != null;
		feedbackCommand = command;
	}

	public CommandType getCommand() {
		return feedbackCommand;
	}
	
	public boolean isErrorMessage() {
		if (isError == true) {
			return true;
		} else {
			return false;
		}
	}
	
	private void setIsError(int statusCode) {
		if (statusCode < 20 && statusCode >= 10) {
			isError = false;
		} else {
			isError = true;
		}
	}

	public String toString() {
		switch (feedbackCommand) {
		case ADD :
			return addFeedback();
		case EDIT:
			return editFeedback();
		case DISPLAY :
			return displayFeedback();
		case DELETE :
			return deleteFeedback();
		case CLEAR :
			return clearFeedback();
		case SORT :
			return sortFeedback();
		case SEARCH:
			return searchFeedback();
		case INVALID :
			return invalidFeedback();
		case UNDO :
			return undoFeedback();
		case REDO:
			return redoFeedback();
		case FINALISE :
			return finaliseFeedback();
		case HELP :
			return helpFeedback();
		case DONE :
			return doneFeedback();
		case GOTO :
			return gotoFeedback();
		case EXIT :
			return exitFeedback();
		default :
			return String.format(Constants.MSG_SHOULD_NOT_HAPPEN, Constants.MSG_DEFAULT);
		}
	}

	private String addFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Task added!";
		} else if (statusCode == Constants.SC_SUCCESS_TASK_OVERDUE) {
			statusMessage = "Task added!\nIt's overdue, though.";
//		} else if (statusCode == Constants.SC_EMPTY_DESCRIPTION_ERROR) {
//			statusMessage = "Error: No task description was given";
		} else {
			statusMessage = String.format(Constants.MSG_SHOULD_NOT_HAPPEN, Constants.COMMAND_ADD);
		}
		return statusMessage;
	}

	private String editFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Task edited!";
		} else if (statusCode == Constants.SC_SUCCESS_TASK_OVERDUE) {
			statusMessage = "Task edited!\nIt's overdue, though.";
		} else if (statusCode == Constants.SC_EMPTY_DESCRIPTION_ERROR) {
			statusMessage = "Error: No task description was given!";
		} else if (statusCode == Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR) {
			statusMessage = "Error: That's not a valid task index!";
		} else if (statusCode == Constants.SC_INTEGER_OUT_OF_BOUNDS_TIME_ERROR) {
			statusMessage = "Error: That's not a valid timeslot index!";
		}else if (statusCode == Constants.SC_NO_ID_INDICATED_ERROR) {
			statusMessage = "Error: You need to specify a task index!";
		} else {
			statusMessage = String.format(Constants.MSG_SHOULD_NOT_HAPPEN, Constants.COMMAND_EDIT);
		}
		return statusMessage;
	}

	private String displayFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Here are your tasks!";
		} else if (statusCode == Constants.SC_NO_TASK_ERROR) {
			statusMessage = "You have no tasks!";
		} else {
			statusMessage = String.format(Constants.MSG_SHOULD_NOT_HAPPEN, Constants.COMMAND_DISPLAY);
		}
		return statusMessage;
	}

	private String deleteFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Task deleted!";
		} else if (statusCode == Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR) {
			statusMessage = "Error: That's not a valid task index!";
		} else if (statusCode == Constants.SC_NO_ID_INDICATED_ERROR) {
			statusMessage = "Error: You need to specify a task index!";
		} else {
			statusMessage = String.format(Constants.MSG_SHOULD_NOT_HAPPEN, Constants.COMMAND_DELETE);
		}
		return statusMessage;
	}

	private String clearFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Tasks cleared!";
		} else if (statusCode == Constants.SC_SUCCESS_CLEAR_DONE) {
			statusMessage = "Tasks cleared!";
		} else if (statusCode == Constants.SC_NO_TASK_ERROR) {
			statusMessage = "Error: You have no tasks to clear!";
		} else {
			statusMessage = String.format(Constants.MSG_SHOULD_NOT_HAPPEN, Constants.COMMAND_CLEAR);
		}
		return statusMessage;
	}

	private String sortFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Tasks sorted!";
		} else if (statusCode == Constants.SC_NO_TASK_ERROR) {
			statusMessage = "Error: You have no tasks to sort!";
		} else {
			statusMessage = String.format(Constants.MSG_SHOULD_NOT_HAPPEN, Constants.COMMAND_SORT);
		}
		return statusMessage;
	}

	private String searchFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Here are your search results!";
		} else if (statusCode == Constants.SC_NO_TASK_ERROR) {
			statusMessage = "Error: You have no tasks to search!";
		} else if (statusCode == Constants.SC_SEARCH_KEYWORD_MISSING_ERROR) {
			statusMessage = "Error: No search description was given!";
		} else {
			statusMessage = String.format(Constants.MSG_SHOULD_NOT_HAPPEN, Constants.COMMAND_SEARCH);
		}
		return statusMessage;
	}

	private String invalidFeedback() {
		if (statusCode == Constants.SC_INVALID_COMMAND_ERROR) {
			statusMessage = "Error: That's not valid command!";
		} else if (statusCode == Constants.SC_EMPTY_COMMAND_ERROR){
			statusMessage = "Error: No command entered!";
		} else if (statusCode == Constants.SC_INVALID_PAGE_INDEX_ERROR){
			statusMessage = "Error: That's not a vaild page index!";
		} else if (statusCode == Constants.SC_INVALID_DATE_ERROR){
			statusMessage = "Error: That's not a valid date!";
		} else if (statusCode == Constants.SC_UNRECOGNIZED_COMMAND_ERROR){
			statusMessage = "Error: Command is not recognised!";
		} else if (statusCode == Constants.SC_TOO_FEW_ARGUMENTS_ERROR){
			statusMessage = "Error: Command is not in the right format!";
		} else if (statusCode == Constants.SC_INVALID_TIMESLOT_INDEX_ERROR){
			statusMessage = "Error: That's not a valid timeslot index!";
		} else if (statusCode == Constants.SC_INVALID_SEARCH_PARAMETERS_ERROR){
			statusMessage = "Error: Search parameters are invalid!";
		}  else if (statusCode == Constants.SC_INVALID_TASK_INDEX_ERROR){
			statusMessage = "Error: Task index is invalid!";
		}else {
			statusMessage = String.format(Constants.MSG_SHOULD_NOT_HAPPEN, Constants.COMMAND_INVALID);
		}
		return statusMessage;
	}

	private String undoFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Undo successful!";
		} else if (statusCode == Constants.SC_UNDO_NO_PRIOR_STATE_ERROR) {
			statusMessage = "Error: There's nothing to undo!";
		} else {
			statusMessage = String.format(Constants.MSG_SHOULD_NOT_HAPPEN, Constants.COMMAND_UNDO);
		}
		return statusMessage;
	}
	
	private String redoFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Redo successful!";
		} else if (statusCode == Constants.SC_REDO_NO_PRIOR_STATE_ERROR) {
			statusMessage = "Error: Nothing to redo!";
		} else {
			statusMessage = String.format(Constants.MSG_SHOULD_NOT_HAPPEN, Constants.COMMAND_REDO);
		}
		return statusMessage;
	}

	private String finaliseFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Task finalised!";
		} else if (statusCode == Constants.SC_SUCCESS_TASK_OVERDUE) {
			statusMessage = "Task finalised!\nIt's overdue, though.";
		} else if (statusCode == Constants.SC_NO_TASK_ERROR) {
			statusMessage = "Error: You have no tasks to finalise!";
		} else if (statusCode == Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR) {
			statusMessage = "Error: That's not a valid task index!";
		} else if (statusCode == Constants.SC_NO_ID_INDICATED_ERROR) {
			statusMessage = "Error: That's not a valid task index!";
		} else if (statusCode == Constants.SC_INTEGER_OUT_OF_BOUNDS_TIME_ERROR) {
			statusMessage = "Error: That's not a valid timeslot index!";
		} else if (statusCode == Constants.SC_NO_ID_INDICATED_TIME_ERROR) {
			statusMessage = "Error: That's not a valid time!";
		} else if (statusCode == Constants.SC_FINALISE_TYPE_MISMATCH_ERROR) {
			statusMessage = "Error: That's not a tentative task!";
		} else {
			statusMessage = String.format(Constants.MSG_SHOULD_NOT_HAPPEN, Constants.COMMAND_FINALISE);
		}
		return statusMessage;
	}	

	private String helpFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = Constants.HELP_INSTRUCTIONS;
		} else {
			statusMessage = String.format(Constants.MSG_SHOULD_NOT_HAPPEN, Constants.COMMAND_HELP);
		}
		return statusMessage;
	}

	private String doneFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Task marked as done!";
		} else if (statusCode == Constants.SC_NO_TASK_ERROR) {
			statusMessage = "Error: You have no tasks to mark as done!";
		} else if (statusCode == Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR) {
			statusMessage = "Error: That's not a valid task index!";
		} else if (statusCode == Constants.SC_NO_ID_INDICATED_ERROR) {
			statusMessage = "Error: That's not a valid task index!";
		} else {
			statusMessage = String.format(Constants.MSG_SHOULD_NOT_HAPPEN, Constants.COMMAND_DONE);
		}
		return statusMessage;
	}
	
	private String gotoFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Page " + gotoPage;
		} else if (statusCode == Constants.SC_INVALID_PAGE_INDEX) {
			statusMessage = "Error: " + gotoPage + " is not a valid page!";
		} else {
			statusMessage = String.format(Constants.MSG_SHOULD_NOT_HAPPEN, Constants.COMMAND_GOTO);
		}
		return statusMessage;
	}

	private String exitFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Saving...";
		} else {
			statusMessage = String.format(Constants.MSG_SHOULD_NOT_HAPPEN, Constants.COMMAND_EXIT);
		}
		return statusMessage;
	}

	public DateTime getDisplayDate() {
		return displayDate;
	}

	public void setDisplayDate(DateTime displayDate) {
		this.displayDate = displayDate;
	}

	public int getGotoPage() {
		return gotoPage;
	}

	public void setGotoPage(int gotoPage) {
		this.gotoPage = gotoPage;
	}

	public Task getAddedTask() {
		return addedTask;
	}

	public void setAddedTask(Task addedTask) {
		this.addedTask = addedTask;
	}
}
