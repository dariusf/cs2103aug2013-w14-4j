package logic;

import org.joda.time.DateTime;

import sun.security.action.GetBooleanAction;

import common.CommandType;
import common.DisplayMode;

public class ActiveFeedback {
	private CommandType feedbackCommand = null;
	private int taskIndex = 0; 
//	private DisplayMode displayMode;
	private Command command = null;
	
	public ActiveFeedback(Command command) {
		this.command = command;
	}
	
	public Command getCommand() {
		return command;
	}
}
