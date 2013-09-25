package UI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

import Logic.Constants;
import Logic.Feedback;
import Logic.Logic;

public class ApplicationWindow {

	protected Shell shell;
	private Text input;
	private Text displayFeedback;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
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

		input = new Text(shell, SWT.BORDER);
		input.setBounds(10, 231, 476, 21);
		
		displayFeedback = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI);
		displayFeedback.setEnabled(false);
		displayFeedback.setBounds(10, 10, 476, 215);
		
		
		enterDriverLoop();
	}

	private String displayWelcomeMessage() {
		String welcomeMessage = Constants.WELCOME_MSG;
		return welcomeMessage;
	}

	private void enterDriverLoop() {
		input.addKeyListener(new KeyListener() {
			String userInput = "";
			String feedbackString = displayWelcomeMessage();
			@Override
			public void keyReleased(KeyEvent arg0) {}

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				if (arg0.character != SWT.CR) {
					char characterEntered = arg0.character;
					userInput = userInput + characterEntered;
				}
				
				if (arg0.character == SWT.CR) {
					//Feedback feedbackObj = Logic.executeCommand(userInput);
					System.out.println(userInput);
					//String output = convertFeedbackToString(feedbackObj);
					String output = convertFeedbackToString();
					feedbackString = feedbackString + "\n" + output;
					displayFeedback.setText(feedbackString);
					userInput = "";
					input.setText("");
					
				}
			}

			
		});
	}
	
	private String convertFeedbackToString(Feedback feedbackObj) {
		// TODO Auto-generated method stub
		String output = "Hello";
		return output;
	}
	
	private String convertFeedbackToString() {
		// TODO Auto-generated method stub
		String output = "Hello";
		return output;
	}
}
