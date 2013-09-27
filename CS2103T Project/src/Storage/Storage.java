package Storage;

import java.util.ArrayList;
import java.util.Iterator;

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
		return taskStorage.get(index);
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
}
