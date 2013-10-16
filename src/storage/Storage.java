package storage;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import logic.Task;

public class Storage implements Closeable {
	
	private interface Action {
		void undo();
		void redo();
	}
	
	private class AddAction implements Action {
		Task changedTask;
		
		AddAction(Task changedTask) {
			this.changedTask = changedTask;
		}

		@Override
		public void undo() {
			taskStorage.remove(changedTask);
		}

		@Override
		public void redo() {
			taskStorage.add(changedTask);
		}
	}
	
	private class RemoveAction implements Action {
		Task changedTask;
		int index;
		
		RemoveAction(Task changedTask, int index) {
			this.changedTask = changedTask;
			this.index = index;
		}

		@Override
		public void undo() {
			taskStorage.add(index, changedTask);
		}

		@Override
		public void redo() {
			taskStorage.remove(index);
		}
	}
	
	private class EditAction implements Action {
		Task before;
		Task after;
		int index;
		
		EditAction(int index, Task before, Task after) {
			this.before = before;
			this.after = after;
			this.index = index;
		}

		@Override
		public void undo() {
			taskStorage.set(index, before);
		}

		@Override
		public void redo() {
			taskStorage.set(index, after);
		}
	}
	
	private class StateAction implements Action {
		ArrayList<Task> previousState;
		ArrayList<Task> nextState;
		
		StateAction(ArrayList<Task> previousState, ArrayList<Task> nextState) {
			this.previousState = previousState;
			this.nextState = nextState;
		}

		@Override
		public void undo() {
			taskStorage = (ArrayList<Task>) previousState.clone();
		}

		@Override
		public void redo() {
			taskStorage = (ArrayList<Task>) nextState.clone();
		}
	}
	
	private ArrayList<Task> taskStorage;
	private StorageLinkedList<Action> actionsPerformed = new StorageLinkedList<>();
	private final String fileName;
	
	public Storage(String fileName) throws IOException {
		this.fileName = fileName;
		File file = new File(fileName);
		if (file.exists()) {
			taskStorage = Json.readFromFile(new File(fileName));
		} else {
			taskStorage = new ArrayList<>();
		}
	}
	
	public Storage() throws IOException {
		this("default.txt");
		File file = new File(fileName);
		if (file.exists()) {
			taskStorage = Json.readFromFile(new File(fileName));
		} else {
			taskStorage = new ArrayList<>();
		}
	}
	
	public void sort() {
		ArrayList<Task> previousState = (ArrayList<Task>) taskStorage.clone();
		Collections.sort(taskStorage);
		ArrayList<Task> nextState = (ArrayList<Task>) taskStorage.clone();
		StateAction thisAction = new StateAction(previousState, nextState);
		actionsPerformed.pushHere(thisAction);
	}
	
	public void add(Task task) {
		taskStorage.add(task);
		Action thisAction = new AddAction(task);
		actionsPerformed.pushHere(thisAction);
	}
	
	public void remove(int index) {
		index--;
		Task removedItem = taskStorage.remove(index);
		Action thisAction = new RemoveAction(removedItem, index);
		actionsPerformed.pushHere(thisAction);
	}
	
	public void remove(Task t) {
		remove(taskStorage.indexOf(t) + 1);
	}

	public void replace(int index, Task task) {
		index--;
		Action thisAction = new EditAction(index, taskStorage.get(index), task);
		taskStorage.set(index, task);
		actionsPerformed.pushHere(thisAction);
	}

	public void clear() {
		Action thisAction = new StateAction(taskStorage, new ArrayList<Task>());
		taskStorage = new ArrayList<Task>();
		actionsPerformed.pushHere(thisAction);
	}

	public Task get(int index) {
		// TODO change implementation based on accessing classes
		try {
			return (Task) taskStorage.get(index - 1).clone(); 
		} catch (CloneNotSupportedException e) {
			throw new Error();
		}
	}
	
	public String getFileName() {
		return fileName;
	}

	public Iterator<Task> iterator() {
		return taskStorage.iterator();
	}
	
	public boolean isEmpty() {
		return taskStorage.isEmpty();
	}
	
	public int size() {
		return taskStorage.size();
	}
	
	public void undo() throws Exception {
		if (!isUndoable()) { throw new Exception(); } // will change to better exception when I find one
		undoAction(actionsPerformed.next());
	}
	
	private void undoAction(Action action) {
		action.undo();
	}
	
	public void redo() throws Exception {
		if (!isRedoable()) { throw new Exception(); } // will change to better exception when I find one
		redoAction(actionsPerformed.previous());
	}
	
	private void redoAction(Action action) {
		action.redo();
	}

	public boolean isUndoable() {
		return actionsPerformed.hasNext();
	}
	
	public boolean isRedoable() {
		return actionsPerformed.hasPrevious();
	}

	@Override
	public void close() throws IOException {
		File file = new File(fileName);
		if(file.exists()) { file.delete(); }
		Json.writeToFile(taskStorage, file);
	}
}
