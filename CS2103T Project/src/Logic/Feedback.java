package Logic;

public class Feedback {
	private CommandType feedbackCommand = null;
	private String feedbackString = null;
	private int statusCode = 0;

	public Feedback(int status) {
		statusCode = status;
	}

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
		switch (statusCode) {
		case 10:
			return "";
		case 61:
			return Constants.MSG_LINE_NUMBER_OVERFLOW;
		default:
			return "";
		}

	}
}
