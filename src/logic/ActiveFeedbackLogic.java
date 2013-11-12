//@author A0097282W
package logic;

import java.util.ArrayList;

import org.eclipse.swt.SWT;

import parser.Parser;

import ui.ContextualHelp;
import ui.TextFormatter;
import ui.ApplicationWindow;

import common.Command;
import common.CommandType;
import common.Constants;
import common.Feedback;
import common.Interval;
import common.Task;
import common.TaskComposite;
import common.TaskType;

public class ActiveFeedbackLogic {
	
	DisplayLogic displayLogic;
	CommandLogic commandLogic;
	ApplicationWindow window;
	
	public ActiveFeedbackLogic(CommandLogic commandLogic, DisplayLogic displayLogic, ApplicationWindow window){
		this.displayLogic = displayLogic;
		this.commandLogic = commandLogic;
		this.window = window;
	}
	
	public Command getActiveFeedback(String userInput) {
		Command command = Parser.parse(userInput);
		return command;
	}
	
	public void processActiveFeedback(String userInput) {
		Command executedCommand = getActiveFeedback(userInput);
		if (executedCommand == null) {
			displayLogic.clearHighlightedTasks();
			return;
		}

		int taskIndex = executedCommand.getTaskIndex();
		CommandType commandType = executedCommand.getCommandType();
		window.displayFeedback.setForeground(window.purple);

		ContextualHelp contextualHelp = new ContextualHelp(
				executedCommand);
		TextFormatter.setFormattedText(window.displayFeedback,
				contextualHelp.toString());

		switch (commandType) {
		case DONE:
			// Fall through
		case DELETE:
			if (taskIndex > 0
					&& taskIndex <= commandLogic.getNumberOfTasks()) {
				highlightTaskFeedback(taskIndex);
			} else if (taskIndex != Constants.INVALID_INDEX) {
				displayInvalidIndexAsFeedback();
			} else {
				defaultFeedback();
			}
			break;
		case FINALISE:
			if (taskIndex > 0
					&& taskIndex <= commandLogic.getNumberOfTasks()) {
				finaliseTaskFeedback(executedCommand, taskIndex);
			} else if (taskIndex != Constants.INVALID_INDEX) {
				displayInvalidIndexAsFeedback();
				return;
			} else {
				defaultFeedback();
			}
			break;
		case EDIT:
			if (taskIndex > 0
					&& taskIndex <= commandLogic.getNumberOfTasks()) {
				editTaskFeedback(executedCommand, taskIndex);
			} else if (taskIndex != Constants.INVALID_INDEX) {
				defaultFeedback();
				displayInvalidIndexAsFeedback();
				return;
			} else {
				defaultFeedback();
			}
			break;
		case ADD:
			addTaskFeedback(executedCommand);
			break;
		case SEARCH:
			searchTaskFeedback(userInput, executedCommand);
			break;
		case INVALID:
			// Fall through
		default:
			defaultFeedback();
			break;
		}

	}

	private void displayInvalidIndexAsFeedback() {
		window.displayFeedback.setText("Task index is not valid!");
		window.displayFeedback.setForeground(window.red);
	}

	private void defaultFeedback() {
		displayLogic.clearHighlightedTasks();
		window.updateTaskDisplay();
		if (window.dummyTaskComposite != null) {
			window.dummyTaskComposite.dispose();
			window.dummyCompositeIsCreated = false;
		}
	}

	private void searchTaskFeedback(String userInput, Command executedCommand) {
		if (!executedCommand.getSearchTerms().isEmpty()
				|| !executedCommand.getTags().isEmpty()) {
			Feedback feedbackObj = commandLogic
					.executeCommand(userInput);
			String feedback = feedbackObj.toString();
			setFeedbackColour(feedbackObj);
			window.displayFeedback.setText(feedback);
			displayLogic.processFeedback(feedbackObj);
			window.updateTaskDisplay();
		}
	}

