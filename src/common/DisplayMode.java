package common;

//@author A0102332A

public enum DisplayMode {
	TODAY, TOMORROW, DEADLINE, TIMED, TENTATIVE, UNTIMED, ALL, SEARCH, OVERDUE, DATE, INVALID, TODO, DONE;
	
	public static DisplayMode fromString(String displayString) {
		try {
			return DisplayMode.valueOf(displayString.toUpperCase());
		} catch (IllegalArgumentException e) {
			return DisplayMode.INVALID;
		}
	}
}
