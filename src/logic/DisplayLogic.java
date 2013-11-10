package logic;

import java.util.ArrayList;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.joda.time.DateTime;

import ui.ApplicationWindow;
import ui.HelpDialog;
import ui.TaskComposite;

import common.Constants;
import common.DisplayMode;
import common.Feedback;
import common.Task;

public class DisplayLogic {

	private CommandLogic commandLogic;
	private DisplayMode displayMode;
	private Composite taskDisplay;
	private int pageNumber;
	
	private DateTime currentDisplayDateTime = new DateTime();
	private int noOfTasksToday = 0;
	private int noOfTasksRemaining = 0;
	private ArrayList<Integer> numberOfTasksOnEachPage;
	private int taskCompositeHeight = 0;
	private int taskCompositeIncrement = 0;
	private int taskCompositeHeightForTwoLines = 0;


	private int taskCompositeHeightForThreeLines = 0;
	private TaskComposite[] taskComposites = null;
	// A list of global indices of tasks that should be highlighted on next display draw
	private ArrayList<Integer> highlightedTasks = new ArrayList<Integer>();
	
	private boolean recreateTaskComposites = false;

	public DisplayLogic(CommandLogic logic, DisplayMode displayMode, int pageNumber) {
		setLogic(logic);
		setDisplayMode(displayMode);
		taskDisplay = new Composite(ApplicationWindow.shell, SWT.NONE);

		determineNumberOfTasksForEachPage(displayMode);
		setPageNumber(pageNumber);
	}
	
	/**
	 * Functions relating to the task display
	 */
	
	public void initialiseTaskDisplay() {
		RowLayout rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.pack = true;
		taskDisplay.setLayout(rowLayout);
		taskDisplay.setBounds(32, 86, 435, 450);
	}
	
	public void deleteTaskComposites() {
		for (Control child : taskDisplay.getChildren()) {
			child.dispose();
		}
	}
	
	public int getTaskDisplayHeight() {
		return taskDisplay.getSize().y;
	}
	
	public Composite getTaskDisplay() {
		return taskDisplay;
	}
	
	/**
	 * Major functions
	 */
	
	public void refreshTaskDisplay() {
		if (recreateTaskComposites) {
			deleteTaskComposites();
			initialiseTaskDisplay();
		}
		displayTasks();
	}

	public void displayTasks() {
		
		determineNumberOfTasksForEachPage(displayMode);
		if(pageNumber > numberOfTasksOnEachPage.size()) {
			setPageNumber(numberOfTasksOnEachPage.size());
		}
		assert (pageNumber > 0 && pageNumber <= numberOfTasksOnEachPage.size()) : "Invalid page number " + pageNumber;

		int startingIndex = 0;
		for (int i = 0; i<pageNumber-1; ++i) {
			startingIndex += numberOfTasksOnEachPage.get(i);
 		}

		ArrayList<Task> taskList = commandLogic.getTasksToDisplay(displayMode, currentDisplayDateTime);
		taskComposites = new TaskComposite[numberOfTasksOnEachPage.get(pageNumber - 1)];

		for (int i = 0; i < taskComposites.length; i++) {
			taskComposites[i] = new TaskComposite(taskDisplay, taskList.get(startingIndex + i), startingIndex + i + 1);
		}

		taskDisplay.pack();

		// Display highlighted tasks on composite creation
		highlightTasks(true);
	}
	
	public void createNewPage() {
		this.pageNumber = numberOfTasksOnEachPage.size()+1;
		taskDisplay.pack();
	}
	
