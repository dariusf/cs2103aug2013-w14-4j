package test;

import static org.junit.Assert.*;

import org.junit.Test;

import common.undo.Action;
import common.undo.ActionStack;
import common.undo.DoublyLinkedList;

public class ActionStackTest {
	
	int actionCounter = 0;
	ActionStack testStack = ActionStack.getInstance();
	class TestAction implements Action {

		@Override
		public void undo() {
			actionCounter -= 1;
		}

		@Override
		public void redo() {
			actionCounter += 1;
		}
	}
	
	@Test
	public void actionStackTest() throws Exception {
		assertFalse(testStack.isUndoable());
		assertFalse(testStack.isRedoable());
		
		performAction(1);
		assertTrue(testStack.isUndoable());
		assertFalse(testStack.isRedoable());
		assertEquals(1, actionCounter);
		testStack.undo();
		assertEquals(0, actionCounter);
		assertTrue(testStack.isRedoable());
		assertFalse(testStack.isUndoable());
		testStack.redo();
		assertEquals(1, actionCounter);
		testStack.undo();
		performAction(10);
		testStack.undo();
		assertEquals(0, actionCounter);
		testStack.redo();
		assertEquals(10, actionCounter);
		actionCounter = 0;
		testStack.undo();
		assertEquals(-10, actionCounter);
		
	}
	
	private void performAction(int quantity) {
		for (int i = quantity; i > 0; i--) {
			testStack.add(new TestAction());
		}
		
		testStack.finaliseActions();
		actionCounter += quantity;
	}

	@Test
	public void storageLinkedListTest () {
		DoublyLinkedList<Integer> testList = new DoublyLinkedList<>();
		
		testList.pushHere(1);
		assertTrue(testList.hasNext());
		assertEquals((Integer)1, testList.next());
		testList = new DoublyLinkedList<>();
		
		for (int i = 5; i > 0; i--) {
			testList.push(i);
		}
		Integer counter = 1;
		while (testList.hasNext()) {
			assertEquals(counter++, testList.next());
		}
		
		for (int i = 5; i > 0; i--) {
			testList.pushHere(i);
		}
		counter = 1;
		while (testList.hasNext()) {
			assertEquals(counter++, testList.next());
		}
	}

}
