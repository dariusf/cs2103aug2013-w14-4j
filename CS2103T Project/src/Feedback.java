import java.util.HashMap;

public class Feedback {
	private static CommandType feedbackCommand = null;
	private static HashMap<String, String> feedbackAttributes = null;
	private static int statusCode = 0;
	
	protected Feedback(int status){
		statusCode = status;
	}
	
	protected Feedback(int status, CommandType command){
		statusCode = status;
		feedbackCommand = command;
	}
	
	protected Feedback(int status, CommandType command, HashMap<String, String> attributes){
		statusCode = status;
		feedbackAttributes = attributes;
		feedbackCommand = command;
	}
	
	protected static void setStatusCode(int status){
		statusCode = status;
	}
	
	protected static int getStatusCode(){
		return statusCode;
	}
	
	protected static void setCommand(CommandType command){
		feedbackCommand = command;
	}
	
	protected static CommandType getCommand(){
		return feedbackCommand;
	}
	
	protected static void setAttribute(String key, String value){
		feedbackAttributes.put(key, value);
	}
	
	protected static String getAttribute(String key){
		return feedbackAttributes.get(key);
	}
	
	protected static String getDisplayString() {
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
