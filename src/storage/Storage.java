// @author: A0097556M

package storage;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import common.Task;

public class Storage implements Closeable, Iterable<Task> {
	
	private static int WRITE_TO_FILE_DELAY = 30000;
	private static int WRITE_TO_FILE_PERIOD = 30000;
	
	class Switch {
		boolean switchState = false;
		
		synchronized void turnOn () {
			switchState = true;
		}
		synchronized void turnOff () {
			switchState = false;
		}
		synchronized void flip () {
			switchState = switchState & false;
		}
		boolean isOn() {
			return (switchState == true);
		}
		boolean isOff() {
			return (switchState == false);
		}
		boolean getState() {
			return switchState;
		}
	}
	
	private ActionCapturer<Task, RealStorage<Task>> taskStorage;
	private final String fileName;
	private Switch hasEditSwitch = new Switch();
	private Timer writeTimer;
	
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
		initialiseTimer();
	}

	private void initialiseTimer() {
		writeTimer = new Timer(true);
		writeTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if(hasEditSwitch.isOn()) {
					try {
						writeToFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}, WRITE_TO_FILE_DELAY, WRITE_TO_FILE_PERIOD);
	}
	
	public void sort() {
		taskStorage.sort();
	}
	
	public void add(Task task) {
		assert(task != null);
		taskStorage.insert(taskStorage.size(), task);
		hasEditSwitch.turnOn();
	}
	
	public void remove(int index) {
		index--;
		taskStorage.remove(index);
		hasEditSwitch.turnOn();
	}
	
	public void remove(Task t) {
		taskStorage.remove(t);
	}

	public void replace(int index, Task task) {
		assert(task != null);
		index--;
		taskStorage.remove(index);
		taskStorage.insert(index, task);
		hasEditSwitch.turnOn();
	}

	public void clear() {
		taskStorage.setState(new ArrayList<Task>());
		hasEditSwitch.turnOn();
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
		hasEditSwitch.turnOn();
	}
	
	@Override
	public void close() throws IOException {
		writeToFile();
	}

	public synchronized void writeToFile() throws IOException {
		File file = new File(fileName);
		
		Json.writeToFile(taskStorage.iterator(), file);
		hasEditSwitch.turnOff();
	}
	
	public void fileWriteNotify () {
		hasEditSwitch.turnOn();
	}
}
