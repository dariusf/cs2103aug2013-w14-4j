package ui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class HelpDialog extends Dialog {

	public HelpDialog(Shell parent) {
		super(parent);
	}

	public void open() {
		Shell parent = getParent();
		Shell dialog = new Shell(parent);
		
		dialog.setSize(100, 100);
		dialog.setText("Help");
		dialog.open();
		
		Display display = parent.getDisplay();
		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
