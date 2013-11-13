package ui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.wb.swt.SWTResourceManager;

import common.CommandType;
import common.Constants;
import common.Feedback;

//@author A0102332A
public class HelpDialog extends Dialog {

	Display display;
	StyledText helpText;
	private Shell dialog;
	final private int padding = 6;
	
	public HelpDialog(Shell parent) {
		super(parent);
	}

	public void open(Feedback feedbackObj) {
		if (dialog != null) {
			if(!dialog.isDisposed()) {
				return;
			}
		}
		Shell parent = getParent();
		dialog = new Shell(parent, SWT.NO_TRIM);
		display = parent.getDisplay();
		
		final Image image = SWTResourceManager.getImage(
				ApplicationWindow.class, "/image/helpbackground.png");
		Region region = new Region();
		final ImageData imageData = image.getImageData();
		if (imageData.alphaData != null) {
			Rectangle pixel = new Rectangle(0, 0, 1, 1);
			for (int y = 0; y < imageData.height; y++) {
				for (int x = 0; x < imageData.width; x++) {
					if (imageData.getAlpha(x, y) == 255) {
						pixel.x = imageData.x + x;
						pixel.y = imageData.y + y;
						region.add(pixel);
					}
				}
			}
		} else {
			ImageData mask = imageData.getTransparencyMask();
			Rectangle pixel = new Rectangle(0, 0, 1, 1);
			for (int y = 0; y < mask.height; y++) {
				for (int x = 0; x < mask.width; x++) {
					if (mask.getPixel(x, y) != 0) {
						pixel.x = imageData.x + x;
						pixel.y = imageData.y + y;
						region.add(pixel);
					}
				}
			}
		}
		dialog.setRegion(region);

		Listener l = new Listener() {
			int startX, startY;

			public void handleEvent(Event e) {
				if (e.type == SWT.KeyDown && e.character == SWT.ESC) {
					dialog.dispose();
				}
				if (e.type == SWT.MouseDown && e.button == 1) {
					startX = e.x;
					startY = e.y;
				}
				if (e.type == SWT.MouseMove && (e.stateMask & SWT.BUTTON1) != 0) {
					Point p = dialog.toDisplay(e.x, e.y);
					p.x -= startX;
					p.y -= startY;
					dialog.setLocation(p);
				}
				if (e.type == SWT.Paint) {
					e.gc.drawImage(image, imageData.x, imageData.y);
				}
			}
		};
		dialog.addListener(SWT.KeyDown, l);
		dialog.addListener(SWT.MouseDown, l);
		dialog.addListener(SWT.MouseMove, l);
		dialog.addListener(SWT.Paint, l);

		dialog.setSize(imageData.x + imageData.width, imageData.y
				+ imageData.height);

		ImageData backgroundData = new ImageData(getClass()
				.getResourceAsStream("/image/helpbackground.png"));
		Image transparentBackgroundImage = new Image(Display.getCurrent(),
				backgroundData);
		dialog.setBackgroundImage(transparentBackgroundImage);
		dialog.setBackgroundMode(SWT.INHERIT_FORCE);

		dialog.setSize(420, 681);
		dialog.setText("Help");
		
		Monitor primary = display.getPrimaryMonitor();
		
		Rectangle monitorBounds = primary.getBounds();
		Rectangle parentShellBounds = parent.getBounds();
		Rectangle helpShellBounds = dialog.getBounds();
		
		int x = calculateXCoordinateForShellPosition(parentShellBounds, monitorBounds, helpShellBounds);
		int y = calculateYCoordinateForShellPosition(parentShellBounds);
		dialog.setLocation(x, y);

		final Button closeButton = new Button(dialog, SWT.NONE);
		closeButton.setBounds(148, 522, 118, 25);
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
		helpText.setBounds(18, 18, 380, 506);
		helpText.setEnabled(false);

		String helpString = getHelpText(feedbackObj.getHelpCommandType());
		
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
	}
	
	//@author A0097556M
	public void close () {
		if (dialog != null && !dialog.isDisposed()) {
			dialog.close();
		}
	}
	
	//@author A0101048X
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
			xCoordinate = parentXCoordinate + parentWidth - padding;
		} else {
			xCoordinate = parentXCoordinate - helpShellWidth + padding;
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
			return Constants.HELP_REDO;
		case DONE :
			return Constants.HELP_DONE;
		case FINALISE :
			return Constants.HELP_FINALISE;
		case SEARCH :
			return Constants.HELP_SEARCH;
		case EXIT :
			return Constants.HELP_EXIT;
		case GOTO :
			return Constants.HELP_GOTO;
		default :
			return Constants.HELP_GENERAL;
		}
	}
}
