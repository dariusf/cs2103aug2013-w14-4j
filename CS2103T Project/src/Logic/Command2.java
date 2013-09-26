package Logic;
import java.util.ArrayList;
import java.util.HashMap;

public class Command2 {
	private CommandType commandType = null;
	
	// TODO: make private once we settle on a data structure for this
	public String description = "";
	public Moment deadline = null;
	public ArrayList<Interval> intervals = new ArrayList<>();
	private HashMap<String, String> commandAttributes = null;
	
	public Command2(CommandType type){
		commandType = type;
	}
		
	public CommandType getCommandType(){
		return commandType;
	}
	
	public HashMap<String, String> getCommandAttributes(){
		return commandAttributes;
	}
	
	public void setValue(String attribute, String value){
		commandAttributes.put(attribute, value);
	}
	
	// TODO: might want to move to a higher level,
	// this is just here for now to illustrate how these
	// fields alone can define the task type clearly
	public String getTaskType() {
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

	public String getDescriptcion() {
		return description;
	}

	public void setDescription(String text) {
		this.description = text;
	}

	public Moment getDeadline() {
		return deadline;
	}

	public void setDeadline(Moment deadline) {
		this.deadline = deadline;
	}

	public ArrayList<Interval> getIntervals() {
		return intervals;
	}

	public void setIntervals(ArrayList<Interval> intervals) {
		this.intervals = intervals;
	}
	
	
}
