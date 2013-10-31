package common.undo;

// TODO remove debug codes, or move them to a logger

public class ActionStack {
	
	private static class ActionSet implements Action {
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
	
	private DoublyLinkedList<Action> actionStack = new DoublyLinkedList<>();;
	private ActionSet currentActionSet = new ActionSet();;
	private static ActionStack instance = new ActionStack();
	
	private ActionStack() {}
			
	private void noPendingActionsCheck () throws Exception {
		if (!currentActionSet.isEmpty()) {
			throw new Exception("Current action stack contains unfinalised actions!" +
					"Please finalise them before undo/redo.");
		}
	}
	
	public void add (Action action) {
		currentActionSet.addAction(action);
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
			System.out.println("empty stack");
			return;
		}
		System.out.println("push success");
		actionStack.pushHere(currentActionSet);
		currentActionSet = new ActionSet();
	}
	
	public void flushCurrentActionSet () {
		System.out.println("flush");
		currentActionSet = new ActionSet();
	}
	
	public static synchronized ActionStack getInstance() {
		return instance;
	}

}
