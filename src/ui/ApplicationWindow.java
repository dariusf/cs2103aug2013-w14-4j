package ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import logic.DisplayLogic;
import logic.CommandLogic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.wb.swt.SWTResourceManager;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.joda.time.DateTime;

import common.Command;
import common.CommandType;
import common.Constants;
import common.DisplayMode;
import common.Feedback;
import common.Interval;
import common.Task;
import common.TaskType;
import common.undo.Action;
import common.undo.ActionStack;

public class ApplicationWindow {
	// Logging
	public static boolean testMode = false;
	public static final Logger logger = Logger
			.getLogger(ApplicationWindow.class.getName());

	// Logical processing
	public static CommandLogic commandLogic;
	public static DisplayLogic displayLogic;

	// UI elements
	public static Shell shell; // accessed by task composite
	public Text input;
	public StyledText displayFeedback;
	public StyledText displayPageNumber;
	public Composite closeButton;
	public StyledText displayTitle;
	public StyledText displayRemainingTaskCount;
	public StyledText displayTodayTaskCount;
	public TaskComposite dummyTaskComposite;
	public static HelpDialog helpDialog;
	private static Tray tray;
	private static TrayItem trayIcon;

	// Colours
	public Color green;
	public Color red;
	public Color purple;

	// Fonts
	public Font windowTitleFont;
	public Font pageNumberFont;
	public Font inputFont;
	public Font displayFeedbackFont;
	public Font indexFont; // accessed by task composite
	public Font titleFont; // accessed by task composite
	public Font descriptionFont; // accessed by task composite

	// Others
	public boolean dummyCompositeIsCreated = false;
	public static ApplicationWindow self;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			logger.setLevel(Level.OFF);
			commandLogic = new CommandLogic();
			ApplicationWindow window = new ApplicationWindow();
			self = window;
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		centerShellWithRespectToScreen(display);
		shell.open();
		shell.layout();
		if (testMode) {
			runTest(display);
		}
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		defineShell();
		// TODO Please change this value to 1 when you compile for use on your
		// computers.
		defineFont(1);
		defineColours();
		defineDisplayPageNumber();
		defineRemainingTaskCount();
		defineTodayTaskCount();
		defineDisplayLogic();
		defineDisplayTitle();
		defineTaskCompositeHeight();
		defineFeedbackWindow();
		defineInputField();
		defineTrayIcon();
		// Tween.registerAccessor(Text.class, new InputAccessor());
		defineWindowButton();
		enableNativeHook();
		setWelcomePage();

		helpDialog = new HelpDialog(shell);

