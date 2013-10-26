package ui;

import logic.Feedback;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.wb.swt.SWTResourceManager;

import common.CommandType;
import common.Constants;

public class HelpDialog extends Dialog {

	Display display;
	StyledText helpText;
	boolean isGeneralHelp = false;

	public HelpDialog(Shell parent) {
		super(parent);
	}

	public void open(Feedback feedbackObj) {
		Shell parent = getParent();
		final Shell dialog = new Shell(parent);
		display = parent.getDisplay();

		dialog.setSize(420, 420);
		dialog.setText("Help");

		final Button closeButton = new Button(dialog, SWT.NONE);
		closeButton.setBounds(148, 357, 118, 25);
		closeButton.setText("Got it!");

		helpText = new StyledText(dialog, SWT.READ_ONLY | SWT.WRAP);
		boolean isWindows = System.getProperty("os.name").toLowerCase()
				.indexOf("win") >= 0;
		if (isWindows) {
			helpText.setFont(SWTResourceManager.getFont("Calibri", 12,
					SWT.NORMAL));
		} else {
			helpText.setFont(SWTResourceManager.getFont("Calibri", 16,
					SWT.NORMAL));
		}
		helpText.setBounds(10, 10, 394, 341);
		helpText.setEnabled(false);

		Color orange = new Color(display, 255, 127, 0);

		String helpString = getHelpText(feedbackObj.getHelpCommandType());
		helpText.setText(helpString);

		setFirstLineStyle(orange, helpString);
		if (isGeneralHelp()) {
			boldCommandsForGeneralHelp(helpString);
		} else {
			setCommandFormatStyle(helpString);
		}

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

		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		orange.dispose();
	}

	private String getHelpText(CommandType helpCommandType) {
		if (helpCommandType == null) {
			isGeneralHelp = true;
			return Constants.HELP_GENERAL;
		}

		switch (helpCommandType) {
		case ADD:
			return Constants.HELP_ADD;
		case DISPLAY:
			return Constants.HELP_DISPLAY;
		case HELP:
			return Constants.HELP_HELP;
		case SORT:
			return Constants.HELP_SORT;
		case DELETE:
			return Constants.HELP_DELETE;
		case EDIT:
			return Constants.HELP_EDIT;
		case CLEAR:
			return Constants.HELP_CLEAR;
		case UNDO:
			return Constants.HELP_UNDO;
		case REDO:
			return Constants.HELP_REDO;
		case DONE:
			return Constants.HELP_DONE;
		case FINALISE:
			return Constants.HELP_FINALISE;
		case SEARCH:
			return Constants.HELP_SEARCH;
		case EXIT:
			return Constants.HELP_EXIT;
		case GOTO:
			return "dummy";
		default:
			isGeneralHelp = true;
			return Constants.HELP_GENERAL;
		}
	}

	private boolean isGeneralHelp() {
		return isGeneralHelp;
	}

	private void setFirstLineStyle(Color orange, String helpString) {
		StyleRange styleRange = new StyleRange();
		styleRange.start = 0;
		styleRange.length = helpString.indexOf("\n");
		styleRange.underline = true;
		styleRange.foreground = orange;
		helpText.setStyleRange(styleRange);
	}

	private void boldCommandsForGeneralHelp(String helpString) {
		StyleRange styleRange;
		int startIndex = helpString.indexOf("\n");
		int colonIndex = helpString.indexOf(":");

		while (startIndex != -1) {
			styleRange = new StyleRange();
			styleRange.start = startIndex + 1;
			styleRange.length = colonIndex - startIndex;
			styleRange.fontStyle = SWT.BOLD;
			helpText.setStyleRange(styleRange);
			startIndex = helpString.indexOf("\n", startIndex + 1);
			colonIndex = helpString.indexOf(":", colonIndex + 1);
		}

		isGeneralHelp = false;
	}

	private void setCommandFormatStyle(String helpString) {
		int startIndex = helpString.indexOf("\n\n");
		int colonIndex = helpString.indexOf(":");
		StyleRange styleRange = new StyleRange();
		styleRange.start = startIndex + 1;
		styleRange.length = colonIndex - startIndex;
		styleRange.fontStyle = SWT.BOLD;
		helpText.setStyleRange(styleRange);
	}
}
