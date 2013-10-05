
package test;

import static org.junit.Assert.*;

import org.junit.Test;
import logic.Command;
import logic.CommandType;
import logic.Constants;
import parser.Parser;

public class ParserTests {

//	public void assertEquals(Object one, Object two) {
//		
//	}
	
	@Test
	public void addCommandTests() {
		// Correct format
		Command command = new Command(CommandType.ADD_TASK);
		command.setValue(Constants.TASK_ATT_NAME, "go home");
		command.setValue(Constants.TASK_ATT_STARTTIME, "10:00 pm");
		assertEquals(new Parser().parse("go home at 10:00 pm"), command);

		// Missing space
		command = new Command(CommandType.ADD_TASK);
		command.setValue(Constants.TASK_ATT_NAME, "go home");
		command.setValue(Constants.TASK_ATT_STARTTIME, "10:00 pm");
		assertEquals(new Parser().parse("add go home at 10:00pm"), command);

		// Invalid 12-hour format
		command = new Command(CommandType.ADD_TASK);
		command.setValue(Constants.TASK_ATT_NAME, "go home pm");
		command.setValue(Constants.TASK_ATT_STARTTIME, "1:00 pm");
		assertEquals(new Parser().parse("add go home at 13:00 pm"), command);

		// Quote
		// TODO: consider stripping quotes from string
		command = new Command(CommandType.ADD_TASK);
		command.setValue(Constants.TASK_ATT_NAME, "'go home at 10:00 pm'");
		assertEquals(new Parser().parse("add 'go home at 10:00 pm'"), command);

		// Typo in pm
		command = new Command(CommandType.ADD_TASK);
		command.setValue(Constants.TASK_ATT_NAME, "go home p");
		command.setValue(Constants.TASK_ATT_STARTTIME, "1:00 pm");
		assertEquals(new Parser().parse("add go home at 13:00 p"), command);

		// Symbols, day alias
		command = new Command(CommandType.ADD_TASK);
		command.setValue(Constants.TASK_ATT_NAME, "go home on tuesday yeah");
		command.setValue(Constants.TASK_ATT_STARTTIME, "1:00 pm");
		assertEquals(new Parser().parse("add go home at 13:00 on tuesday yeah!"), command);

		// Proper date format
		command = new Command(CommandType.ADD_TASK);
		command.setValue(Constants.TASK_ATT_NAME, "go home");
		command.setValue(Constants.TASK_ATT_DEADLINE, "1/1/13");
		assertEquals(new Parser().parse("add go home on 1/1/13"), command);

		// No year
		command = new Command(CommandType.ADD_TASK);
		command.setValue(Constants.TASK_ATT_NAME, "go home");
		command.setValue(Constants.TASK_ATT_DEADLINE, "1/1/13");
		assertEquals(new Parser().parse("add go home on 1/1"), command);

		// Date and time
		command = new Command(CommandType.ADD_TASK);
		command.setValue(Constants.TASK_ATT_NAME, "go home");
		command.setValue(Constants.TASK_ATT_STARTTIME, "10:00 pm");
		command.setValue(Constants.TASK_ATT_DEADLINE, "1/2/13");
		assertEquals(new Parser().parse("add go home at 10:00 pm on 1/2/13"), command);

		// Fake on and at keywords
		command = new Command(CommandType.ADD_TASK);
		command.setValue(Constants.TASK_ATT_NAME, "go home at kitchen on top");
		command.setValue(Constants.TASK_ATT_DEADLINE, "1/2/13");
		command.setValue(Constants.TASK_ATT_STARTTIME, "10:00 pm");
		assertEquals(new Parser().parse("add go home at kitchen on top at 10:00 pm on 1/2/13"), command);

		command = new Command(CommandType.ADD_TASK);
		command.setValue(Constants.TASK_ATT_NAME, "go home at at at at");
		command.setValue(Constants.TASK_ATT_STARTTIME, "10:00 pm");
		assertEquals(new Parser().parse("add go home at at at at at 10:00 pm"), command);
	}

	@Test
	public void addCommandFromTests() {
//		Command command = new Command(CommandType.ADD_TASK);
//		command.setValue(Constants.TASK_ATT_NAME, "go home");
//		command.setValue(Constants.TASK_ATT_STARTTIME, "10:00 pm");
//		assertEquals(new Parser().parse("go home at 10:00 pm"), command);
	}
}