		enterDriverLoop();

		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				commandLogic.executeCommand(Constants.COMMAND_EXIT);
				System.exit(0);
			}
		});

		adjustPageNumberAlignment();

	}

	/**
	 * Methods required for initiating and defining UI elements in Application
	 * Window
	 */
	private void defineColours() {
		red = new Color(shell.getDisplay(), 0x99, 0, 0);
		green = new Color(shell.getDisplay(), 0, 0x66, 0);
		purple = SWTResourceManager.getColor(102, 0, 255);
	}

	private void defineInputField() {
		input = new Text(shell, SWT.BORDER);
		input.setFocus();
		input.setFont(inputFont);
		input.setBounds(20, 608, 442, 50);
		input.setBackground(SWTResourceManager.getColor(255, 255, 255));
	}

	private void defineDisplayLogic() {
		displayLogic = new DisplayLogic(commandLogic, DisplayMode.TODO,
				Constants.DEFAULT_PAGE_NUMBER);
		displayLogic.initialiseTaskDisplay();
	}

	private void defineFeedbackWindow() {
		displayFeedback = new StyledText(shell, SWT.READ_ONLY | SWT.WRAP
				| SWT.MULTI);
		displayFeedback.setEnabled(false);
		displayFeedback.setBounds(35, 558, 412, 40);
		displayFeedback.setFont(displayFeedbackFont);
		displayFeedback.setText(displayWelcomeMessage());
		displayFeedback.setForeground(purple);
	}

	private void defineDisplayTitle() {
		displayTitle = new StyledText(shell, SWT.READ_ONLY | SWT.WRAP
				| SWT.SINGLE);
		displayTitle.setEnabled(false);
		displayTitle.setBounds(36, 23, 311, 50);
		displayTitle.setForeground(red);
		displayTitle.setLineAlignment(0, 1, SWT.LEFT);
		displayTitle.setFont(windowTitleFont);
	}

	private void defineTodayTaskCount() {
		displayTodayTaskCount = new StyledText(shell, SWT.READ_ONLY | SWT.WRAP
				| SWT.SINGLE);
		displayTodayTaskCount.setEnabled(false);
		displayTodayTaskCount.setBounds(370, 41, 77, 14);
		displayTodayTaskCount.setFont(descriptionFont);
		displayTodayTaskCount.setLineAlignment(0, 1, SWT.RIGHT);
	}

	private void defineRemainingTaskCount() {
		displayRemainingTaskCount = new StyledText(shell, SWT.READ_ONLY
				| SWT.WRAP | SWT.SINGLE);
		displayRemainingTaskCount.setEnabled(false);
		displayRemainingTaskCount.setBounds(370, 54, 77, 14);
		displayRemainingTaskCount.setFont(descriptionFont);
		displayRemainingTaskCount.setLineAlignment(0, 1, SWT.RIGHT);
	}

	private void defineDisplayPageNumber() {
		displayPageNumber = new StyledText(shell, SWT.READ_ONLY | SWT.SINGLE);
		displayPageNumber.setEnabled(false);
		displayPageNumber.setSize(105, 25);
		displayPageNumber.setLocation(335, 567);
	}

	private void defineShell() {
		shell = new Shell(Display.getDefault(), SWT.NO_TRIM);

		shell.setImage(SWTResourceManager.getImage(ApplicationWindow.class,
				"/image/basketIcon.gif"));

		final Image image = SWTResourceManager.getImage(
				ApplicationWindow.class, "/image/background.png");
		Region region = new Region();
		final ImageData imageData = image.getImageData();
		if (imageData.alphaData != null) {
			Rectangle pixel = new Rectangle(0, 0, 1, 1);
			for (int y = 0; y < imageData.height; y++) {
				for (int x = 0; x < imageData.width; x++) {
					if (imageData.getAlpha(x, y) == 255) {
						pixel.x = imageData.x + x;
						pixel.y = imageData.y + y;
						region.add(pixel);
					}
				}
			}
		} else {
			ImageData mask = imageData.getTransparencyMask();
			Rectangle pixel = new Rectangle(0, 0, 1, 1);
			for (int y = 0; y < mask.height; y++) {
				for (int x = 0; x < mask.width; x++) {
					if (mask.getPixel(x, y) != 0) {
						pixel.x = imageData.x + x;
						pixel.y = imageData.y + y;
						region.add(pixel);
					}
				}
			}
		}
		shell.setRegion(region);

		Listener l = new Listener() {
			int startX, startY;

			public void handleEvent(Event e) {
				if (e.type == SWT.KeyDown && e.character == SWT.ESC) {
					shell.dispose();
				}
				if (e.type == SWT.MouseDown && e.button == 1) {
					startX = e.x;
					startY = e.y;
				}
				if (e.type == SWT.MouseMove && (e.stateMask & SWT.BUTTON1) != 0) {
					Point p = shell.toDisplay(e.x, e.y);
					p.x -= startX;
					p.y -= startY;
					shell.setLocation(p);
				}
				if (e.type == SWT.Paint) {
					e.gc.drawImage(image, imageData.x, imageData.y);
				}
			}
		};
		shell.addListener(SWT.KeyDown, l);
		shell.addListener(SWT.MouseDown, l);
		shell.addListener(SWT.MouseMove, l);
		shell.addListener(SWT.Paint, l);

		shell.setSize(imageData.x + imageData.width, imageData.y
				+ imageData.height);

		ImageData backgroundData = new ImageData(getClass()
				.getResourceAsStream("/image/background.png"));
		Image transparentBackgroundImage = new Image(Display.getCurrent(),
				backgroundData);
		shell.setBackgroundImage(transparentBackgroundImage);
		shell.setBackgroundMode(SWT.INHERIT_FORCE);

		shell.setSize(482, 681);
		shell.setText(Constants.APP_NAME);
	}

	private void defineWindowButton() {
		closeButton = new Composite(shell, SWT.NONE);
		closeButton.setBounds(433, 0, 49, 27);

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				if (event.type == SWT.MouseUp) {
					Point pt1 = shell.toDisplay(0, 0);
					Point pt2 = Display.getCurrent().getCursorLocation();
					Point offset = new Point(pt2.x - pt1.x, pt2.y - pt1.y);

					if (offset.x > 455 && offset.y < 27) {
						executeUserInput(Constants.COMMAND_EXIT);
						System.exit(0);
					} else if (offset.x > 433 && offset.y < 27) {
						toggleMinimizeState();
					}
				}

			}

		};
		closeButton.addListener(SWT.MouseUp, listener);
	}

	public void defineFont(double scaling) {
		boolean isWindows = System.getProperty("os.name").toLowerCase()
				.indexOf("win") >= 0;
		if (isWindows) {
			windowTitleFont = new Font(shell.getDisplay(), "Calibri",
					(int) (33 * scaling), SWT.NORMAL);
			pageNumberFont = new Font(shell.getDisplay(), "Calibri",
					(int) (13 * scaling), SWT.NORMAL);
			indexFont = new Font(shell.getDisplay(), "Calibri",
					(int) (40 * scaling), SWT.NORMAL);
			titleFont = new Font(shell.getDisplay(), "Calibri",
					(int) (18 * scaling), SWT.NORMAL);
			descriptionFont = new Font(shell.getDisplay(), "Calibri",
					(int) (9 * scaling), SWT.NORMAL);
			inputFont = new Font(shell.getDisplay(), "Calibri",
					(int) (18 * scaling), SWT.NORMAL);
			displayFeedbackFont = new Font(shell.getDisplay(), "Calibri",
					(int) (10 * scaling), SWT.NORMAL);
		} else {
			windowTitleFont = new Font(shell.getDisplay(), "Calibri", 44,
					SWT.NORMAL);
			pageNumberFont = new Font(shell.getDisplay(), "Calibri", 18,
					SWT.NORMAL);
			indexFont = new Font(shell.getDisplay(), "Calibri", 60, SWT.NORMAL);
			titleFont = new Font(shell.getDisplay(), "Calibri", 24, SWT.NORMAL);
			descriptionFont = new Font(shell.getDisplay(), "Calibri", 12,
					SWT.NORMAL);
			inputFont = new Font(shell.getDisplay(), "Calibri", 24, SWT.NORMAL);
			displayFeedbackFont = new Font(shell.getDisplay(), "Calibri", 13,
					SWT.NORMAL);
		}
	}

	public void defineTrayIcon() {
		tray = shell.getDisplay().getSystemTray();
		trayIcon = new TrayItem(tray, SWT.NONE);
		trayIcon.setImage(SWTResourceManager.getImage(ApplicationWindow.class,
				"/image/basketIcon.gif"));

		trayIcon.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				toggleMinimizeState();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				toggleMinimizeState();
			}
		});
	}

	// Used to determine the height of a task composite by creating several
	// dummy task composites
	public void defineTaskCompositeHeight() {
		Command command1 = new Command(CommandType.ADD);
		command1.setDescription("haha");
		Task task1 = new Task(command1);
		task1.setType(TaskType.UNTIMED);
		TaskComposite taskComposite1 = new TaskComposite(
				displayLogic.getTaskDisplay(), task1, 1);
		int taskCompositeHeight = taskComposite1.getSize().y;
		displayLogic.setTaskCompositeHeight(taskCompositeHeight);

		DateTime startDate1 = new DateTime(2013, 10, 30, 15, 0, 0);
		DateTime endDate1 = new DateTime(2013, 10, 30, 16, 0, 0);
		Interval interval1 = new Interval();
		interval1.setStartDateTime(startDate1);
		interval1.setEndDateTime(endDate1);
		DateTime startDate2 = new DateTime(2013, 10, 30, 16, 0, 0);
		DateTime endDate2 = new DateTime(2013, 10, 30, 17, 0, 0);
		Interval interval2 = new Interval();
		interval2.setStartDateTime(startDate2);
		interval2.setEndDateTime(endDate2);
		ArrayList<Interval> intervalList = new ArrayList<Interval>();
		intervalList.add(interval1);
		intervalList.add(interval2);
		task1.setType(TaskType.TENTATIVE);
		task1.setPossibleTime(intervalList);
		TaskComposite taskComposite2 = new TaskComposite(
				displayLogic.getTaskDisplay(), task1, 1);
		int taskComposite2LinesHeight = taskComposite2.getSize().y;
		displayLogic
				.setTaskCompositeHeightForTwoLines(taskComposite2LinesHeight);

		DateTime startDate3 = new DateTime(2013, 10, 30, 17, 0, 0);
		DateTime endDate3 = new DateTime(2013, 10, 30, 18, 0, 0);
		Interval interval3 = new Interval();
		interval3.setStartDateTime(startDate3);
		interval3.setEndDateTime(endDate3);
		intervalList.add(interval3);

		task1.setType(TaskType.TENTATIVE);
		task1.setPossibleTime(intervalList);
		TaskComposite taskComposite3 = new TaskComposite(
				displayLogic.getTaskDisplay(), task1, 1);
		int taskComposite3LinesHeight = taskComposite3.getSize().y;
		displayLogic
				.setTaskCompositeHeightForThreeLines(taskComposite3LinesHeight);

		ArrayList<String> tags = new ArrayList<String>();
		tags.add("TGIF");
		task1.setTags(tags);
		TaskComposite taskComposite4 = new TaskComposite(
				displayLogic.getTaskDisplay(), task1, 1);
		int taskCompositeIncrement = taskComposite4.getSize().y
				- taskComposite3LinesHeight;
		displayLogic.setTaskCompositeIncrement(taskCompositeIncrement);
	}

	/**
	 * Methods required to align windows at the centre of the monitor display
	 */
	private void centerShellWithRespectToScreen(Display display) {
		Monitor primary = display.getPrimaryMonitor();

		Rectangle monitorBounds = primary.getBounds();
		Rectangle shellBounds = shell.getBounds();

		int x = calculateXCoordinateForShellPosition(monitorBounds, shellBounds);
		int y = calculateYCoordinateForShellPosition(monitorBounds, shellBounds);

		shell.setLocation(x, y);
	}

	private int calculateYCoordinateForShellPosition(Rectangle monitorBounds,
			Rectangle shellBounds) {
		int difference = calculateDifferenceInHeight(monitorBounds, shellBounds);
		int heightOffset = calculateHalfOfNumber(difference);
		int yCoordinate = monitorBounds.y + heightOffset;
		return yCoordinate;
	}

	private int calculateDifferenceInHeight(Rectangle monitorBounds,
			Rectangle shellBounds) {
		return monitorBounds.height - shellBounds.height;
	}

	private int calculateXCoordinateForShellPosition(Rectangle monitorBounds,
			Rectangle shellBounds) {
		int difference = calculateDifferenceInWidth(monitorBounds, shellBounds);
		int widthOffset = calculateHalfOfNumber(difference);
		int xCoordinate = monitorBounds.x + widthOffset;
		return xCoordinate;
	}

	private int calculateDifferenceInWidth(Rectangle monitorBounds,
			Rectangle shellBounds) {
		return monitorBounds.width - shellBounds.width;
	}

	private int calculateHalfOfNumber(int difference) {
		return difference / 2;
	}

	/**
	 * Methods for updating the UI elements in Application Window
	 */
	public void updateTaskDisplay() {
		displayLogic.refreshTaskDisplay();
		updateTaskStatistics();
	}

	public void adjustPageNumberAlignment() {
		updateTaskStatistics();
	}

	private void updateTaskStatistics() {
		ArrayList<Integer> numberOfTasksOnEachPage = displayLogic
				.getNumberOfTasksPerPage();
		displayPageNumber.setText("Page " + displayLogic.getPageNumber()
				+ " of " + numberOfTasksOnEachPage.size());
		displayPageNumber.setAlignment(SWT.CENTER);
		displayPageNumber.setFont(pageNumberFont);

		displayRemainingTaskCount.setText("Remaining: "
				+ displayLogic.getNumberOfRemainingTasks());
		displayTodayTaskCount.setText("Today: "
				+ displayLogic.getNumberOfTasksToday());

		displayTitle.setText(displayLogic.getDisplayWindowTitle());
	}

	public String displayWelcomeMessage() {
		String welcomeMessage = Constants.MSG_AVAILABLE_COMMANDS;
		return welcomeMessage;
	}

	public void setWelcomePage() {
		executeUserInput(Constants.WELCOME_PAGE_DISPLAY_COMMAND);
	}

	String userInput = "";
	UserInputHistory inputHistory = new UserInputHistory();

	private void getUpHistory() {
		int currentIndex = inputHistory.getIndex();
		if (currentIndex != -1) {
			String commandField = inputHistory.getInput(currentIndex);
			input.setText(commandField);
			inputHistory.setIndex(currentIndex - 1);
		}
	}

	private void getDownHistory() {
		if (!inputHistory.isEndOfHistory()) {
			int currentIndex = inputHistory.getIndex();
			String commandField = inputHistory.getInput(currentIndex + 1);
			input.setText(commandField);
			input.setSelection(input.getText().length());
			inputHistory.setIndex(currentIndex + 1);
		}
	}

	protected void setFeedbackColour(Feedback feedbackObj) {
		if (feedbackObj.isErrorMessage()) {
			displayFeedback.setForeground(red);
		} else if (!feedbackObj.isErrorMessage()) {
			displayFeedback.setForeground(green);
		}
	}

	public void enterDriverLoop() {
		input.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (isKeyboardInput(arg0.keyCode)) {
					userInput = input.getText();
					Command activeFeedback = commandLogic
							.activeFeedback(userInput);
					processActiveFeedback(activeFeedback);
				}
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.keyCode == SWT.ARROW_DOWN) {
					getDownHistory();
				} else if (arg0.keyCode == SWT.ARROW_UP) {
					getUpHistory();
				} else if (arg0.character == SWT.CR) {
					userInput = input.getText();
					dummyCompositeIsCreated = false;
					if (dummyTaskComposite != null
							&& !dummyTaskComposite.isDisposed()) {
						dummyTaskComposite.dispose();
					}
					inputHistory.addInput(userInput);

					executeUserInput(userInput);
				} else if (arg0.keyCode == SWT.PAGE_UP) {
					displayLogic.setPageNumber(Math.max(
							displayLogic.getPageNumber() - 1, 1));
					updateTaskDisplay();
					logger.log(Level.INFO, generateLoggingString());
				} else if (arg0.keyCode == SWT.PAGE_DOWN) {
					displayLogic.setPageNumber(Math.min(displayLogic
							.getPageNumber() + 1, displayLogic
							.getNumberOfTasksPerPage().size()));
					updateTaskDisplay();
					logger.log(Level.INFO, generateLoggingString());
				} else if (arg0.keyCode == SWT.F1
						&& arg0.stateMask != SWT.SHIFT) {
					executeUserInput(Constants.COMMAND_HELP);
				} else if (arg0.keyCode == SWT.F2) {
					executeUserInput(Constants.COMMAND_DISPLAY);
				} else if (arg0.keyCode == SWT.F3) {
					executeUserInput(Constants.COMMAND_DISPLAY + " today");
				} else if (arg0.keyCode == SWT.F4) {
					executeUserInput(Constants.COMMAND_DISPLAY + " tomorrow");
				} else if (arg0.keyCode == SWT.F5) {
					executeUserInput(Constants.COMMAND_DISPLAY + " all");
				} else if (arg0.keyCode == SWT.F6) {
					executeUserInput(Constants.COMMAND_DISPLAY + " done");
				} else if (arg0.keyCode == SWT.F7) {
					executeUserInput(Constants.COMMAND_DISPLAY + " overdue");
				} else if (arg0.keyCode == SWT.F8) {
					executeUserInput(Constants.COMMAND_DISPLAY + " untimed");
				} else if (arg0.keyCode == SWT.F9) {
					executeUserInput(Constants.COMMAND_DISPLAY + " deadline");
				} else if (arg0.keyCode == SWT.F10) {
					executeUserInput(Constants.COMMAND_DISPLAY + " timed");
				} else if (arg0.keyCode == SWT.F11) {
					executeUserInput(Constants.COMMAND_DISPLAY + " tentative");
				} else if (arg0.keyCode == SWT.F12) {
					int index = new Random()
							.nextInt(Constants.RANDOM_JOKES.length);
					displayFeedback.setForeground(red);
					displayFeedback.setText(Constants.RANDOM_JOKES[index]);
				} else if (arg0.keyCode == SWT.ESC) {
					helpDialog.close();
				}
			}

			private void processActiveFeedback(Command executedCommand) {

				if (executedCommand == null) {
					displayLogic.clearHighlightedTasks();
					return;
				}

				int taskIndex = executedCommand.getTaskIndex();
				CommandType commandType = executedCommand.getCommandType();
				displayFeedback.setForeground(purple);

				ContextualHelp contextualHelp = new ContextualHelp(
						executedCommand);
				TextFormatter.setFormattedText(displayFeedback,
						contextualHelp.toString());

				switch (commandType) {
				case DONE:
					// Fall through
				case DELETE:
					if (taskIndex > 0
							&& taskIndex <= commandLogic.getNumberOfTasks()) {
						highlightTaskFeedback(taskIndex);
					} else if (taskIndex != -1) {
						displayInvalidIndexAsFeedback();
					} else {
						defaultFeedback();
					}
					break;
				case FINALISE:
					if (taskIndex > 0
							&& taskIndex <= commandLogic.getNumberOfTasks()) {
						finaliseTaskFeedback(executedCommand, taskIndex);
					} else if (taskIndex != -1) {
						displayInvalidIndexAsFeedback();
						return;
					} else {
						defaultFeedback();
					}
					break;
				case EDIT:
					// TODO timeslot index has to be checked lower down,
					// inside
					// edit and finalise

					if (taskIndex > 0
							&& taskIndex <= commandLogic.getNumberOfTasks()) {
						editTaskFeedback(executedCommand, taskIndex);
					} else if (taskIndex != -1) {
						defaultFeedback();
						displayInvalidIndexAsFeedback();
						return;
					} else {
						defaultFeedback();
					}
					break;
				case ADD:
					addTaskFeedback(executedCommand);
					break;
				case SEARCH:
					searchTaskFeedback(executedCommand);
					break;
				case INVALID:
					// Fall through
				default:
					defaultFeedback();
					break;
				}

			}

			private void displayInvalidIndexAsFeedback() {
				displayFeedback.setText("Task index is not valid!");
				displayFeedback.setForeground(red);
			}

			private void defaultFeedback() {
				displayLogic.clearHighlightedTasks();
				updateTaskDisplay();
				if (dummyTaskComposite != null) {
					dummyTaskComposite.dispose();
					dummyCompositeIsCreated = false;
				}
			}

			private void searchTaskFeedback(Command executedCommand) {
				// TODO: This solution is too cheapskate, will think of a better
				// solution
				if (!executedCommand.getSearchTerms().isEmpty()
						|| !executedCommand.getTags().isEmpty()) {
					Feedback feedbackObj = commandLogic
							.executeCommand(userInput);
					String feedback = feedbackObj.toString();
					setFeedbackColour(feedbackObj);
					displayFeedback.setText(feedback);
					displayLogic.processFeedback(feedbackObj, helpDialog);
					updateTaskDisplay();
				}
			}

			private void addTaskFeedback(Command executedCommand) {
				Task dummyTask = new Task(executedCommand);
				if (dummyTask.isEmpty()) {
					defaultFeedback();
				} else {
					if (dummyCompositeIsCreated) {
						boolean willOverflow = displayLogic
								.getTaskDisplayHeight()
								- dummyTaskComposite.getSize().y
								+ displayLogic.determineTaskHeight(dummyTask) > 450;
						if (willOverflow) {
							displayLogic.deleteTaskComposites();
							int newLastPageIndex = displayLogic
									.getNumberOfPages() + 1;
							displayPageNumber.setText("Page "
									+ newLastPageIndex + " of "
									+ newLastPageIndex);
							displayPageNumber.setAlignment(SWT.CENTER);
						}
						modifyDummyTaskComposite(executedCommand);
						dummyTaskComposite.pack();
						displayLogic.getTaskDisplay().pack();
					} else {
						dummyCompositeIsCreated = true;
						displayLogic.goToLastPage();
						updateTaskDisplay();

						boolean willOverflow = displayLogic
								.getTaskDisplayHeight()
								+ displayLogic.determineTaskHeight(dummyTask) > 450;

						if (willOverflow) {
							displayLogic.deleteTaskComposites();
							int newLastPageIndex = displayLogic
									.getNumberOfPages() + 1;
							displayPageNumber.setText("Page "
									+ newLastPageIndex + " of "
									+ newLastPageIndex);
							displayPageNumber.setAlignment(SWT.CENTER);
						} else {
							displayLogic.goToLastPage();
							updateTaskDisplay();
						}

						dummyTaskComposite = new TaskComposite(displayLogic
								.getTaskDisplay(), dummyTask, displayLogic
								.getTotalNumberOfComposites() + 1);
						dummyTaskComposite.setHighlighted(true);
						modifyDummyTaskComposite(executedCommand);
						displayLogic.getTaskDisplay().pack();
					}

				}
			}

			private void modifyDummyTaskComposite(Command executedCommand) {
				TaskType finalType = executedCommand.getTaskType();
				if (!executedCommand.getDescription().isEmpty()) {
					dummyTaskComposite.setTaskName(executedCommand
							.getDescription());
				}
				StringBuilder descriptionBuilder = new StringBuilder();
				if (finalType.equals(TaskType.DEADLINE)) {
					descriptionBuilder.append("by "
							+ Task.format(executedCommand.getDeadline()));
				} else if (finalType.equals(TaskType.TIMED)) {
					Interval taskInterval = executedCommand.getIntervals().get(
							0);
					descriptionBuilder.append("from "
							+ Task.intervalFormat(
									taskInterval.getStartDateTime(),
									taskInterval.getEndDateTime()));
				} else if (finalType.equals(TaskType.TENTATIVE)) {
					descriptionBuilder.append("on ");
					ArrayList<Interval> possibleIntervals = executedCommand
							.getIntervals();
					int index = 1;
					for (Interval slot : possibleIntervals) {
						descriptionBuilder.append("(");
						descriptionBuilder.append(index);
						descriptionBuilder.append(") ");
						descriptionBuilder.append(Task.intervalFormat(
								slot.getStartDateTime(), slot.getEndDateTime()));
						if (index != possibleIntervals.size()) {
							descriptionBuilder.append("\nor ");
						}
						index++;
					}
				}
				ArrayList<String> tags = executedCommand.getTags();
				if (tags.size() > 0) {
					if (finalType.equals(TaskType.DEADLINE)
							| finalType.equals(TaskType.TIMED)
							| finalType.equals(TaskType.TENTATIVE)) {
						descriptionBuilder.append("\n");
					}
					for (String tag : tags) {
						descriptionBuilder.append(tag + " ");
					}
				}

				dummyTaskComposite.setDescription(descriptionBuilder.toString());

				dummyTaskComposite.pack();
			}

			private void editTaskFeedback(Command executedCommand, int taskIndex) {

				highlightTaskFeedback(taskIndex);

				TaskComposite currentComposite = displayLogic
						.getCompositeGlobal(taskIndex);

				if (executedCommand.getTimeslotIndex() != -1) {
					if (executedCommand.getTimeslotIndex() > 0) {
						int timeSlot = executedCommand.getTimeslotIndex();
						ArrayList<Interval> possibleIntervals = executedCommand
								.getIntervals();
						if (!possibleIntervals.isEmpty()) {
							Interval interval = possibleIntervals.get(0);
							if (timeSlot == 1) {
								String description = "on (1) "
										+ Task.intervalFormat(
												interval.getStartDateTime(),
												interval.getEndDateTime());
								;
								currentComposite.setTentativeTaskAtLine(
										description, timeSlot);
							} else {
								String description = "or ("
										+ timeSlot
										+ ") "
										+ Task.intervalFormat(
												interval.getStartDateTime(),
												interval.getEndDateTime());
								currentComposite.setTentativeTaskAtLine(
										description, timeSlot);

							}
							currentComposite.highlightLine(timeSlot);
						}
					}
				} else {
					TaskType finalType = executedCommand.getTaskType();

					if (!executedCommand.getDescription().isEmpty()) {
						currentComposite.setTaskName(executedCommand
								.getDescription());
					}

					StringBuilder descriptionBuilder = new StringBuilder();
					if (finalType.equals(TaskType.DEADLINE)) {
						descriptionBuilder.append("by "
								+ Task.format(executedCommand.getDeadline()));
					} else if (finalType.equals(TaskType.TIMED)) {
						Interval taskInterval = executedCommand.getIntervals()
								.get(0);
						descriptionBuilder.append("from ");
						descriptionBuilder.append(Task.intervalFormat(
								taskInterval.getStartDateTime(),
								taskInterval.getEndDateTime()));
					} else if (finalType.equals(TaskType.TENTATIVE)) {
						descriptionBuilder.append("on ");
						ArrayList<Interval> possibleIntervals = executedCommand
								.getIntervals();
						int index = 1;
						for (Interval slot : possibleIntervals) {
							descriptionBuilder.append("(");
							descriptionBuilder.append(index);
							descriptionBuilder.append(") ");
							descriptionBuilder.append(Task.intervalFormat(
									slot.getStartDateTime(),
									slot.getEndDateTime()));
							if (index != possibleIntervals.size()) {
								descriptionBuilder.append("\nor ");
							}
							index++;
						}
					} else {
						descriptionBuilder.append(currentComposite
								.getTimeString());
					}

					String currentTags = currentComposite.getTags();
					ArrayList<String> newTags = executedCommand.getTags();
					StringBuilder tagsBuilder = new StringBuilder();
					tagsBuilder.append(currentTags);

					for (String tag : newTags) {
						tagsBuilder.append(tag);
						tagsBuilder.append(" ");
					}

					if (tagsBuilder.length() > 0) {
						if (!descriptionBuilder.toString().isEmpty()) {
							descriptionBuilder.append("\n");
						}
						descriptionBuilder.append(tagsBuilder.toString());
					}
					if (!descriptionBuilder.toString().isEmpty()) {
						currentComposite.setDescription(descriptionBuilder
								.toString());
					}

					currentComposite.pack();

				}

			}

			private void finaliseTaskFeedback(Command executedCommand,
					int taskIndex) {
				highlightTaskFeedback(taskIndex);
				if (executedCommand.getTimeslotIndex() > 0) {
					displayLogic.getCompositeGlobal(taskIndex).highlightLine(
							executedCommand.getTimeslotIndex());
				}
			}

			private void highlightTaskFeedback(int taskIndex) {
				displayLogic.clearHighlightedTasks();
				displayLogic.addHighlightedTask(taskIndex);
				displayLogic.setPageNumber(displayLogic
						.getPageOfTask(taskIndex));
				updateTaskDisplay();
			}
		});
	}

	public boolean isKeyboardInput(int keyCode) {
		return (keyCode < 127 && keyCode > 31) || keyCode == SWT.BS;
	}

	public void toggleMinimizeState() {
		if (shell.getVisible()) {
			shell.setVisible(false);
			commandLogic.forceFileWrite();
		} else {
			shell.setVisible(true);
			input.setFocus();
		}
	}

	public void enableNativeHook() {
		class UiUpdater implements Runnable {
			public void run() {
				toggleMinimizeState();
			}
		}

		class NativeHook implements NativeKeyListener {
			public void nativeKeyPressed(NativeKeyEvent e) {
				if (e.getModifiers() == (NativeInputEvent.SHIFT_MASK)
						&& e.getKeyCode() == NativeKeyEvent.VK_F1) {
					Display.getDefault().asyncExec(new UiUpdater());
				}
			}

			public void nativeKeyReleased(NativeKeyEvent e) { /* do nothing */
			}

			public void nativeKeyTyped(NativeKeyEvent e) { /* do nothing */
			}
		}

		try {
			GlobalScreen.registerNativeHook();
			GlobalScreen.getInstance().addNativeKeyListener(new NativeHook());
		} catch (Exception e) {
			System.err.println("Unable to initialise global hotkey!"
					+ "Please check your system accessibility settings!"
					+ "Basket will continue without hotkey.");
		}
	}

	public void executeUserInput(String userInput) {

		class StartDisplayAction implements Action {
			DisplayMode mode;
			int pageNumber;

			public StartDisplayAction(DisplayMode mode, int pageNumber) {
				this.mode = mode;
				this.pageNumber = pageNumber;
			}

			public void undo() {
				displayLogic.setDisplayMode(mode);
				displayLogic.setPageNumber(pageNumber);
			}

			public void redo() {
				// do nothing
			}
		}

		class EndDisplayAction implements Action {
			DisplayMode mode;
			int pageNumber;

			public EndDisplayAction(DisplayMode mode, int pageNumber) {
				this.mode = mode;
				this.pageNumber = pageNumber;
			}

			public void undo() {
				// do nothing
			}

			public void redo() {
				displayLogic.setDisplayMode(mode);
				displayLogic.setPageNumber(pageNumber);
			}
		}

		if (!userInput.isEmpty()) {
			ActionStack actionStack = ActionStack.getInstance();
			StartDisplayAction originalDisplayStateAction = new StartDisplayAction(
					displayLogic.getDisplayMode(), displayLogic.getPageNumber());
			actionStack.add(originalDisplayStateAction);
			Feedback feedbackObj = commandLogic.executeCommand(userInput);

			if (feedbackObj.getCommand() == CommandType.EXIT) {
				shell.dispose();
				trayIcon.dispose();
				tray.dispose();
				System.exit(0);
			} else {
				String feedback = feedbackObj.toString();
				setFeedbackColour(feedbackObj);
				displayFeedback.setText(feedback);
				if (feedbackObj.isErrorMessage()) {
					getUpHistory();
				} else {
					input.setText("");
				}

				displayLogic.processFeedback(feedbackObj, helpDialog);
				EndDisplayAction currentDisplayStateAction = new EndDisplayAction(
						displayLogic.getDisplayMode(),
						displayLogic.getPageNumber());
				actionStack.add(currentDisplayStateAction);
				if (isStateChangingOperation(feedbackObj.getCommand())) {
					actionStack.finaliseActions();
					commandLogic.notifyStorage();
				} else {
					actionStack.flushCurrentActionSet();
				}

				updateTaskDisplay();

				if (testMode) {
					logger.log(Level.INFO, generateLoggingString());
				}
			}
		}
	}

	private boolean isStateChangingOperation(CommandType command) {
		return (command == CommandType.ADD || command == CommandType.DELETE
				|| command == CommandType.CLEAR || command == CommandType.DONE
				|| command == CommandType.EDIT
				|| command == CommandType.FINALISE || command == CommandType.SORT);
	}

	/**
	 * Test methods
	 */

	private void runTest(Display display) {
		try {
			Scanner scanner = new Scanner(new File("testCommands.txt"));
			ArrayList<String> testCommands = new ArrayList<>();
			while (scanner.hasNextLine()) {
				String currentLine = scanner.nextLine();
				testCommands.add(currentLine);
			}

			for (String string : testCommands) {
				executeUserInput(string);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String generateLoggingString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(displayTitle.getText() + "\n");
		stringBuilder.append(displayTodayTaskCount.getText() + "\n");
		stringBuilder.append(displayRemainingTaskCount.getText() + "\n");
		Control[] controls = displayLogic.getTaskDisplay().getChildren();
		for (Control control : controls) {
			Composite taskComposite = (Composite) control;
			Control[] taskControls = taskComposite.getChildren();
			StyledText taskIndex = (StyledText) taskControls[0];
			Composite taskDescriptionComposite = (Composite) taskControls[2];
			StyledText taskTitle = (StyledText) taskDescriptionComposite
					.getChildren()[0];
			StyledText taskDescription = (StyledText) taskDescriptionComposite
					.getChildren()[1];
			stringBuilder.append(taskIndex.getText() + " "
					+ taskTitle.getText() + "\n" + taskDescription.getText()
					+ "\n");
		}
		stringBuilder.append(displayPageNumber.getText() + "\n");
		stringBuilder.append(displayFeedback.getText() + "\n");
		return stringBuilder.toString();
	}
}
