package logic;

import common.CommandType;
import common.Constants;

public class Feedback {
	private CommandType feedbackCommand = null;
	private String feedbackString = null;
	private int statusCode = 0;
	private String statusMessage = null;
	private boolean isError = false;

	Feedback(int status, CommandType command) {
		setStatusCode(status);
		setCommand(command);
		setIsError(statusCode);
	}

	Feedback(int status, CommandType command, String string) {
		setStatusCode(status);
		setCommand(command);
		feedbackString = string;
		setIsError(statusCode);
	}

	protected void setStatusCode(int status) {
		assert (status <= 110) && (status >= 10);
		statusCode = status;
		setIsError(statusCode);
	}

	protected int getStatusCode() {
		return statusCode;
	}

	protected void setCommand(CommandType command) {
		feedbackCommand = command;
	}

	protected CommandType getCommand() {
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
		switch (statusCode) {
		case Constants.SC_SUCCESS :
			isError = false;
			break;
		case Constants.SC_SUCCESS_TASK_OVERDUE :
			isError = false;
			break;
		case Constants.SC_INVALID_COMMAND_ERROR :
			isError = true;
			break;
		case Constants.SC_EMPTY_DESCRIPTION_ERROR :
			isError = true;
			break;
		case Constants.SC_UNRECOGNISED_ATTRIBUTE_ERROR :
			isError = true;
			break;
		case Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR :
			isError = true;
			break;
		case Constants.SC_NO_TASK_ERROR :
			isError = true;
			break;
		case Constants.SC_NO_ID_INDICATED_ERROR :
			isError = true;
			break;
		case Constants.SC_TASK_ALREADY_FINALISED_ERROR :
			isError = true;
			break;
		case Constants.SC_TASK_ALREADY_DONE_ERROR :
			isError = true;
			break;
		case Constants.SC_INTEGER_OUT_OF_BOUNDS_TIME_ERROR :
			isError = true;
			break;
		case Constants.SC_NO_ID_INDICATED_TIME_ERROR :
			isError = true;
			break;
		case Constants.SC_SEARCH_KEYWORD_MISSING_ERROR :
			isError = true;
			break;
		case Constants.SC_FINALISE_TYPE_MISMATCH_ERROR :
			isError = true;
			break;
		case Constants.SC_UNDO_NO_PRIOR_STATE_ERROR :
			isError = true;
			break;
			// TODO: add in default case
		}
	}

	public String toString() {
		switch (feedbackCommand) {
		case ADD_TASK :
			return addFeedback();
		case EDIT_TASK:
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
		case EXIT :
			System.exit(0);
		default :
			throw new Error(Constants.MSG_UNRECOGNISED_COMMAND);
		}
	}

