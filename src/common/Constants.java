package common;

import java.util.ArrayList;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Constants {
	public static final String APP_NAME = "Basket";
	public static final String DEFAULT_FILENAME = "default.txt";

	public static String WELCOME_MSG = "Welcome to Basket! \nType 'help' if you are unsure of what to do.";
	public static String WELCOME_PAGE_DISPLAY = "display";

	public static final String MSG_UNRECOGNISED_COMMAND = "Unrecognized command type";

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
			+ "clear:      Removes all tasks\n" + "display:  Shows all tasks\n"
			+ "search:   Shows the task(s) matching the keyword(s)\n"
			+ "goto:      Jumps to the specified page\n"
			+ "undo:     Reverses the previous action\n"
			+ "redo:      Reverses undo\n"
			+ "help <command>: Explains the command in detail";
	public static final String HELP_ADD = "Adding a Task\n"
			+ "You may add a timed task, untimed task, floating task or deadline task.\n\n"
			+ "Command format:\n"
			+ "* Start with the keyword 'add'\n"
			+ "* Use 'by' to denote a deadline\n"
			+ "* Use 'on' or 'from' to denote the time the task is supposed to occur\n"
			+ "  ** You can specify a time period by using 'to'\n"
			+ "  ** You can specify multiple choices of timeslots using 'or'\n"
			+ "* Use # to group items by hashtag\n\n" + "Examples:\n"
			+ "add Perform April Fool's Prank on 01/04 at 1200 #forfun\n"
			+ "add Save Princess Peach by 22/12/2012\n"
			+ "add Meet Boss at 11am or 12pm";
	public static final String HELP_DISPLAY = "Displaying Task(s)\n"
			+ "Displays all tasks in the database. \n\n"
			+ "Command format:\n"
			+ "* Start with the keyword 'display'. The default case shows all undone task(s)\n"
			+ "* Use 'all' to display all task(s)\n"
			+ "* Choose a 'date' to display task(s) for the date\n"
			+ "  ** Dates available: today, tomorrow, 13/10\n"
			+ "* Choose a 'type' to display task(s) corresponding to the specified type\n"
			+ "  ** Types available: untimed, deadline, timed, floating, overdue, done\n\n"
			+ "Examples:\n" + "display all\n" + "display 25 Oct\n"
			+ "display overdue";
	public static final String HELP_HELP = "Help\n"
			+ "Displays help for a specific command. Includes the description of the command as well as examples.\n\n"
			+ "Command format:\n"
			+ "help: Provides a brief description of each command\n"
			+ "help add: Provides a detailed description of the 'add' command and some sample usages";
	public static final String HELP_SORT = "Sorting Task(s)\n"
			+ "Performing this action will result in tasks sorted by date order.\n"
			+ "The task with the earliest date will be listed first.\n\n"
			+ "Command format:\n" + "'sort'";
	public static final String HELP_DELETE = "Deleting a Task\n"
			+ "You may delete a specified task.\n"
			+ "Use 'undo' if you wish to reverse the deletion\n\n"
			+ "Command format:\n"
			+ "Start with the keyword 'delete' followed by the task's index\n\n"
			+ "Example:\n" + "delete 1";
	public static final String HELP_EDIT = "Editing a Task\n"
			+ "You may edit the name or task type of a specified task.\n\n"
			+ "Command format:\n"
			+ "* Start with the keyword 'edit' followed by the task's index\n"
			+ "* You may choose to edit the name or task type or both\n"
			+ "  ** To edit the name of task, simply type your new task's name\n"
			+ "  ** To edit the task type, use keywords such as 'by', 'from', 'on', 'or' to denote the new task type\n\n"
			+ "Examples:\n" + "edit 1 Awesome Task!\n"
			+ "edit 2 by 12:00 pm 30/10/2013\n"
			+ "edit 1 on 31/10 9am to 04/11 5pm\n"
			+ "edit 3 from 2000 to 2130\n"
			+ "edit 4 Get hair cut on 31/10 or 01/11";
	public static final String HELP_CLEAR = "Clearing Task(s)\n"
			+ "Performing this action will remove all/done tasks.\n\n"
			+ "Command format:\n" + "* Use 'clear' to remove all tasks\n"
			+ "* Use 'clear done' to remove all the completed tasks";
	public static final String HELP_UNDO = "Undoing\n"
			+ "This reverses the last action performed.\n"
			+ "Multiple undos are supported.\n\n" + "Command format:\n"
			+ "'undo'";
	public static final String HELP_REDO = "Redoing\n"
			+ "This reverses the last 'undo' made.\n"
			+ "Multiple redos are supported.\n\n" + "Command format:\n"
			+ "'redo'";
	public static final String HELP_DONE = "Marking Task as Done\n"
			+ "Marks a completed task as done. \n\n" + "Command format:\n"
			+ "Start with the keyword 'done' followed by the task's index\n\n"
			+ "Example:\n" + "done 2";
	public static final String HELP_FINALISE = "Finalising Timing For a Floating Task\n"
			+ "Perform this action when you have decided on the timeslot for a particular floating task.\n\n"
			+ "Command format:\n"
			+ "* Start with the keyword 'finalise' followed by the task's index\n"
			+ "* Next set the confirmed slot as the finalised timing by using its index\n\n"
			+ "Example:\n" + "finalise 1 2";
	public static final String HELP_SEARCH = "Searching For Task(s)\n"
			+ "Perform this action if you wish to filter your tasks by word(s)/hashtag(s).\n\n"
			+ "Command format:\n"
			+ "Start with the keyword 'search' followed by the word(s) to filter by\n\n"
			+ "Examples:\n" + "search boss\n" + "search #homework\n"
			+ "search #important #homework";
	public static final String HELP_EXIT = "Exiting\n"
			+ "Perform this action if you wish to close the application.\n\n"
			+ "Command format:\n" + "'exit'";

	public static final int SC_SUCCESS = 10;
	public static final int SC_SUCCESS_TASK_OVERDUE = 11;
	public static final int SC_SUCCESS_CLEAR_DONE = 12;
	public static final int SC_INVALID_COMMAND_ERROR = 20;
	public static final int SC_EMPTY_COMMAND_ERROR = 21;
	public static final int SC_UNRECOGNIZED_COMMAND_ERROR = 22;
	public static final int SC_TOO_FEW_ARGUMENTS_ERROR = 23;
	public static final int SC_INVALID_PAGE_INDEX_ERROR = 24;
	public static final int SC_INVALID_SEARCH_PARAMETERS_ERROR = 25;
	public static final int SC_INVALID_TASK_INDEX_ERROR = 26;
	public static final int SC_INVALID_DATE_ERROR = 27;
	public static final int SC_INVALID_TIMESLOT_INDEX_ERROR = 28;
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

	public static DateTimeFormatter fullDateTimeFormat = DateTimeFormat
			.forPattern("h:mm a 'on' E, d/M/YY");
	public static DateTimeFormatter dateOnlyFormat = DateTimeFormat
			.forPattern("d MMM',' EE");
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
	public static final String MODE_TODO = "Todo";
	public static final String MODE_DONE = "Completed Tasks";

	public static final int DEFAULT_PAGE_NUMBER = 1;

	public static final String[] RANDOM_JOKES = { "Why do Java Programmers wear glasses?\nBecause they don't see sharp.", "What is the object-oriented way to\nbecome wealthy? Inheritance.", "[\"hip\", \"hip\"]\n(Get it?)", "Is it a boy or a girl?\nTrue", "Why did the programmer quit his job?\nBecause he didn't get arrays.", "Why did the integer drown?\nBecause it couldn't float!"};
	public static final String MSG_ENTER_COMMAND = "Listening to command...";
}
