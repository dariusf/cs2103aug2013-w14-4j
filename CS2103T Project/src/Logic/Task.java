package Logic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class Task {
	public static void main(String[] args){
		HashMap<String, String> testMap1 = new HashMap<String, String>();
		testMap1.put(Constants.TASK_ATT_NAME, "Buy milk");
		testMap1.put(Constants.TASK_ATT_LOCATION, "NTUC");
		testMap1.put(Constants.TASK_ATT_TYPE, "floating");
		testMap1.put(Constants.TASK_ATT_POSSIBLETIME, "10:00 PM;11:00 PM;8:00 PM;9:00 PM");
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
	private Date startTime = null;
	private Date endTime = null;
	private Date deadline = null;
	private List<Slot> possibleTime = new ArrayList<Slot>();
	private boolean done = false;

	private SimpleDateFormat dateParser = new SimpleDateFormat("h:mm a");

	protected class Slot {
		private Date startTime = null;
		private Date endTime = null;

		protected Slot(Date start, Date end) {
			startTime = start;
			endTime = end;
		}

		protected Date getStartTime() {
			return startTime;
		}

		protected Date getEndTime() {
			return endTime;
		}

		protected void setStartTime(Date start) {
			startTime = start;
		}

		protected void setEndTime(Date end) {
			endTime = end;
		}
		
		public String toString(){
			return startTime.toString() + " " + endTime.toString();
		}
	}

	protected Task(HashMap<String, String> attributes) {
		try {
			name = attributes.get(Constants.TASK_ATT_NAME);
			type = attributes.get(Constants.TASK_ATT_TYPE);
			location = attributes.get(Constants.TASK_ATT_LOCATION);
			done = false;
			if (attributes.containsKey(Constants.TASK_ATT_TAGS)){
				tags = tagsParser(attributes.get(Constants.TASK_ATT_TAGS));
			}
			if (attributes.containsKey(Constants.TASK_ATT_STARTTIME)) {
				startTime = dateParser.parse(attributes
						.get(Constants.TASK_ATT_STARTTIME));
			}
			if (attributes.containsKey(Constants.TASK_ATT_ENDTIME)) {
				endTime = dateParser.parse(attributes
						.get(Constants.TASK_ATT_ENDTIME));
			}
			if (attributes.containsKey(Constants.TASK_ATT_DEADLINE)) {
				deadline = dateParser.parse(attributes
						.get(Constants.TASK_ATT_DEADLINE));
			}
			if (attributes.containsKey(Constants.TASK_ATT_POSSIBLETIME)) {
				possibleTime = possibleTimeParser(attributes
						.get(Constants.TASK_ATT_POSSIBLETIME));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<String> tagsParser(String tags) {
		return Arrays.asList(tags.split(" "));
	}

	private List<Slot> possibleTimeParser(String possibleTime) {
		List<Slot> output = new ArrayList<Slot>();
		String[] listOfTime = possibleTime.split(";");

		for (int i = 0; i < listOfTime.length; i += 2) {
			try {
				Date startTime = dateParser.parse(listOfTime[i]);
				Date endTime = dateParser.parse(listOfTime[i + 1]);
				output.add(new Slot(startTime, endTime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return output;
	}

	protected String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected String getType() {
		return type;
	}

	protected void setType(String type) {
		this.type = type;
	}

	protected String getLocation() {
		return location;
	}

	protected void setLocation(String location) {
		this.location = location;
	}

	protected List<String> getTags() {
		return tags;
	}

	protected void setTags(List<String> tags) {
		this.tags = tags;
	}

	protected Date getStartTime() {
		return startTime;
	}

	protected void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	protected Date getEndTime() {
		return endTime;
	}

	protected void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	protected Date getDeadline() {
		return deadline;
	}

	protected void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	protected List<Slot> getPossibleTime() {
		return possibleTime;
	}

	protected void setPossibleTime(List<Slot> possibleTime) {
		this.possibleTime = possibleTime;
	}

	protected boolean isDone() {
		return done;
	}

	protected void markDone() {
		this.done = true;
	}

	protected void markUndone() {
		this.done = false;
	}
	
	protected boolean isTimedTask(){
		return this.type.equals(Constants.TASK_TYPE_TIMED);
	}
	
	protected boolean isDeadlineTask(){
		return this.type.equals(Constants.TASK_TYPE_DEADLINE);
	}
	
	protected boolean isFloatingTask(){
		return this.type.equals(Constants.TASK_TYPE_FLOATING);
	}
	
	protected boolean isUntimedTask(){
		return this.type.equals(Constants.TASK_TYPE_UNTIMED);
	}

	public String toString(){
		StringBuilder output = new StringBuilder();
		output.append(name);
		
		if(!location.isEmpty()){
			output.append(" at " + location);
		}
		
		if(isDeadlineTask()){
			output.append( " before " + deadline.toString()); 
		} else if (isTimedTask()){
			output.append(" from " + startTime.toString() + " to " + endTime.toString());
		} else if (isFloatingTask()){
			output.append( " on ");
			int index = 1;
			for(Slot slot : possibleTime){
				output.append("(");
				output.append(index);
				output.append(") ");
				output.append(slot.getStartTime().toLocaleString());
				output.append(" to ");
				output.append(slot.getEndTime().toLocaleString());
				if(index != possibleTime.size()){
					output.append(" or ");
				}
				index++;
			}
		}
		
		if(tags.size() > 0){
			for(String tag : tags){
				output.append(" #" + tag);
			}
		}
		
		return output.toString();
	}
}
