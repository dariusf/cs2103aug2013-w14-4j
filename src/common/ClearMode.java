package common;

public enum ClearMode {
	TODAY, TOMORROW, DEADLINE, TIMED, FLOATING, UNTIMED, ALL, OVERDUE, DATE, INVALID, TODO, DONE;
	
	public static ClearMode fromString(String clearModeString) {
		try {
			return ClearMode.valueOf(clearModeString.toUpperCase());
		} catch (IllegalArgumentException e) {
			return ClearMode.INVALID;
		}
	}
}
