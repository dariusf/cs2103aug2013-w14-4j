package ui;

import java.util.ArrayList;

import logic.Feedback;
import logic.Logic;
import logic.Task;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.wb.swt.SWTResourceManager;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Quad;

import common.Constants;

public class ApplicationWindow {

	protected Shell shell;
	private Text input;
	private Text displayFeedback;
	private static Logic logic;
	private Composite displayTask;
	private Text displayIndex;

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
				.getResourceAsStream("/image/backgroundWithTask.png"));
		int whitePixel = backgroundData.palette
				.getPixel(new RGB(255, 255, 255));
		backgroundData.transparentPixel = whitePixel;
		Image transparentBackgroundImage = new Image(Display.getCurrent(),
				backgroundData);
		shell.setBackgroundImage(transparentBackgroundImage);
		shell.setBackgroundMode(SWT.INHERIT_FORCE);
		shell.setSize(482, 681);
		shell.setText(Constants.APP_NAME);

		displayIndex = new Text(shell, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI
				| SWT.RIGHT);
		displayIndex.setFont(SWTResourceManager.getFont("Myriad Pro", 48,
				SWT.NORMAL));
		displayIndex.setForeground(SWTResourceManager.getColor(0x99, 0, 0));
		displayIndex.setBounds(35, 86, 60, 450);

		displayTask = new Composite(shell, SWT.NONE);
		displayTasksOnWindow();

		displayFeedback = new Text(shell, SWT.READ_ONLY | SWT.WRAP
				| SWT.V_SCROLL | SWT.MULTI);
		displayFeedback.setForeground(SWTResourceManager
				.getColor(0x99, 0, 0));
		displayFeedback.setBounds(35, 558, 412, 40);

		input = new Text(shell, SWT.BORDER);
		input.setFocus();
		input.setBounds(20, 608, 442, 50);
		input.setBackground(SWTResourceManager.getColor(255,255,255));

		// Tween.registerAccessor(Text.class, new InputAccessor());

		displayFeedback.setText(displayWelcomeMessage());

		enterDriverLoop();

		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				logic.executeCommand("exit");
			}
		});

		enableDrag();
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
//					System.out.println(offset[0]);
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

	private void displayTasksOnWindow() {
		// possibly could change this so a new composite isn't created every time
		// (to remove the flicker)
		displayTask.dispose();
		displayTask = new Composite(shell, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		displayTask.setLayout(gridLayout);
		displayTask.setBounds(116, 86, 324, 450);

		ArrayList<Task> taskList = logic.getTasksToDisplay();
		int numberOfTasks = taskList.size();

		if (numberOfTasks == 0) return;

		Composite first = createTaskItemComposite(taskList.get(0));
		int compositeHeight = first.getSize().y;
		int compositesThatWillFitIntoPanel = Math.min(displayTask.getSize().y / compositeHeight, numberOfTasks);

		Composite[] taskComposites = new Composite[numberOfTasks];
		taskComposites[0] = first;

		for (int i=1; i<compositesThatWillFitIntoPanel; i++) {
			taskComposites[i] = createTaskItemComposite(taskList.get(i));
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
	}

	private Composite createTaskItemComposite(Task task) {
		Composite taskItemComposite = new Composite(displayTask, SWT.NONE);
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		taskItemComposite.setLayout(fillLayout);
		taskItemComposite.setSize(320, 200);
		Label taskName = new Label(taskItemComposite, SWT.READ_ONLY);
		taskName.setText(task.getName());
		taskName.setFont(SWTResourceManager.getFont("Myriad Pro", 24,
				SWT.NORMAL));

		Label taskDescription = new Label(taskItemComposite,
				SWT.READ_ONLY);
		taskDescription.setText(task.getInfoString());
		taskDescription.setFont(SWTResourceManager.getFont("Myriad Pro",
				12, SWT.NORMAL));

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
				}
				if (arg0.keyCode == SWT.ARROW_UP) {
					int currentIndex = inputHistory.getIndex();
					if (currentIndex != -1) {
						String commandField = inputHistory
								.getInput(currentIndex);
						input.setText(commandField);
						inputHistory.setIndex(currentIndex - 1);
					}
				}
				if (arg0.character == SWT.CR) {
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
}
