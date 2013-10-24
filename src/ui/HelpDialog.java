package ui;

import logic.Feedback;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;

import common.CommandType;
import common.Constants;

public class HelpDialog extends Dialog {

	public HelpDialog(Shell parent) {
		super(parent);
	}

	public void open(Feedback feedbackObj) {
		Shell parent = getParent();
		final Shell dialog = new Shell(parent);
		
		dialog.setSize(367, 420);
		dialog.setText("Help");
		
		final Button closeButton = new Button(dialog, SWT.NONE);
		closeButton.setBounds(121, 357, 118, 25);
		closeButton.setText("Got it!");
		
		StyledText helpText = new StyledText(dialog, SWT.READ_ONLY | SWT.WRAP);
		helpText.setBounds(10, 10, 341, 341);
		helpText.setEnabled(false);

		String helpString = getHelpText(feedbackObj.getHelpCommandType());
		helpText.setText(helpString);
		
		dialog.open();
		
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.widget == closeButton) {
					dialog.close();
				}
			}
		};

		closeButton.addListener(SWT.Selection, listener);
		
		Display display = parent.getDisplay();
		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
	private String getHelpText(CommandType helpCommandType){
		if (helpCommandType == null) {
			return Constants.HELP_GENERAL;
		}
		
		switch (helpCommandType) {
		case ADD :
			return Constants.HELP_ADD;
		case DISPLAY :
			return Constants.HELP_DISPLAY;
		case HELP :
			return Constants.HELP_HELP;
		case SORT :
			return Constants.HELP_SORT;
		case DELETE :
			return Constants.HELP_DELETE;
		case EDIT :
			return Constants.HELP_EDIT;
		case CLEAR :
			return Constants.HELP_CLEAR;
		case UNDO :
			return Constants.HELP_UNDO;
		case REDO :
			return "dummy";
		case DONE :
			return Constants.HELP_DONE;
		case FINALISE :
			return Constants.HELP_FINALISE;
		case SEARCH :
			return Constants.HELP_SEARCH;
		case EXIT :
			return Constants.HELP_EXIT;
		case GOTO :
			return "dummy";
		default:
			return Constants.HELP_GENERAL;
		}
	}
}