	public void processFeedback(Feedback feedback, HelpDialog helpDialog) {
		switch (feedback.getCommand()) {
		case ADD:
			Task task = feedback.getAddedTask();
			if(!taskIsInCurrentDisplayMode(task)){
				this.setDisplayMode(DisplayMode.TODO);
			}
			goToLastPage();
			break;
		case EDIT:
			// fallthrough
		case DONE:
			// fallthrough
		case FINALISE:
			highlightedTasks = new ArrayList<>();
			if (!feedback.isErrorMessage()) {
				this.setPageNumber(getPageOfTask(feedback.getTaskIndex()));
			}
			break;
		case DELETE:
			highlightedTasks = new ArrayList<>();
			break;
		case DISPLAY:
			this.setDisplayMode(feedback.getDisplayMode());
			if (this.getDisplayMode() == DisplayMode.DATE) {
				this.setDisplayDateTime(feedback.getDisplayDate());
			}
			this.setPageNumber(Constants.DEFAULT_PAGE_NUMBER);
			break;
		case SEARCH:
			this.setPageNumber(Constants.DEFAULT_PAGE_NUMBER);
			this.setDisplayMode(DisplayMode.SEARCH);
			break;
		case GOTO:
			if (!feedback.isErrorMessage()) {
				this.setPageNumber(feedback.getPageNumber());
			}
			break;
		
		case CLEAR:
			this.setPageNumber(Constants.DEFAULT_PAGE_NUMBER);
			this.setDisplayMode(DisplayMode.TODO);
			break;
		case UNDO:
			// fallthrough
		case REDO:
			// fallthrough
		case SORT:
			this.setPageNumber(Constants.DEFAULT_PAGE_NUMBER);
			break;
		case HELP:
			helpDialog.open(feedback);
		case EXIT:
		case INVALID:
		default:
			break;
		}
	}

	private boolean taskIsInCurrentDisplayMode(Task task) {
		switch (displayMode) {
		case ALL:
			return true;
		case DATE:
			return task.isOnDate(currentDisplayDateTime);
		case DEADLINE:
			return task.isDeadlineTask();
		case DONE:
			return task.isDone();
		case INVALID:
			return false;
		case OVERDUE:
			return task.isOverdue();
		case SEARCH:
			return false;
		case TENTATIVE:
			return task.isFloatingTask();
		case TIMED:
			return task.isTimedTask();
		case TODAY:
			return task.isOnDate(new DateTime());
		case TODO:
			return true;
		case TOMORROW:
			return task.isOnDate(new DateTime().plusDays(1));
		case UNTIMED:
			return task.isUntimedTask();
		default:
			break;
	
		}
		return false;
	}

