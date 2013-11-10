package test;

import static org.junit.Assert.*;
import logic.Feedback;

import org.junit.Test;

import common.CommandType;
import common.Constants;

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
		assertEquals("Task added successfully!", testFeedback.toString());
		
		testFeedback.setCommand(CommandType.EDIT);
		assertEquals("Task edited successfully!", testFeedback.toString());
		
		testFeedback.setCommand(CommandType.DISPLAY);
		assertEquals("Here are your tasks.", testFeedback.toString());
		
		testFeedback.setCommand(CommandType.DELETE);
		assertEquals("Task deleted successfully!", testFeedback.toString());
		
		testFeedback.setCommand(CommandType.CLEAR);
		assertEquals("All tasks cleared!", testFeedback.toString());

		testFeedback.setCommand(CommandType.SORT);
		assertEquals("All tasks sorted!", testFeedback.toString());
		
		testFeedback.setCommand(CommandType.SEARCH);
		assertEquals("Here are your search results.", testFeedback.toString());
		
		testFeedback.setCommand(CommandType.UNDO);
		assertEquals("Undo successful!", testFeedback.toString());
		
		testFeedback.setCommand(CommandType.REDO);
		assertEquals("Redo successful!", testFeedback.toString());
		
		testFeedback.setCommand(CommandType.FINALISE);
		assertEquals("Task finalised successfully!", testFeedback.toString());

		/*testFeedback.setCommand(CommandType.HELP);
		assertEquals("Help successful!", testFeedback.toString());*/

		testFeedback.setCommand(CommandType.DONE);
		assertEquals("Task marked as done!", testFeedback.toString());
		
		testFeedback.setCommand(CommandType.GOTO);
		testFeedback.setGotoPage(2);
		assertEquals("Page 2", testFeedback.toString());
	}

}
