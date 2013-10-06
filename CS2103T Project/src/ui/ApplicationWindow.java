package ui;

import logic.Constants;
import logic.Feedback;
import logic.Logic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;


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
		shell.setSize(512, 300);
		shell.setText(Constants.APP_NAME);
		shell.setLayout(new GridLayout(2, false));
		
		displayFeedback = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
		displayFeedback.setEnabled(false);
		GridData gd_feedback = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_feedback.widthHint = 302;
		gd_feedback.heightHint = 215;
		displayFeedback.setLayoutData(gd_feedback);
		
		displayTask = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
		displayTask.setEnabled(false);
		GridData gd_displayTask = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2);
		gd_displayTask.widthHint = 153;
		gd_displayTask.heightHint = 230;
		displayTask.setLayoutData(gd_displayTask);
		
		input = new Text(shell, SWT.BORDER);
		GridData gd_input = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd_input.heightHint = 16;
		input.setLayoutData(gd_input);

		displayFeedback.setText(displayWelcomeMessage());
		displayTask.setText(logic.displayOnWindow());
		
		enterDriverLoop();
	}

	private String displayWelcomeMessage() {
		String welcomeMessage = Constants.WELCOME_MSG;
		return welcomeMessage;
	}

	private void enterDriverLoop() {	
		input.addKeyListener(new KeyListener() {
			String userInput = "";
			String output = displayWelcomeMessage();
			String tasks = "";
			UserInputHistory inputHistory = new UserInputHistory();
			
			@Override
			public void keyReleased(KeyEvent arg0) {}

			@Override
			public void keyPressed(KeyEvent arg0) {		
				if (arg0.keyCode == SWT.ARROW_DOWN) {
					//System.out.println("keydown pressed");
					if (!inputHistory.isEndOfHistory()) {
						int currentIndex = inputHistory.getIndex();
						String commandField = inputHistory
								.getInput(currentIndex + 1);
						input.setText(commandField);
						inputHistory.setIndex(currentIndex + 1);
					}
				}
				if (arg0.keyCode == SWT.ARROW_UP) {
					//System.out.println("keyup pressed");
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
					output = output + "\n" + feedback;
					displayFeedback.setText(output);
					input.setText("");
					
					tasks = logic.displayOnWindow();
					displayTask.setText(tasks);
				}
			}
			
		});
	}
}
