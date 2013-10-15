package parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

public class TimeToken extends Token {

	private static final String REGEX_TIME = "(1[012]|[1-9]):([0-5][0-9])\\s?(?i)(am|pm)|([01]?[0-9]|2[0-3]):([0-5][0-9])";
	private static Pattern timePattern = Pattern.compile(REGEX_TIME);
	
	// Time is internally represented in 24-hour format
	int hour;
	int minute;
	
	public TimeToken(String contents) {
		super(contents);

		Matcher matcher = timePattern.matcher(contents); 
		matcher.find();

		if (matcher.group(1) == null) {
			// 24 hour time matched
			hour = Integer.parseInt(matcher.group(4));
			minute = Integer.parseInt(matcher.group(5));
		}
		else {
			// 12 hour time matched
			hour = Integer.parseInt(matcher.group(1));
			minute = Integer.parseInt(matcher.group(2));
			String period = matcher.group(3).trim();
			
			if (period.equalsIgnoreCase("pm")) {
				if (hour != 12) {
					hour += 12;
				}
			} else if (period.equalsIgnoreCase("am")) {
				if (hour == 12) {
					hour = 0;
				}
			}
		}
	}
	
	public String timeString() {
		String period = "am";
		int hour12 = hour;
		
		if (hour > 12) {
			period = "pm";
			hour12 -= 12;
		}
		
		return hour12 + ":" + (minute < 10 ? "0" + minute : minute) + " " + period;
	}
	
	public String toString() {
		return "Time " + contents;
	}
	
	public DateTime mergeInto(DateTime currentDateTime) {
		return currentDateTime.withTime(hour, minute, 0, 0);
	}
	
	public DateTime toDateTime() {
		return mergeInto(new DateTime());
	}
}