package common;

public enum DisplayMode {
	TODAY, TOMORROW, DEADLINE, TIMED, FLOATING, UNTIMED, ALL, SEARCH, OVERDUE, DATE, INVALID, TODO;
	
	public static DisplayMode fromString(String displayString) {
		try {
			return DisplayMode.valueOf(displayString.toUpperCase());
		} catch (IllegalArgumentException e) {
			return DisplayMode.INVALID;
		}
	}
}
