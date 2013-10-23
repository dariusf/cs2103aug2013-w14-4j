package ui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;

public class HelpDialog extends Dialog {

	public HelpDialog(Shell parent) {
		super(parent);
	}

	public void open() {
		Shell parent = getParent();
		final Shell dialog = new Shell(parent);
		
		dialog.setSize(367, 420);
		dialog.setText("Help");
		
		final Button closeButton = new Button(dialog, SWT.NONE);
		closeButton.setBounds(121, 357, 118, 25);
		closeButton.setText("Got it!");
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
}
