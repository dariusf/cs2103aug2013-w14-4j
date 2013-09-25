import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

public class ApplicationWindow {

	protected Shell shell;
	private Text input;
	private Text displayFeedback;
	private static String APPNAME = "Basket";

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
		shell.setText(APPNAME);

		input = new Text(shell, SWT.BORDER);
		input.setBounds(10, 231, 476, 21);
		
		input.addKeyListener(new KeyListener() {
			String userInput = "";
			@Override
			public void keyReleased(KeyEvent arg0) {}

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0);
				if (arg0.character != SWT.CR) {
					char characterEntered = arg0.character;
					userInput = userInput + characterEntered;
				}
				
				if (arg0.character == SWT.CR) {
					displayFeedback.setText(userInput);
					input.setText("");
					
				}
			}
		});

		displayFeedback = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI);
		displayFeedback.setEnabled(false);
		displayFeedback.setBounds(10, 10, 476, 215);

	}
}
