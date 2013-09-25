package Logic;

public class Constants {
	public static final String APP_NAME = "Basket";
	
	public static String WELCOME_MSG = "Welcome to TextBuddy. %1$s is ready for use";

	public static final String MSG_INVALID_FORMAT = "invalid command format: %1$s";
	public static final String MSG_ADDED_LINE = "added to %1$s: \"%2$s\"";
	public static final String MSG_EMPTY_LINE = "the text is empty!";
	public static final String MSG_DELETED_LINE = "deleted from %1$s: \"%2$s\"";
	public static final String MSG_CLEAR = "all content deleted from %1$s";
	public static final String MSG_EMPTY_FILE = "%1$s is empty";
	public static final String MSG_LINE_NUMBER_OVERFLOW = "%1$s only has %2$s line(s)!";
	public static final String MSG_OUTPUT_ERROR = "Problem writing to the file %1$s";
	public static final String MSG_NO_FILENAME = "Please input file name!";
	public static final String MSG_UNRECOGNISED_COMMAND = "Unrecognized command type";
	public static final String MSG_NO_KEYWORD = "Please input search keyword!";
	public static final String MSG_NO_RESULT = "No result found!";
	public static final String MSG_COMMAND = "command: ";

	public static final String EMPTY_STRING = "";

	public static final String COMMAND_ADD = "add";
	public static final String COMMAND_DELETE = "delete";
	public static final String COMMAND_CLEAR = "clear";
	public static final String COMMAND_DISPLAY = "display";
	public static final String COMMAND_EXIT = "exit";
	public static final String COMMAND_SORT = "sort";
	public static final String COMMAND_SEARCH = "search";
	public static final String COMMAND_HELP = "help";
	public static final String COMMAND_FINALISE = "finalise";
	public static final String COMMAND_EDIT = "edit";
	
	public static final String TASK_ATT_NAME = "name";
	public static final String TASK_ATT_LOCATION = "location";
	public static final String TASK_ATT_STARTTIME = "startTime";
	public static final String TASK_ATT_ENDTIME = "endTime";
	public static final String TASK_ATT_DEADLINE = "deadline";
	public static final String TASK_ATT_TYPE = "type";
	public static final String TASK_ATT_TAGS = "tags";
	public static final String TASK_ATT_POSSIBLETIME = "possibleTime";
	public static final String TASK_ATT_DONE = "done";
	
	public static final String DELETE_ATT_LINE= "lineNumber";
	
	public static final String TASK_TYPE_FLOATING = "floating";
	public static final String TASK_TYPE_TIMED = "timed";
	public static final String TASK_TYPE_DEADLINE = "deadline";
	public static final String TASK_TYPE_UNTIMED = "untimed";
	
	public static final String HELP_GENERAL = "Here are the available commands:" +
			"add: Add a new task" +
			"edit: Edit an existing task" +
			"done: Mark a task as done" +
			"finalise: Finalise the timing of a floating task" +
			"delete: Delete an existing task" +
			"search: Search for existing task(s)" +
			"undo: Return to the previous edit" +
			"help: Brings up the help guide";
	public static final String HELP_ADD_TASK = "";
	public static final String HELP_DISPLAY = "";
	public static final String HELP_HELP = "";
	public static final String HELP_SORT = "";
	public static final String HELP_DELETE = "";
	public static final String HELP_EDIT_TASK = "";
	public static final String HELP_CLEAR = "";
	public static final String HELP_UNDO = "";
	public static final String HELP_DONE = "";
	public static final String HELP_FINALISE = "";
	public static final String HELP_SEARCH = "";
	public static final String HELP_EXIT = "";
	
	public static final int SC_SUCCESS = 10;
	public static final int SC_SUCCESS_TASK_OVERDUE = 11;
	public static final int SC_INVALID_COMMAND_ERROR = 20;
	public static final int SC_EMPTY_DESCRIPTION_ERROR = 30;
	public static final int SC_UNRECOGNISED_ATTRIBUTE_ERROR = 31;
	public static final int SC_INTEGER_OUT_OF_BOUNDS_ERROR = 41;
	public static final int SC_NO_TASK_ERROR = 50;
	public static final int SC_NO_ID_INDICATED_ERROR = 60;
	public static final int SC_TASK_ALREADY_FINALISED_ERROR = 62;
	public static final int SC_TASK_ALREADY_DONE_ERROR = 63;
	public static final int SC_INTEGER_OUT_OF_BOUNDS_TIME_ERROR = 64;
	public static final int SC_NO_ID_INDICATED_TIME_ERROR = 65;
	public static final int SC_SEARCH_KEYWORD_MISSING_ERROR = 80;
	public static final int SC_UNDO_NO_PRIOR_STATE_ERROR = 100;
}
