package ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.undo.UndoableEdit;

import common.DisplayMode;

public class DisplayStateHistory {
	List<DisplayMode> displayModeHistory;
	List<Integer> pageNumberHistory;
	int index = -1;
	
	public DisplayStateHistory(){
		displayModeHistory = new ArrayList<>();
		pageNumberHistory = new ArrayList<>();
	}
	
	public void addDisplayState(DisplayMode displayMode, int pageNumber){
		displayModeHistory.add(displayMode);
		pageNumberHistory.add(pageNumber);
		index++;
	}
	
	public DisplayMode getCurrentDisplayMode(){
		return displayModeHistory.get(index);		
	}
	
	public int getCurrentPageNumber(){
		return pageNumberHistory.get(index);
	}
	
	public void undo(){
		if(isUndoable()){
			index--;
		}
	}
	
	public void redo(){
		if(isRedoable()){
			index++;
		}
	}
	
	public boolean isUndoable(){
		return index > 0;
	}
	
	public boolean isRedoable(){
		return index < displayModeHistory.size()-1;
	}
	
}
