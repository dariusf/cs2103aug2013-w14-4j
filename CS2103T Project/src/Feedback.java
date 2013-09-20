import java.util.HashMap;

public class Feedback {
	private static CommandType feedbackCommand = null;
	private static Task feedbackTask = null;
	private static int statusCode = 0;
	
	protected Feedback(int status){
		statusCode = status;
	}
	
	protected Feedback(int status, CommandType command){
		statusCode = status;
		feedbackCommand = command;
	}
	
	protected Feedback(int status, CommandType command, Task task){
		statusCode = status;
		feedbackTask = task;
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
	
	protected static void setTask(Task task){
		feedbackTask = task;
	}
	
	protected static Task getTask(){
		return feedbackTask;
	}
}
