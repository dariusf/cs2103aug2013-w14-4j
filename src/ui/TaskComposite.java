package ui;

import java.util.Arrays;

import logic.Task;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.joda.time.DateTime;

public class TaskComposite extends Composite {

	private static final RowLayout innerRowLayout = new RowLayout();

	private StyledText taskIndex;
	private StyledText taskName;
	private StyledText taskDescription;

	public TaskComposite(Composite parent, Task task, int index) {
		super(parent, SWT.NONE);

		// 340 is the fixed width and 69 is the fixed height. use SWT.default if
		// you do not want to fix the lengths.
		this.setLayoutData(new RowData(425, SWT.DEFAULT));
		this.setLayout(innerRowLayout);

		RowData taskIndexLayoutData = new RowData(60, 73);
		RowData paddingLayoutData = new RowData(8, SWT.DEFAULT);
		RowData taskDescriptionLayoutData = new RowData(340, SWT.DEFAULT);

		taskIndex = new StyledText(this, SWT.WRAP | SWT.READ_ONLY);
		taskIndex.setEnabled(false);
		taskIndex.setText(String.valueOf(index));
		taskIndex.setFont(ApplicationWindow.self.indexFont);
		taskIndex.setForeground(new Color(ApplicationWindow.self.shell
				.getDisplay(), 0x99, 0, 0));
		taskIndex.setLineAlignment(0, 1, SWT.RIGHT);
		taskIndex.setLayoutData(taskIndexLayoutData);

		Composite paddingComposite = new Composite(this, SWT.NONE);
		paddingComposite.setLayoutData(paddingLayoutData);

		Composite taskDetailsComposite = new Composite(this, SWT.NONE);
		taskDetailsComposite.setLayoutData(taskDescriptionLayoutData);
		taskDetailsComposite.setLayout(new GridLayout());

		taskName = new StyledText(taskDetailsComposite, SWT.READ_ONLY);
		taskName.setEnabled(false);
		taskName.setLayoutData(new GridData(340, SWT.DEFAULT));
		taskName.setFont(ApplicationWindow.self.titleFont);
		this.setTaskName(task.getName());

		taskDescription = new StyledText(taskDetailsComposite, SWT.READ_ONLY);
		taskDescription.setEnabled(false);
		taskDescription.setText(task.getInfoString());
		taskDescription.setFont(ApplicationWindow.self.descriptionFont);

		if (task.isDone()) {
			StyleRange style1 = new StyleRange();
			style1.start = 0;
			style1.length = task.getName().length();
			style1.strikeout = true;
			taskName.setStyleRange(style1);
		} else if (task.isOverdue()) {
			taskName.setForeground(new Color(ApplicationWindow.self.shell
					.getDisplay(), 0x99, 0, 0));
		}

		taskDetailsComposite.pack();

		this.pack();
	}

	public void setTaskIndex(int index) {
		taskIndex.setText(String.valueOf(index));
	}

	public int getTaskIndex() {
		int result = -1;
		try {
			result = Integer.parseInt(taskIndex.getText());
		} catch (NumberFormatException e) {
			assert false : "Invaild task index text stored";
		}
		assert result >= 0;
		return result;
	}

	public void setTaskName(String name) {
		taskName.setText(name);
		this.pack();

		StyledText measurement = new StyledText(taskName.getParent(), SWT.NONE);
		measurement.setText(name);
		measurement.setFont(new Font(taskName.getDisplay(), "Calibri", 24,
				SWT.NORMAL));
		measurement.pack();
		int xSize = measurement.getSize().x;

		boolean isWindows = System.getProperty("os.name").toLowerCase()
				.indexOf("win") >= 0;

		if (xSize > 330) {
			if (isWindows) {
				int newFontSize = Math.max(330 * 18 / xSize, 9);
				Font newFont = new Font(taskName.getDisplay(), "Calibri",
						newFontSize, SWT.NORMAL);
				taskName.setFont(newFont);
			} else {
				int newFontSize = Math.max(330 * 24 / xSize, 12);
				Font newFont = new Font(taskName.getDisplay(), "Calibri",
						newFontSize, SWT.NORMAL);
				taskName.setFont(newFont);
			}

		}
		measurement.dispose();
	}

	public String getTaskName(String name) {
		return taskName.getText();
	}

	public void setDescription(String name) {
		taskDescription.setText(name);
		taskDescription.pack();
	}

	public void setTentativeTaskAtLine(String description, int line) {
		String[] currentDescription = taskDescription.getText().split("\n");
	
		int numberOfLines = currentDescription.length;
		if (this.isTagged()) {
			numberOfLines = numberOfLines - 1;
		}

		if (line <= numberOfLines && !taskDescription.getText().isEmpty()) {
			currentDescription[line - 1] = description;
			StringBuilder builder = new StringBuilder();

			for (String string : currentDescription) {
				if (builder.length() > 0) {
					builder.append("\n");
				}
				builder.append(string);
			}

			String newString = builder.toString();
			taskDescription.setText(newString);
			taskDescription.pack();
		}
	}

	public String getDescription(String name) {
		return taskDescription.getText();
	}

	public void setHighlighted(boolean highlighted) {
		if (highlighted) {
			setBackground(new Color(ApplicationWindow.self.shell.getDisplay(),
					0xdd, 0xdd, 0xdd));
		} else {
			setBackground(null);
		}
	}

	public void highlightLine(int line) {
		String[] currentDescription = taskDescription.getText().split("\n");
		int numberOfLines = currentDescription.length;
		if (this.isTagged()) {
			numberOfLines = numberOfLines - 1;
		}
		if (line > 0 && line <= numberOfLines && !taskDescription.getText().isEmpty()) {
			taskDescription.setLineBackground(line - 1, 1,
					new Color(ApplicationWindow.self.shell.getDisplay(), 0x00,
							0xdd, 0x00));
		}
	}

	public boolean isTagged() {
		String[] taskDescriptionArray = taskDescription.getText().split("\n");
		if (taskDescriptionArray.length > 0) {
			String lastLine = taskDescriptionArray[taskDescriptionArray.length - 1];
			return lastLine.startsWith("#");
		}
		return false;
	}

	public String getTags() {
		if (isTagged()) {
			String[] taskDescriptionArray = taskDescription.getText().split(
					"/n");
			return taskDescriptionArray[taskDescriptionArray.length - 1];
		} else {
			return "";
		}
	}

	public String getTimeString() {
		if (isTagged()) {
			String[] taskDescriptionArray = taskDescription.getText().split(
					"/n");
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < taskDescriptionArray.length - 1; i++) {
				stringBuilder.append(taskDescriptionArray[i]);
				if (i != taskDescriptionArray.length - 2) {
					stringBuilder.append(" ");
				}
			}
			return stringBuilder.toString();
		} else {
			return taskDescription.getText();
		}
	}
}
