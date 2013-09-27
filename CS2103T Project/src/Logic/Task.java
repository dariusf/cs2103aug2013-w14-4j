package Logic;

import java.io.ObjectInputStream.GetField;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
	private Date startTime = null;
	private Date endTime = null;
	private Date deadline = null;
	private List<Slot> possibleTime = new ArrayList<Slot>();
	private boolean done = false;

	private SimpleDateFormat dateParser = new SimpleDateFormat("h:mm a");

	public Task(HashMap<String, String> attributes) {
		try {
			name = attributes.get(Constants.TASK_ATT_NAME);
			type = attributes.get(Constants.TASK_ATT_TYPE);
			if (attributes.containsKey(Constants.TASK_ATT_LOCATION)) {
				location = attributes.get(Constants.TASK_ATT_LOCATION);
			}
			
			done = false;
			if (attributes.containsKey(Constants.TASK_ATT_TAGS)) {
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

	private String tagsToString() {
		StringBuilder output = new StringBuilder();
		for (String tag : tags) {
			output.append("#" + tag + " ");
		}
		return output.toString();
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

	public void set(String attribute, String value) {
		try {
			if (attribute == Constants.TASK_ATT_DEADLINE) {
				deadline = dateParser.parse(value);
			} else if (attribute == Constants.TASK_ATT_DONE) {
				done = Boolean.parseBoolean(value);
			} else if (attribute == Constants.TASK_ATT_LOCATION) {
				location = value;
			} else if (attribute == Constants.TASK_ATT_NAME) {
				name = value;
			} else if (attribute == Constants.TASK_ATT_TYPE) {
				type = value;
			} else if (attribute == Constants.TASK_ATT_STARTTIME) {
				startTime = dateParser.parse(value);
			} else if (attribute == Constants.TASK_ATT_POSSIBLETIME) {
				possibleTime = possibleTimeParser(value);
			} else if (attribute == Constants.TASK_ATT_ENDTIME) {
				endTime = dateParser.parse(value);
			} else if (attribute == Constants.TASK_ATT_TAGS) {
				tags = tagsParser(value);
			} else {
				// Nothing happens here!
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			output = startTime.toLocaleString();
		} else if (attribute == Constants.TASK_ATT_POSSIBLETIME) {
			output = possibleTime.toString();
		} else if (attribute == Constants.TASK_ATT_ENDTIME) {
			output = endTime.toLocaleString();
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

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public List<Slot> getPossibleTime() {
		return possibleTime;
	}

	public void setPossibleTime(List<Slot> possibleTime) {
		this.possibleTime = possibleTime;
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
			output.append(" from " + startTime.toString() + " to "
					+ endTime.toString());
		} else if (isFloatingTask()) {
			output.append(" on ");
			int index = 1;
			for (Slot slot : possibleTime) {
				output.append("(");
				output.append(index);
				output.append(") ");
				output.append(slot.getStartTime().toLocaleString());
				output.append(" to ");
				output.append(slot.getEndTime().toLocaleString());
				if (index != possibleTime.size()) {
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
