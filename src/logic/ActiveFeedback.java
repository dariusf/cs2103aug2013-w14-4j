package logic;

import common.Command;

public class ActiveFeedback {
	private Command command = null;
	
	public ActiveFeedback(Command command) {
		this.command = command;
	}
	
	public Command getCommand() {
		return command;
	}
}
