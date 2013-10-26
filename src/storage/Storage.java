package storage;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import logic.Task;

public class Storage implements Closeable, Iterable<Task> {
	private ActionCapturer<Task, RealStorage<Task>> taskStorage;
	private final String fileName;
	private boolean definingCustomAction;
	
	public Storage(String fileName) throws IOException {
		this.fileName = fileName;
		File file = new File(fileName);
		definingCustomAction = false;
		class TaskCloner implements Cloner<Task> {
			public Task clone(Task original) {
				return new Task(original);
			}
		}
		class StorageCloner implements Cloner<RealStorage<Task>> {
			public RealStorage<Task> clone(RealStorage<Task> original) {
				return new RealStorage<>(original);
			}
		}
		try {
			if (file.exists()) {
				ArrayList<Task> taskList = Json.readFromFile(new File(fileName));
				RealStorage<Task> taskSet = new RealStorage<>(taskList);
				taskStorage = new ActionCapturer<>(taskSet, new TaskCloner(), new StorageCloner());
			} else {
				taskStorage = new ActionCapturer<> (new RealStorage<Task>(), new TaskCloner(), new StorageCloner());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Storage() throws IOException {
		this("default.txt");
	}

	private void finaliseActions() {
		if (!definingCustomAction) {
			taskStorage.finaliseActions();
		}
		
		try {
			writeToFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void beginCustomActionSet() {
		definingCustomAction = true;
	}
	
	public void endCustomActionSet() {
		definingCustomAction = false;
		finaliseActions();
	}
	
	public void sort() {
		taskStorage.sort();
		finaliseActions();
	}
	
	public void add(Task task) {
		taskStorage.insert(taskStorage.size(), task);
		finaliseActions();
	}
	
	public void remove(int index) {
		index--;
		taskStorage.remove(index);
		finaliseActions();
	}
	
	public void remove(Task t) {
		taskStorage.remove(t);
		finaliseActions();
	}

	public void replace(int index, Task task) {
		index--;
		taskStorage.remove(index);
		taskStorage.insert(index, task);
		finaliseActions();
	}

	public void clear() {
		taskStorage.setState(new ArrayList<Task>());
		finaliseActions();
	}

	public Task get(int index) {
		index--;
		return taskStorage.get(index);
	}
	
	public String getFileName() {
		return fileName;
	}

	public Iterator<Task> iterator() {
		return taskStorage.iterator();
	}
	
	public boolean isEmpty() {
		return (taskStorage.size() == 0);
	}
	
	public int size() {
		return taskStorage.size();
	}
	
	public void undo() throws Exception {
		taskStorage.undo();
		writeToFile();
	}
	
	public void redo() throws Exception {
		taskStorage.redo();
		writeToFile();
	}

	public boolean isUndoable() {
		return taskStorage.isUndoable();
	}
	
	public boolean isRedoable() {
		return taskStorage.isRedoable();
	}
	
	public void removeSet(List<Task> items) {
		Iterator<Task> itemsIterator = items.iterator();
		beginCustomActionSet();
		while (itemsIterator.hasNext()) {
			remove(itemsIterator.next());
		}
		endCustomActionSet();
	}
	
	@Override
	public void close() throws IOException {
		writeToFile();
	}

	private void writeToFile() throws IOException {
		File file = new File(fileName);
		//if(file.exists()) { file.delete(); }
		
		Json.writeToFile(convertIteratorToList(taskStorage.iterator()), file);
	}
	
	private static <E> ArrayList<E> convertIteratorToList (Iterator<E> iter) {
		ArrayList<E> result = new ArrayList<>();
		while (iter.hasNext()) {
			result.add(iter.next());
		}
		return result;
	}
}
