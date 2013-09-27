package Storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import Logic.Task;

public class Storage {
	private enum ActionType { ADD, REMOVE, EDIT, CLEAR }
	
	private abstract class Action {
		ActionType actionType;
		
		Action(ActionType actionType) {
			this.actionType = actionType;
		}
	}
	
	private class AddAction extends Action {
		Task changedTask;
		
		AddAction(Task changedTask) {
			super(ActionType.ADD);
			this.changedTask = changedTask;
		}
	}
	
	private class RemoveAction extends Action {
		Task changedTask;
		int index;
		
		RemoveAction(Task changedTask, int index) {
			super(ActionType.REMOVE);
			this.changedTask = changedTask;
			this.index = index;
		}
	}
	
	private class EditAction extends Action {
		Task before;
		Task after;
		int index;
		
		EditAction(int index, Task before, Task after) {
			super(ActionType.EDIT);
			this.before = before;
			this.after = after;
			this.index = index;
		}
	}
	
	private class ClearAction extends Action {
		ArrayList<Task> previousState;
		
		ClearAction(ArrayList<Task> previousState) {
			super(ActionType.CLEAR);
			this.previousState = previousState;
		}
	}
	
	private static ArrayList<Task> taskStorage = new ArrayList<Task>();;
	private StorageLinkedList<Action> actionsPerformed = new StorageLinkedList<>();
	
	public Storage() {
		
	}
	
	public void sort() {
		
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
		Action thisAction = new ClearAction(taskStorage);
		taskStorage = new ArrayList<Task>();
		actionsPerformed.pushHere(thisAction);
	}

	public Task get(int index) {
		return taskStorage.get(index - 1);
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
		// TODO check if this method is recursive
		undoAction(action);
	}
	
	private void undoAction(AddAction action) {
		taskStorage.remove(action.changedTask);
	}
	
	private void undoAction(RemoveAction action) {
		taskStorage.add(action.index, action.changedTask);
	}

	private void undoAction(EditAction action) {
		taskStorage.set(action.index, action.before);
	}
	
	private void undoAction(ClearAction action) {
		taskStorage = action.previousState;
	}
	
	public void redo() throws Exception {
		if (!isRedoable()) { throw new Exception(); } // will change to better exception when I find one
		redoAction(actionsPerformed.next());
	}
	
	private void redoAction(Action action) {
		// TODO check if this method is recursive
		redoAction(action);
	}
	
	private void redoAction(AddAction action) {
		taskStorage.add(action.changedTask);
	}
	
	private void redoAction(RemoveAction action) {
		taskStorage.remove(action.index);
	}
	
	private void redoAction(EditAction action) {
		taskStorage.set(action.index, action.after);
	}
	
	private void redoAction(ClearAction action) {
		taskStorage = new ArrayList<Task>();
	}

	public boolean isUndoable() {
		return actionsPerformed.hasNext();
	}
	
	public boolean isRedoable() {
		return actionsPerformed.hasPrevious();
	}
}
