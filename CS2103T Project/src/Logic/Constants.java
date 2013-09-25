package Logic;

public class Constants {
	public static String WELCOME_MSG = "Welcome to TextBuddy. %1$s is ready for use";

	protected static final String MSG_INVALID_FORMAT = "invalid command format: %1$s";
	protected static final String MSG_ADDED_LINE = "added to %1$s: \"%2$s\"";
	protected static final String MSG_EMPTY_LINE = "the text is empty!";
	protected static final String MSG_DELETED_LINE = "deleted from %1$s: \"%2$s\"";
	protected static final String MSG_CLEAR = "all content deleted from %1$s";
	protected static final String MSG_EMPTY_FILE = "%1$s is empty";
	protected static final String MSG_LINE_NUMBER_OVERFLOW = "%1$s only has %2$s line(s)!";
	protected static final String MSG_OUTPUT_ERROR = "Problem writing to the file %1$s";
	protected static final String MSG_NO_FILENAME = "Please input file name!";
	protected static final String MSG_UNRECOGNISED_COMMAND = "Unrecognized command type";
	protected static final String MSG_NO_KEYWORD = "Please input search keyword!";
	protected static final String MSG_NO_RESULT = "No result found!";
	protected static final String MSG_COMMAND = "command: ";

	protected static final String EMPTY_STRING = "";

	protected static final String COMMAND_ADD = "add";
	protected static final String COMMAND_DELETE = "delete";
	protected static final String COMMAND_CLEAR = "clear";
	protected static final String COMMAND_DISPLAY = "display";
	protected static final String COMMAND_EXIT = "exit";
	protected static final String COMMAND_SORT = "sort";
	protected static final String COMMAND_SEARCH = "search";
	
	protected static final String TASK_ATT_NAME = "name";
	protected static final String TASK_ATT_LOCATION = "location";
	protected static final String TASK_ATT_STARTTIME = "startTime";
	protected static final String TASK_ATT_ENDTIME = "endTime";
	protected static final String TASK_ATT_DEADLINE = "deadline";
	protected static final String TASK_ATT_TYPE = "type";
	protected static final String TASK_ATT_TAGS = "tags";
	protected static final String TASK_ATT_POSSIBLETIME = "possibleTime";
	protected static final String TASK_ATT_DONE = "done";
	
	protected static final String DELETE_ATT_LINE= "lineNumber";
	
	protected static final String TASK_TYPE_FLOATING = "floating";
	protected static final String TASK_TYPE_TIMED = "timed";
	protected static final String TASK_TYPE_DEADLINE = "deadline";
	protected static final String TASK_TYPE_UNTIMED = "untimed";
	
}
