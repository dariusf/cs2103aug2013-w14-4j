package common;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Constants {
	public static final String APP_NAME = "Basket";
	public static final String DEFAULT_FILENAME = "default.txt";

	public static final String WELCOME_PAGE_DISPLAY = "display";

	public static final String MSG_UNRECOGNISED_COMMAND = "Unrecognized command type";
	public static final String MSG_WELCOME = "Welcome to " + APP_NAME + "! \nType 'help' if you are unsure of what to do.";
	public static final String MSG_AVAILABLE_COMMANDS = "Commands available: add|edit|display|sort|search\nfinalise|help|goto|undo|redo|clear|done|exit";

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

	public static final String HELP_INSTRUCTIONS = "Refer to the help window.";
	public static final String HELP_GENERAL = "_Hotkeys_\n"
			+ "*Shift+F1*: Minimises/restores Basket\n"
			+ "*Page Up*: Previous page\n"
			+ "*Page Down*: Next page\n"
			+ "*F1*: Opens help window\n"
			+ "*Esc*: Closes help window\n"
			+ "*F12*: Some jokes\n\n"
			+ "_Display modes_\n"
			+ "*F2*: Uncompleted tasks\n"
			+ "*F3*: Today's tasks\n"
			+ "*F4*: Tomorrow's tasks\n"
			+ "*F5*: All tasks\n"
			+ "*F6*: Completed tasks\n"
			+ "*F7*: Overdue tasks\n"
			+ "*F8*: Untimed tasks\n"
			+ "*F9*: Deadlines\n"
			+ "*F10*: Timed tasks\n"
			+ "*F11*: Tentative tasks\n";
	public static final String HELP_ADD = "_add_\n"
			+ "This command is used to add tasks to Basket.\n\n"
			+ "Tasks may be timed, untimed, tentative, or have a deadline.\n\n"
			+ "Command format:\n"
			+ "- Start with the keyword 'add', followed by a description.\n"
			+ "- Use 'on', 'at', or 'from' to specify a time interval.\n"
			+ "- Time intervals are indicated with the words 'to' or 'until'.\n"
			+ "- Use 'or' after that to specify other timeslots.\n"
			+ "- Alternatively, use 'by' to specify a deadline.\n"
			+ "- Use # to add a hashtag\n\n" 
			+ "Examples:\n"
			+ "- add Perform April Fool's prank on 1 Apr at 1600 #forfun\n"
			+ "- add Save Princess Peach by 22/12/2012\n"
			+ "- add Meet boss at 11:00 am today or 12:00 pm tomorrow\n"
			+ "- Take afternoon nap";
	public static final String HELP_DISPLAY = "_display_\n"
			+ "Displays all saved tasks. \n\n"
			+ "Command format:\n"
			+ "- Start with the keyword 'display'.\n"
			+ "- Pick a display mode. Options are 'today', 'tomorrow', 'deadline', 'timed', 'tentative', 'untimed', 'all', 'overdue', 'todo', and 'done'.\n"
			+ "- Alternatively, specify a date to display task(s) for that day.\n\n"
			+ "- Undone tasks (todo) will be shown by default.\n"
			+ "Examples:\n" 
			+ "- display all\n" 
			+ "- display 25 Oct\n"
			+ "- display overdue";
	public static final String HELP_HELP = "Help\n"
			+ "Getting meta, are we?\n\n"
			+ "Displays help for a specific command. Includes the description of the command as well as examples.\n\n"
			+ "Command format:\n"
			+ "help: Provides a brief description of each command\n"
			+ "help add: Provides a detailed description of the 'add' command and some sample usages";
	public static final String HELP_SORT = "Sorting Task(s)\n"
			+ "Performing this action will result in tasks sorted by date order.\n"
			+ "The task with the earliest date will be listed first.\n\n"
			+ "Command format:\n" 
			+ "'sort'";
	public static final String HELP_DELETE = "Deleting a Task\n"
			+ "You may delete a specified task.\n"
			+ "Use 'undo' if you wish to reverse the deletion\n\n"
			+ "Command format:\n"
			+ "Start with the keyword 'delete' followed by the task's index\n\n"
			+ "Example:\n" 
			+ "delete 1";
	public static final String HELP_EDIT = "Editing a Task\n"
			+ "You may edit the name or task type of a specified task.\n\n"
			+ "Command format:\n"
			+ "* Start with the keyword 'edit' followed by the task's index\n"
			+ "* You may choose to edit the name or task type or both\n"
			+ "  ** To edit the name of task, simply type your new task's name\n"
			+ "  ** To edit the task type, use keywords such as 'by', 'from', 'on', 'or' to denote the new task type\n\n"
			+ "Examples:\n" 
			+ "edit 1 Awesome Task!\n"
			+ "edit 2 by 12:00 pm 30/10/2013\n"
			+ "edit 1 on 31/10 9am to 04/11 5pm\n"
			+ "edit 3 from 2000 to 2130\n"
			+ "edit 4 Get hair cut on 31/10 or 01/11";
	public static final String HELP_CLEAR = "Clearing Task(s)\n"
			+ "Performing this action will remove all/done tasks.\n\n"
			+ "Command format:\n" 
			+ "* Start with the keyword 'clear'. 'clear' alone will remove all tasks\n"
			+ "* Choose a 'type' of task to clear\n"
			+ "  ** Types available: untimed, deadline, timed, tentative, overdue, done\n"
			+ "* Or choose a 'date' to clear all task(s) with the date\n"
			+ "  ** Dates available: today, tomorrow, 13/10\n\n"
			+ "Examples:\n" 
			+ "clear done\n" 
			+ "clear today\n"
			+ "clear overdue";
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
			+ "Example:\n" 
			+ "done 2";
	public static final String HELP_FINALISE = "Finalising Timing For a Tentative Task\n"
			+ "Perform this action when you have decided on the timeslot for a particular tentative task.\n\n"
			+ "Command format:\n"
			+ "* Start with the keyword 'finalise' followed by the task's index\n"
			+ "* Next set the confirmed slot as the finalised timing by using its index\n\n"
			+ "Example:\n" 
			+ "finalise 1 2";
	public static final String HELP_SEARCH = "Searching For Task(s)\n"
			+ "Perform this action if you wish to filter your tasks by word(s)/hashtag(s).\n\n"
			+ "Command format:\n"
			+ "Start with the keyword 'search' followed by the word(s) to filter by\n\n"
			+ "Examples:\n" 
			+ "search boss\n" 
			+ "search #homework\n"
			+ "search #important #homework";
	public static final String HELP_EXIT = "Exiting\n"
			+ "Perform this action if you wish to close the application.\n\n"
			+ "Command format:\n" 
			+ "'exit'";
	public static final String HELP_GOTO = "Navigating using 'goto'\n"
			+ "Navigates to the page specified. You may also use the hotkeys page up and page down to go to the previous or next page.\n\n"
			+ "Command format:\n"
			+ "Start with the keyword 'goto' followed by the page number\n\n"
			+ "Example:\n" 
			+ "goto 2";

	// Please keep success code numbers between 10 and 19.
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

	public static final DateTimeFormatter verboseTimeFormat = DateTimeFormat.forPattern("h:mm a");
	public static final DateTimeFormatter verboseDateFormat = DateTimeFormat.forPattern("E, d MMM YY");
	public static final DateTimeFormatter simpleTimeFormat = DateTimeFormat.forPattern("h a");
	public static final DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("E, d MMM");

	public static final DateTimeFormatter dateOnlyFormat = DateTimeFormat.forPattern("d MMM',' EE");
	public static final String DATE_TIME_FORMAT = "d/M/yy h:mm a";

	public static final String MODE_TODAY = "Today";
	public static final String MODE_TOMORROW = "Tomorrow";
	public static final String MODE_DEADLINE = "Deadlines";
	public static final String MODE_TIMED = "Timed Tasks";
	public static final String MODE_TENTATIVE = "Tentative Tasks";
	public static final String MODE_UNTIMED = "Untimed Tasks";
	public static final String MODE_ALL = "All Tasks";
	public static final String MODE_SEARCH = "Search Results";
	public static final String MODE_OVERDUE = "Overdue Tasks";
	public static final String MODE_TODO = "Todo";
	public static final String MODE_DONE = "Completed Tasks";

	public static final int DEFAULT_PAGE_NUMBER = 1;
	
	public static final String[] RANDOM_JOKES = { "Why do Java Programmers wear glasses?\nBecause they don't see sharp.", "What is the object-oriented way to\nbecome wealthy? Inheritance.", "[\"hip\", \"hip\"]\n(Get it?)", "Is it a boy or a girl?\nTrue", "Why did the programmer quit his job?\nBecause he didn't get arrays.", "Why did the integer drown?\nBecause it couldn't float!"};

	public static final String FORMATTING_ALLOWED_CHARACTERS = "[a-z0-9}{A-Z+><.|# ]";
	public static final String FORMATTING_REGEX_UNDERLINE = "_(" + FORMATTING_ALLOWED_CHARACTERS + "+)_";
	public static final String FORMATTING_REGEX_BOLD = "\\*(" + FORMATTING_ALLOWED_CHARACTERS + "+)\\*";
	public static final String FORMATTING_REGEX_COLOUR1 = "&(" + FORMATTING_ALLOWED_CHARACTERS + "+)&";
	public static final String[] FORMATTING_SUBSTITUTIONS = {
		"- ", "• "
	};

    public static final String CONTEXTUAL_HELP_DESC = "<description>";
    public static final String CONTEXTUAL_HELP_DESC_OPTIONAL = "{description}";
    public static final String CONTEXTUAL_HELP_DEADLINE = "{by}";
    public static final String CONTEXTUAL_HELP_INTERVAL_ALTERNATIVE = "{or}";
    public static final String CONTEXTUAL_HELP_INTERVAL = "{at|on|from..to}";
    public static final String CONTEXTUAL_HELP_HASHTAG = "{#hashtag}";
    public static final String CONTEXTUAL_HELP_TASK_INDEX = "<task index>";
    public static final String CONTEXTUAL_HELP_TIMESLOT_INDEX = "<timeslot index>";
    public static final String CONTEXTUAL_HELP_TIMESLOT_INDEX_OPTIONAL = "{timeslot index}";
    public static final String CONTEXTUAL_HELP_CLEAR_MODE = "{deadline|timed|<date>|done|overdue|...}";
    public static final String CONTEXTUAL_HELP_DISPLAY_MODE = "{done|today|tomorrow|deadline|timed|...}";
    public static final String CONTEXTUAL_HELP_DISPLAY_MODE_TIP = "Type 'help display' to see more options.";
    public static final String CONTEXTUAL_HELP_CLEAR_MODE_TIP = "Type 'help clear' to see more options.";
    public static final String CONTEXTUAL_HELP_SEARCH_TERMS = "{keyword|hashtag}+";
    public static final String CONTEXTUAL_HELP_COMMAND = "{command}";
    public static final String CONTEXTUAL_HELP_PAGE_INDEX = "{page number}";
    public static final String CONTEXTUAL_HELP_SORT = "Tasks will be sorted by date.";
    public static final String CONTEXTUAL_HELP_UNDO = "Undo reverts the last operation on the task list.";
    public static final String CONTEXTUAL_HELP_REDO = "Redo reverts the last undo.";
    public static final String CONTEXTUAL_HELP_EXIT = "Exit quits the application.";
    
}
