package logic;
import java.util.ArrayList;
import org.joda.time.DateTime;
import common.CommandType;
import common.Constants;
import common.DisplayMode;
import common.ClearMode;
import common.InvalidCommandReason;

public class Command {
	private CommandType commandType = null;
	
	private String description = "";
	private DateTime deadline = null;
	private ArrayList<Interval> intervals = new ArrayList<>();
	private ArrayList<String> tags = new ArrayList<>();

	private int taskIndex = -1;
	private int pageIndex = -1;
	private int timeslotIndex = -1;
	
	private ClearMode clearMode;
	private CommandType helpCommand;
	private String searchString = "";
	private InvalidCommandReason invalidCommandReason;
	private DisplayMode displayMode;
	private DateTime displayDateTime = null;
	private DateTime clearDateTime = null;
		
	public Command(CommandType type){
		commandType = type;
	}

	public CommandType getCommandType(){
		return commandType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String text) {
		this.description = text;
	}

	public DateTime getDeadline() {
		return deadline;
	}

	public void setDeadline(DateTime deadline) {
		this.deadline = deadline;
	}

	public ArrayList<Interval> getIntervals() {
		return intervals;
	}

	public void setIntervals(ArrayList<Interval> intervals) {
		this.intervals = intervals;
	}
	
	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
	
	public int getTaskIndex() {
		return taskIndex;
	}

	public void setTaskIndex(int taskIndex) {
		this.taskIndex = taskIndex;
	}

	public int getTimeslotIndex() {
		return timeslotIndex;
	}

	public void setTimeslotIndex(int timeslotIndex) {
		this.timeslotIndex = timeslotIndex;
	}

	public DateTime getDisplayDateTime() {
		return displayDateTime;
	}

	public void setDisplayDateTime(DateTime searchDateTime) {
		this.displayDateTime = searchDateTime;
	}
		
	public CommandType getHelpCommand() {
		return helpCommand;
	}

	public void setHelpCommand(CommandType helpCommand) {
		this.helpCommand = helpCommand;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public InvalidCommandReason getInvalidCommandReason() {
		return invalidCommandReason;
	}

	public void setInvalidCommandReason(InvalidCommandReason invalidCommandReason) {
		this.invalidCommandReason = invalidCommandReason;
	}
	
	// TODO: might want to move to a higher level,
	// this is just here for now to illustrate how these
	// fields alone can define the task type clearly
	public String getTaskType() {
		assert commandType == CommandType.ADD || commandType == CommandType.EDIT;
		if (deadline != null) {
			return Constants.TASK_TYPE_DEADLINE;
		}
		else if (intervals.size() == 0) {
			return Constants.TASK_TYPE_UNTIMED;
		}
		else if (intervals.size() == 1) {
			return Constants.TASK_TYPE_TIMED;
		}
		else {
			return Constants.TASK_TYPE_FLOATING;
		}
	}
	
	public boolean isEmptyAddCommand(){
		assert (this.commandType == CommandType.ADD);
		return this.description.isEmpty() && this.deadline == null && this.tags.isEmpty() && this.intervals.isEmpty();
	}

	public DisplayMode getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(DisplayMode displayMode) {
		this.displayMode = displayMode;
	}

	public ClearMode getClearMode() {
		return clearMode;
	}

	public void setClearMode(ClearMode clearMode) {
		this.clearMode = clearMode;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public DateTime getClearDateTime() {
		return clearDateTime;
	}

	public void setClearDateTime(DateTime clearDateTime) {
		this.clearDateTime = clearDateTime;
	}

	@Override
	public String toString() {
		return "Command [commandType=" + commandType + ", description="
				+ description + ", deadline=" + deadline + ", intervals="
				+ intervals + ", tags=" + tags + ", taskIndex=" + taskIndex
				+ ", pageIndex=" + pageIndex + ", timeslotIndex="
				+ timeslotIndex + ", clearMode=" + clearMode + ", helpCommand="
				+ helpCommand + ", searchString=" + searchString
				+ ", invalidCommandReason=" + invalidCommandReason
				+ ", displayMode=" + displayMode + ", displayDateTime="
				+ displayDateTime + ", clearDateTime=" + clearDateTime + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Command other = (Command) obj;
		if (clearDateTime == null) {
			if (other.clearDateTime != null)
				return false;
		} else if (!clearDateTime.equals(other.clearDateTime))
			return false;
		if (clearMode != other.clearMode)
			return false;
		if (commandType != other.commandType)
			return false;
		if (deadline == null) {
			if (other.deadline != null)
				return false;
		} else if (!deadline.equals(other.deadline))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (displayDateTime == null) {
			if (other.displayDateTime != null)
				return false;
		} else if (!displayDateTime.equals(other.displayDateTime))
			return false;
		if (displayMode != other.displayMode)
			return false;
		if (helpCommand != other.helpCommand)
			return false;
		if (intervals == null) {
			if (other.intervals != null)
				return false;
		} else if (!intervals.equals(other.intervals))
			return false;
		if (invalidCommandReason != other.invalidCommandReason)
			return false;
		if (pageIndex != other.pageIndex)
			return false;
		if (searchString == null) {
			if (other.searchString != null)
				return false;
		} else if (!searchString.equals(other.searchString))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		if (taskIndex != other.taskIndex)
			return false;
		if (timeslotIndex != other.timeslotIndex)
			return false;
		return true;
	}
	
}
