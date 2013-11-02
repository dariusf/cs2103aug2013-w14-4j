package ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import logic.ActiveFeedback;
import logic.Command;
import logic.Feedback;
import logic.Interval;
import logic.Logic;
import logic.Task;

import common.*;

import org.eclipse.swt.*;
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

import common.Constants;
import common.undo.Action;
import common.undo.ActionStack;

public class ApplicationWindow {

	public static boolean testMode = false;
	public static final Logger logger = Logger
			.getLogger(ApplicationWindow.class.getName());
	static Shell shell; // accessed by task composite
	public Text input;
	public StyledText displayFeedback;
	public static Logic logic;
	public StyledText displayPageNumber;
	public Composite closeButton;
	public StyledText displayTitle;
	public StyledText displayRemainingTaskCount;
	public StyledText displayTodayTaskCount;
	public TaskComposite dummyTaskComposite;

	public static HelpDialog helpDialog;
	public static DisplayLogic displayLogic;

	public Font windowTitleFont;
	Font indexFont; // accessed by task composite
	Font titleFont; // accessed by task composite
	Font descriptionFont; // accessed by task composite
	public Font pageNumberFont;
	public Font inputFont;
	
	public Color green;
	public Color red;
	public Color purple;
	
	public static ApplicationWindow self; // singleton?
	public boolean moving = false;
	
	private static Tray tray;
	private static TrayItem trayIcon;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			logger.setLevel(Level.OFF);
			logic = new Logic();
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
	
	
	private double sqr (double i) {
		return i * i;
	}
	
