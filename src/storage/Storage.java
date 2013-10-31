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
	
	public Storage(String fileName) throws IOException {
		this.fileName = fileName;
		File file = new File(fileName);
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
	
	public void sort() {
		taskStorage.sort();
	}
	
	public void add(Task task) {
		taskStorage.insert(taskStorage.size(), task);
	}
	
	public void remove(int index) {
		index--;
		taskStorage.remove(index);
	}
	
	public void remove(Task t) {
		taskStorage.remove(t);
	}

	public void replace(int index, Task task) {
		index--;
		taskStorage.remove(index);
		taskStorage.insert(index, task);
	}

	public void clear() {
		taskStorage.setState(new ArrayList<Task>());
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
	
	public void removeSet(List<Task> items) {
		Iterator<Task> itemsIterator = items.iterator();
		while (itemsIterator.hasNext()) {
			remove(itemsIterator.next());
		}
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