	private void addTaskFeedback(Command executedCommand) {
		Task dummyTask = new Task(executedCommand);
		if (dummyTask.isEmpty()) {
			defaultFeedback();
		} else {
			if (window.dummyCompositeIsCreated) {
				boolean willOverflow = displayLogic
						.getTaskDisplayHeight()
						- window.dummyTaskComposite.getSize().y
						+ displayLogic.determineTaskHeight(dummyTask) > 450;
				if (willOverflow) {
					displayLogic.deleteTaskComposites();
					int newLastPageIndex = displayLogic
							.getNumberOfPages() + 1;
					window.displayPageNumber.setText("Page "
							+ newLastPageIndex + " of "
							+ newLastPageIndex);
					window.displayPageNumber.setAlignment(SWT.CENTER);
				}
				modifyDummyTaskComposite(executedCommand);
				window.dummyTaskComposite.pack();
				displayLogic.getTaskDisplay().pack();
			} else {
				window.dummyCompositeIsCreated = true;
				displayLogic.goToLastPage();
				window.updateTaskDisplay();

				boolean willOverflow = displayLogic
						.getTaskDisplayHeight()
						+ displayLogic.determineTaskHeight(dummyTask) > 450;

				if (willOverflow) {
					displayLogic.deleteTaskComposites();
					int newLastPageIndex = displayLogic
							.getNumberOfPages() + 1;
					window.displayPageNumber.setText("Page "
							+ newLastPageIndex + " of "
							+ newLastPageIndex);
					window.displayPageNumber.setAlignment(SWT.CENTER);
				} else {
					displayLogic.goToLastPage();
					window.updateTaskDisplay();
				}

				window.dummyTaskComposite = new TaskComposite(displayLogic
						.getTaskDisplay(), dummyTask, displayLogic
						.getTotalNumberOfComposites() + 1);
				window.dummyTaskComposite.setHighlighted(true);
				modifyDummyTaskComposite(executedCommand);
				displayLogic.getTaskDisplay().pack();
			}

		}
	}

	private void editTaskFeedback(Command executedCommand, int taskIndex) {

		highlightTaskFeedback(taskIndex);
		TaskComposite currentComposite = displayLogic
				.getCompositeGlobal(taskIndex);

		if (executedCommand.getTimeslotIndex() != Constants.INVALID_INDEX) {
			modifyTimeSlotOfTaskComposite(executedCommand, currentComposite);
		} else {
			modifyTaskComposite(executedCommand, currentComposite);
		}

	}

	private void finaliseTaskFeedback(Command executedCommand,
			int taskIndex) {
		highlightTaskFeedback(taskIndex);
		TaskComposite currentComposite = displayLogic.getCompositeGlobal(taskIndex);
		if (executedCommand.getTimeslotIndex() > 0 && currentComposite.isTentativeTaskComposite()) {
			currentComposite.highlightLine(
					executedCommand.getTimeslotIndex());
		}
	}

	private void highlightTaskFeedback(int taskIndex) {
		displayLogic.clearHighlightedTasks();
		displayLogic.addHighlightedTask(taskIndex);
		displayLogic.setPageNumber(displayLogic
				.getPageOfTask(taskIndex));
		window.updateTaskDisplay();
	}
	
	private void modifyDummyTaskComposite(Command executedCommand) {
		TaskType finalType = executedCommand.getTaskType();
		if (!executedCommand.getDescription().isEmpty()) {
			window.dummyTaskComposite.setTaskName(executedCommand
					.getDescription());
		}
		StringBuilder descriptionBuilder = new StringBuilder();
		if (finalType.equals(TaskType.DEADLINE)) {
			descriptionBuilder.append("by "
					+ Task.format(executedCommand.getDeadline()));
		} else if (finalType.equals(TaskType.TIMED)) {
			Interval taskInterval = executedCommand.getIntervals().get(
					0);
			descriptionBuilder.append("from "
					+ Task.intervalFormat(
							taskInterval.getStartDateTime(),
							taskInterval.getEndDateTime()));
		} else if (finalType.equals(TaskType.TENTATIVE)) {
			descriptionBuilder.append("on ");
			ArrayList<Interval> possibleIntervals = executedCommand
					.getIntervals();
			int index = 1;
			for (Interval slot : possibleIntervals) {
				descriptionBuilder.append("(");
				descriptionBuilder.append(index);
				descriptionBuilder.append(") ");
				descriptionBuilder.append(Task.intervalFormat(
						slot.getStartDateTime(), slot.getEndDateTime()));
				if (index != possibleIntervals.size()) {
					descriptionBuilder.append("\nor ");
				}
				index++;
			}
		}
		ArrayList<String> tags = executedCommand.getTags();
		if (tags.size() > 0) {
			if (finalType.equals(TaskType.DEADLINE)
					| finalType.equals(TaskType.TIMED)
					| finalType.equals(TaskType.TENTATIVE)) {
				descriptionBuilder.append("\n");
			}
			for (String tag : tags) {
				descriptionBuilder.append(tag + " ");
			}
		}
	
		window.dummyTaskComposite.setDescription(descriptionBuilder.toString());
		window.dummyTaskComposite.pack();
	}

