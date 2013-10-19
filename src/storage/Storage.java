package storage;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import logic.Task;

public class Storage implements Closeable, Iterable<Task> {
	
	private abstract class Action {
		protected abstract void undoImplementation();
		protected abstract void redoImplementation();
		protected abstract void executeImplementation();
		
		void undo() {
			undoImplementation();
			actionFinaliser();
		}
		
		void redo() {
			redoImplementation();
			actionFinaliser();
		}
		
		protected void execute() {
			executeImplementation();
			actionFinaliser();
		}
		
		private void actionFinaliser() throws Error {
			try {
				writeToFile();
			} catch (IOException e) {
				throw new Error(e);
			}
		}
	}
	
	private class AddAction extends Action {
		Task changedTask;
		
		AddAction(Task changedTask) {
			this.changedTask = changedTask;
		}

		@Override
		protected void undoImplementation() {
			taskStorage.remove(changedTask);
		}

		@Override
		protected void redoImplementation() {
			taskStorage.add(changedTask);
		}
		
		@Override
		protected void executeImplementation() {
			taskStorage.add(changedTask);
		}
	}
	
	private class RemoveAction extends Action {
		Task changedTask;
		int index;
		
		RemoveAction(int index) {
			this.changedTask = taskStorage.get(index);
			this.index = index;
		}

		@Override
		protected void undoImplementation() {
			taskStorage.add(index, changedTask);
		}

		@Override
		protected void redoImplementation() {
			taskStorage.remove(index);
		}
		
		@Override
		protected void executeImplementation() {
			taskStorage.remove(index);
		}
	}
	
	private class EditAction extends Action {
		Task before;
		Task after;
		int index;
		
		EditAction(int index, Task after) {
			this.before = taskStorage.get(index);
			this.after = after;
			this.index = index;
		}

		@Override
		protected void undoImplementation() {
			taskStorage.set(index, before);
		}

		@Override
		protected void redoImplementation() {
			taskStorage.set(index, after);
		}
		
		@Override
		protected void executeImplementation() {
			taskStorage.set(index, after);
		}
	}
	
	private abstract class StateAction extends Action {
		ArrayList<Task> previousState;
		ArrayList<Task> nextState;

		@Override
		protected void undoImplementation() {
			taskStorage = (ArrayList<Task>) previousState.clone();
		}

		@Override
		protected void redoImplementation() {
			taskStorage = (ArrayList<Task>) nextState.clone();
		}
		
		@Override
		protected void executeImplementation() {
			taskStorage = (ArrayList<Task>) nextState.clone();
		}
	}
	
	private class ClearAction extends StateAction {
		ClearAction() {
			previousState = taskStorage;
			nextState = new ArrayList<>();
		}
	}
	
	private class SortAction extends StateAction {
		SortAction() {
			previousState = (ArrayList<Task>) taskStorage.clone();
			nextState = (ArrayList<Task>) previousState.clone();
			Collections.sort(nextState);
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
	}
	
	private void executeAndStore(Action action) {
		action.execute();
		actionsPerformed.pushHere(action);
	}
	
	public void sort() {
		StateAction thisAction = new SortAction();
		executeAndStore(thisAction);
	}
	
	public void add(Task task) {
		Action thisAction = new AddAction(task);
		executeAndStore(thisAction);
	}
	
	public void remove(int index) {
		index--;
		Action thisAction = new RemoveAction(index);
		executeAndStore(thisAction);
	}
	
	public void remove(Task t) {
		remove(taskStorage.indexOf(t) + 1);
	}

	public void replace(int index, Task task) {
		index--;
		Action thisAction = new EditAction(index, task);
		executeAndStore(thisAction);
	}

	public void clear() {
		Action thisAction = new ClearAction();
		executeAndStore(thisAction);
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
		writeToFile();
	}

	private void writeToFile() throws IOException {
		File file = new File(fileName);
		if(file.exists()) { file.delete(); }
		Json.writeToFile(taskStorage, file);
	}
}
