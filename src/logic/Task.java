package logic;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import common.CommandType;
import common.Constants;

public class Task implements Comparable<Task>, Cloneable{

	private String name = "";
	private String type = "";
	private ArrayList<String> tags = new ArrayList<String>();
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
		} else if (attribute == Constants.TASK_ATT_NAME) {
			output = name;
		} else if (attribute == Constants.TASK_ATT_TYPE) {
			output = type;
		} else if (attribute == Constants.TASK_ATT_STARTTIME) {
			output = interval.getStartDateTime().toString();
		} else if (attribute == Constants.TASK_ATT_POSSIBLETIME) {
			output = possibleIntervals.toString();
		} else if (attribute == Constants.TASK_ATT_ENDTIME) {
			output = interval.getEndDateTime().toString();
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


	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}

	public DateTime getStartTime() {
		if(interval == null){
			return null;
		}
		return interval.getStartDateTime();
	}

	public void setStartTime(DateTime startTime) {
		if(interval == null){
			interval = new Interval();
		}
		interval.setStartDateTime(startTime);
	}

	public DateTime getEndTime() {
		if(interval == null){
			return null;
		}
		return interval.getEndDateTime();
	}

	public void setEndTime(DateTime endTime) {
		if(interval == null){
			interval = new Interval();
		}
		interval.setEndDateTime(endTime);
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

		DateTimeFormatter format = DateTimeFormat.forPattern("h:mm a 'on' E, d/M/Y");;
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
				output.append(format.print(slot.getStartDateTime()));
				output.append(" to ");
				output.append(format.print(slot.getEndDateTime()));
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
		newTask.setName(new String(name));
		newTask.setTags(new ArrayList<String>(tags));
		newTask.setDeadline(new DateTime(deadline));
		newTask.setPossibleTime(new ArrayList<Interval>(possibleIntervals));
		newTask.setInterval(interval);
		if(done){
			newTask.markDone();
		}
		newTask.setType(new String(type));
		return newTask;
	}

	public Interval getInterval() {
		return interval;
	}

	public void setInterval(Interval interval) {
		this.interval = interval;
	}
	
	public boolean getDone(){
		return done;
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
			if(interval.getStartDateTime().compareTo(earliestTime) < 0){
				earliestTime = interval.getStartDateTime();
			}
		}
		return earliestTime;
	}
	
	public static void main(String[] args) throws CloneNotSupportedException {
		Command command6 = new Command(CommandType.ADD_TASK);
		DateTime startDate6a = new DateTime(2012, 10, 30, 12, 0, 0);
		DateTime endDate6a = new DateTime(2012, 10, 30, 16, 0, 0);
		Interval interval6a = new Interval();
		interval6a.setStartDateTime(startDate6a);
		interval6a.setEndDateTime(endDate6a);
		DateTime startDate6b = new DateTime(2012, 10, 30, 16, 0, 0);
		DateTime endDate6b = new DateTime(2012, 10, 30, 17, 0, 0);
		Interval interval6b = new Interval();
		interval6b.setStartDateTime(startDate6b);
		interval6b.setEndDateTime(endDate6b);
		DateTime startDate6c = new DateTime(2012, 10, 30, 17, 0, 0);
		DateTime endDate6c = new DateTime(2012, 10, 30, 18, 0, 0);
		Interval interval6c = new Interval();
		interval6c.setStartDateTime(startDate6c);
		interval6c.setEndDateTime(endDate6c);
		ArrayList<Interval> intervalList6 = new ArrayList<Interval>();
		intervalList6.add(interval6a);
		intervalList6.add(interval6b);
		intervalList6.add(interval6c);
		command6.setIntervals(intervalList6);
		command6.setDescription("An overdue floating event!");
		Task task1 = new Task(command6);
		Task task2 = (Task) task1.clone();
		task2.setPossibleTime(null);
		task2.setName("hahaha");
		
		System.out.println(task1);
		System.out.println(task2);
	}
}
