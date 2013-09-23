import java.util.ArrayList;
import java.util.HashMap;

public class Feedback {
	private CommandType feedbackCommand = null;
	private HashMap<String, String> feedbackAttributes = null;
	private String feedbackString = null;
	private int statusCode = 0;

	protected Feedback(int status) {
		statusCode = status;
	}

	protected Feedback(int status, CommandType command) {
		statusCode = status;
		feedbackCommand = command;
	}

	protected Feedback(int status, CommandType command,
			HashMap<String, String> attributes) {
		statusCode = status;
		feedbackAttributes = attributes;
		feedbackCommand = command;
	}

	protected Feedback(int status, CommandType command, String string) {
		statusCode = status;
		feedbackString = string;
		feedbackCommand = command;
	}

	protected void setStatusCode(int status) {
		statusCode = status;
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

	protected void setAttribute(String key, String value) {
		feedbackAttributes.put(key, value);
	}

	protected String getAttribute(String key) {
		return feedbackAttributes.get(key);
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