	private String determineTitle() {
		switch (displayMode) {
		case TODAY:
			return Constants.MODE_TODAY;
		case DONE:
			return Constants.MODE_DONE;
		case TOMORROW:
			return Constants.MODE_TOMORROW;
		case DEADLINE:
			return Constants.MODE_DEADLINE;
		case TENTATIVE:
			return Constants.MODE_TENTATIVE;
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
	
	/**
	 * Everything related to composites and pages
	 */
	
	public boolean isTaskDisplayedCurrently(int index) {
		return getPageNumber() == getPageOfTask(index);
	}

	public TaskComposite getCompositeLocal (int index) {
		assert index >= 0 && index < taskComposites.length : "Invalid local composite index";
		return taskComposites[index];
	}
	
	public TaskComposite getCompositeGlobal (int index) {
		assert isTaskDisplayedCurrently(index) : "Cannot get composite of task that isn't currently displayed";
		assert index > 0 && index <= getTotalNumberOfComposites();
		int indexOfFirstTask = 1;
		for (int i=0; i<getPageNumber()-1; i++) {
			indexOfFirstTask += numberOfTasksOnEachPage.get(i);
		}
		return taskComposites[index - indexOfFirstTask];
	}
	
	public int getPageOfTask(int index) {
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
	
	public ArrayList<Integer> getNumberOfTasksPerPage() {
		determineNumberOfTasksForEachPage(displayMode);
		// TODO ^ don't have to do that calculation if it hasn't changed
		return numberOfTasksOnEachPage;
	}
	
	public int getTotalNumberOfComposites() {
		int sum = 0;
		for(Integer integer : numberOfTasksOnEachPage){
			sum += integer;
		}
		return sum;
	}

	private void determineNumberOfTasksForEachPage(DisplayMode displayMode) {
		ArrayList<Task> taskList = commandLogic.getTasksToDisplay(displayMode, currentDisplayDateTime);
		int numberOfTasks = taskList.size();
		int[] heights = new int[numberOfTasks];
		int index = 0;
		for (Task task : taskList) {
			heights[index] = determineTaskHeight(task);
			index++;
		}
		
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
		
	}

	public int determineTaskHeight(Task task) {
		if (!task.isFloatingTask()) {
			return taskCompositeHeight;
		} else {
			int numberOfSlots = task.getPossibleTime().size();
			boolean hasTags = task.getTags().size() > 0;
			int numberOfLines = numberOfSlots;
			if (hasTags) {
				numberOfLines++;
			}
			if(numberOfLines == 2){
				return taskCompositeHeightForTwoLines;
			} if (numberOfLines == 3) {
				return taskCompositeHeightForThreeLines;
			} else {
				return (numberOfLines - 3) * taskCompositeIncrement
						+ taskCompositeHeightForThreeLines;
			}
		}
	}
	
	/**
	 * Facilities for keeping track of highlighted tasks
	 */
	
	public void clearHighlightedTasks () {
		highlightTasks(false);
		highlightedTasks.clear();
	}

	public void addHighlightedTask(int index) {
		assert index > 0 && index <= getTotalNumberOfComposites();
		highlightedTasks.add(index);
	}

	private void highlightTasks(boolean on) {
		for (Integer taskIndex : highlightedTasks) {
			if (isTaskDisplayedCurrently(taskIndex)) {
				getCompositeGlobal(taskIndex).setHighlighted(on);
			}
		}
	}

	/**
	 * Getters and setters
	 */
	
	public int getNumberOfPages() {
		return numberOfTasksOnEachPage.size();
	}
	
	public void setPageNumber(int pageNumber) {
//		assert (pageNumber > 0 && pageNumber <= numberOfTasksOnEachPage.size()) : "Invalid page number " + pageNumber;
		if (this.pageNumber != pageNumber) {
			recreateTaskComposites = true;
			this.pageNumber = pageNumber;//Math.max(1, Math.min(pageNumber, numberOfTasksOnEachPage.size()));;
		}
	}
	
	public void goToLastPage() {
		setPageNumber(Integer.MAX_VALUE);
	}
	
	public void goToFirstPage() {
		setPageNumber(1);
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public DisplayMode getDisplayMode() {
		return displayMode;
	}

	public int getNumberOfRemainingTasks() {
		noOfTasksRemaining = commandLogic.getNumberOfRemainingTasks();
		assert (noOfTasksRemaining >= 0);
		return noOfTasksRemaining;
	}

	public int getNumberOfTasksToday() {
		noOfTasksToday = commandLogic.getNumberOfTasksToday();
		assert (noOfTasksToday >= 0);
		return noOfTasksToday;
	}

	public String getDisplayWindowTitle() {
		return determineTitle();
	}
	
	private void setLogic(CommandLogic logic) {
		assert (logic != null);
		this.commandLogic = logic;
	}

	public void setDisplayMode(DisplayMode displayMode) {
		assert (displayMode != null);
		this.displayMode = displayMode;
	}

	public void setDisplayDateTime(DateTime currentDisplayDateTime) {
		assert (currentDisplayDateTime != null);
		this.currentDisplayDateTime = currentDisplayDateTime;
	}
	

	public void setTaskCompositeHeight(int height) {
		assert (taskCompositeHeight >= 0);
		taskCompositeHeight = height;
	}
	
	public void setTaskCompositeIncrement(int increment) {
		assert (taskCompositeIncrement >= 0);
		taskCompositeIncrement = increment;
	}

	public void setTaskCompositeHeightForThreeLines(int height) {
		assert (taskCompositeHeightForThreeLines >= 0);
		taskCompositeHeightForThreeLines = height;
	}
	
	public int getTaskCompositeHeightForTwoLines() {
		return taskCompositeHeightForTwoLines;
	}

	public void setTaskCompositeHeightForTwoLines(int taskCompositeHeightForTwoLines) {
		this.taskCompositeHeightForTwoLines = taskCompositeHeightForTwoLines;
	}
}
