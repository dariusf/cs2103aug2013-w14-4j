package ui;

import java.io.InputStream;
import java.util.ArrayList;

import javax.management.loading.PrivateClassLoader;

import logic.Feedback;
import logic.Logic;
import logic.Task;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.wb.swt.SWTResourceManager;

import com.joestelmach.natty.generated.DateParser_NumericRules.int_00_to_23_optional_prefix_return;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Quad;

import common.Constants;

public class ApplicationWindow {

	protected static Shell shell;
	private Text input;
	private Text displayFeedback;
	private static Logic logic;
	private Composite displayTask;
	private Text displayIndex;
	private Composite closeButton;

	private Font myriadProIndex;
	private Font myriadProTitle;
	private Font myriadProDescription;

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

		displayIndex = new Text(shell, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI
				| SWT.RIGHT);
		displayIndex.setFont(myriadProIndex);
		displayIndex.setForeground(SWTResourceManager.getColor(0x99, 0, 0));
		displayIndex.setBounds(40, 86, 60, 450);

		displayTask = new Composite(shell, SWT.NONE);
		displayTasksOnWindow(0);

		displayFeedback = new Text(shell, SWT.READ_ONLY | SWT.WRAP
				| SWT.V_SCROLL | SWT.MULTI);
		displayFeedback.setForeground(SWTResourceManager.getColor(0x99, 0, 0));
		displayFeedback.setBounds(35, 558, 412, 40);

		input = new Text(shell, SWT.BORDER);
		input.setFocus();
		input.setBounds(20, 608, 442, 50);
		input.setBackground(SWTResourceManager.getColor(255, 255, 255));

		// Tween.registerAccessor(Text.class, new InputAccessor());

		displayFeedback.setText(displayWelcomeMessage());

		closeButton = new Composite(shell, SWT.NONE);
		closeButton.setBounds(455, 0, 27, 27);
		enableCloseButton();
		
		enterDriverLoop();

		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				logic.executeCommand("exit");
			}
		});

		enableDrag();
	}

	private void defineFont() {
		System.out.println(shell.getDisplay().loadFont(
				"src/font/MyriadPro-Semibold.otf"));
		System.out.println(shell.getDisplay().loadFont(
				"src/font/MyriadPro-Regular.otf"));

		myriadProIndex = new Font(shell.getDisplay(), "Myriad Pro Semibold",
				48, SWT.NORMAL);
		myriadProTitle = new Font(shell.getDisplay(), "Myriad Pro Regular", 24,
				SWT.NORMAL);
		myriadProDescription = new Font(shell.getDisplay(),
				"Myriad Pro Regular", 10, SWT.NORMAL);
	}

	private void enableCloseButton() {
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				if (event.type == SWT.MouseUp) {
					Point pt1 = shell.toDisplay(0, 0);
					Point pt2 = Display.getCurrent().getCursorLocation();
					Point offset = new Point(pt2.x - pt1.x, pt2.y - pt1.y);
					if(offset.x > 455 && offset.y < 27){
						shell.dispose();
					} else if (offset.x > 433 && offset.y < 27) {
						shell.setMinimized(true);
					}
				}

			}

		};
		closeButton.addListener(SWT.MouseUp, listener);
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

	private String displayWelcomeMessage() {
		String welcomeMessage = Constants.WELCOME_MSG;
		return welcomeMessage;
	}

	private int taskDisplayStart, taskDisplayEnd;

	private void displayTasksOnWindow(int startingIndex) {

		// displayTask.dispose();
		// displayTask = new Composite(shell, SWT.NONE);

		for (Control child : displayTask.getChildren()) {
			child.dispose();
		}

		GridLayout gridLayout = new GridLayout(1, false);
		displayTask.setLayout(gridLayout);
		displayTask.setBounds(105, 86, 324, 450);

		ArrayList<Task> taskList = logic.getTasksToDisplay();
		int numberOfTasks = taskList.size();
		if (numberOfTasks == 0)
			return;
		assert startingIndex < numberOfTasks;

		Composite[] taskComposites = new Composite[numberOfTasks];
		int compositesThatWillFitIntoPanel = determineCompositesThatWillFit(startingIndex);

		for (int i = 0; i < compositesThatWillFitIntoPanel; i++) {
			taskComposites[i] = createTaskItemComposite(taskList
					.get(startingIndex + i));
		}

		displayTask.pack();

		StringBuilder taskIndexStringBuilder = new StringBuilder();
		for (int i = 0; i < numberOfTasks; i++) {
			taskIndexStringBuilder.append(i + 1);
			if (i < numberOfTasks - 1) {
				taskIndexStringBuilder.append("\n");
			}
		}
		displayIndex.setText(taskIndexStringBuilder.toString());

		taskDisplayStart = startingIndex;
		taskDisplayEnd = startingIndex + compositesThatWillFitIntoPanel - 1;
	}

	private int determineCompositesThatWillFit(int startingIndex) {
		ArrayList<Task> taskList = logic.getTasksToDisplay();
		int numberOfTasks = taskList.size();
		if (numberOfTasks == 0)
			return 0;
		else {
			Composite temp = createTaskItemComposite(taskList.get(0));
			int compositeHeight = temp.getSize().y;
			temp.dispose();
			return Math.min(displayTask.getSize().y / compositeHeight,
					numberOfTasks - startingIndex);
		}
	}

	private int determineCompositesThatCanFit(int startingIndex) {
		ArrayList<Task> taskList = logic.getTasksToDisplay();
		Composite temp = createTaskItemComposite(taskList.get(0));
		int compositeHeight = temp.getSize().y;
		temp.dispose();
		// todo: magic number
		return 450 / compositeHeight;
	}

	private Composite createTaskItemComposite(Task task) {
		Composite taskItemComposite = new Composite(displayTask, SWT.NONE);
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		taskItemComposite.setLayout(fillLayout);
		taskItemComposite.setSize(320, 200);
		Label taskName = new Label(taskItemComposite, SWT.READ_ONLY);
		taskName.setText(task.getName());
		taskName.setFont(myriadProTitle);

		Label taskDescription = new Label(taskItemComposite, SWT.READ_ONLY);
		taskDescription.setText(task.getInfoString());
		taskDescription.setFont(myriadProDescription);

		taskItemComposite.pack();
		return taskItemComposite;
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
					System.out.println(userInput);
					String feedback = feedbackObj.toString();
					if (feedbackObj.isErrorMessage()) {
						displayFeedback.setForeground(red);
					} else if (!feedbackObj.isErrorMessage()) {
						displayFeedback.setForeground(green);
					}
					displayFeedback.setText(feedback);
					input.setText("");
					displayTasksOnWindow(taskDisplayStart);
				} else if (arg0.keyCode == SWT.PAGE_UP) {
					// when non-fixed-height composites are added, on every
					// change
					// go through the whole list to get the numbers for each
					// page,
					// then page based on those.
					// for now, since it's fixed-width...
					int compositesPerPage = determineCompositesThatCanFit(0);
					taskDisplayStart = Math.max(taskDisplayStart
							- compositesPerPage, 0);
					displayTasksOnWindow(taskDisplayStart);
				} else if (arg0.keyCode == SWT.PAGE_DOWN) {
					ArrayList<Task> tasks = logic.getTasksToDisplay();
					int prospectiveIndex = taskDisplayEnd + 1;
					if (prospectiveIndex <= tasks.size() - 1) {
						taskDisplayStart = prospectiveIndex;
						displayTasksOnWindow(prospectiveIndex);
					}
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
}
