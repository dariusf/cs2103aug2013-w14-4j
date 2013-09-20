import java.util.ArrayList;

public class Storage {
	protected static ArrayList<Task> taskStorage = null;
	
	protected Storage(){
		taskStorage = new ArrayList<Task>();
	}
	
	protected void sort(){
		
	}
	
	protected void add(Task task){
		taskStorage.add(task);
	}
	
	protected void remove(int index){
		taskStorage.remove(index - 1);
	}
	
	protected void clear(){
		taskStorage.clear();
	}
	
	protected void get(int index){
		taskStorage.get(index);
	}
}
