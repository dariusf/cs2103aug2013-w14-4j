package ui;

import logic.Task;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

import sun.nio.cs.ext.TIS_620;

import com.sun.accessibility.internal.resources.accessibility;

public class TaskComposite extends Composite {

    private static RowLayout innerRowLayout = new RowLayout();
    
    public TaskComposite(Composite parent, Task task, int index) {
        super(parent, SWT.NONE);

        // 340 is the fixed width and 69 is the fixed height. use SWT.default if
        // you do not want to fix the lengths.
        this.setLayoutData(new RowData(415, SWT.DEFAULT));
        this.setLayout(innerRowLayout);

        RowData taskIndexLayoutData = new RowData(60, 73);
        RowData paddingLayoutData = new RowData(8, SWT.DEFAULT);
        RowData taskDescriptionLayoutData = new RowData(330, SWT.DEFAULT);

        StyledText taskIndex = new StyledText(this, SWT.WRAP);
        taskIndex.setText(String.valueOf(index));
        taskIndex.setFont(ApplicationWindow.self.indexFont);
        taskIndex.setForeground(new Color(ApplicationWindow.self.shell.getDisplay(), 0x99, 0, 0));
        taskIndex.setLineAlignment(0, 1, SWT.RIGHT);
        taskIndex.setLayoutData(taskIndexLayoutData);

        Composite paddingComposite = new Composite(this, SWT.NONE);
        paddingComposite.setLayoutData(paddingLayoutData);

        Composite taskDetailsComposite = new Composite(this,
                SWT.NONE);
        taskDetailsComposite.setLayoutData(taskDescriptionLayoutData);
        taskDetailsComposite.setLayout(innerRowLayout);

        StyledText taskName = new StyledText(taskDetailsComposite,
                SWT.READ_ONLY);
        taskName.setText(task.getName());
        taskName.setFont(ApplicationWindow.self.titleFont);
        if (task.isDone()) {
            StyleRange style1 = new StyleRange();
            style1.start = 0;
            style1.length = task.getName().length();
            style1.strikeout = true;
            taskName.setStyleRange(style1);
        } else if (task.isOverdue()) {
            taskName.setForeground(new Color(ApplicationWindow.self.shell.getDisplay(), 0x99, 0, 0));
        }
        taskName.setLayoutData(taskDescriptionLayoutData);

        StyledText taskDescription = new StyledText(taskDetailsComposite,
                SWT.READ_ONLY);
        taskDescription.setText(task.getInfoString());
        taskDescription.setFont(ApplicationWindow.self.descriptionFont);

        taskDetailsComposite.pack();

        this.pack();
    }

}
