package ui;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.swt.widgets.Composite;

import common.Constants;
import common.DisplayMode;

import logic.Logic;
import logic.Task;

public class DisplayLogic {

	private Logic logic;
	private DisplayMode displayMode;
	private Composite displayTask;
	private int pageNumber = 1;

	private org.joda.time.DateTime currentDisplayDateTime = new org.joda.time.DateTime();
	
	private int noOfTasksToday = 0;
	private int noOfTasksRemaining = 0;
	private ArrayList<Integer> numberOfTasksOnEachPage;

	private int taskCompositeHeight = 0;
	private int taskCompositeIncrement = 0;
	private int taskCompositeHeightForThreeLines = 0;

	public DisplayLogic(Logic logic, DisplayMode displayMode, Composite displayTask, int pageNumber) {
		this.logic = logic;
		this.displayMode = displayMode;
		this.displayTask = displayTask;
		this.pageNumber = pageNumber;
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
	
	protected void setDisplayMode(DisplayMode displayMode) {
		this.displayMode = displayMode;
	}
	
	protected void setDisplayDateTime(org.joda.time.DateTime currentDisplayDateTime) {
		this.currentDisplayDateTime = currentDisplayDateTime;
	}
	
	protected void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	protected int getPageNumber() {
		return pageNumber;
	}
	
	protected DisplayMode getDisplayMode() {
		//TODO: add in conditions to ensure that it is not null.
		return displayMode;
	}

	protected int getNumberOfRemainingTasks() {
		noOfTasksRemaining = logic.getNumberOfRemainingTasks();
		return noOfTasksRemaining;
	}

	protected int getNumberOfTasksToday() {
		noOfTasksToday = logic.getNumberOfTasksToday();
		return noOfTasksToday;
	}
	
	public String getDisplayWindowTitle() {
		return determineTitle();
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
	
	private String determineTitle() {
		switch (displayMode) {
		case TODAY:
			return Constants.MODE_TODAY;
		case TOMORROW:
			return Constants.MODE_TOMORROW;
		case DEADLINE:
			return Constants.MODE_DEADLINE;
		case FLOATING:
			return Constants.MODE_FLOATING;
		case TIMED:
			return Constants.MODE_TIMED;
		case UNTIMED:
			return Constants.MODE_UNTIMED;
		case SEARCH:
			return Constants.MODE_SEARCH;
		case OVERDUE:
			return Constants.MODE_OVERDUE;
		case ALL:
			return Constants.MODE_ALL;
		case DATE:
			// TODO: ensure that currentDisplayDateTime is set. otherwise will be defaulted to today.
			return Constants.dateOnlyFormat.print(currentDisplayDateTime);
		default:
			return "Congrats! You have managed to break our application!";
		}
	}
	
	protected void displayOnWindow() {
		if (pageNumber > numberOfTasksOnEachPage.size()) {
			pageNumber = numberOfTasksOnEachPage.size();
		}
		if (pageNumber <= 0) {
			pageNumber = 1;
		}

		int startingIndex = 0;
		for (int i = 0; i < pageNumber - 1; i++) {
			startingIndex += numberOfTasksOnEachPage.get(i);
		}

		ArrayList<Task> taskList = logic.getTasksToDisplay();
		int numberOfTasks = taskList.size();

		Composite[] taskComposites = new Composite[numberOfTasks];

		for (int i = 0; i < numberOfTasksOnEachPage.get(pageNumber - 1); i++) {
			taskComposites[i] = new TaskComposite(displayTask,
					taskList.get(startingIndex + i), startingIndex + i + 1);
		}
	}

}
