package ui;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;

import common.Constants;
import common.DisplayMode;

import logic.Feedback;
import logic.Logic;
import logic.Task;

public class DisplayLogic {

	private Logic logic;
	private DisplayMode displayMode;
	private Composite displayTask;
	private int pageNumber;
	private DisplayStateHistory displayStateHistory;

	private org.joda.time.DateTime currentDisplayDateTime = new org.joda.time.DateTime();

	private int noOfTasksToday = 0;
	private int noOfTasksRemaining = 0;
	private ArrayList<Integer> numberOfTasksOnEachPage;

	private int taskCompositeHeight = 0;
	private int taskCompositeIncrement = 0;
	private int taskCompositeHeightForThreeLines = 0;

	public DisplayLogic(Logic logic, DisplayMode displayMode,
			Composite displayTask, int pageNumber) {
		setLogic(logic);
		setDisplayMode(displayMode);
		setDisplayTask(displayTask);
		setPageNumber(pageNumber);
		this.displayStateHistory = new DisplayStateHistory();
	}

	protected void setTaskCompositeHeight(int height) {
		assert (taskCompositeHeight >= 0);
		taskCompositeHeight = height;
	}

	protected void setTaskCompositeIncrement(int increment) {
		assert (taskCompositeIncrement >= 0);
		taskCompositeIncrement = increment;
	}

	protected void setTaskCompositeHeightForThreeLines(int height) {
		assert (taskCompositeHeightForThreeLines >= 0);
		taskCompositeHeightForThreeLines = height;
	}
	
	private void setLogic(Logic logic) {
		assert (this.logic != null);
		this.logic = logic;
	}

	protected void setDisplayMode(DisplayMode displayMode) {
		assert (this.displayMode != null);
		this.displayMode = displayMode;
	}
	
	private void setDisplayTask(Composite displayTask) {
		assert (this.displayTask != null);
		this.displayTask = displayTask;
	}

	protected void setDisplayDateTime(
			org.joda.time.DateTime currentDisplayDateTime) {
		assert (this.currentDisplayDateTime != null);
		this.currentDisplayDateTime = currentDisplayDateTime;
	}

	protected void setPageNumber(int pageNumber) {
		assert (this.pageNumber > 0);
		this.pageNumber = pageNumber;
	}

	protected int getPageNumber() {
		assert (pageNumber > 0);
		return pageNumber;
	}

	protected DisplayMode getDisplayMode() {
		return displayMode;
	}

	protected int getNumberOfRemainingTasks() {
		noOfTasksRemaining = logic.getNumberOfRemainingTasks();
		assert (noOfTasksRemaining >= 0);
		return noOfTasksRemaining;
	}

	protected int getNumberOfTasksToday() {
		noOfTasksToday = logic.getNumberOfTasksToday();
		assert (noOfTasksToday >= 0);
		return noOfTasksToday;
	}

	public String getDisplayWindowTitle() {
		return determineTitle();
	}

	protected ArrayList<Integer> getNumberOfTasksForEachPage() {
		determineNumberOfTasksForEachPage(displayMode);
		return numberOfTasksOnEachPage;
	}

	public int getPage(int index) {
		int page = 1;
		int count = 0;
		for (int i = 0; i < numberOfTasksOnEachPage.size(); i++) {
			count += numberOfTasksOnEachPage.get(i);
			if (index <= count) {
				return page;
			} else {
				page++;
			}
		}
		return page;
	}

	private void determineNumberOfTasksForEachPage(DisplayMode displayMode) {
		ArrayList<Task> taskList = logic.getTasksToDisplay(displayMode);
		int numberOfTasks = taskList.size();
		int[] heights = new int[numberOfTasks];
		int index = 0;
		for (Task task : taskList) {
			heights[index] = determineTaskHeight(task);
			index++;
		}
//		System.out.println(Arrays.toString(heights));

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
//		System.out.println(numberOfTasksOnEachPage);
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
		case TODO:
			return Constants.MODE_TODO;
		case DATE:
			return Constants.dateOnlyFormat.print(currentDisplayDateTime);
		default:
			return "Congrats! You have managed to break our application!";
		}
	}

	protected void displayTasks() {
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

		ArrayList<Task> taskList = logic.getTasksToDisplay(displayMode);

		Composite[] taskComposites = new Composite[numberOfTasksOnEachPage
				.get(pageNumber - 1)];

		for (int i = 0; i < taskComposites.length; i++) {
			taskComposites[i] = new TaskComposite(displayTask,
					taskList.get(startingIndex + i), startingIndex + i + 1);
		}
		
		displayTask.pack();
	}

	protected void processFeedbackObject(Feedback feedbackObj,
			HelpDialog helpDialog) {
		switch (feedbackObj.getCommand()) {
		case ADD:
			this.setDisplayMode(DisplayMode.TODO);
			this.setPageNumber(Integer.MAX_VALUE);
			displayStateHistory.addDisplayState(DisplayMode.ALL,
					Integer.MAX_VALUE);
			break;
		case EDIT:
		case DELETE:
		case DONE:
		case FINALISE:
			if (!feedbackObj.isErrorMessage()) {
				this.setPageNumber(getPage(feedbackObj.getTaskIndex()));
			}
			displayStateHistory.addDisplayState(this.getDisplayMode(),
					this.getPageNumber());
			break;
		case DISPLAY:
			this.setDisplayMode(feedbackObj.getDisplayMode());
			if (this.getDisplayMode() == DisplayMode.DATE) {
				this.setDisplayDateTime(feedbackObj.getDisplayDate());
			}
			this.setPageNumber(Constants.DEFAULT_PAGE_NUMBER);
			displayStateHistory.addDisplayState(this.getDisplayMode(),
					this.getPageNumber());
			break;
		case SEARCH:
			this.setPageNumber(Constants.DEFAULT_PAGE_NUMBER);
			this.setDisplayMode(DisplayMode.SEARCH);
			break;
		case GOTO:
			this.setPageNumber(feedbackObj.getGotoPage());
			break;
		case SORT:
		case CLEAR:
			this.setPageNumber(Constants.DEFAULT_PAGE_NUMBER);
			this.setDisplayMode(DisplayMode.TODO);
			displayStateHistory.addDisplayState(this.getDisplayMode(),
					this.getPageNumber());
			break;
		case UNDO:
			this.setDisplayMode(displayStateHistory.getCurrentDisplayMode());
			this.setPageNumber(displayStateHistory.getCurrentPageNumber());
			displayStateHistory.undo();
			break;
		case REDO:
			displayStateHistory.redo();
			this.setDisplayMode(displayStateHistory.getCurrentDisplayMode());
			this.setPageNumber(displayStateHistory.getCurrentPageNumber());
			break;
		case HELP:
			helpDialog.open(feedbackObj);
		case EXIT:
		case INVALID:
		default:
		}
	}

}
