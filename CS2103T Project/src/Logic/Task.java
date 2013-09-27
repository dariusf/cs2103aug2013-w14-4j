package Logic;

import java.io.ObjectInputStream.GetField;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;

public class Task {
	public static void main(String[] args) {
		HashMap<String, String> testMap1 = new HashMap<String, String>();
		testMap1.put(Constants.TASK_ATT_NAME, "Buy milk");
		testMap1.put(Constants.TASK_ATT_LOCATION, "NTUC");
		testMap1.put(Constants.TASK_ATT_TYPE, "floating");
		testMap1.put(Constants.TASK_ATT_POSSIBLETIME,
				"10:00 PM;11:00 PM;8:00 PM;9:00 PM");
		testMap1.put(Constants.TASK_ATT_TAGS, "haha hahaha hahahaha");
		Task testTask1 = new Task(testMap1);
		System.out.println(testTask1);

		HashMap<String, String> testMap2 = new HashMap<String, String>();
		testMap2.put(Constants.TASK_ATT_NAME, "Buy milk");
		testMap2.put(Constants.TASK_ATT_LOCATION, "NTUC");
		testMap2.put(Constants.TASK_ATT_TYPE, "deadline");
		testMap2.put(Constants.TASK_ATT_DEADLINE, "10:00 PM");
		testMap2.put(Constants.TASK_ATT_TAGS, "haha hahaha hahahaha");
		Task testTask2 = new Task(testMap2);
		System.out.println(testTask2);
	}

	private String name = "";
	private String type = "";
	private String location = "";
	private List<String> tags = new ArrayList<String>();
	private Interval interval = null;
	private DateTime deadline = null;
	private List<Interval> possibleIntervals = new ArrayList<Interval>();
	private boolean done = false;

	private SimpleDateFormat dateParser = new SimpleDateFormat("h:mm a");

	public Task(Command command) {
		name = command.getDescription();
		type = command.getTaskType();
		done = false;

		if (type == Constants.TASK_TYPE_TIMED) {
			interval = command.getIntervals().get(0);
		} else if (type == Constants.TASK_TYPE_DEADLINE) {
			deadline = command.getDeadline();
		} else if (type == Constants.TASK_TYPE_FLOATING) {
			possibleIntervals = command.getIntervals(); 
		}

		if (command.getTags() != null) {
			tags = command.getTags();
		}
	}

	private String tagsToString() {
		StringBuilder output = new StringBuilder();
		for (String tag : tags) {
			output.append("#" + tag + " ");
		}
		return output.toString();
	}

	public String get(String attribute) {
		String output = "";

		if (attribute == Constants.TASK_ATT_DEADLINE) {
			output = deadline.toString();
		} else if (attribute == Constants.TASK_ATT_DONE) {
			output = done ? "done" : "not done";
		} else if (attribute == Constants.TASK_ATT_LOCATION) {
			output = location;
		} else if (attribute == Constants.TASK_ATT_NAME) {
			output = name;
		} else if (attribute == Constants.TASK_ATT_TYPE) {
			output = type;
		} else if (attribute == Constants.TASK_ATT_STARTTIME) {
			output = interval.getStart().toString();
		} else if (attribute == Constants.TASK_ATT_POSSIBLETIME) {
			output = possibleIntervals.toString();
		} else if (attribute == Constants.TASK_ATT_ENDTIME) {
			output = interval.getEnd().toString();
		} else if (attribute == Constants.TASK_ATT_TAGS) {
			output = tagsToString();
		} else {
			output = "";
		}

		return output;
	}

	// public HashMap<String, String> getAttributes() {
	// HashMap<String, String> output = new HashMap<>();
	// output.put(Constants.TASK_ATT_NAME, name);
	// output.put(Constants.TASK_ATT_DONE, String.valueOf(done));
	// output.put(Constants.TASK_ATT_TYPE, type);
	// output.put(Constants.TASK_ATT_TAGS, tagsToString());
	// if(isDeadlineTask()){
	// output.put(Constants.TASK_ATT_DEADLINE, deadline.toString());
	// } else if (isTimedTask()){
	// output.put(Constants.TASK_ATT_STARTTIME, startTime.toString());
	// } else if (isFloatingTask()){
	// output.put(key, value)
	// }
	//
	// }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public DateTime getStartTime() {
		return interval.getStart();
	}

	public void setStartTime(DateTime startTime) {
		interval.setStart(startTime);
	}

	public DateTime getEndTime() {
		return interval.getEnd();
	}

	public void setEndTime(DateTime endTime) {
		interval.setEnd(endTime);
	}

	public DateTime getDeadline() {
		return deadline;
	}

	public void setDeadline(DateTime deadline) {
		this.deadline = deadline;
	}

	public List<Interval> getPossibleTime() {
		return possibleIntervals;
	}

	public void setPossibleTime(List<Interval> possibleIntervals) {
		this.possibleIntervals = possibleIntervals;
	}

	public boolean isDone() {
		return done;
	}

	public void markDone() {
		this.done = true;
	}

	public void markUndone() {
		this.done = false;
	}

	public boolean isTimedTask() {
		return this.type.equals(Constants.TASK_TYPE_TIMED);
	}

	public boolean isDeadlineTask() {
		return this.type.equals(Constants.TASK_TYPE_DEADLINE);
	}

	public boolean isFloatingTask() {
		return this.type.equals(Constants.TASK_TYPE_FLOATING);
	}

	public boolean isUntimedTask() {
		return this.type.equals(Constants.TASK_TYPE_UNTIMED);
	}

	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append(name);

		if (!location.isEmpty()) {
			output.append(" at " + location);
		}

		if (isDeadlineTask()) {
			output.append(" before " + deadline.toString());
		} else if (isTimedTask()) {
			output.append(" from " + getStartTime().toString() + " to "
					+ getEndTime().toString());
		} else if (isFloatingTask()) {
			output.append(" on ");
			int index = 1;
			for (Interval slot : possibleIntervals) {
				output.append("(");
				output.append(index);
				output.append(") ");
				output.append(slot.getStart().toString());
				output.append(" to ");
				output.append(slot.getEnd().toString());
				if (index != possibleIntervals.size()) {
					output.append(" or ");
				}
				index++;
			}
		}

		if (tags.size() > 0) {
			for (String tag : tags) {
				output.append(" #" + tag);
			}
		}

		return output.toString();
	}
}
