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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Task implements Comparable<Task>, Cloneable{

	private String name = "";
	private String type = "";
	private String location = "";
	private List<String> tags = new ArrayList<String>();
	private Interval interval = null;
	private DateTime deadline = null;
	private List<Interval> possibleIntervals = new ArrayList<Interval>();
	private boolean done = false;

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

	public Task() {

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
		DateTimeFormatter format = DateTimeFormat.forPattern("K:mm a 'on' E, d/M/Y");;
		if (isDeadlineTask()) {
			output.append(" before " + format.print(deadline));
		} else if (isTimedTask()) {
			output.append(" from " + format.print(getStartTime()) + " to "
					+ format.print(getEndTime()));
		} else if (isFloatingTask()) {
			output.append(" on ");
			int index = 1;
			for (Interval slot : possibleIntervals) {
				output.append("(");
				output.append(index);
				output.append(") ");
				output.append(format.print(slot.getStart()));
				output.append(" to ");
				output.append(format.print(slot.getEnd()));
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
		
		if(done){
			output.append(" done");
		}
		
		return output.toString();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		Task newTask = new Task();
		newTask.setName(name);
		newTask.setTags(tags);
		newTask.setLocation(location);
		newTask.setDeadline(deadline);
		newTask.setPossibleTime(possibleIntervals);
		newTask.setInterval(interval);
		if(done){
			newTask.markDone();
		}
		newTask.setType(type);
		return newTask;
	}

	public Interval getInterval() {
		return interval;
	}

	public void setInterval(Interval interval) {
		this.interval = interval;
	}

	@Override
	public int compareTo(Task o) {
		if(isDeadlineTask() && o.isDeadlineTask()){
			return deadline.compareTo(o.getDeadline());
		} else if (isDeadlineTask() && o.isTimedTask()){
			return deadline.compareTo(o.getStartTime());
		} else if (isDeadlineTask() && o.isFloatingTask()){
			return deadline.compareTo(getEarliestTime(o.getPossibleTime()));
		} else if (isTimedTask() && o.isDeadlineTask()){
			return getStartTime().compareTo(o.getDeadline());
		} else if (isTimedTask() && o.isTimedTask()){
			return getStartTime().compareTo(o.getStartTime());
		} else if (isTimedTask() && o.isFloatingTask()){
			return getStartTime().compareTo(getEarliestTime(o.getPossibleTime()));
		} else if (isFloatingTask() && o.isDeadlineTask()){
			return getEarliestTime(getPossibleTime()).compareTo(o.getDeadline());
		} else if (isTimedTask() && o.isTimedTask()){
			return getEarliestTime(getPossibleTime()).compareTo(o.getStartTime());
		} else if (isDeadlineTask() && o.isFloatingTask()){
			getEarliestTime(getPossibleTime()).compareTo(getEarliestTime(o.getPossibleTime()));
		} else if (isUntimedTask()){
			return -1;
		} else {
			return 1;
		}
		return 0;
	}
	
	public DateTime getEarliestTime (List<Interval> list){
		DateTime earliestTime = new DateTime(9999, 12, 31, 23, 59);
		for(Interval interval : list){
			if(interval.getStart().compareTo(earliestTime) < 0){
				earliestTime = interval.getStart();
			}
		}
		return earliestTime;
	}
	
}
