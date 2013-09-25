package UI;

import java.util.Scanner;

import Logic.Constants;
import Logic.Feedback;
import Logic.Logic;

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
		String welcome = String.format(Constants.WELCOME_MSG, "");//fileName);
		showToUser(welcome);
	}
	
	private static Feedback readCommand() {
		System.out.print(Constants.MSG_COMMAND);
		String command = scanner.nextLine();
		return basketEngine.executeCommand(command);
	}
	
	private static void showToUser(String feedback) {
		System.out.println(feedback);
	}
	
	private static void displayFeedback(Feedback feedback){
		
	}
}
