package Logic;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.DateTime;

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Command2 [commandType=");
		builder.append(commandType);
		builder.append(", description=");
		builder.append(description);
		builder.append(", deadline=");
		builder.append(deadline);
		builder.append(", intervals=");
		builder.append(intervals);
		builder.append(", commandAttributes=");
		builder.append(commandAttributes);
		builder.append("]");
		return builder.toString();
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
	
	
}
