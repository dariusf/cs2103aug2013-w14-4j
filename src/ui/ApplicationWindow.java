package ui;

import java.io.File;
import java.util.ArrayList;
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
import org.eclipse.swt.layout.*;
import org.eclipse.wb.swt.SWTResourceManager;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.joda.time.DateTime;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Quad;

import common.Constants;

public class ApplicationWindow {

	public static boolean testMode = false;
	public static final Logger logger = Logger
			.getLogger(ApplicationWindow.class.getName());
	static Shell shell; // accessed by task composite
	public Text input;
	public StyledText displayFeedback;
	public static Logic logic;
//	public Composite displayTask;
	public StyledText displayPageNumber;
	public Composite closeButton;
	public ArrayList<Integer> numberOfTasksOnEachPage;
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

	public static ApplicationWindow self; // singleton?
	public boolean moving = false;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell(SWT.NO_TRIM | SWT.DRAG);
		shell.setImage(SWTResourceManager.getImage(ApplicationWindow.class,
				"/image/basketIcon.jpg"));
		ImageData backgroundData = new ImageData(getClass()
				.getResourceAsStream("/image/background.png"));
		int whitePixel = backgroundData.palette
				.getPixel(new RGB(255, 255, 255));
		backgroundData.transparentPixel = whitePixel;
		Image transparentBackgroundImage = new Image(Display.getCurrent(),
				backgroundData);
		shell.setBackgroundImage(transparentBackgroundImage);
		shell.setBackgroundMode(SWT.INHERIT_FORCE);
		shell.setSize(482, 681);
		shell.setText(Constants.APP_NAME);
		defineFont();

		displayPageNumber = new StyledText(shell, SWT.READ_ONLY | SWT.WRAP
				| SWT.SINGLE);
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
		displayLogic.initialiseDisplayTasks();

		displayTitle = new StyledText(shell, SWT.READ_ONLY | SWT.WRAP
				| SWT.SINGLE);
		displayTitle.setEnabled(false);
		displayTitle.setBounds(36, 23, 311, 50);
		displayTitle.setForeground(new Color(shell.getDisplay(), 0x99, 0, 0));
		displayTitle.setLineAlignment(0, 1, SWT.LEFT);
		displayTitle.setFont(windowTitleFont);

		defineTaskCompositeHeight();

		displayFeedback = new StyledText(shell, SWT.READ_ONLY | SWT.WRAP
				| SWT.MULTI);
		displayFeedback.setEnabled(false);
		displayFeedback.setForeground(SWTResourceManager.getColor(0x99, 0, 0));
		displayFeedback.setBounds(35, 558, 412, 40);

		input = new Text(shell, SWT.BORDER);
		input.setFocus();
		input.setBounds(20, 608, 442, 50);
		input.setBackground(SWTResourceManager.getColor(255, 255, 255));

		// Tween.registerAccessor(Text.class, new InputAccessor());

		closeButton = new Composite(shell, SWT.NONE);
		closeButton.setBounds(433, 0, 49, 27);
		enableWindowButton();

		enableNativeHook();

		setWelcomePage();
		displayFeedback.setText(displayWelcomeMessage());

		helpDialog = new HelpDialog(shell);

