package storage;

import java.util.Iterator;
import java.util.List;

import logic.Task;

import com.rits.cloning.Cloner;

public class ActionCapturer<E extends Comparable<E>> implements StorageBase<E> {
	
	StorageBase<E> realStorage;
	Cloner cloner;
	
	private interface Action {
		void undo();
		void redo();
	}
	
	private class InsertAction implements Action {
		int index;
		E changedItem;
		
		InsertAction(int index, E changedItem) {
			this.index = index;
			this.changedItem = changedItem;
		}

		@Override
		public void undo() {
			realStorage.remove(index);
		}

		@Override
		public void redo() {
			realStorage.insert(index, changedItem);
		}
	}
	
	private class RemoveAction implements Action {
		E changedItem;
		int index;
		
		RemoveAction(int index, E item) {
			this.changedItem = item;
			this.index = index;
		}

		@Override
		public void undo() {
			realStorage.insert(index, changedItem);
		}

		@Override
		public void redo() {
			realStorage.remove(index);
		}
	}
	
	private class StateAction implements Action {
		StorageBase<E> previousState;
		StorageBase<E> nextState;
		
		public StateAction(StorageBase<E> previousState, StorageBase<E> nextState) {
			this.previousState = previousState;
			this.nextState = nextState;
		}

		@Override
		public void undo() {
			realStorage = previousState;
		}

		@Override
		public void redo() {
			realStorage = nextState;
		}
	}
	
	private class ActionSet implements Action {
		DoublyLinkedList<Action> set;
		
		public ActionSet() {
			set = new DoublyLinkedList<>();
		}
		
		public void addAction (Action action) {
			set.push(action);
		}

		@Override
		public void undo() {
			while (set.hasNext()) {
				set.next().undo();
			}
		}

		@Override
		public void redo() {
			while (set.hasPrevious()) {
				set.previous().redo();
			}
		}
		
		public boolean isEmpty() {
			return (!set.hasNext() && !set.hasPrevious());
		}
	}
	
	DoublyLinkedList<Action> actionStack;
	ActionSet currentActionSet;
	
	public ActionCapturer(StorageBase<E> realStorage) throws Exception {
		if (realStorage instanceof ActionCapturer) {
			throw new Exception("Cannot instantiate ActionCapturer with an ActionCapturer!");
		}
		this.realStorage = realStorage;
		this.actionStack = new DoublyLinkedList<>();
		this.currentActionSet = new ActionSet();
		this.cloner = new Cloner();
		
		cloner.setCloneAnonymousParent(false);
		cloner.setNullTransient(true);
	}

	@Override
	public void insert(int index, E item) {
		InsertAction thisAction = new InsertAction(index, item);
		realStorage.insert(index, item);
		currentActionSet.addAction(thisAction);
	}

	@Override
	public void remove(int index) {
		E removedItem = cloner.deepClone(realStorage.get(index));
		RemoveAction thisAction = new RemoveAction(index, removedItem);
		realStorage.remove(index);
		currentActionSet.addAction(thisAction);
	}

	@Override
	public void remove(E item) {
		E removedItem = (E) new Task((Task) item);
		int index = realStorage.getIndex(item);
		RemoveAction thisAction = new RemoveAction(index, removedItem);
		realStorage.remove(item);
		currentActionSet.addAction(thisAction);
	}

	@Override
	public void setState(List<E> items) {
		StorageBase<E> previousState = cloner.shallowClone(realStorage);
		realStorage.setState(items);
		StorageBase<E> nextState = cloner.shallowClone(realStorage);
		StateAction thisAction = new StateAction(previousState, nextState);
		currentActionSet.addAction(thisAction);
	}
	
	@Override
	public void sort () {
		StorageBase<E> previousState = cloner.shallowClone(realStorage);
		realStorage.sort();
		StorageBase<E> nextState = cloner.shallowClone(realStorage);
		StateAction thisAction = new StateAction(previousState, nextState);
		currentActionSet.addAction(thisAction);
	}
	
	@Override
	public E get(int index) {
		Task task = new Task((Task) realStorage.get(index)) ;
		E e = (E) task;
		return e;
	}
	
	@Override
	public int getIndex(E item) {
		return realStorage.getIndex(item);
	}

	@Override
	public Iterator<E> iterator() {
		return realStorage.iterator();
	}
	
	@Override
	public int size() {
		return realStorage.size();
	}
	
	private void noPendingActionsCheck () throws Exception {
		if (!currentActionSet.isEmpty()) {
			throw new Exception("Current action stack contains unfinalised actions!" +
					"Please finalise them before undo/redo.");
		}
	}
	
	public void undo () throws Exception {
		if (!isUndoable()) {
			throw new Exception("No actions in stack!");
		}
		
		noPendingActionsCheck();
		actionStack.next().undo();
	}
	
	public void redo () throws Exception {
		if (!isRedoable()) {
			throw new Exception("No actions in stack!");
		}
		
		noPendingActionsCheck();
		actionStack.previous().redo();
	}
	
	public boolean isUndoable() {
		return actionStack.hasNext();
	}
	
	public boolean isRedoable() {
		return actionStack.hasPrevious();
	}
	
	public void finaliseActions() {
		if (currentActionSet.isEmpty()) {
			return;
		}
		
		actionStack.pushHere(currentActionSet);
		currentActionSet = new ActionSet();
	}
}
