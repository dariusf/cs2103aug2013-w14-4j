package test;

import static org.junit.Assert.*;
import logic.Feedback;

import org.junit.Test;

import common.CommandType;
import common.Constants;

//@author A0101048X
public class FeedbackTest {
	
	@Test
	public final void test() {
		
		testForSuccessCases();

	}

	/**
	 * This method ensures that for each command type,
	 * the success string is as expected.
	 */
	private void testForSuccessCases() {
		Feedback testFeedback = new Feedback(Constants.SC_SUCCESS, CommandType.ADD); 
		assertEquals(Constants.FEEDBACK_SUCCESS_ADD, testFeedback.toString());
		
		testFeedback.setCommand(CommandType.EDIT);
		assertEquals(Constants.FEEDBACK_SUCCESS_EDIT, testFeedback.toString());
		
		testFeedback.setCommand(CommandType.DISPLAY);
		assertEquals(Constants.FEEDBACK_SUCCESS_DISPLAY, testFeedback.toString());
		
		testFeedback.setCommand(CommandType.DELETE);
		assertEquals(Constants.FEEDBACK_SUCCESS_DELETE, testFeedback.toString());
		
		testFeedback.setCommand(CommandType.CLEAR);
		assertEquals(Constants.FEEDBACK_SUCCESS_CLEAR, testFeedback.toString());

		testFeedback.setCommand(CommandType.SORT);
		assertEquals(Constants.FEEDBACK_SUCCESS_SORT, testFeedback.toString());
		
		testFeedback.setCommand(CommandType.SEARCH);
		assertEquals(Constants.FEEDBACK_SUCCESS_SEARCH, testFeedback.toString());
		
		testFeedback.setCommand(CommandType.UNDO);
		assertEquals(Constants.FEEDBACK_SUCCESS_UNDO, testFeedback.toString());
		
		testFeedback.setCommand(CommandType.REDO);
		assertEquals(Constants.FEEDBACK_SUCCESS_REDO, testFeedback.toString());
		
		testFeedback.setCommand(CommandType.FINALISE);
		assertEquals(Constants.FEEDBACK_SUCCESS_FINALISE, testFeedback.toString());

		testFeedback.setCommand(CommandType.HELP);
		assertEquals(Constants.FEEDBACK_SUCCESS_HELP, testFeedback.toString());

		testFeedback.setCommand(CommandType.DONE);
		assertEquals(Constants.FEEDBACK_SUCCESS_DONE, testFeedback.toString());
		
		testFeedback.setCommand(CommandType.GOTO);
		int expectedPageNumber = 2;
		testFeedback.setPageNumber(expectedPageNumber);
		assertEquals(expectedPageNumber, testFeedback.getPageNumber());
		assertEquals(String.format(Constants.FEEDBACK_SUCCESS_GOTO, expectedPageNumber), testFeedback.toString());
		
		testFeedback.setStatusCode(Constants.SC_SUCCESS_TASK_OVERDUE);
		testFeedback.setCommand(CommandType.ADD);
		assertEquals(Constants.FEEDBACK_SUCCESS_OVERDUE_ADD, testFeedback.toString());
		
		testFeedback.setCommand(CommandType.EDIT);
		assertEquals(Constants.FEEDBACK_SUCCESS_OVERDUE_EDIT, testFeedback.toString());
		
		testFeedback.setStatusCode(Constants.SC_SUCCESS_CLEAR_DONE);
		testFeedback.setCommand(CommandType.CLEAR);
		assertEquals(Constants.FEEDBACK_SUCCESS_CLEAR, testFeedback.toString());
	}

}
