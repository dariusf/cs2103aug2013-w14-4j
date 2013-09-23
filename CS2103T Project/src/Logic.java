import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class Logic {
	private static Storage storage = null;
	
	protected Logic(){
		storage = new Storage();
	}
	
	static Feedback executeCommand(String userCommand) {
		Command command = new Parser().parse(userCommand);
		CommandType commandType = Command.getCommandType();

		switch (commandType) {
		case ADD_TASK:
			return addTask(command);
		case DISPLAY:
			return displayTasks();
		case DELETE:
			return deleteTask(command);
		case CLEAR:
			return clearTasks(command);
		case EXIT:
			exitProgram();
		case SEARCH:
			return searchTasks(command);
		case UNDO:
			return undoState();
		case EDIT_TASK:
			return editTask(command);
		case HELP:
			return showHelp(command);
		case SORT:
			return sortTask();
		default:
			throw new Error(Constants.MSG_UNRECOGNISED_COMMAND);
		}
	}
	

	private static Feedback sortTask() {
		Feedback feedback = null;
		if(storage.size()>0){
			storage.sort();
			feedback = new Feedback(10, CommandType.SORT);
		} else {
			feedback = new Feedback(70);
		}
		return feedback;
	}

	private static Feedback showHelp(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Feedback editTask(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Feedback undoState() {
		// TODO Auto-generated method stub
		return null;
	}

	private static Feedback searchTasks(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	private static void exitProgram() {
		System.exit(0);
	}

	private static Feedback clearTasks(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Feedback deleteTask(Command command) {
		HashMap<String, String> commandAttributes = Command.getCommandAttributes();
		int lineNumber = Integer.parseInt(commandAttributes.get(Constants.DELETE_ATT_LINE));
		
		Feedback feedback = null;
		if(lineNumber <= storage.size()){
			storage.remove(lineNumber);
			feedback = new Feedback(10, CommandType.DELETE, commandAttributes);
		} else {
			feedback = new Feedback(61);
		}
		
		return feedback;
	}

	private static Feedback displayTasks() {
		Feedback feedback = null;
		if(storage.size() > 0){
			StringBuilder output = new StringBuilder();
			int index = 1;
			for (Task task : storage.getAll()){
				output.append(index+". ");
				output.append(task.toString());
				if(index < storage.size()){
					output.append("/n");
				}
			}
			feedback = new Feedback(10, CommandType.DISPLAY, output.toString());
		} else {
			feedback = new Feedback(50);
		}
		return feedback;
	}

	private static Feedback addTask(Command command) {
		HashMap<String, String> taskAttributes = Command.getCommandAttributes();
		Task newTask = new Task(taskAttributes);
		storage.add(newTask);
		
		Feedback feedback = null;
		if(isTaskOver(newTask)){
			feedback = new Feedback(11, CommandType.ADD_TASK, taskAttributes);
		} else {
			feedback = new Feedback(10, CommandType.ADD_TASK, taskAttributes);
		}
		
		return feedback;
	}

	private static boolean isTaskOver(Task task){
		if(task.isDeadlineTask()){
			Date deadline = task.getDeadline();
			return isTimePastAlready(deadline);
		} else if (task.isTimedTask()){
			Date endTime = task.getEndTime();
			return isTimePastAlready(endTime);
		} else if (task.isUntimedTask()){
			List<Task.Slot> possibleTime = task.getPossibleTime();
			return isUntimedTaskOver(possibleTime);
		} else {
			return false;
		}
	}
	
	private static boolean isUntimedTaskOver(List<Task.Slot> possibleTime) {
		boolean isAllSlotOver = true;
		for(Task.Slot slot : possibleTime){
			if(!isTimePastAlready(slot.getEndTime())){
				isAllSlotOver = false;
			}
		}
		return isAllSlotOver;
		
	}
	
	private static boolean isTimePastAlready(Date time){
		return time.compareTo(new Date()) < 0;
	}
}