		enterDriverLoop();

		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				logic.executeCommand("exit");
			}
		});

		enableDrag();
	}

	// TODO this needs to not recreate the whole list on every keypress
	public void displayTasksOnWindow() {
		for (Control child : displayLogic.getDisplayTask().getChildren()) {
			child.dispose();
		}
		
		displayLogic.initialiseDisplayTasks();

		numberOfTasksOnEachPage = displayLogic.getNumberOfTasksForEachPage();

		displayLogic.displayTasks();

		displayPageNumber.setText("Page " + displayLogic.getPageNumber()
				+ " of " + numberOfTasksOnEachPage.size());
		displayPageNumber.setLineAlignment(0, 1, SWT.CENTER);
		displayPageNumber.setFont(pageNumberFont);

		displayRemainingTaskCount.setText("Remaining: "
				+ displayLogic.getNumberOfRemainingTasks());
		displayTodayTaskCount.setText("Today: "
				+ displayLogic.getNumberOfTasksToday());

		displayTitle.setText(displayLogic.getDisplayWindowTitle());
	}

	public String displayWelcomeMessage() {
		String welcomeMessage = Constants.WELCOME_MSG;
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
					logger.log(Level.INFO, generateLoggingString());
				} else if (arg0.keyCode == SWT.PAGE_UP) {
					displayLogic.setPageNumber(Math.max(
							displayLogic.getPageNumber() - 1, 0));
					displayTasksOnWindow();
					logger.log(Level.INFO, generateLoggingString());
				} else if (arg0.keyCode == SWT.PAGE_DOWN) {
					displayLogic.setPageNumber(Math.min(
							displayLogic.getPageNumber() + 1,
							numberOfTasksOnEachPage.size()));
					displayTasksOnWindow();
					logger.log(Level.INFO, generateLoggingString());
				}
			}

			private void processFeedback(ActiveFeedback activeFeedback) {

				if (activeFeedback == null) {
					displayLogic.clearHighlightedTasks();
					return;
				}

				Command executedCommand = activeFeedback.getCommand();
				int taskIndex = executedCommand.getTaskIndex();

				switch (executedCommand.getCommandType()) {
				case DONE:
				case DELETE:
					displayLogic.clearHighlightedTasks();
					displayLogic.addHighlightedTask(taskIndex);
					displayLogic.setPageNumber(displayLogic
							.getPageOfTask(taskIndex));
					displayTasksOnWindow(); // TODO this needs to not recreate
											// the whole list on every keypress
					break;
				case EDIT:
					displayLogic.clearHighlightedTasks();
					displayLogic.addHighlightedTask(taskIndex);
					displayLogic.setPageNumber(displayLogic
							.getPageOfTask(taskIndex));
					displayTasksOnWindow();
					if (executedCommand.getTimeslotIndex() != -1) {
						// TODO
					} else {
						String finalType = executedCommand.getTaskType();
						if (!executedCommand.getDescription().isEmpty()) {
							displayLogic.getCompositeGlobal(taskIndex)
									.setTaskName(
											executedCommand.getDescription());
						}

						StringBuilder descriptionBuilder = new StringBuilder();
						if (finalType.equals(Constants.TASK_TYPE_DEADLINE)) {
							descriptionBuilder.append("by "
									+ Constants.fullDateTimeFormat
											.print(executedCommand
													.getDeadline()));
						} else if (finalType.equals(Constants.TASK_TYPE_TIMED)) {
							Interval taskInterval = executedCommand
									.getIntervals().get(0);
							descriptionBuilder.append("from "
									+ Constants.fullDateTimeFormat
											.print(taskInterval
													.getStartDateTime())
									+ " to "
									+ Constants.fullDateTimeFormat
											.print(taskInterval
													.getEndDateTime()));
						} else if (finalType
								.equals(Constants.TASK_TYPE_FLOATING)) {
							descriptionBuilder.append("on ");
							ArrayList<Interval> possibleIntervals = executedCommand
									.getIntervals();
							int index = 1;
							for (Interval slot : possibleIntervals) {
								descriptionBuilder.append("(");
								descriptionBuilder.append(index);
								descriptionBuilder.append(") ");
								descriptionBuilder.append(Constants.fullDateTimeFormat
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
						ArrayList<String> tags = executedCommand.getTags();
						if (tags.size() > 0) {
							if (finalType.equals(Constants.TASK_TYPE_DEADLINE)
									| finalType
											.equals(Constants.TASK_TYPE_TIMED)
									| finalType
											.equals(Constants.TASK_TYPE_FLOATING)) {
								descriptionBuilder.append("\n");
							}
							for (String tag : tags) {
								descriptionBuilder.append("#" + tag + " ");
							}
						}
						if (!descriptionBuilder.toString().isEmpty()) {
							displayLogic.getCompositeGlobal(taskIndex)
									.setDescription(
											descriptionBuilder.toString());
						}
						displayLogic.getCompositeGlobal(taskIndex).pack();
					}
					break;
				case ADD:
					if (dummyTaskComposite != null) {
						dummyTaskComposite.dispose();
					}

					if (!executedCommand.isEmptyAddCommand()) {
						displayLogic.clearHighlightedTasks();

						displayLogic.setPageNumber(Integer.MAX_VALUE);
						displayTasksOnWindow();
						Task dummyTask = new Task(executedCommand);

						// Check if the tasks overflow
						if (displayLogic.getDisplayTask().getSize().y
								+ displayLogic.determineTaskHeight(dummyTask) > 450) {
							for (Control child : displayLogic.getDisplayTask().getChildren()) {
								child.dispose();
							}
							int newLastPage = displayLogic.getNumberOfPages() + 1;
							displayPageNumber.setText("Page " + newLastPage
									+ " of " + newLastPage);
							displayPageNumber.setAlignment(SWT.CENTER);
						} else {
							displayLogic.setPageNumber(Integer.MAX_VALUE);
							displayTasksOnWindow();
						}

						dummyTaskComposite = new TaskComposite(displayLogic.getDisplayTask(),
								dummyTask, displayLogic
										.getTotalNumberOfComposites() + 1);

						String finalType = executedCommand.getTaskType();
						if (!executedCommand.getDescription().isEmpty()) {
							dummyTaskComposite.setTaskName(executedCommand
									.getDescription());
						}
						StringBuilder descriptionBuilder = new StringBuilder();
						if (finalType.equals(Constants.TASK_TYPE_DEADLINE)) {
							descriptionBuilder.append("by "
									+ Constants.fullDateTimeFormat
											.print(executedCommand
													.getDeadline()));
						} else if (finalType.equals(Constants.TASK_TYPE_TIMED)) {
							Interval taskInterval = executedCommand
									.getIntervals().get(0);
							descriptionBuilder.append("from "
									+ Constants.fullDateTimeFormat
											.print(taskInterval
													.getStartDateTime())
									+ " to "
									+ Constants.fullDateTimeFormat
											.print(taskInterval
													.getEndDateTime()));
						} else if (finalType
								.equals(Constants.TASK_TYPE_FLOATING)) {
							descriptionBuilder.append("on ");
							ArrayList<Interval> possibleIntervals = executedCommand
									.getIntervals();
							int index = 1;
							for (Interval slot : possibleIntervals) {
								descriptionBuilder.append("(");
								descriptionBuilder.append(index);
								descriptionBuilder.append(") ");
								descriptionBuilder.append(Constants.fullDateTimeFormat
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
						ArrayList<String> tags = executedCommand.getTags();
						if (tags.size() > 0) {
							if (finalType.equals(Constants.TASK_TYPE_DEADLINE)
									| finalType
											.equals(Constants.TASK_TYPE_TIMED)
									| finalType
											.equals(Constants.TASK_TYPE_FLOATING)) {
								descriptionBuilder.append("\n");
							}
							for (String tag : tags) {
								descriptionBuilder.append("#" + tag + " ");
							}
						}
						if (!descriptionBuilder.toString().isEmpty()) {
							dummyTaskComposite
									.setDescription(descriptionBuilder
											.toString());
						}
						dummyTaskComposite.pack();
						displayLogic.getDisplayTask().pack();
					} else {
						displayLogic.clearHighlightedTasks();
						displayTasksOnWindow();
						if (dummyTaskComposite != null) {
							dummyTaskComposite.dispose();
						}
					}

					System.out.println(displayLogic.getDisplayTask().getSize().y);

					break;
				case SEARCH:
					// TODO: This solution is too cheapskate, will think of a better solution
					if(!executedCommand.getSearchString().isEmpty() || !executedCommand.getTags().isEmpty()){
						Feedback feedbackObj = logic.executeCommand(userInput);
						String feedback = feedbackObj.toString();
						setFeedbackColour(feedbackObj);
						displayFeedback.setText(feedback);
						displayLogic.processFeedback(feedbackObj, helpDialog);
						displayTasksOnWindow();
					}
					break;
				default:
					System.out.println("here");
					displayLogic.clearHighlightedTasks();
					displayTasksOnWindow();
					if (dummyTaskComposite != null) {
						dummyTaskComposite.dispose();
					}
					System.out
							.println("Instant feedback not yet implemented for command "
									+ executedCommand.getCommandType());
					break;
				}
			}

			public void performTween() {
				final int currentPosition = 296;
				final int offset = 15;
				final int duration = 20;
				if (!moving) {
					moving = true;
					Tween.to(input, 0, duration)
							.target(currentPosition - offset).ease(Quad.INOUT)
							.start(InputAccessor.manager)
							.setCallback(new TweenCallback() {
								@Override
								public void onEvent(int type,
										BaseTween<?> source) {
									Tween.to(input, 0, duration * 2)
											.target(currentPosition + offset)
											.start(InputAccessor.manager)
											.setCallback(new TweenCallback() {
												@Override
												public void onEvent(int type,
														BaseTween<?> source) {
													Tween.to(input, 0, duration)
															.target(currentPosition)
															.start(InputAccessor.manager);
													moving = false;
												}
											});
								}
							});
				}
			}
		});
	}

	public boolean isKeyboardInput(int keyCode) {
		return (keyCode < 127 && keyCode > 31) || keyCode == SWT.BS;
	}

	/**
	 * @param feedbackObj
	 */
	protected void setFeedbackColour(Feedback feedbackObj) {
		Color green = new Color(shell.getDisplay(), 0, 0x66, 0);
		Color red = new Color(shell.getDisplay(), 0x99, 0, 0);

		if (feedbackObj.isErrorMessage()) {
			displayFeedback.setForeground(red);
		} else if (!feedbackObj.isErrorMessage()) {
			displayFeedback.setForeground(green);
		}
	}

	public void defineFont() {
		boolean isWindows = System.getProperty("os.name").toLowerCase()
				.indexOf("win") >= 0;
		if (isWindows) {
			windowTitleFont = new Font(shell.getDisplay(), "Calibri", 33,
					SWT.NORMAL);
			pageNumberFont = new Font(shell.getDisplay(), "Calibri", 13,
					SWT.NORMAL);
			indexFont = new Font(shell.getDisplay(), "Calibri", 45, SWT.NORMAL);
			titleFont = new Font(shell.getDisplay(), "Calibri", 18, SWT.NORMAL);
			descriptionFont = new Font(shell.getDisplay(), "Calibri", 9,
					SWT.NORMAL);
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
				case SWT.MouseDown:
					Point pt1 = shell.toDisplay(0, 0);
					Point pt2 = Display.getCurrent().getCursorLocation();
					offset[0] = new Point(pt2.x - pt1.x, pt2.y - pt1.y);

					break;
				case SWT.MouseMove:
					if (offset[0] != null) {
						Point pt = offset[0];
						Point newMouseLoc = Display.getCurrent()
								.getCursorLocation();
						shell.setLocation(newMouseLoc.x - pt.x, newMouseLoc.y
								- pt.y);
					}
					break;
				case SWT.MouseUp:
					offset[0] = null;
					break;
				}
			}
		};

		shell.addListener(SWT.MouseDown, listener);
		shell.addListener(SWT.MouseUp, listener);
		shell.addListener(SWT.MouseMove, listener);
	}

	public void enableWindowButton() {
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				if (event.type == SWT.MouseUp) {
					Point pt1 = shell.toDisplay(0, 0);
					Point pt2 = Display.getCurrent().getCursorLocation();
					Point offset = new Point(pt2.x - pt1.x, pt2.y - pt1.y);

					if (offset.x > 455 && offset.y < 27) {

						logic.executeCommand("exit");
						shell.dispose();
					} else if (offset.x > 433 && offset.y < 27) {

						shell.setMinimized(true);
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
				if (shell.getMinimized()) {
					shell.setMinimized(false);
				} else {
					shell.setMinimized(true);
				}
			}

		}

		class NativeHook implements NativeKeyListener {

			@Override
			public void nativeKeyPressed(NativeKeyEvent e) {
				if (e.getModifiers() == (NativeInputEvent.ALT_MASK + NativeInputEvent.CTRL_MASK)
						&& e.getKeyCode() == NativeKeyEvent.VK_Z) {
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
		Feedback feedbackObj = logic.executeCommand(userInput);

		String feedback = feedbackObj.toString();
		setFeedbackColour(feedbackObj);

		displayFeedback.setText(feedback);
		input.setText("");

		displayLogic.processFeedback(feedbackObj, helpDialog);

		displayTasksOnWindow();

		if (testMode) {
			logger.log(Level.INFO, generateLoggingString());
		}

	}

	public void defineTaskCompositeHeight() {
		Command command1 = new Command(CommandType.ADD);
		command1.setDescription("haha");
		Task task1 = new Task(command1);
		task1.setType(Constants.TASK_TYPE_UNTIMED);
		TaskComposite taskComposite1 = new TaskComposite(displayLogic.getDisplayTask(), task1, 1);
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
		DateTime startDate3 = new DateTime(2013, 10, 30, 17, 0, 0);
		DateTime endDate3 = new DateTime(2013, 10, 30, 18, 0, 0);
		Interval interval3 = new Interval();
		interval3.setStartDateTime(startDate3);
		interval3.setEndDateTime(endDate3);
		ArrayList<Interval> intervalList = new ArrayList<Interval>();
		intervalList.add(interval1);
		intervalList.add(interval2);
		intervalList.add(interval3);

		task1.setType(Constants.TASK_TYPE_FLOATING);
		task1.setPossibleTime(intervalList);
		TaskComposite taskComposite2 = new TaskComposite(displayLogic.getDisplayTask(), task1, 1);
		int taskComposite3LinesHeight = taskComposite2.getSize().y;
		displayLogic
				.setTaskCompositeHeightForThreeLines(taskComposite3LinesHeight);

		ArrayList<String> tags = new ArrayList<String>();
		tags.add("TGIF");
		task1.setTags(tags);
		TaskComposite taskComposite3 = new TaskComposite(displayLogic.getDisplayTask(), task1, 1);
		int taskCompositeIncrement = taskComposite3.getSize().y
				- taskComposite3LinesHeight;
		displayLogic.setTaskCompositeIncrement(taskCompositeIncrement);

	}

	public String generateLoggingString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(displayTitle.getText() + "\n");
		stringBuilder.append(displayTodayTaskCount.getText() + "\n");
		stringBuilder.append(displayRemainingTaskCount.getText() + "\n");
		Control[] controls = displayLogic.getDisplayTask().getChildren();
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
