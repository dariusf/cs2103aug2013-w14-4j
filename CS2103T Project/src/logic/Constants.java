package logic;

public class Constants {
	public static final String APP_NAME = "Basket";

	public static String WELCOME_MSG = "Welcome to Basket!";

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

	public static final String DELETE_ATT_LINE = "deleteIndex";
	
	public static final String EDIT_ATT_LINE = "editIndex";
	
	public static final String DONE_ATT_LINE = "doneIndex";
	
	public static final String FINALISE_ATT_LINE = "finaliseIndex";
	public static final String FINALISE_ATT_INDEX = "finaliseItem";

	public static final String CLEAR_ATT_DONE = "clearDone";
	
	public static final String TASK_TYPE_FLOATING = "floating";
	public static final String TASK_TYPE_TIMED = "timed";
	public static final String TASK_TYPE_DEADLINE = "deadline";
	public static final String TASK_TYPE_UNTIMED = "untimed";

	public static final String HELP_GENERAL = "Here are the available commands:\n"
			+ "add: Add a new task\n"
			+ "edit: Edit an existing task\n"
			+ "done: Mark a task as done\n"
			+ "finalise: Finalise the timing of a floating task\n"
			+ "display: Display all tasks"
			+ "delete: Delete an existing task\n"
			+ "clear: Remove all tasks\n"
			+ "search: Search for existing task(s)\n"
			+ "sort: Sort the tasks based on dates\n"
			+ "undo: Return to the previous edit\n"
			+ "help: Brings up the help guide\n";

	public static final String HELP_ADD_TASK = "Add Task:\n"
			+ "Adds a new task into the database. You may add a timed task, untimed task, floating task or deadline task.\n\n"
			+ "Command format: \n"
			+ "add Perform April Fool's Prank in Office on 01/04 at 1200 #forfun: Adds a new timed task with the description 'Perform April Fool's Prank in Office' from 1200 to 1300 on 1/4 this year."
			+ "";
	public static final String HELP_DISPLAY = "Display Task:\n"
			+ "Displays all tasks in the database. \n\n" + "Command format: \n"
			+ "display: List all tasks from the database.";
	public static final String HELP_HELP = "Display Help:\n"
			+ "Displays help for specific command. Includes the description of the command as well as examples.\n\n"
			+ "Command format: \n"
			+ "help: Provides a description of every command\n"
			+ "help add: Provides a description of the 'add' command and some example usage";
	public static final String HELP_SORT = "Sort Tasks:\n"
			+ "Sorts all the tasks in the database based on the dates of the tasks, with the earliest one listed first.\n\n"
			+ "Command format: \n" + "sort: Sorts the database";
	public static final String HELP_DELETE = "";
	public static final String HELP_EDIT_TASK = "";
	public static final String HELP_CLEAR = "";
	public static final String HELP_UNDO = "";
	public static final String HELP_DONE = "Mark as Done:\n"
			+ "Marks a completed task as done. \n\n" + "Command format: \n"
			+ "done 2: Mark the task with id 2 as done.";
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
	public static final int SC_FINALISE_TYPE_MISMATCH_ERROR = 90;
	public static final int SC_UNDO_NO_PRIOR_STATE_ERROR = 100;

	public static final String DATE_TIME_FORMAT = "dd/MM/yy hh:mm a";
}
