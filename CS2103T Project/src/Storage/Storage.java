package Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.joda.time.DateTime;
import org.yaml.*;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import Logic.Command;
import Logic.CommandType;
import Logic.Task;

public class Storage {
	public static ArrayList<Task> taskStorage = null;
	
	public Storage(){
		taskStorage = new ArrayList<Task>();
	}
	
	public void sort(){
		
	}
	
	public void add(Task task){
		taskStorage.add(task);
	}
	
	public void remove(int index){
		taskStorage.remove(index - 1);
	}
	
	public void remove(Task t){
		taskStorage.remove(t);
	}
	
	public void clear(){
		taskStorage.clear();
	}
	
	public Task get(int index){
		return taskStorage.get(index-1);
	}
	
	public Iterator<Task> iterator() { // returns shallow copy
		return taskStorage.iterator();
	}
	
	public boolean isEmpty() {
		return taskStorage.isEmpty();
	}
	
	public int size() {
		return taskStorage.size();
	}
	
	public void undo(){
		
	}
	
	public boolean isUndoable(){
		return true;
	}
	
	public void replace(int index, Task task){
		taskStorage.set(index-1, task);
	}
	
	public static HashMap<String, Object> taskToHashMap (Task task){
		HashMap<String, Object> output = new HashMap<String, Object>();
		output.put("Description", task.getName());
		output.put("Type", task.getType());
		output.put("Tags", task.getTags());
		output.put("Done", task.getDone());
		if(task.isDeadlineTask()){
			output.put("Deadline", task.getDeadline().toString());
		} else if (task.isTimedTask()){
			output.put("Start Time", task.getStartTime().toString());
			output.put("End Time", task.getStartTime().toString());
		} else if (task.isFloatingTask()){
			output.put("Possible Time", task.getPossibleTime().toString());
		}
		return output;
	}
	
	public static void main(String[] args){
		Yaml yaml = new Yaml();
		Command command1 = new Command(CommandType.ADD_TASK);
		DateTime date1 = new DateTime(2013, 10, 14, 23, 59, 59);
		command1.setDeadline(date1);
		command1.setDescription("Submit V0.1");
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("TGIF");
		tags.add("forfun");
		command1.setTags(tags);
		Task testTask = new Task(command1);
		Map<String, Object> testMap = taskToHashMap(testTask);
		System.out.println(yaml.dump(testMap));
		
		String output = yaml.dump(testMap);
		HashMap<String, Object> data = (HashMap<String, Object>) yaml.load(output);
		System.out.println(data);
	}
}
