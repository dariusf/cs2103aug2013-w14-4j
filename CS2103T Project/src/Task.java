import java.util.HashMap;

public class Task {
	private static HashMap<String, String> taskAttributes = null;
	
	protected Task(HashMap<String, String> attributes){
		taskAttributes = attributes;
	}
	
	protected static String getValue(String attribute){
		return taskAttributes.get(attribute);
	}
	
	protected static void setValue(String attribute, String value){
		taskAttributes.put(attribute, value);
	}
}
