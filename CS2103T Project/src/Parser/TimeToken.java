package parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

class TimeToken extends Token {

	private static final String REGEX_TIME = "(1[012]|[1-9]):([0-5][0-9])\\s?(?i)(am|pm)|([01]?[0-9]|2[0-3]):([0-5][0-9])";
	private static Pattern timePattern = Pattern.compile(REGEX_TIME);
	
	int hour;
	int minute;
	String period;
	
	public TimeToken(String contents) {
		super(contents);

		Matcher matcher = timePattern.matcher(contents); 
		matcher.find();

		if (matcher.group(1) == null) {
			// 24 hour time matched
			hour = Integer.parseInt(matcher.group(4));
			minute = Integer.parseInt(matcher.group(5));
			setPeriod();
		}
		else {
			// 12 hour time matched
			hour = Integer.parseInt(matcher.group(1));
			minute = Integer.parseInt(matcher.group(2));
			period = matcher.group(3).toLowerCase();
		}
	}

	private void setPeriod() {
		if (hour - 12 < 0) {
			period = "am";
		}
		else {
			period = "pm";
			hour -= 12;
		}
	}
	
	// TODO: consider using tostirng instead
	public String timeString() {
		return hour + ":" + (minute < 10 ? "0" + minute : minute) + " " + period;
	}
	
	public String toString() {
		return "Time " + contents;
	}
	
	public DateTime toDateTime() {
		
		// convert to 24 hour time
		
		int h = hour;
		if (hour == 12 && period.equals("am")) {
			h = 0;
		}
		else if (period.equals("pm") && hour != 12) {
			h = hour + 12;
		}
		
		return new DateTime().withTime(h, minute, 0, 0);
	}
}