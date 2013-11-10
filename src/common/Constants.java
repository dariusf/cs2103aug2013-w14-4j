package common;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Constants {
		
	public static final String APP_NAME = "Basket";
	public static final String DEFAULT_FILENAME = "default.txt";

	public static final String WELCOME_PAGE_DISPLAY_COMMAND = "display";

    public static final int INVALID_INDEX = -1;

    public static final String MSG_AVAILABLE_COMMANDS = "Commands available: add|edit|display|sort|search\nfinalise|help|goto|undo|redo|clear|done|exit";
	public static final String MSG_SHOULD_NOT_HAPPEN = "%1$s error: Something has gone wrong; please contact the developers";
	public static final String MSG_DEFAULT = "default";
	public static final String MSG_MARK_AS_DONE = "mark as done";
	
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
	public static final String COMMAND_INVALID = "invalid";
	
	public static final String FEEDBACK_SUCCESS_ADD = "Task added!";
	public static final String FEEDBACK_SUCCESS_EDIT = "Task edited!";
	public static final String FEEDBACK_SUCCESS_DISPLAY = "Here are your tasks!";
	public static final String FEEDBACK_SUCCESS_DELETE = "Task deleted!";
	public static final String FEEDBACK_SUCCESS_CLEAR = "Tasks cleared!";
	public static final String FEEDBACK_SUCCESS_SORT = "Tasks sorted!";
	public static final String FEEDBACK_SUCCESS_SEARCH = "Here are your search results!";
	public static final String FEEDBACK_SUCCESS_UNDO = "Undo successful!";
	public static final String FEEDBACK_SUCCESS_REDO = "Redo successful!";
	public static final String FEEDBACK_SUCCESS_FINALISE = "Task finalised!";
	public static final String FEEDBACK_SUCCESS_HELP = "Refer to the help window.";
	public static final String FEEDBACK_SUCCESS_DONE = "Task marked as done!";
	public static final String FEEDBACK_SUCCESS_GOTO = "Page %1$s";
	public static final String FEEDBACK_SUCCESS_EXIT = "Saving...";
	public static final String FEEDBACK_SUCCESS_OVERDUE_ADD = "Task added!\nIt's overdue, though.";
	public static final String FEEDBACK_SUCCESS_OVERDUE_EDIT = "Task edited!\nIt's overdue, though.";
	public static final String FEEDBACK_SUCCESS_OVERDUE_FINALISE = "Task finalised!\nIt's overdue, though.";
	
	public static final String FEEDBACK_ERROR_EMPTY_DESCRIPTION = "Error: No task description was given!";
	public static final String FEEDBACK_ERROR_INTEGER_OUT_OF_BOUNDS = "Error: That's not a valid task index!";
	public static final String FEEDBACK_ERROR_INTEGER_OUT_OF_BOUNDS_TIME = "Error: That's not a valid timeslot index!";
	public static final String FEEDBACK_ERROR_NO_INDEX_INDICATED = "Error: You need to specify a task index!";
	public static final String FEEDBACK_ERROR_NO_TIME_INDICATED = "Error: That's not a valid time!";
	public static final String FEEDBACK_ERROR_NO_TASK = "Error: You have no tasks to %1$s!";
	public static final String FEEDBACK_ERROR_NO_TASK_DISPLAY = "Error: You have no tasks!";
	public static final String FEEDBACK_ERROR_NO_KEYWORD_SEARCH = "Error: No search description was given!";
	public static final String FEEDBACK_ERROR_NO_PRIOR_STATE = "Error: There's nothing to %1$s!";
	public static final String FEEDBACK_ERROR_FINALISE_TYPE_MISMATCH = "Error: That's not a tentative task!";
	public static final String FEEDBACK_ERROR_INVALID_PAGE_NUMBER = "Error: %1$s is not a valid page!";
	public static final String FEEDBACK_ERROR_INVALID_COMMAND = "Error: That's not valid command!";
	public static final String FEEDBACK_ERROR_NO_COMMAND = "Error: No command entered!";
	public static final String FEEDBACK_ERROR_INVALID_PAGE_INDEX = "Error: That's not a vaild page index!";
	public static final String FEEDBACK_ERROR_INVALID_DATE = "Error: That's not a valid date!";
	public static final String FEEDBACK_ERROR_UNRECOGNISED_COMMAND = "Error: Command is not recognised!";
	public static final String FEEDBACK_ERROR_TOO_FEW_ARGUMENTS = "Error: Command is not in the right format!";
	public static final String FEEDBACK_ERROR_INVALID_SEARCH_PARAMETERS = "Error: Search parameters are invalid!";
	
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
			+ "*F11*: Tentative tasks\n\n"
			+ "_Available commands_\n"
			+ "add, edit, display, sort, search, finalise, help, goto, undo, redo, clear, done, exit\n\n"
			+ "Type 'help', followed by any of the above, to see usage tips and examples.\n"
			;
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
			+ "This command displays all saved tasks. \n\n"
			+ "Command format:\n"
			+ "- Start with the keyword 'display'.\n"
			+ "- Pick a display mode. Options are 'today', 'tomorrow', 'deadline', 'timed', 'tentative', 'untimed', 'all', 'overdue', 'todo', and 'done'.\n"
			+ "- Alternatively, specify a date to display task(s) for that day.\n"
			+ "- Undone tasks (todo) will be shown by default.\n\n"
			+ "This command is very similar in usage to the 'clear' command.\n\n"
			+ "Examples:\n"
			+ "- display all\n" 
			+ "- display 25 Oct\n"
			+ "- display overdue";
	public static final String HELP_HELP = "_help_\n"
			+ "Getting meta, are we?\n\n"
			+ "This command displays help for a specific command. The description of the command, as well as examples of its usage, will be shown.\n\n"
			+ "Command format:\n"
			+ "- Simply typing 'help' will show a dialog with a listing of hotkeys and display modes, and some general tips.\n"
			+ "- Follow that up with the name of a particular help to get advice related to that command.\n\n"
			+ "Examples:\n"
			+ "- help\n"
			+ "- help add\n";
	public static final String HELP_SORT = "_sort_\n"
			+ "This command will sort the tasks in the current display list in order of date and type.\n\n"
			+ "Command format:\n"
			+ "- Simply type 'sort'.\n\n"
			+ "Examples:\n"
			+ "- sort";
	public static final String HELP_DELETE = "_delete_\n"
			+ "This command will delete the task specified. You may use 'undo' to reverse an erroneous deletion.\n\n"
			+ "Command format:\n"
			+ "- Start with the keyword 'delete', then specify the index of the task you want deleted.\n\n"
			+ "Examples:\n" 
			+ "- delete 1";
	public static final String HELP_EDIT = "_edit_\n"
			+ "This command allows you to change the attributes of a task.\n\n"
			+ "Command format:\n"
			+ "- Start with the keyword 'edit', followed by the task's index.\n"
			+ "- If you want to edit a timeslot on a task, you may also specify the timeslot index.\n"
			+ "- Specify the changes you want to make, whether to task name, deadline, or intervals.\n\n"
			+ "This command is very similar in usage to the 'add' command. Changes specified will be added on to each task.\n\n"
			+ "Examples:\n" 
			+ "- edit 1 Dental appointment\n"
			+ "- edit 2 by 12:00 pm 30/10\n"
			+ "- edit 1 on 31 Oct at 9am to 4 Apr 5pm\n"
			+ "- edit 3 from 2000 to 2130\n"
			+ "- edit 4 Get hair cut on 31/10 or 01/11";
	public static final String HELP_CLEAR = "_clear_\n"
			+ "The 'clear' command allows you to remove tasks en masse.\n\n"
			+ "Command format:\n"
			+ "- Start with the keyword 'clear'.\n"
			+ "- Pick a clear mode. Options are 'today', 'tomorrow', 'deadline', 'timed', 'tentative', 'untimed', 'all', 'overdue', 'todo', and 'done'.\n"
			+ "- Alternatively, specify a date to display task(s) for that day.\n"
			+ "- By default, all tasks will be cleared.\n\n"
			+ "This command is similar in usage to the display command. Don't worry if you deleted everything by mistake; you can always undo things with the 'undo' command.\n\n"
			+ "Examples:\n" 
			+ "- clear done\n" 
			+ "- clear today\n"
			+ "- clear overdue";
	public static final String HELP_UNDO = "_undo_\n"
			+ "This command reverts the last operation on the task list. You can undo indefinitely, all the way up to the point when you started the application.\n\n"
			+ "Command format:\n"
			+ "- Simply type 'undo'.\n\n"
			+ "Examples:\n"
			+ "- undo";
	public static final String HELP_REDO = "_undo_\n"
			+ "This command reverts the last last 'undo' operation. You can redo indefinitely.\n\n"
			+ "Command format:\n"
			+ "- Simply type 'redo'.\n\n"
			+ "Examples:\n"
			+ "- redo";
	public static final String HELP_DONE = "_done_\n"
			+ "This command marks a task as done. A done task will not show up in the display list; to find it, use 'display done'.\n\n"
			+ "Command format:\n"
			+ "- Start with the keyword 'done'."
			+ "- Specify the index of the task to mark as completed.\n\n"
			+ "Example:\n"
			+ "- done 2";
	public static final String HELP_FINALISE = "_finalise_\n"
			+ "Tentative tasks may have multiple timeslots. This command will finalise a particular timeslot, removing the others and changing the task into a normal timed one.\n\n"
			+ "Command format:\n"
			+ "- Start with the keyword 'finalise'.\n"
			+ "- Specify the index of the task you want to finalise.\n"
			+ "- Specify the index of the timeslot to choose.\n\n"
			+ "Example:\n"
			+ "- finalise 1 2";
	public static final String HELP_SEARCH = "_search_\n"
			+ "This command will search for tasks containing certain keywords or hashtags. You can't search by date with this command; use the 'display' command for that.\n\n"
			+ "Command format:\n"
			+ "- Start with the keyword 'search' followed by the words and/or hashtags to filter by.\n\n"
			+ "Examples:\n"
			+ "- search boss #important\n" 
			+ "- search #important 2103 homework";
	public static final String HELP_EXIT = "_exit_\n"
			+ "This command will exit the application.\n\n"
			+ "Command format:\n" 
			+ "- Type 'exit'. There's not much more to it.\n\n"
			+ "Examples:\n" 
			+ "- exit";
	public static final String HELP_GOTO = "_goto_\n"
			+ "This command will bring you to a specified page. You may also use the Page Up and Page Down keys to go to the previous or next pages.\n\n"
			+ "Command format:\n"
			+ "- Start with the keyword 'goto'.\n"
			+ "- Specify the page number to jump to.\n\n"
			+ "Example:\n" 
			+ "- goto 2";

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
		"- ", "� "
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

    public static final boolean PARSER_DEBUG_PRINT_LEXER_TOKENS = false;
    public static final boolean PARSER_DEBUG_PRINT_MATCHED_COMMAND_TYPE = false;
    public static final boolean PARSER_DEBUG_PRINT_PARSED_COMMAND = true;
    public static final int PARSER_FUZZY_MATCH_THRESHOLD = 3;
    
    public static final String PARSER_ASSERTION_ERROR_CANNOT_PARSE_DATE_STRING = "Cannot parse date string";
    public static final String PARSER_ASSERTION_ERROR_DATE_TOKEN_CONTENTS_DID_NOT_MATCH = "Date token contents did not match anything; possibly a regex bug in either DateToken or lexer";
    public static final String PARSER_ASSERTION_ERROR_DATETOKEN_LOGIC = "Error in DateToken logic";
    public static final String PARSER_ASSERTION_ERROR_DAY_1_31 = "Day %d should be within [1, 31]";
    public static final String PARSER_ASSERTION_ERROR_INVALID_DURATION = "Invalid duration %s; possibly a regex bug in either DateToken or lexer";
    public static final String PARSER_ASSERTION_ERROR_INVALID_MONTH = "Invalid month";
    public static final String PARSER_ASSERTION_ERROR_INVALID_MONTH_OF_YEAR = "Invalid month of year %d";
    public static final String PARSER_ASSERTION_ERROR_MONTH_1_12 = "Month %d should be within [1, 12]";
    public static final String PARSER_ASSERTION_ERROR_NO_SUCH_COMMAND = "No such command type";
    public static final String PARSER_ASSERTION_ERROR_NO_TOKENS_REMAINING = "No tokens remaining";
    public static final String PARSER_ASSERTION_ERROR_TIMETOKEN_CONTENTS_DID_NOT_MATCH_ANYTHING = "Time token contents did not match anything; possibly a regex bug in either DateToken or lexer";
    public static final String PARSER_ASSERTION_ERROR_YEAR_4_DIGITS = "Year %d should only be 4 digits long";
    
    public static final String PARSER_DATE_APR = "apr";
    public static final String PARSER_DATE_APRIL = "april";
    public static final String PARSER_DATE_AUG = "aug";
    public static final String PARSER_DATE_AUGUST = "august";
    public static final String PARSER_DATE_CHRISTMAS = "christmas";
    public static final String PARSER_DATE_DEC = "dec";
    public static final String PARSER_DATE_DECEMBER = "december";
    public static final String PARSER_DATE_FEB = "feb";
    public static final String PARSER_DATE_FEBRUARY = "february";
    public static final String PARSER_DATE_FORTNIGHT = "fortnight";
    public static final String PARSER_DATE_FRI = "fri";
    public static final String PARSER_DATE_FRIDAY = "friday";
    public static final String PARSER_DATE_HALLOWEEN = "halloween";
    public static final String PARSER_DATE_JAN = "jan";
    public static final String PARSER_DATE_JANUARY = "january";
    public static final String PARSER_DATE_JUL = "jul";
    public static final String PARSER_DATE_JULY = "july";
    public static final String PARSER_DATE_JUN = "jun";
    public static final String PARSER_DATE_JUNE = "june";
    public static final String PARSER_DATE_MAR = "mar";
    public static final String PARSER_DATE_MARCH = "march";
    public static final String PARSER_DATE_MAY = "may";
    public static final String PARSER_DATE_MON = "mon";
    public static final String PARSER_DATE_MONDAY = "monday";
    public static final String PARSER_DATE_MONTH = "month";
    public static final String PARSER_DATE_NOV = "nov";
    public static final String PARSER_DATE_NOVEMBER = "november";
    public static final String PARSER_DATE_OCT = "oct";
    public static final String PARSER_DATE_OCTOBER = "october";
    public static final String PARSER_DATE_QUALIFIER_LAST = "last";
    public static final String PARSER_DATE_QUALIFIER_NEXT = "next";
    public static final String PARSER_DATE_SAT = "sat";
    public static final String PARSER_DATE_SATURDAY = "saturday";
    public static final String PARSER_DATE_SEP = "sep";
    public static final String PARSER_DATE_SEPTEMBER = "september";
    public static final String PARSER_DATE_SUN = "sun";
    public static final String PARSER_DATE_SUNDAY = "sunday";
    public static final String PARSER_DATE_THU = "thu";
    public static final String PARSER_DATE_THURS = "thurs";
    public static final String PARSER_DATE_THURSDAY = "thursday";
    public static final String PARSER_DATE_TMR = "tmr";
    public static final String PARSER_DATE_TMRW = "tmrw";
    public static final String PARSER_DATE_TODAY = "today";
    public static final String PARSER_DATE_TOMORROW = "tomorrow";
    public static final String PARSER_DATE_TONIGHT = "tonight";
    public static final String PARSER_DATE_TUE = "tue";
    public static final String PARSER_DATE_TUES = "tues";
    public static final String PARSER_DATE_TUESDAY = "tuesday";
    public static final String PARSER_DATE_WED = "wed";
    public static final String PARSER_DATE_WEDNESDAY = "wednesday";
    public static final String PARSER_DATE_WEEK = "week";
    public static final String PARSER_DATE_YEAR = "year";
    public static final String PARSER_DATE_YESTERDAY = "yesterday";
    public static final String PARSER_DATESTRING = "%d/%d/%s";
    
    public static final String PARSER_EXCEPTION_INVALID_DAY_D_OF_MONTH = "Invalid day %d of month %d";
    
    public static final String PARSER_KEYWORD_BEFORE = "before";
    public static final String PARSER_KEYWORD_BY = "by";
    public static final String PARSER_KEYWORD_OR = "or";
    public static final String PARSER_KEYWORD_TILL = "till";
    public static final String PARSER_KEYWORD_TO = "to";
    public static final String PARSER_KEYWORD_UNTIL = "until";
    
    public static final String PARSER_LEXER_INPUT_ENCODING = "UTF-8";
    
    public static final String PARSER_LOG_ERROR_GETTING_NEXT_TOKEN = "Error getting next token!";
    public static final String PARSER_LOG_EXACT = "exact";
    public static final String PARSER_LOG_FUZZY = "fuzzy";
    public static final String PARSER_LOG_LEXER_TOKENS = "\nLexer tokens:";
    public static final String PARSER_LOG_MATCHED_COMMAND = "Command (%s): %s";
    
    public static final String PARSER_REGEX_ALIAS_DATE = "(yesterday|today|tonight|tomorrow|tmrw|tmr|halloween|christmas)";
    public static final String PARSER_REGEX_ALIAS_TIME = "(midnight|noon|morning|afternoon|evening)";
    public static final String PARSER_REGEX_MIXED_DATE = "((0?[1-9]|[12][0-9]|3[01])[ ]*(january|february|march|april|may|june|july|august|september|october|november|december|jan|feb|mar|apr|jun|jul|aug|sep|oct|nov|dec)[ ]*((19|20)?[0-9][0-9])?)";
    public static final String PARSER_REGEX_RELATIVE_DATE = "(next[ ]+)(week|year|month|fortnight)";
    public static final String PARSER_REGEX_RELATIVE_DAY_DATE = "((this|next|last)[ ]+)?(((mon|tues|wednes|thurs|fri|satur|sun)day)|mon|tues|tue|wed|thurs|thu|fri|sat|sun)";
    public static final String PARSER_REGEX_STANDARD_DATE = "(0?[1-9]|[12][0-9]|3[01])[-/](1[012]|0?[1-9])([-/]((19|20)?[0-9][0-9]))?";
    public static final String PARSER_REGEX_TIME = "(((1[012]|[1-9])([:.]([0-5][0-9]))?[ ]*(am|pm))|((2[0-3]|[01]?[0-9])[:.]?([0-5][0-9])))";
    
    public static final String PARSER_TIME_AFTERNOON = "afternoon";
    public static final String PARSER_TIME_EVENING = "evening";
    public static final String PARSER_TIME_MIDNIGHT = "midnight";
    public static final String PARSER_TIME_MORNING = "morning";
    public static final String PARSER_TIME_NOON = "noon";
    public static final String PARSER_TIME_PERIOD_AM = "am";
    public static final String PARSER_TIME_PERIOD_PM = "pm";
    public static final String PARSER_TIMESTRING = "%d:%s %s";
    
    public static final String PARSER_TOKEN_PREFIX_DATETOKEN = "Date ";
    public static final String PARSER_TOKEN_PREFIX_KEYWORD = "Keyword ";
    public static final String PARSER_TOKEN_PREFIX_TAG = "Tag ";
    public static final String PARSER_TOKEN_PREFIX_TIME = "Time ";
    public static final String PARSER_TOKEN_PREFIX_WORD = "Word ";

	// Prevents this class from being instantiated
	private Constants() {}

}
