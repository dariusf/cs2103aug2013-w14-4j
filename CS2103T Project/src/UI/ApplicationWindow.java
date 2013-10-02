package UI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import Logic.CommandType;
import Logic.Constants;
import Logic.Feedback;
import Logic.Logic;

public class ApplicationWindow {

	protected Shell shell;
	private Text input;
	private Text displayFeedback;
	private static Logic logic;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ApplicationWindow window = new ApplicationWindow();
			logic = new Logic();
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
		shell.setLayout(new GridLayout(1, false));
		
		displayFeedback = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI);
		displayFeedback.setEnabled(false);
		GridData gd_feedback = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_feedback.heightHint = 215;
		displayFeedback.setLayoutData(gd_feedback);
		
		input = new Text(shell, SWT.BORDER);
		GridData gd_input = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd_input.heightHint = 16;
		input.setLayoutData(gd_input);

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
			
			@Override
			public void keyReleased(KeyEvent arg0) {}

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.character != SWT.CR) {
					char characterEntered = arg0.character;
					userInput = userInput + characterEntered;
				}
				
				if (arg0.character == SWT.CR) {
					Feedback feedbackObj = logic.executeCommand(userInput);
					System.out.println(userInput);
					String feedback = feedbackObj.toString();
					output = output + "\n" + feedback;
					displayFeedback.setText(output);
					userInput = "";
					input.setText("");
				}
			}
		});
	}
}
