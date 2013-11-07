package ui;

import logic.Feedback;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.wb.swt.SWTResourceManager;

import common.CommandType;
import common.Constants;

public class HelpDialog extends Dialog {

	Display display;
	StyledText helpText;
	boolean isGeneralHelp = false;
	private Shell dialog;

	// @author A0101048X
	public HelpDialog(Shell parent) {
		super(parent);
	}

	public void open(Feedback feedbackObj) {
		Shell parent = getParent();
		dialog = new Shell(parent);
		display = parent.getDisplay();

		dialog.setSize(420, 460);
		dialog.setText("Help");
		
		Monitor primary = display.getPrimaryMonitor();
		
		Rectangle monitorBounds = primary.getBounds();
		Rectangle parentShellBounds = parent.getBounds();
		Rectangle helpShellBounds = dialog.getBounds();
		
		int x = calculateXCoordinateForShellPosition(parentShellBounds, monitorBounds, helpShellBounds);
		int y = calculateYCoordinateForShellPosition(parentShellBounds);
		dialog.setLocation(x, y);

		final Button closeButton = new Button(dialog, SWT.NONE);
		closeButton.setBounds(148, 397, 118, 25);
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
		helpText.setBounds(10, 10, 394, 381);
		helpText.setEnabled(false);

		Color orange = new Color(display, 255, 127, 0);

		String helpString = getHelpText(feedbackObj.getHelpCommandType());
//		helpText.setText(helpString);

//		setFirstLineStyle(orange, helpString);
//		if (isGeneralHelp()) {
//			boldCommandsForGeneralHelp(helpString);
//		} else {
//			setCommandFormatStyle(helpString);
//		}
		
//		ArrayList<Integer> formats = getFormatRanges(helpString);
//		helpText.setText(helpString.replaceAll(Constants.FORMATTING_REGEX_UNDERLINE, "$1").replaceAll(Constants.FORMATTING_REGEX_BOLD, "$1"));
//		applyFormatting(helpString, formats);
		
		TextFormatter.setFormattedText(helpText, helpString);
		
		dialog.open();

		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.widget == closeButton) {
					close();
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
	
	// @author: A0097556M
	public void close () {
		if (!dialog.isDisposed()) {
			dialog.close();
		}
	}
	
	// @author A0101048X
	private int calculateYCoordinateForShellPosition(
			Rectangle parentShellBounds) {
		int parentYCoordinate = parentShellBounds.y;
		int yCoordinate = parentYCoordinate;
		
		return yCoordinate;
	}

	private int calculateXCoordinateForShellPosition(
			Rectangle parentShellBounds, Rectangle monitorBounds, Rectangle helpShellBounds) {
		int parentWidth = parentShellBounds.width;
		int parentXCoordinate = parentShellBounds.x;
		int monitorWidth = monitorBounds.width;
		int helpShellWidth = helpShellBounds.width;
		int xCoordinate;
		boolean positionHelpOnRight = isShellInViewOnRight(parentWidth, parentXCoordinate, monitorWidth, helpShellWidth);
		
		if (positionHelpOnRight) {
			xCoordinate = parentXCoordinate + parentWidth;
		} else {
			xCoordinate = parentXCoordinate - helpShellWidth;
		}
		
		return xCoordinate;
	}

	private boolean isShellInViewOnRight(int parentWidth, int parentXCoordinate, int monitorWidth, int helpShellWidth) {
		boolean exceeds = isTotalWidthGreaterThanMonitorWidth(parentWidth, parentXCoordinate, monitorWidth,
				helpShellWidth);
		if (exceeds) {
			return false;
		} else {
			return true;
		}
	}

	private boolean isTotalWidthGreaterThanMonitorWidth(int parentWidth,
			int parentXCoordinate, int monitorWidth, int helpShellWidth) {
		int totalWidth = parentWidth + parentXCoordinate + helpShellWidth;
		boolean totalWidthExceedsMonitorWidth = monitorWidth < totalWidth;
		return totalWidthExceedsMonitorWidth;
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
			return Constants.HELP_GOTO;
		default:
			isGeneralHelp = true;
			return Constants.HELP_GENERAL;
		}
	}

//	private boolean isGeneralHelp() {
//		return isGeneralHelp;
//	}

	// private void setFirstLineStyle(Color colour, String helpString) {
	// 	StyleRange styleRange = new StyleRange();
	// 	styleRange.start = 0;
	// 	styleRange.length = helpString.indexOf("\n");
	// 	styleRange.underline = true;
	// 	styleRange.foreground = colour;
	// 	helpText.setStyleRange(styleRange);
	// }

	// private void boldCommandsForGeneralHelp(String helpString) {
	// 	StyleRange styleRange;
	// 	int startIndex = helpString.indexOf("\n");
	// 	int colonIndex = helpString.indexOf(":");

	// 	while (startIndex != -1 && colonIndex != -1) {
	// 		styleRange = new StyleRange();
	// 		styleRange.start = startIndex + 1;
	// 		styleRange.length = colonIndex - startIndex;
	// 		styleRange.fontStyle = SWT.BOLD;
	// 		helpText.setStyleRange(styleRange);
	// 		colonIndex = helpString.indexOf(":", colonIndex + 1);
	// 		startIndex = helpString.indexOf("\n", startIndex + 1);
	// 	}

	// 	isGeneralHelp = false;
	// }

	// private void setCommandFormatStyle(String helpString) {
	// 	int startIndex = helpString.indexOf("\n\n");
	// 	int colonIndex = helpString.indexOf(":");
	// 	StyleRange styleRange = new StyleRange();
	// 	styleRange.start = startIndex + 1;
	// 	styleRange.length = colonIndex - startIndex;
	// 	styleRange.fontStyle = SWT.BOLD;
	// 	helpText.setStyleRange(styleRange);
	// }
}