	/**
	 * Constructs a point array representing a rounded rectangle. 
	 * The implementation can be imagined as a large oval, with top, bottom and sides chopped off
	 * 
	 * @param windowWidth
	 * @param windowHeight
	 * @param ovalX
	 * @param ovalY
	 * @return
	 */
	private int[] roundedRectangle (int windowWidth, int windowHeight, int ovalX, int ovalY) {
		assert(windowWidth > 0);
		assert(windowHeight > 0);
		assert(ovalX > 0);
		assert(ovalY > 0);
		
		class Point {
			double x;
			double y;
			
			public Point(double x, double y) {
				this.x = x;
				this.y = y;
			}
			
			void offset(double xOffset, double yOffset) { //shifts this point by the specified offset
				x += xOffset;
				y += yOffset;
			}
		}
		
		class PointOperations {
			int[] flatten (Point[] points) {
				int[] result = new int[points.length * 2];
				for(int i = 0; i < points.length; i++) {
					result[i * 2] = (int) points[i].x;
					result[i * 2 + 1] = (int) points[i].y;
				}
				return result;
			}
			
			// mirrors all points along the X axis and joins the two together to form a line
			Point[] mirrorX (Point[] points) {
				Point[] result = new Point[points.length * 2];
				for (int i = 0; i < points.length; i++) {
					Point currentPoint = points[i];
					Point reflectedPoint = new Point(-currentPoint.x, currentPoint.y);
					result[i] = currentPoint;
					result[result.length - 1 - i] = reflectedPoint;
				}
				return result;
			}
			
			Point[] mirrorY (Point[] points) {
				Point[] result = new Point[points.length * 2];
				for (int i = 0; i < points.length; i++) {
					Point currentPoint = points[i];
					Point reflectedPoint = new Point(currentPoint.x, -currentPoint.y);
					result[i] = currentPoint;
					result[result.length - 1 - i] = reflectedPoint;
				}
				return result;
			}
		}
		
		PointOperations pointManipulator = new PointOperations();
		Point[] quarterArc = new Point[ovalX + 1];
		
		// eqn: (x^2)/(ovalX^2) + (y^2)/(ovalY^2) = 1
		for (int i = 0; i <= ovalX; i++) {
			double xCoord = i;
			double yCoord = Math.sqrt(1 - (sqr(xCoord)/sqr(ovalX))) * (sqr(ovalY));
			quarterArc[quarterArc.length - 1 - i] = new Point(xCoord, yCoord);
		}
		
		Point[] oval = pointManipulator.mirrorY(pointManipulator.mirrorX(quarterArc));
		for (Point point : oval) {
			point.offset(calculateHalfOfNumber(windowWidth), calculateHalfOfNumber(windowHeight)); // shift oval to center on window
			
			if (point.x < 0) {
				point.x = 0;
			} else if (point.x > windowWidth) {
				point.x = windowWidth;
			}
			
			if (point.y < 0) {
				point.y = 0;
			} else if (point.y > windowHeight) {
				point.y = windowHeight;
			}
		}
		return pointManipulator.flatten(oval);
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell(SWT.NO_TRIM | SWT.DRAG | SWT.ON_TOP);
		
		shell.setImage(SWTResourceManager.getImage(ApplicationWindow.class,
				"/image/basketIcon.jpg"));
		
		ImageData backgroundData = new ImageData(getClass()
				.getResourceAsStream("/image/background.png"));
		Image transparentBackgroundImage = new Image(Display.getCurrent(),
				backgroundData);
		shell.setBackgroundImage(transparentBackgroundImage);
		shell.setBackgroundMode(SWT.INHERIT_FORCE);
		
//		Region shellRegion = new Region();
//		shellRegion.add(roundedRectangle(482, 681, 400, 600));
//		shell.setRegion(shellRegion);
//		
//		Rectangle size = shellRegion.getBounds();
//		
//		shell.setSize(size.width, size.height);
		
		shell.setSize(482, 681);
		shell.setText(Constants.APP_NAME);
		// TODO Please change this value to 1 when you compile for use on your computers.
		defineFont(1);
		
		red = new Color(shell.getDisplay(), 0x99, 0, 0);
		green = new Color(shell.getDisplay(), 0, 0x66, 0);
		purple = SWTResourceManager.getColor(102, 0, 255);

		displayPageNumber = new StyledText(shell, SWT.READ_ONLY | SWT.SINGLE);
		displayPageNumber.setEnabled(false);
		displayPageNumber.setSize(105, 25);
		displayPageNumber.setLocation(335, 567);

		displayRemainingTaskCount = new StyledText(shell, SWT.READ_ONLY
				| SWT.WRAP | SWT.SINGLE);
		displayRemainingTaskCount.setEnabled(false);
		displayRemainingTaskCount.setBounds(370, 54, 77, 14);
		displayRemainingTaskCount.setFont(descriptionFont);
		displayRemainingTaskCount.setLineAlignment(0, 1, SWT.RIGHT);

		displayTodayTaskCount = new StyledText(shell, SWT.READ_ONLY | SWT.WRAP
				| SWT.SINGLE);
		displayTodayTaskCount.setEnabled(false);
		displayTodayTaskCount.setBounds(370, 41, 77, 14);
		displayTodayTaskCount.setFont(descriptionFont);
		displayTodayTaskCount.setLineAlignment(0, 1, SWT.RIGHT);

		displayLogic = new DisplayLogic(logic, DisplayMode.TODO,
				Constants.DEFAULT_PAGE_NUMBER);
		displayLogic.initialiseTaskDisplay();

		displayTitle = new StyledText(shell, SWT.READ_ONLY | SWT.WRAP
				| SWT.SINGLE);
		displayTitle.setEnabled(false);
		displayTitle.setBounds(36, 23, 311, 50);
		displayTitle.setForeground(red);
		displayTitle.setLineAlignment(0, 1, SWT.LEFT);
		displayTitle.setFont(windowTitleFont);

		defineTaskCompositeHeight();

		displayFeedback = new StyledText(shell, SWT.READ_ONLY | SWT.WRAP
				| SWT.MULTI);
		displayFeedback.setEnabled(false);
		displayFeedback.setBounds(35, 558, 412, 40);

		input = new Text(shell, SWT.BORDER);
		input.setFocus();
		input.setFont(inputFont);
		input.setBounds(20, 608, 442, 50);
		input.setBackground(SWTResourceManager.getColor(255, 255, 255));
		
		tray = shell.getDisplay().getSystemTray();
		trayIcon = new TrayItem(tray, SWT.NONE);
		trayIcon.setImage(SWTResourceManager.getImage(ApplicationWindow.class,
				"/image/basketIcon.jpg"));
		enableTraySelection();

		// Tween.registerAccessor(Text.class, new InputAccessor());

		closeButton = new Composite(shell, SWT.NONE);
		closeButton.setBounds(433, 0, 49, 27);
		enableWindowButton();

		enableNativeHook();

		setWelcomePage();
		displayFeedback.setText(displayWelcomeMessage());
		displayFeedback.setForeground(purple);

		helpDialog = new HelpDialog(shell);

		enterDriverLoop();

		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				logic.executeCommand(Constants.COMMAND_EXIT);
			}
		});
		adjustPageNumberAlignment();
		enableDrag();
	}

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
		String welcomeMessage = Constants.WELCOME_MSG;
		if(Math.random() < 0.05){
			int index = new Random().nextInt(Constants.RANDOM_JOKES.length);
			welcomeMessage = Constants.RANDOM_JOKES[index];
		} else {
			welcomeMessage = Constants.MSG_AVAILABLE_COMMANDS;
		}
		return welcomeMessage;
	}

	public void setWelcomePage() {
		executeUserInput(Constants.WELCOME_PAGE_DISPLAY);
	}

	public void enterDriverLoop() {
		input.addKeyListener(new KeyListener() {
			String userInput = "";
			UserInputHistory inputHistory = new UserInputHistory();

			@Override
			public void keyReleased(KeyEvent arg0) {
				if (isKeyboardInput(arg0.keyCode)) {
					userInput = input.getText();
					ActiveFeedback activeFeedback = logic
							.activeFeedback(userInput);
					processFeedback(activeFeedback);
				}
			}

			@Override
			public void keyPressed(KeyEvent arg0) {

				// performTween();
				if (arg0.keyCode == SWT.ARROW_DOWN) {
					if (!inputHistory.isEndOfHistory()) {
						int currentIndex = inputHistory.getIndex();
						String commandField = inputHistory
								.getInput(currentIndex + 1);
						input.setText(commandField);
						input.setSelection(input.getText().length());
						inputHistory.setIndex(currentIndex + 1);
					}
				} else if (arg0.keyCode == SWT.ARROW_UP) {
					int currentIndex = inputHistory.getIndex();
					if (currentIndex != -1) {
						String commandField = inputHistory
								.getInput(currentIndex);
						input.setText(commandField);
						inputHistory.setIndex(currentIndex - 1);
					}
				} else if (arg0.character == SWT.CR) {
					userInput = input.getText();

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
				} else if (arg0.keyCode == SWT.F1 && arg0.stateMask != SWT.SHIFT) {
					executeUserInput("help");
				} else if (arg0.keyCode == SWT.F2) {
					executeUserInput("display");
				} else if (arg0.keyCode == SWT.F3) {
					executeUserInput("display today");
				} else if (arg0.keyCode == SWT.F4) {
					executeUserInput("display tomorrow");
				} else if (arg0.keyCode == SWT.F5) {
					executeUserInput("display all");
				} else if (arg0.keyCode == SWT.F6) {
					executeUserInput("display done");
				} else if (arg0.keyCode == SWT.F7) {
					executeUserInput("display overdue");
				} else if (arg0.keyCode == SWT.F8) {
					executeUserInput("display untimed");
				} else if (arg0.keyCode == SWT.F9) {
					executeUserInput("display deadline");
				} else if (arg0.keyCode == SWT.F10) {
					executeUserInput("display timed");
				} else if (arg0.keyCode == SWT.F11) {
					executeUserInput("display floating");
				}
			}

			private void processFeedback(ActiveFeedback activeFeedback) {

				if (activeFeedback == null) {
					displayLogic.clearHighlightedTasks();
					return;
				}

				Command executedCommand = activeFeedback.getCommand();
				int taskIndex = executedCommand.getTaskIndex();
				CommandType command = executedCommand.getCommandType();
				displayFeedback.setForeground(purple);

				ContextualHelp contextualHelp = new ContextualHelp(command);
				displayFeedback.setText(contextualHelp.toString());

				switch (command) {
				case DONE :
					break;
				case DELETE :
					if (taskIndex > 0 && taskIndex <= logic.getNumberOfTasks()) {
						highlightTaskFeedback(taskIndex);
					} else {
						displayFeedback.setText("Task index is not valid!");
						displayFeedback.setForeground(red);
					}
					break;
				case FINALISE :
					if (taskIndex > 0 && taskIndex <= logic.getNumberOfTasks()) {
						finaliseTaskFeedback(executedCommand, taskIndex);
					} else {
						displayFeedback.setText("Task index is not valid!");
						displayFeedback.setForeground(red);
						return;
					}
					break;
				case EDIT :
					// TODO timeslot index has to be checked lower down, inside
					// edit and finalise
				
					if (taskIndex > 0 && taskIndex <= logic.getNumberOfTasks()) {
						editTaskFeedback(executedCommand, taskIndex);
					} else {
						displayFeedback.setText("Task index is not valid!");
						displayFeedback.setForeground(red);
						return;
					}
					break;
				case ADD :
					addTaskFeedback(executedCommand);
					break;
				case SEARCH :
					searchTaskFeedback(executedCommand);
					break;
				case INVALID :
					break;
				default:
					defaultFeedback();
					break;
				}
			}
			
			private void defaultFeedback() {
				displayLogic.clearHighlightedTasks();
				updateTaskDisplay();
				if (dummyTaskComposite != null) {
					dummyTaskComposite.dispose();
				}
			}

			private void searchTaskFeedback(Command executedCommand) {
				// TODO: This solution is too cheapskate, will think of a better
				// solution
				if (!executedCommand.getSearchString().isEmpty()
						|| !executedCommand.getTags().isEmpty()) {
					Feedback feedbackObj = logic.executeCommand(userInput);
					String feedback = feedbackObj.toString();
					setFeedbackColour(feedbackObj);
					displayFeedback.setText(feedback);
					displayLogic.processFeedback(feedbackObj, helpDialog);
					updateTaskDisplay();
				}
			}

			private void addTaskFeedback(Command executedCommand) {
				if (dummyTaskComposite != null) {
					dummyTaskComposite.dispose();
				}

				if (!executedCommand.isEmptyAddCommand()) {
					displayLogic.clearHighlightedTasks();
					
					displayLogic.goToLastPage();
					updateTaskDisplay();

					// Check if the tasks overflow if a new task is added
					Task dummyTask = new Task(executedCommand);
					boolean willOverflow = displayLogic.getTaskDisplayHeight()
							+ displayLogic.determineTaskHeight(dummyTask) > 450;

					if (willOverflow) {
						displayLogic.deleteTaskComposites();
						int newLastPageIndex = displayLogic.getNumberOfPages() + 1;
						displayPageNumber.setText("Page " + newLastPageIndex
								+ " of " + newLastPageIndex);
						displayPageNumber.setAlignment(SWT.CENTER);
					} else {
						displayLogic.goToLastPage();
						updateTaskDisplay();
					}

					dummyTaskComposite = new TaskComposite(displayLogic
							.getTaskDisplay(), dummyTask, displayLogic
							.getTotalNumberOfComposites() + 1);

					dummyTaskComposite.setHighlighted(true);
					
					String finalType = executedCommand.getTaskType();
					if (!executedCommand.getDescription().isEmpty()) {
						dummyTaskComposite.setTaskName(executedCommand
								.getDescription());
					}
					StringBuilder descriptionBuilder = new StringBuilder();
					if (finalType.equals(Constants.TASK_TYPE_DEADLINE)) {
						descriptionBuilder.append("by " + Task.format(executedCommand.getDeadline()));
					} else if (finalType.equals(Constants.TASK_TYPE_TIMED)) {
						Interval taskInterval = executedCommand.getIntervals()
								.get(0);
						descriptionBuilder.append("from "
								+ Task.intervalFormat(taskInterval.getStartDateTime(), taskInterval.getEndDateTime()));
					} else if (finalType.equals(Constants.TASK_TYPE_FLOATING)) {
						descriptionBuilder.append("on ");
						ArrayList<Interval> possibleIntervals = executedCommand
								.getIntervals();
						int index = 1;
						for (Interval slot : possibleIntervals) {
							descriptionBuilder.append("(");
							descriptionBuilder.append(index);
							descriptionBuilder.append(") ");
							descriptionBuilder
									.append(Task.intervalFormat(slot.getStartDateTime(), slot.getEndDateTime()));
							if (index != possibleIntervals.size()) {
								descriptionBuilder.append("\nor ");
							}
							index++;
						}
					}
					ArrayList<String> tags = executedCommand.getTags();
					if (tags.size() > 0) {
						if (finalType.equals(Constants.TASK_TYPE_DEADLINE)
								| finalType.equals(Constants.TASK_TYPE_TIMED)
								| finalType
										.equals(Constants.TASK_TYPE_FLOATING)) {
							descriptionBuilder.append("\n");
						}
						for (String tag : tags) {
							descriptionBuilder.append("#" + tag + " ");
						}
					}
					if (!descriptionBuilder.toString().isEmpty()) {
						dummyTaskComposite.setDescription(descriptionBuilder
								.toString());
					}
					dummyTaskComposite.pack();
					displayLogic.getTaskDisplay().pack();
				} else {
					defaultFeedback();
				}
			}

			private void editTaskFeedback(Command executedCommand, int taskIndex) {
				highlightTaskFeedback(taskIndex);
				TaskComposite currentComposite = displayLogic.getCompositeGlobal(taskIndex);
				if (executedCommand.getTimeslotIndex() != -1) {
					if (executedCommand.getTimeslotIndex() > 0) {
						int timeSlot = executedCommand.getTimeslotIndex();
						ArrayList<Interval> possibleIntervals = executedCommand
								.getIntervals();
						if (!possibleIntervals.isEmpty()) {
							Interval interval = possibleIntervals.get(0);
							if (timeSlot == 1) {
								String description = "on (1) "
										+ Constants.fullDateTimeFormat
												.print(interval
														.getStartDateTime())
										+ " to "
										+ Constants.fullDateTimeFormat
												.print(interval
														.getEndDateTime());
								currentComposite
										.setDescriptionAtLine(description,
												timeSlot);
							} else {
								String description = "or ("+timeSlot+") "
										+ Constants.fullDateTimeFormat
												.print(interval
														.getStartDateTime())
										+ " to "
										+ Constants.fullDateTimeFormat
												.print(interval
														.getEndDateTime());
								currentComposite
										.setDescriptionAtLine(description,
												timeSlot);
							}
						}
					}
				} else {
					String finalType = executedCommand.getTaskType();

					if (!executedCommand.getDescription().isEmpty()) {
						currentComposite.setTaskName(
								executedCommand.getDescription());
					}

					StringBuilder descriptionBuilder = new StringBuilder();
					if (finalType.equals(Constants.TASK_TYPE_DEADLINE)) {
						descriptionBuilder.append("by "
								+ Constants.fullDateTimeFormat
										.print(executedCommand.getDeadline()));
					} else if (finalType.equals(Constants.TASK_TYPE_TIMED)) {
						Interval taskInterval = executedCommand.getIntervals()
								.get(0);
						descriptionBuilder.append("from "
								+ Constants.fullDateTimeFormat
										.print(taskInterval.getStartDateTime())
								+ " to "
								+ Constants.fullDateTimeFormat
										.print(taskInterval.getEndDateTime()));
					} else if (finalType.equals(Constants.TASK_TYPE_FLOATING)) {
						descriptionBuilder.append("on ");
						ArrayList<Interval> possibleIntervals = executedCommand
								.getIntervals();
						int index = 1;
						for (Interval slot : possibleIntervals) {
							descriptionBuilder.append("(");
							descriptionBuilder.append(index);
							descriptionBuilder.append(") ");
							descriptionBuilder
									.append(Constants.fullDateTimeFormat
											.print(slot.getStartDateTime()));
							descriptionBuilder.append(" to ");
							descriptionBuilder
									.append(Constants.fullDateTimeFormat
											.print(slot.getEndDateTime()));
							if (index != possibleIntervals.size()) {
								descriptionBuilder.append("\nor ");
							}
							index++;
						}
					}
					String currentTags = currentComposite.getTags();
					ArrayList<String> newTags = executedCommand.getTags();
					String combinedTags = currentTags;
					for(String tag : newTags){
						combinedTags = combinedTags + "#" + tag + " ";
					}

					if (!combinedTags.isEmpty()) {
						if (finalType.equals(Constants.TASK_TYPE_DEADLINE)
								| finalType.equals(Constants.TASK_TYPE_TIMED)
								| finalType
										.equals(Constants.TASK_TYPE_FLOATING)) {
							descriptionBuilder.append("\n");
						}
						descriptionBuilder.append(combinedTags);
					}
					if (!descriptionBuilder.toString().isEmpty()) {
						currentComposite
								.setDescription(descriptionBuilder.toString());
					}
					currentComposite.pack();
				}
			}

			private void finaliseTaskFeedback(Command executedCommand, int taskIndex){
				highlightTaskFeedback(taskIndex);
				if(executedCommand.getTimeslotIndex() > 0){
					displayLogic.getCompositeGlobal(taskIndex)
					.highlightLine(executedCommand.getTimeslotIndex());
				}
			}

			private void highlightTaskFeedback(int taskIndex) {
				displayLogic.clearHighlightedTasks();
				displayLogic.addHighlightedTask(taskIndex);
				displayLogic.setPageNumber(displayLogic
						.getPageOfTask(taskIndex));
				updateTaskDisplay();
			}

			// public void performTween() {
			// final int currentPosition = 296;
			// final int offset = 15;
			// final int duration = 20;
			// if (!moving) {
			// moving = true;
			// Tween.to(input, 0, duration)
			// .target(currentPosition - offset).ease(Quad.INOUT)
			// .start(InputAccessor.manager)
			// .setCallback(new TweenCallback() {
			// @Override
			// public void onEvent(int type,
			// BaseTween<?> source) {
			// Tween.to(input, 0, duration * 2)
			// .target(currentPosition + offset)
			// .start(InputAccessor.manager)
			// .setCallback(new TweenCallback() {
			// @Override
			// public void onEvent(int type,
			// BaseTween<?> source) {
			// Tween.to(input, 0, duration)
			// .target(currentPosition)
			// .start(InputAccessor.manager);
			// moving = false;
			// }
			// });
			// }
			// });
			// }
			// }
		});
	}

	public boolean isKeyboardInput(int keyCode) {
		return (keyCode < 127 && keyCode > 31) || keyCode == SWT.BS;
	}

	/**
	 * @param feedbackObj
	 */
	protected void setFeedbackColour(Feedback feedbackObj) {
		if (feedbackObj.isErrorMessage()) {
			displayFeedback.setForeground(red);
		} else if (!feedbackObj.isErrorMessage()) {
			displayFeedback.setForeground(green);
		}
	}

	public void defineFont(double scaling) {
		boolean isWindows = System.getProperty("os.name").toLowerCase()
				.indexOf("win") >= 0;
		if (isWindows) {
			windowTitleFont = new Font(shell.getDisplay(), "Calibri", (int) (33 * scaling),
					SWT.NORMAL);
			pageNumberFont = new Font(shell.getDisplay(), "Calibri", (int) (13 * scaling),
					SWT.NORMAL);
			indexFont = new Font(shell.getDisplay(), "Calibri", (int) (45 * scaling), SWT.NORMAL);
			titleFont = new Font(shell.getDisplay(), "Calibri", (int) (18 * scaling), SWT.NORMAL);
			descriptionFont = new Font(shell.getDisplay(), "Calibri", (int) (9 * scaling),
					SWT.NORMAL);
			inputFont = new Font(shell.getDisplay(), "Calibri", 17, SWT.NORMAL);
		} else {
			windowTitleFont = new Font(shell.getDisplay(), "Calibri", 44,
					SWT.NORMAL);
			pageNumberFont = new Font(shell.getDisplay(), "Calibri", 18,
					SWT.NORMAL);
			indexFont = new Font(shell.getDisplay(), "Calibri", 60, SWT.NORMAL);
			titleFont = new Font(shell.getDisplay(), "Calibri", 24, SWT.NORMAL);
			descriptionFont = new Font(shell.getDisplay(), "Calibri", 12,
					SWT.NORMAL);
		}
	}

	public void enableDrag() {
		final Point[] offset = new Point[1];
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.MouseDown :
					Point pt1 = shell.toDisplay(0, 0);
					Point pt2 = Display.getCurrent().getCursorLocation();
					offset[0] = new Point(pt2.x - pt1.x, pt2.y - pt1.y);

					break;
				case SWT.MouseMove :
					if (offset[0] != null) {
						Point pt = offset[0];
						Point newMouseLoc = Display.getCurrent()
								.getCursorLocation();
						shell.setLocation(newMouseLoc.x - pt.x, newMouseLoc.y
								- pt.y);
					}
					break;
				case SWT.MouseUp :
					offset[0] = null;
					break;
				}
			}
		};

		shell.addListener(SWT.MouseDown, listener);
		shell.addListener(SWT.MouseUp, listener);
		shell.addListener(SWT.MouseMove, listener);
	}
	
	public void enableTraySelection() {
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
	
	public void toggleMinimizeState() {
		if (shell.getVisible()) {
			shell.setVisible(false);
		} else {
			shell.setVisible(true);
			input.setFocus();
		}
	}

	public void enableWindowButton() {
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				if (event.type == SWT.MouseUp) {
					Point pt1 = shell.toDisplay(0, 0);
					Point pt2 = Display.getCurrent().getCursorLocation();
					Point offset = new Point(pt2.x - pt1.x, pt2.y - pt1.y);

					if (offset.x > 455 && offset.y < 27) {
						executeUserInput(Constants.COMMAND_EXIT);
					} else if (offset.x > 433 && offset.y < 27) {
						toggleMinimizeState();
					}
				}

			}

		};
		closeButton.addListener(SWT.MouseUp, listener);
	}

	public void enableNativeHook() {
		class UiUpdater implements Runnable {

			@Override
			public void run() {
				toggleMinimizeState();
			}

		}

		class NativeHook implements NativeKeyListener {

			@Override
			public void nativeKeyPressed(NativeKeyEvent e) {
				if (e.getModifiers() == (NativeInputEvent.SHIFT_MASK)
						&& e.getKeyCode() == NativeKeyEvent.VK_F1) {
					Display.getDefault().asyncExec(new UiUpdater());
				}
			}

			@Override
			public void nativeKeyReleased(NativeKeyEvent e) {
				// do nothing
			}

			@Override
			public void nativeKeyTyped(NativeKeyEvent e) {
				// do nothing
			}
		}
		
		try {
			GlobalScreen.registerNativeHook();
			GlobalScreen.getInstance().addNativeKeyListener(new NativeHook());
		} catch (Exception e) {
			// I can't actually fix this bug, because it is an OS level problem
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

			@Override
			public void undo() {
				displayLogic.setDisplayMode(mode);
				displayLogic.setPageNumber(pageNumber);
			}

			@Override
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

			@Override
			public void undo() {
				// do nothing
			}

			@Override
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

			Feedback feedbackObj = logic.executeCommand(userInput);

			if (feedbackObj.getCommand() == CommandType.EXIT) {
				shell.dispose();
				trayIcon.dispose();
				tray.dispose();
			} else {

				String feedback = feedbackObj.toString();
				setFeedbackColour(feedbackObj);

				displayFeedback.setText(feedback);
				input.setText("");

				displayLogic.processFeedback(feedbackObj, helpDialog);

				EndDisplayAction currentDisplayStateAction = new EndDisplayAction(
						displayLogic.getDisplayMode(),
						displayLogic.getPageNumber());
				actionStack.add(currentDisplayStateAction);

				if (isStateChangingOperation(feedbackObj.getCommand())) {
					actionStack.finaliseActions();
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
		return (command == CommandType.ADD ||
				command == CommandType.DELETE ||
				command == CommandType.CLEAR ||
				command == CommandType.DONE ||
				command == CommandType.EDIT ||
				command == CommandType.FINALISE ||
				command == CommandType.SORT);
	}

	public void defineTaskCompositeHeight() {
		Command command1 = new Command(CommandType.ADD);
		command1.setDescription("haha");
		Task task1 = new Task(command1);
		task1.setType(Constants.TASK_TYPE_UNTIMED);
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
		task1.setType(Constants.TASK_TYPE_FLOATING);
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

		task1.setType(Constants.TASK_TYPE_FLOATING);
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
