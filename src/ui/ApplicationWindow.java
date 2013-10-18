package ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import logic.Feedback;
import logic.Logic;
import logic.Task;

import common.*;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.wb.swt.SWTResourceManager;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Quad;

import common.CommandType;
import common.Constants;

public class ApplicationWindow {

	protected static Shell shell;
	private Text input;
	private Text displayFeedback;
	private static Logic logic;
	private Composite displayTask;
	private StyledText displayPageNumber;
	private Composite closeButton;
	private ArrayList<Integer> numberOfTasksOnEachPage;

	private Font indexFont;
	private Font titleFont;
	private Font descriptionFont;
	private Font pageNumberFont;

	private int pageNumber = 1;
	public static ApplicationWindow self;
	public boolean moving = false;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
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
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
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
		displayPageNumber.setSize(105, 25);
		displayPageNumber.setLocation(335, 567);

		displayTask = new Composite(shell, SWT.NONE);
		displayTasksOnWindow();

		displayFeedback = new Text(shell, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		displayFeedback.setForeground(SWTResourceManager.getColor(0x99, 0, 0));
		displayFeedback.setBounds(35, 558, 412, 40);

		input = new Text(shell, SWT.BORDER);
		input.setFocus();
		input.setBounds(20, 608, 442, 50);
		input.setBackground(SWTResourceManager.getColor(255, 255, 255));

		// Tween.registerAccessor(Text.class, new InputAccessor());

		displayFeedback.setText(displayWelcomeMessage());

		closeButton = new Composite(shell, SWT.NONE);
		closeButton.setBounds(433, 0, 49, 27);
		enableWindowButton();

		enterDriverLoop();

		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				logic.executeCommand("exit");
			}
		});

		enableDrag();
	}

	private void displayTasksOnWindow() {

		// displayTask.dispose();
		// displayTask = new Composite(shell, SWT.NONE);

		for (Control child : displayTask.getChildren()) {
			child.dispose();
		}

		RowLayout rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.pack = true;
		displayTask.setLayout(rowLayout);
		displayTask.setBounds(32, 86, 405, 450);

		determineNumberOfTasksForEachPage();
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
		if (numberOfTasks == 0)
			return;
		assert startingIndex < numberOfTasks;

		Composite[] taskComposites = new Composite[numberOfTasks];

		for (int i = 0; i < numberOfTasksOnEachPage.get(pageNumber - 1); i++) {
			taskComposites[i] = createTaskItemComposite(
					taskList.get(startingIndex + i), startingIndex + i + 1);
		}

		displayPageNumber.setText("Page " + pageNumber + " of "
				+ numberOfTasksOnEachPage.size());
		displayPageNumber.setLineAlignment(0, 1, SWT.CENTER);
		displayPageNumber.setFont(pageNumberFont);
		displayTask.pack();

	}

	private int getPage(int index) {
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

	private void determineNumberOfTasksForEachPage() {
		ArrayList<Task> taskList = logic.getTasksToDisplay();
		int numberOfTasks = taskList.size();
		Composite[] taskComposites = new Composite[numberOfTasks];
		int index = 0;
		for (Task task : taskList) {
			taskComposites[index] = createTaskItemComposite(task, index + 1);
			index++;
		}
		int[] heights = new int[numberOfTasks];
		for (int i = 0; i < numberOfTasks; i++) {
			heights[i] = taskComposites[i].getSize().y;
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
		for (Control child : displayTask.getChildren()) {
			child.dispose();
		}
	}

	private Composite createTaskItemComposite(Task task, int index) {
		Composite taskItemComposite = new Composite(displayTask, SWT.NONE);
		// 340 is the fixed width and 69 is the fixed height. use SWT.default if
		// you do not want to fix the lengths.
		taskItemComposite.setLayoutData(new RowData(405, SWT.DEFAULT));
		RowLayout innerRowLayout = new RowLayout();
		taskItemComposite.setLayout(innerRowLayout);

		RowData taskIndexLayoutData = new RowData(60, 73);
		RowData paddingLayoutData = new RowData(8, SWT.DEFAULT);
		RowData taskDescriptionLayoutData = new RowData(320, SWT.DEFAULT);

		StyledText taskIndex = new StyledText(taskItemComposite, SWT.WRAP);
		taskIndex.setText(String.valueOf(index));
		taskIndex.setFont(indexFont);
		taskIndex.setForeground(new Color(shell.getDisplay(), 0x99, 0, 0));
		taskIndex.setLineAlignment(0, 1, SWT.RIGHT);
		taskIndex.setLayoutData(taskIndexLayoutData);

		Composite paddingComposite = new Composite(taskItemComposite, SWT.NONE);
		paddingComposite.setLayoutData(paddingLayoutData);

		Composite taskDetailsComposite = new Composite(taskItemComposite,
				SWT.NONE);
		taskDetailsComposite.setLayoutData(taskDescriptionLayoutData);
		taskDetailsComposite.setLayout(innerRowLayout);

		StyledText taskName = new StyledText(taskDetailsComposite,
				SWT.READ_ONLY);
		taskName.setText(task.getName());
		taskName.setFont(titleFont);
		if (task.isDone()) {
			StyleRange style1 = new StyleRange();
			style1.start = 0;
			style1.length = task.getName().length();
			style1.strikeout = true;
			taskName.setStyleRange(style1);
		} else if (task.isOverdue()) {
			taskName.setForeground(new Color(shell.getDisplay(), 0x99, 0, 0));
		}
		taskName.setLayoutData(taskDescriptionLayoutData);

		StyledText taskDescription = new StyledText(taskDetailsComposite,
				SWT.READ_ONLY);
		taskDescription.setText(task.getInfoString());
		taskDescription.setFont(descriptionFont);

		taskDetailsComposite.pack();

		taskItemComposite.pack();

		return taskItemComposite;
	}

	private String displayWelcomeMessage() {
		String welcomeMessage = Constants.WELCOME_MSG;
		return welcomeMessage;
	}

	private void enterDriverLoop() {
		input.addKeyListener(new KeyListener() {
			Color green = new Color(shell.getDisplay(), 0, 0x66, 0);
			Color red = new Color(shell.getDisplay(), 0x99, 0, 0);
			String userInput = "";
			String tasks = "";
			UserInputHistory inputHistory = new UserInputHistory();

			@Override
			public void keyReleased(KeyEvent arg0) {
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
					Feedback feedbackObj = logic.executeCommand(userInput);

					String feedback = feedbackObj.toString();
					if (feedbackObj.isErrorMessage()) {
						displayFeedback.setForeground(red);
					} else if (!feedbackObj.isErrorMessage()) {
						displayFeedback.setForeground(green);
					}

					switch (feedbackObj.getCommand()) {
					case ADD:
						pageNumber = Integer.MAX_VALUE;
						break;
					case EDIT:
					case DELETE:
					case DONE:
					case FINALISE:
						if (!feedbackObj.isErrorMessage()) {
							pageNumber = getPage(feedbackObj.getTaskIndex());
						}
						break;
					case DISPLAY:
					case SORT:
					case CLEAR:
					case SEARCH:
						pageNumber = 1;
						break;
					case HELP:
					case EXIT:
					case UNDO:
					case REDO:
					case INVALID:
					default:
					}

					displayFeedback.setText(feedback);
					input.setText("");
					displayTasksOnWindow();
				} else if (arg0.keyCode == SWT.PAGE_UP) {
					// when non-fixed-height composites are added, on every
					// change
					// go through the whole list to get the numbers for each
					// page,
					// then page based on those.
					// for now, since it's fixed-width...
					pageNumber = Math.max(pageNumber - 1, 0);
					displayTasksOnWindow();
				} else if (arg0.keyCode == SWT.PAGE_DOWN) {
					pageNumber = Math.min(pageNumber + 1,
							numberOfTasksOnEachPage.size());
					displayTasksOnWindow();
				}
			}

			private void performTween() {
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

	private void defineFont() {
		pageNumberFont = new Font(shell.getDisplay(), "Calibri", 18, SWT.NORMAL);
		indexFont = new Font(shell.getDisplay(), "Calibri", 60, SWT.NORMAL);
		titleFont = new Font(shell.getDisplay(), "Calibri", 24, SWT.NORMAL);
		descriptionFont = new Font(shell.getDisplay(), "Calibri", 12,
				SWT.NORMAL);
	}

	private void enableDrag() {
		final Point[] offset = new Point[1];
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.MouseDown:
					Point pt1 = shell.toDisplay(0, 0);
					Point pt2 = Display.getCurrent().getCursorLocation();
					offset[0] = new Point(pt2.x - pt1.x, pt2.y - pt1.y);
					// System.out.println(offset[0]);
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

	private void enableWindowButton() {
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
}