	private String addFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Task added successfully! \n" + feedbackString;
		} else if (statusCode == Constants.SC_SUCCESS_TASK_OVERDUE) {
			statusMessage = "Task added successfully! Task is overdue \n" + feedbackString;
		} else if (statusCode == Constants.SC_EMPTY_DESCRIPTION_ERROR) {
			statusMessage = "Error: No task description was given";
		} else if (statusCode == Constants.SC_UNRECOGNISED_ATTRIBUTE_ERROR) {
			statusMessage = "Error: Attribute is unrecognised";
		} else {
			statusMessage = "Error: Invalid add (this should not happen!)";
		}
		return statusMessage+"\n";
	}

	private String editFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Task edited successfully! \n" + feedbackString;
		} else if (statusCode == Constants.SC_SUCCESS_TASK_OVERDUE) {
			statusMessage = "Task edited successfully! Task is overdue \n" + feedbackString;
		} else if (statusCode == Constants.SC_EMPTY_DESCRIPTION_ERROR) {
			statusMessage = "Error: No task description was given";
		} else if (statusCode == Constants.SC_UNRECOGNISED_ATTRIBUTE_ERROR) {
			statusMessage = "Error: Attribute is unrecognised";
		} else if (statusCode == Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR) {
			statusMessage = "Error: ID is not valid";
		} else if (statusCode == Constants.SC_NO_ID_INDICATED_ERROR) {
			statusMessage = "Error: No ID is indicated";
		} else {
			statusMessage = "Error: Invalid edit (this should not happen!)";
		}
		return statusMessage+"\n";
	}

	private String displayFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Here are your tasks.";
		} else if (statusCode == Constants.SC_NO_TASK_ERROR) {
			statusMessage = "You have no tasks :)";
		} else {
			statusMessage = "Error: Invalid display (this should not happen!)";
		}
		return statusMessage+"\n";
	}

	private String deleteFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Task deleted successfully! \n" + "The task \""+feedbackString+"\" is deleted";
		} else if (statusCode == Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR) {
			statusMessage = "Error: ID is not valid";
		} else if (statusCode == Constants.SC_NO_ID_INDICATED_ERROR) {
			statusMessage = "Error: No ID is indicated";
		} else {
			statusMessage = "Error: Invalid delete (this should not happen!)";
		}
		return statusMessage+"\n";
	}

	private String clearFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "All tasks cleared!";
		} else if (statusCode == Constants.SC_NO_TASK_ERROR) {
			statusMessage = "Error: You have no tasks to clear!";
		} else {
			statusMessage = "Error: Invalid clear (this should not happen!)";
		}
		return statusMessage+"\n";
	}

	private String sortFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "All tasks sorted!";
		} else if (statusCode == Constants.SC_NO_TASK_ERROR) {
			statusMessage = "Error: You have no tasks to sort!";
		} else {
			statusMessage = "Error: Invalid sort (this should not happen!)";
		}
		return statusMessage+"\n";
	}

	private String searchFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Here are your search results.";
		} else if (statusCode == Constants.SC_NO_TASK_ERROR) {
			statusMessage = "Error: You have no tasks to search!";
		} else if (statusCode == Constants.SC_SEARCH_KEYWORD_MISSING_ERROR) {
			statusMessage = "Error: No search description was given";
		} else if (statusCode == Constants.SC_UNRECOGNISED_ATTRIBUTE_ERROR) {
			statusMessage = "Error: Attribute is unrecognised";
		} else {
			statusMessage = "Error: Invalid search (this should not happen!)";
		}
		return statusMessage+"\n";
	}

	private String invalidFeedback() {
		if (statusCode == Constants.SC_INVALID_COMMAND_ERROR) {
			statusMessage = "Error: Invalid command!";
		} else {
			statusMessage = "Error: Invalid command (this should not happen!)";
		}
		return statusMessage+"\n";
	}

	private String undoFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Undo successful!";
		} else if (statusCode == Constants.SC_UNDO_NO_PRIOR_STATE_ERROR) {
			statusMessage = "Error: Nothing to undo!";
		} else {
			statusMessage = "Error: Invalid undo (this should not happen!)";
		}
		return statusMessage+"\n";
	}
	
	private String redoFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Redo successful!";
		} else if (statusCode == Constants.SC_REDO_NO_PRIOR_STATE_ERROR) {
			statusMessage = "Error: Nothing to redo!";
		} else {
			statusMessage = "Error: Invalid redo (this should not happen!)";
		}
		return statusMessage+"\n";
	}

	private String finaliseFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Task finalised successfully! \n" + feedbackString;
		} else if (statusCode == Constants.SC_SUCCESS_TASK_OVERDUE) {
			statusMessage = "Task finalised successfully! Task is overdue \n" + feedbackString;
		} else if (statusCode == Constants.SC_NO_TASK_ERROR) {
			statusMessage = "Error: You have no tasks to finalise!";
		} else if (statusCode == Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR) {
			statusMessage = "Error: ID is not valid";
		} else if (statusCode == Constants.SC_NO_ID_INDICATED_ERROR) {
			statusMessage = "Error: No ID is indicated";
		} else if (statusCode == Constants.SC_INTEGER_OUT_OF_BOUNDS_TIME_ERROR) {
			statusMessage = "Error: Time ID is not valid";
		} else if (statusCode == Constants.SC_NO_ID_INDICATED_TIME_ERROR) {
			statusMessage = "Error: Time ID is not specified";
		} else if (statusCode == Constants.SC_FINALISE_TYPE_MISMATCH_ERROR) {
			statusMessage = "Error: Task selected is not a flaoting task";
		} else {
			statusMessage = "Error: Invalid finalise (this should not happen!)";
		}
		return statusMessage+"\n";
	}	

	private String helpFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = feedbackString;
		} else {
			statusMessage = "Error: Invalid help (this should not happen!)";
		}
		return statusMessage+"\n";
	}

	private String doneFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Task marked as done! \n" + feedbackString;
		} else if (statusCode == Constants.SC_NO_TASK_ERROR) {
			statusMessage = "Error: You have no tasks to mark as done!";
		} else if (statusCode == Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR) {
			statusMessage = "Error: ID is not valid";
		} else if (statusCode == Constants.SC_NO_ID_INDICATED_ERROR) {
			statusMessage = "Error: No ID is indicated";
		} else {
			statusMessage = "Error: Invalid done (this should not happen!)";
		}
		return statusMessage+"\n";
	}
}
