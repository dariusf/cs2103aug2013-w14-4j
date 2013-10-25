package common;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Constants {
	public static final String APP_NAME = "Basket";

	public static String WELCOME_MSG = "Welcome to Basket! \nType 'help' if you are unsure of what to do.";
	public static String WELCOME_PAGE_DISPLAY = "display";

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
	public static final String COMMAND_GOTO = "goto";
	public static final String COMMAND_UNDO = "undo";
	public static final String COMMAND_REDO = "redo";
	public static final String COMMAND_DONE = "done";

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
	public static final String FINALISE_ATT_INDEX = "slotIndex";

	public static final String CLEAR_ATT_DONE = "clearDone";
	
	public static final String TASK_TYPE_FLOATING = "floating";
	public static final String TASK_TYPE_TIMED = "timed";
	public static final String TASK_TYPE_DEADLINE = "deadline";
	public static final String TASK_TYPE_UNTIMED = "untimed";

	public static final String HELP_INSTRUCTIONS = "Refer to the help window.";
	public static final String HELP_GENERAL = "Available Commands\n"
			+ "add:       Adds a new task\n"
			+ "edit:       Modifies a specified task\n"
			+ "done:     Marks a specified task as done\n"
			+ "finalise:  Confirms the timing of a specified floating task\n"
			+ "sort:       Organises the tasks by date order\n"
			+ "delete:   Removes a specified task\n"
			+ "clear:      Removes all tasks\n"
			+ "display:  Shows all tasks\n"
			+ "search:   Shows the task(s) matching the keyword(s)\n"
			+ "goto:      Jumps to the specified page\n"
			+ "undo:     Reverses the previous action\n"
			+ "redo:      Reverses undo\n"
			+ "help:       Brings up the help guide\n"
			+ "help <command>: Explains the command in detail";
	public static final String HELP_ADD = "Adding a Task\n"
			+ "Adds a new task into the database. You may add a timed task, untimed task, floating task or deadline task.\n\n"
			+ "Command format: \n"
			+ "add Perform April Fool's Prank in Office on 01/04 at 1200 #forfun: Adds a new timed task with the description 'Perform April Fool's Prank in Office' from 1200 to 1300 on 1st April this year.\n"
			+ "add Save Princess Peach by 22/12/2012: Adds a new deadline task with the description 'Save Princess Peach' before 2359 on 22/12/2012\n"
			+ "add Meet Boss at 11am or 12pm: Adds a new floating task with the description 'Meet Boss' from 11am - 12pm or from 12pm - 1pm today.";
	public static final String HELP_DISPLAY = "Displaying Task(s)\n"
			+ "Displays all tasks in the database. \n\n" 
			+ "Command format: \n"
			+ "1. display: Default case. Shows all undone task(s)\n"
			+ "2. display all: Shows all task(s)\n"
			+ "3. display <date>: Shows task(s) for the date\n"
			+ "Dates available: today, tomorrow, 13/10\n"
			+ "4. display <type>: Shows task(s) corresponding to the specified type\n"
			+ "Types available: untimed, deadline, timed, floating, overdue, done(not implemented yet)";
	public static final String HELP_HELP = "Help\n"
			+ "Displays help for a specific command. Includes the description of the command as well as examples.\n\n"
			+ "Command format: \n"
			+ "help: Provides a brief description of each command\n"
			+ "help add: Provides a detailed description of the 'add' command and some sample usages";
	public static final String HELP_SORT = "Sorting Task(s):\n"
			+ "Sorts all the tasks in the database based on the dates of the tasks, with the earliest one listed first.\n\n"
			+ "Command format: \n" + "sort: Sorts the database";
	public static final String HELP_DELETE = "Deleting a Task\n"
			+ "Deletes a task specified by the index of the task.\n\n"
			+ "Command format: \n"
			+ "delete 1: Deletes the task that corresponds to index 1.";
	public static final String HELP_EDIT = "Editing a Task\n"
			+ "Edits the task specified by the index of the task.\n\n"
			+ "Command format:\n"
			+ "edit 1 Awesome Task!: Edits name of the task that corresponds to index 1 to 'Awesome Task!'.\n"
			+ "edit 2 by 12:00 pm 30/10/2013: Changes the task that corresponds to index 2 to a deadline task.";
	public static final String HELP_CLEAR = "Clearing Task(s)\n"
			+ "Clears the tasks in the database.\n\n" +
			"Command format:\n"
			+ "clear: Clears all the tasks in the database.\n"
			+ "clear done: Clears all the completed tasks in the database.";
	public static final String HELP_UNDO = "Undoing\n"
			+ "Undo the most recent action. Multiple undos are allowed.\n\n"
			+ "Command format:\n" + "undo: Undo the most recent change.";
	public static final String HELP_REDO = "Redoing\n"
			+ "Reverses the last undo made. Multiple redos are allowed.\n\n"
			+ "Command format:\n" + "redo: Reverses undo.";
	public static final String HELP_DONE = "Marking Task as Done\n"
			+ "Marks a completed task as done. \n\n" + "Command format: \n"
			+ "done 2: Mark the task with id 2 as done.";
	public static final String HELP_FINALISE = "Finalising Timing For a Floating Task\n"
			+ "Finalise the timing of a floating task.\n\n"
			+ "Command format:\n"
			+ "finalise 1 2: Set the slot 2 as the finalised timing for the task that corresponds to index 1.";
	public static final String HELP_SEARCH = "Searching For Task(s)\n"
			+ "Searches tasks based on the keyword input.\n\n"
			+ "Command format:\n"
			+ "search boss: Searches and displays all tasks with name containing the word boss\n"
			+ "search #homework: Displays all tasks with the tag #homework\n"
			+ "search 1/1/2014: Displays all tasks on 1/1/2014";
	public static final String HELP_EXIT = "Exiting\n"
			+ "Exits application.\n\n" + "Command format:\n"
			+ "exit: Quits applicatoin.";

	public static final int SC_SUCCESS = 10;
	public static final int SC_SUCCESS_TASK_OVERDUE = 11;
	public static final int SC_SUCCESS_CLEAR_DONE = 12;
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
	public static final int SC_REDO_NO_PRIOR_STATE_ERROR = 110;
	public static final int SC_INVALID_PAGE_INDEX = 120;

	public static DateTimeFormatter fullDateTimeFormat = DateTimeFormat.forPattern("h:mm a 'on' E, d/M/YY");
	public static DateTimeFormatter dateOnlyFormat = DateTimeFormat.forPattern("d MMM',' EE");
	public static final String DATE_TIME_FORMAT = "d/M/yy h:mm a";
	
	public static final String MODE_TODAY = "Today";
	public static final String MODE_TOMORROW = "Tomorrow";
	public static final String MODE_DEADLINE = "Deadlines";
	public static final String MODE_TIMED = "Timed Tasks";
	public static final String MODE_FLOATING = "Floating Tasks";
	public static final String MODE_UNTIMED = "Untimed Tasks";
	public static final String MODE_ALL = "All Tasks";
	public static final String MODE_SEARCH = "Search Result";
	public static final String MODE_OVERDUE = "Overdue Tasks";
	public static final String MODE_TODO = "Undone Tasks";
	
	public static final int DEFAULT_PAGE_NUMBER = 1;
}
