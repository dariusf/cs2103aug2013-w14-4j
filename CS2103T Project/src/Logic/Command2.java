package Logic;
import java.util.ArrayList;

public class Command2 {
	private CommandType commandType = null;
	
	// TODO: make private once we settle on a data structure for this
	public String text = "";
	public Moment deadline = null;
	public ArrayList<Interval> intervals = new ArrayList<>();
	
	public Command2(CommandType type){
		commandType = type;
	}
		
	public CommandType getCommandType(){
		return commandType;
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
}
