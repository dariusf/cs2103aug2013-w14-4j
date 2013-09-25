package Logic;
import java.util.HashMap;

public class Command {
	private CommandType type = null;
	private HashMap<String, String> commandAttributes = null;
	
	public Command(CommandType command, HashMap<String, String> attributes){
		type = command;
		commandAttributes = attributes;
	}
	
	public Command(CommandType command){
		type = command;
		commandAttributes = new HashMap<String, String>();
	}
	
	public CommandType getCommandType(){
		return type;
	}
	
	public HashMap<String, String> getCommandAttributes(){
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((commandAttributes == null) ? 0 : commandAttributes
						.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Command other = (Command) obj;
		if (commandAttributes == null) {
			if (other.commandAttributes != null)
				return false;
		} else if (!commandAttributes.equals(other.commandAttributes))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
    
    
}
