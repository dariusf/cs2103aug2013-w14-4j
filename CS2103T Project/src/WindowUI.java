import java.util.Scanner;

public class WindowUI {
	
	private static String fileName = "fileName";

	private static Scanner scanner = new Scanner(System.in);
	private static Logic basketEngine = new Logic();
	
	public static void main(String[] args) {
		checkArguments(args);
		displayWelcomeMessage();
		
		while (true) {
			Feedback feedback = readCommand();
			displayFeedback(feedback);
		}
	}
	
	private static void displayWelcomeMessage() {
		String welcome = String.format(Constants.WELCOME_MESSAGE, fileName);
		showToUser(welcome);
	}
	
	private static Feedback readCommand() {
		System.out.print(Constants.MESSAGE_COMMAND);
		String command = scanner.nextLine();
		return basketEngine.executeCommand(command);
	}
	
	private static void checkArguments(String[] args) {
		if (args.length == 0) {
			showToUser(Constants.MESSAGE_NO_FILENAME);
			basketEngine.exitProgram();
		}
		fileName = args[0];
	}
	
	private static void showToUser(Feedback feedback) {
		System.out.println(feedback);
	}
	
	private static void displayFeedback(Feedback feedback){
		
	}
}
