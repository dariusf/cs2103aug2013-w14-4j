// @author: A0097556M

package storage;

import java.util.Iterator;
import java.util.List;

import common.undo.Action;
import common.undo.ActionStack;

public class ActionCapturer<E extends Comparable<E>, T extends StorageBase<E>> implements StorageBase<E> {
	
	T realStorage;
	Cloner<E> itemCloner;
	Cloner<T> storageCloner;
	ActionStack actionStack;
	
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
		T previousState;
		T nextState;
		
		public StateAction(T previousState, T nextState) {
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

	public ActionCapturer(T realStorage, Cloner<E> itemCloner, Cloner<T> storageCloner) {
		this.realStorage = realStorage;
		this.itemCloner = itemCloner;
		this.storageCloner = storageCloner;
		this.actionStack = ActionStack.getInstance();
	}

	@Override
	public void insert(int index, E item) throws IllegalArgumentException {
		if(item == null) {
			throw new IllegalArgumentException();
		}
		InsertAction thisAction = new InsertAction(index, item);
		realStorage.insert(index, item);
		actionStack.add(thisAction);
	}

	@Override
	public void remove(int index) {
		E removedItem = itemCloner.clone(realStorage.get(index));
		RemoveAction thisAction = new RemoveAction(index, removedItem);
		realStorage.remove(index);
		actionStack.add(thisAction);
	}

	@Override
	public void remove(E item) {
		if(item == null) {
			return;
		}
		int index = realStorage.getIndex(item);
		RemoveAction thisAction = new RemoveAction(index, item);
		realStorage.remove(item);
		actionStack.add(thisAction);
	}

	@Override
	public void setState(List<E> items) {
		T previousState = storageCloner.clone(realStorage);
		realStorage.setState(items);
		T nextState = storageCloner.clone(realStorage);
		StateAction thisAction = new StateAction(previousState, nextState);
		actionStack.add(thisAction);
	}
	
	@Override
	public void sort () {
		T previousState = storageCloner.clone(realStorage);
		realStorage.sort();
		T nextState = storageCloner.clone(realStorage);
		StateAction thisAction = new StateAction(previousState, nextState);
		actionStack.add(thisAction);
	}
	
	@Override
	public E get(int index) {
		return itemCloner.clone(realStorage.get(index));
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
}
