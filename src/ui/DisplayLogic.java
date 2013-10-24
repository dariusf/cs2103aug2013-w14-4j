package ui;

import logic.Logic;

public class DisplayLogic {
	
	private Logic logic;
	private int noOfTasksToday = 0;
	private int noOfTasksRemaining = 0;
	
	public DisplayLogic(Logic logic) {
		this.logic = logic;
		setNumberOfRemainingTasks();
		setNumberOfTasksToday();
	}
	
	private void setNumberOfRemainingTasks() {
		noOfTasksRemaining = logic.getNumberOfRemainingTasks();
	}
	
	private void setNumberOfTasksToday() {
		noOfTasksToday = logic.getNumberOfTasksToday();
	}
	
	protected int getNumberOfRemainingTasks() {
		return noOfTasksRemaining;
	}

	protected int getNumberOfTasksToday() {
		return noOfTasksToday;
	}

}