	private void modifyTaskComposite(Command executedCommand,
			TaskComposite currentComposite) {
		TaskType finalType = executedCommand.getTaskType();
	
		if (!executedCommand.getDescription().isEmpty()) {
			currentComposite.setTaskName(executedCommand
					.getDescription());
		}
	
		StringBuilder descriptionBuilder = new StringBuilder();
		if (finalType.equals(TaskType.DEADLINE)) {
			descriptionBuilder.append("by "
					+ Task.format(executedCommand.getDeadline()));
		} else if (finalType.equals(TaskType.TIMED)) {
			Interval taskInterval = executedCommand.getIntervals()
					.get(0);
			descriptionBuilder.append("from ");
			descriptionBuilder.append(Task.intervalFormat(
					taskInterval.getStartDateTime(),
					taskInterval.getEndDateTime()));
		} else if (finalType.equals(TaskType.TENTATIVE)) {
			descriptionBuilder.append("on ");
			ArrayList<Interval> possibleIntervals = executedCommand
					.getIntervals();
			int index = 1;
			for (Interval slot : possibleIntervals) {
				descriptionBuilder.append("(");
				descriptionBuilder.append(index);
				descriptionBuilder.append(") ");
				descriptionBuilder.append(Task.intervalFormat(
						slot.getStartDateTime(),
						slot.getEndDateTime()));
				if (index != possibleIntervals.size()) {
					descriptionBuilder.append("\nor ");
				}
				index++;
			}
		} else {
			descriptionBuilder.append(currentComposite
					.getTimeString());
		}
	
		String currentTags = currentComposite.getTags();
		ArrayList<String> newTags = executedCommand.getTags();
		StringBuilder tagsBuilder = new StringBuilder();
		tagsBuilder.append(currentTags);
	
		for (String tag : newTags) {
			tagsBuilder.append(tag);
			tagsBuilder.append(" ");
		}
	
		if (tagsBuilder.length() > 0) {
			if (!descriptionBuilder.toString().isEmpty()) {
				descriptionBuilder.append("\n");
			}
			descriptionBuilder.append(tagsBuilder.toString());
		}
		if (!descriptionBuilder.toString().isEmpty()) {
			currentComposite.setDescription(descriptionBuilder
					.toString());
		}
	
		currentComposite.pack();
	}

	private void modifyTimeSlotOfTaskComposite(Command executedCommand,
			TaskComposite currentComposite) {
		if (executedCommand.getTimeslotIndex() > 0) {
			int timeSlot = executedCommand.getTimeslotIndex();
			ArrayList<Interval> possibleIntervals = executedCommand
					.getIntervals();
			if (!possibleIntervals.isEmpty()) {
				Interval interval = possibleIntervals.get(0);
				if (timeSlot == 1) {
					String description = "on (1) "
							+ Task.intervalFormat(
									interval.getStartDateTime(),
									interval.getEndDateTime());
					;
					currentComposite.setTentativeTaskAtLine(
							description, timeSlot);
				} else {
					String description = "or ("
							+ timeSlot
							+ ") "
							+ Task.intervalFormat(
									interval.getStartDateTime(),
									interval.getEndDateTime());
					currentComposite.setTentativeTaskAtLine(
							description, timeSlot);
	
				}
				currentComposite.highlightLine(timeSlot);
			}
		}
	}

	protected void setFeedbackColour(Feedback feedbackObj) {
		if (feedbackObj.isErrorMessage()) {
			window.displayFeedback.setForeground(window.red);
		} else if (!feedbackObj.isErrorMessage()) {
			window.displayFeedback.setForeground(window.green);
		}
	}
}
