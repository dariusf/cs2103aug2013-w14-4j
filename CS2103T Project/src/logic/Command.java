package logic;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.DateTime;

import common.CommandType;
import common.Constants;

import parser.Interval;

public class Command {
	private CommandType commandType = null;
	
	private String description = "";
	private DateTime deadline = null;
	private ArrayList<String> tags = null;
	private ArrayList<Interval> intervals = new ArrayList<>();

	private HashMap<String, String> commandAttributes = null;
	
	public Command(CommandType type){
		commandAttributes = new HashMap<String, String>();
		commandType = type;
	}
		
	public CommandType getCommandType(){
		return commandType;
	}
	
	public HashMap<String, String> getCommandAttributes(){
		return commandAttributes;
	}
	
	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}

	public void setValue(String attribute, String value){
		commandAttributes.put(attribute, value);
	}
	
	// TODO: might want to move to a higher level,
	// this is just here for now to illustrate how these
	// fields alone can define the task type clearly
	public String getTaskType() {
		assert commandType == CommandType.ADD_TASK;
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

	@Override
	public String toString() {
		return "Command [commandType=" + commandType + ", description="
				+ description + ", deadline=" + deadline + ", tags=" + tags
				+ ", intervals=" + intervals + ", commandAttributes="
				+ commandAttributes + "]";
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
		
		if (commandAttributes == null) {
			if (other.commandAttributes != null)
				return false;
		} else if (!commandAttributes.equals(other.commandAttributes))
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
		if (intervals == null) {
			if (other.intervals != null)
				return false;
		} else if (!intervals.equals(other.intervals))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		return true;
	}

	
}
