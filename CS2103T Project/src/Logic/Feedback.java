package Logic;

public class Feedback {
	private CommandType feedbackCommand = null;
	private String feedbackString = null;
	private int statusCode = 0;
	private String statusMessage = null;

	public Feedback(int status, CommandType command) {
		statusCode = status;
		feedbackCommand = command;
	}

	public Feedback(int status, CommandType command, String string) {
		statusCode = status;
		feedbackString = string;
		feedbackCommand = command;
	}

	public void setStatusCode(int status) {
		statusCode = status;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setCommand(CommandType command) {
		feedbackCommand = command;
	}

	public CommandType getCommand() {
		return feedbackCommand;
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
		return statusMessage;
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
		return statusMessage;
	}

	private String displayFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = feedbackString;
		} else if (statusCode == Constants.SC_DISPLAY_NO_TASK_ERROR) {
			statusMessage = "You have no tasks :)";
		} else {
			statusMessage = "Error: Invalid display (this should not happen!)";
		}
		return statusMessage;
	}

	private String deleteFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "Task deleted successfully! \n" + feedbackString;
		} else if (statusCode == Constants.SC_INTEGER_OUT_OF_BOUNDS_ERROR) {
			statusMessage = "Error: ID is not valid";
		} else if (statusCode == Constants.SC_NO_ID_INDICATED_ERROR) {
			statusMessage = "Error: No ID is indicated";
		} else {
			statusMessage = "Error: Invalid delete (this should not happen!)";
		}
		return statusMessage;
	}

	private String clearFeedback() {
		if (statusCode == Constants.SC_SUCCESS) {
			statusMessage = "All tasks cleared!";
		} else if (statusCode == Constants.SC_NO_TASK_TO_CLEAR_ERROR) {
			statusMessage = "Error: You have no tasks to clear!";
		} else {
			statusMessage = "Error: Invalid clear (this should not happen!)";
		}
		return statusMessage;
	}

	private String sortFeedback() {
		// TODO Auto-generated method stub
		return null;
	}

	private String searchFeedback() {
		// TODO Auto-generated method stub
		return null;
	}

	private String invalidFeedback() {
		if (statusCode == Constants.SC_INVALID_COMMAND_ERROR) {
			statusMessage = "Error: Invalid command!";
		} else {
			// should not happen
			statusMessage = "Error: Invalid command (this should not happen!)";
		}
		return statusMessage;
	}

	private String undoFeedback() {
		// TODO Auto-generated method stub
		return null;
	}

	private String finaliseFeedback() {
		// TODO Auto-generated method stub
		return null;
	}

	private String helpFeedback() {
		// TODO Auto-generated method stub
		return null;
	}

	private String doneFeedback() {
		// TODO Auto-generated method stub
		return null;
	}
}
