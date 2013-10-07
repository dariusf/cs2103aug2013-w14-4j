package ui;

import logic.Feedback;
import logic.Logic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;

import common.Constants;

public class ApplicationWindow {

	protected Shell shell;
	private Text input;
	private Text displayFeedback;
	private static Logic logic;
	private Text displayTask;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			logic = new Logic();
			ApplicationWindow window = new ApplicationWindow();
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
		shell = new Shell();
		shell.setImage(SWTResourceManager.getImage(ApplicationWindow.class, "/image/basketIcon.jpg"));
		shell.setForeground(SWTResourceManager.getColor(0, 0, 0));
		shell.setBackground(SWTResourceManager.getColor(0, 0, 0));
		shell.setSize(446, 361);
		shell.setText(Constants.APP_NAME);
		shell.setLayout(new GridLayout(1, false));
		
		displayTask = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		displayTask.setFont(SWTResourceManager.getFont("Garamond", 16, SWT.NORMAL));
		displayTask.setBackground(SWTResourceManager.getColor(255, 255, 204));
		displayTask.setForeground(SWTResourceManager.getColor(153, 102, 51));
		GridData gd_displayTask = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_displayTask.widthHint = 153;
		gd_displayTask.heightHint = 182;
		displayTask.setLayoutData(gd_displayTask);
		displayTask.setText(logic.displayOnWindow());
		
		displayFeedback = new Text(shell, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		displayFeedback.setForeground(SWTResourceManager.getColor(255, 153, 102));
		displayFeedback.setBackground(SWTResourceManager.getColor(0, 0, 0));
		GridData gd_feedback = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd_feedback.widthHint = 302;
		gd_feedback.heightHint = 32;
		displayFeedback.setLayoutData(gd_feedback);
		
		input = new Text(shell, SWT.BORDER);
		GridData gd_input = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd_input.heightHint = 16;
		input.setLayoutData(gd_input);
		input.setFocus();

		displayFeedback.setText(displayWelcomeMessage());
		
		enterDriverLoop();
		/*System.out.println("hello");
		Feedback feedbackObj = logic.executeCommand("exit");*/
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				logic.executeCommand("exit");
			}
		});
	}

	private String displayWelcomeMessage() {
		String welcomeMessage = Constants.WELCOME_MSG;
		return welcomeMessage;
	}

	private void enterDriverLoop() {	
		input.addKeyListener(new KeyListener() {
			Color green = shell.getDisplay().getSystemColor(SWT.COLOR_GREEN);
			Color red = new Color(shell.getDisplay(), 245, 126, 133);
			String userInput = "";
			String tasks = "";
			UserInputHistory inputHistory = new UserInputHistory();
			
			@Override
			public void keyReleased(KeyEvent arg0) {}

			@Override
			public void keyPressed(KeyEvent arg0) {		
				if (arg0.keyCode == SWT.ARROW_DOWN) {
					if (!inputHistory.isEndOfHistory()) {
						int currentIndex = inputHistory.getIndex();
						String commandField = inputHistory
								.getInput(currentIndex + 1);
						input.setText(commandField);
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
					tasks = logic.displayOnWindow();
					displayTask.setText(tasks);
				}
			}
			
		});
	}
}
