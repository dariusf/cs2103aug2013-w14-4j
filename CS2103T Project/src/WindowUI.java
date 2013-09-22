import java.util.Scanner;

public class WindowUI {

	private static Scanner scanner = new Scanner(System.in);
	private static Logic basketEngine = new Logic();
	
	public static void main(String[] args) {
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
	
	private static void showToUser(String feedback) {
		System.out.println(feedback);
	}
	
	private static void displayFeedback(Feedback feedback){
		
	}
}
