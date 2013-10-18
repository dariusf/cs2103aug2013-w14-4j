package logic;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.DateTime;

import common.CommandType;
import common.Constants;
import common.InvalidCommandReason;


public class Command {
	private CommandType commandType = null;
	
	private String description = "";
	private DateTime deadline = null;
	private ArrayList<Interval> intervals = new ArrayList<>();
	private ArrayList<String> tags = new ArrayList<>();

	private int taskIndex = 0;
	private int finaliseIndex = 0;
	private DateTime displayDateTime = null;
	private boolean clearDone = false;
	private CommandType helpCommand;
	private String searchString = "";
	private InvalidCommandReason invalidCommandReason;
	private boolean displayDone = false;

	// TODO: remove
	@Deprecated
	private HashMap<String, String> commandAttributes = null;
	
	public Command(CommandType type){
		commandAttributes = new HashMap<String, String>();
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

	public int getFinaliseIndex() {
		return finaliseIndex;
	}

	public void setFinaliseIndex(int finaliseIndex) {
		this.finaliseIndex = finaliseIndex;
	}

	public DateTime getDisplayDateTime() {
		return displayDateTime;
	}

	public void setDisplayDateTime(DateTime searchDateTime) {
		this.displayDateTime = searchDateTime;
	}
	
	public boolean getClearDone() {
		return clearDone;
	}

	public void setClearDone(boolean clearDone) {
		this.clearDone = clearDone;
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

	// TODO: remove
	@Deprecated
	public HashMap<String, String> getCommandAttributes(){
		return commandAttributes;
	}
	
	// TODO: remove
	@Deprecated
	public void setValue(String attribute, String value){
		commandAttributes.put(attribute, value);
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

	@Override
	public String toString() {
		return "Command [commandType=" + commandType + ", description="
				+ description + ", deadline=" + deadline + ", intervals="
				+ intervals + ", tags=" + tags + ", taskIndex=" + taskIndex
				+ ", finaliseIndex=" + finaliseIndex + ", displayDateTime="
				+ displayDateTime + ", clearDone=" + clearDone
				+ ", helpCommand=" + helpCommand + ", searchString="
				+ searchString + ", invalidCommandReason="
				+ invalidCommandReason + "]";
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
		if (clearDone != other.clearDone)
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
		if (finaliseIndex != other.finaliseIndex)
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
		return true;
	}

	public boolean getDisplayDone() {
		return displayDone;
	}

	public void setDisplayDone(boolean displayDone) {
		this.displayDone = displayDone;
	}
}
