package Logic;
import java.util.HashMap;

public class Command {
	private static CommandType type = null;
	private static HashMap<String, String> commandAttributes = null;
	
	public Command(CommandType command, HashMap<String, String> attributes){
		type = command;
		commandAttributes = attributes;
	}
	
	public Command(CommandType command){
		type = command;
		commandAttributes = new HashMap<String, String>();
	}
	
	public static CommandType getCommandType(){
		return type;
	}
	
	public static HashMap<String, String> getCommandAttributes(){
		return commandAttributes;
	}
	
	public void setValue(String attribute, String value){
		commandAttributes.put(attribute, value);
	}
	
    public String toString() {
    	StringBuilder result = new StringBuilder(type.toString() + " ");
    	result.append("{");
    	
    	int entryCount = commandAttributes.keySet().size();
    	int i=0;
    	
    	for (String prop : commandAttributes.keySet()) {
    		result.append(prop + ": " + commandAttributes.get(prop));
    		if (i < entryCount-1) result.append(", ");
    		i++;
    	}
    	
    	result.append("}");
    	
    	return result.toString();
    }
}
