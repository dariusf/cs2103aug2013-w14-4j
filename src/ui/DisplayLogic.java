package ui;

import java.util.ArrayList;
import java.util.Arrays;

import logic.Logic;
import logic.Task;

public class DisplayLogic {

	private Logic logic;
	private int noOfTasksToday = 0;
	private int noOfTasksRemaining = 0;
	private ArrayList<Integer> numberOfTasksOnEachPage;

	private int taskCompositeHeight = 0;
	private int taskCompositeIncrement = 0;
	private int taskCompositeHeightForThreeLines = 0;

	public DisplayLogic(Logic logic) {
		this.logic = logic;
	}

	protected void setTaskCompositeHeight(int height) {
		taskCompositeHeight = height;
	}

	protected void setTaskCompositeIncrement(int increment) {
		taskCompositeIncrement = increment;
	}

	protected void setTaskCompositeHeightForThreeLines(int height) {
		taskCompositeHeightForThreeLines = height;
	}

	protected int getNumberOfRemainingTasks() {
		noOfTasksRemaining = logic.getNumberOfRemainingTasks();
		return noOfTasksRemaining;
	}

	protected int getNumberOfTasksToday() {
		noOfTasksToday = logic.getNumberOfTasksToday();
		return noOfTasksToday;
	}

	protected ArrayList<Integer> getNumberOfTasksForEachPage() {
		determineNumberOfTasksForEachPage();
		return numberOfTasksOnEachPage;
	}

	private void determineNumberOfTasksForEachPage() {
		ArrayList<Task> taskList = logic.getTasksToDisplay();
		int numberOfTasks = taskList.size();
		int[] heights = new int[numberOfTasks];
		int index = 0;
		for (Task task : taskList) {
			heights[index] = determineTaskHeight(task);
			index++;
		}
		System.out.println(Arrays.toString(heights));

		numberOfTasksOnEachPage = new ArrayList<>();
		int currentCountOfTasks = 0;
		int currentHeight = 0;
		for (int i = 0; i < numberOfTasks; i++) {
			if (currentHeight + heights[i] > 450) {
				numberOfTasksOnEachPage.add(currentCountOfTasks);
				currentCountOfTasks = 1;
				currentHeight = heights[i];
			} else {
				currentCountOfTasks++;
				currentHeight += heights[i];
			}
		}
		numberOfTasksOnEachPage.add(currentCountOfTasks);
		System.out.println(numberOfTasksOnEachPage);
	}

	private int determineTaskHeight(Task task) {
		if (!task.isFloatingTask()) {
			return taskCompositeHeight;
		} else {
			int numberOfSlots = task.getPossibleTime().size();
			boolean hasTags = task.getTags().size() > 0;
			int numberOfLines = numberOfSlots;
			if (hasTags) {
				numberOfLines++;
			}
			if (numberOfLines == 3) {
				return taskCompositeHeightForThreeLines;
			} else {
				return (numberOfLines - 3) * taskCompositeIncrement
						+ taskCompositeHeightForThreeLines;
			}
		}
	}

}
