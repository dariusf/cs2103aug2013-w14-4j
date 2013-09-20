import java.util.HashMap;

public class Command {
	private static CommandType type = null;
	private static HashMap<String, String> commandAttributes = null;
	
	protected Command(CommandType command, HashMap<String, String> attributes){
		type = command;
		commandAttributes = attributes;
	}
	
	protected Command(CommandType command){
		type = command;
		commandAttributes = new HashMap<String, String>();
	}
	
	protected static CommandType getCommandType(){
		return type;
	}
	
	protected static HashMap<String, String> getCommandAttributes(){
		return commandAttributes;
	}
	
	protected void setValue(String attribute, String value){
		commandAttributes.put(attribute, value);
	}
}
