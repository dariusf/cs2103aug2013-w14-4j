package common;

//@author A0097282W
public enum ClearMode {
	DEADLINE, TIMED, TENTATIVE, UNTIMED, ALL, OVERDUE, DATE, INVALID, DONE;
	
	public static ClearMode fromString(String clearModeString) {
		try {
			return ClearMode.valueOf(clearModeString.toUpperCase());
		} catch (IllegalArgumentException e) {
			return ClearMode.INVALID;
		}
	}
}
