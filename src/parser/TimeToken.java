package parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

public class TimeToken extends Token {

	private static final String REGEX_ALIAS_TIME = "(midnight|noon|morning|afternoon|evening)";
	private static Pattern aliasTimePattern = Pattern.compile(REGEX_ALIAS_TIME, Pattern.CASE_INSENSITIVE);

	private static final String REGEX_TIME = "(((1[012]|[1-9])([:.]([0-5][0-9]))?[ ]*(am|pm))|((2[0-3]|[01]?[0-9])[:.]?([0-5][0-9])))";
	private static Pattern timePattern = Pattern.compile(REGEX_TIME, Pattern.CASE_INSENSITIVE);
	
	// Time is internally represented in 24-hour format
	int hour;
	int minute;
	
	public TimeToken(String contents) {
		super(contents);

		if (matchTime(contents)) return;
		else if (matchAliasTime(contents)) return;
		else assert false : "Time token contents did not match anything; possibly a regex bug in either DateToken or lexer";
	}

	private boolean matchAliasTime(String contents) {
		Matcher matcher = aliasTimePattern.matcher(contents); 
		if (!matcher.find()) return false;
		
		// Capturing groups:
		
		// 1: whole
		
		String matchedWord = matcher.group(1);

		switch (matchedWord) {
		case "midnight":
			hour = 0;
			break;
		case "noon":
			hour = 12;
			break;
		case "morning":
			hour = 8;
			break;
		case "afternoon":
			hour = 13;
			break;
		case "evening":
			hour = 19;
			break;
		default:
			break;
		}
		
		minute = 0;
		
		return true;
	}

	private boolean matchTime(String contents) {
		Matcher matcher = timePattern.matcher(contents); 
		if (!matcher.find()) return false;
		
		// Capturing groups:
		
		// 1: whole
		
		// 2: 12-hour time
		// 3: hour
		// 4: :minute
		// 5: minute
		// 6: period

		// 7: 24-hour time
		// 8: hour
		// 9: minute

		if (matcher.group(2) == null) {
			// 24 hour time matched
			hour = Integer.parseInt(matcher.group(8));
			minute = Integer.parseInt(matcher.group(9));
		}
		else {
			// 12 hour time matched
			hour = Integer.parseInt(matcher.group(3));
			if (matcher.group(5) != null) {
				minute = Integer.parseInt(matcher.group(5));
			}
			else {
				minute = 0;
			}
			String period = matcher.group(6).trim();
			
			if (period.equalsIgnoreCase("pm")) {
				if (hour != 12) {
					hour += 12;
				}
			} else {
				assert period.equalsIgnoreCase("am");
				if (hour == 12) {
					hour = 0;
				}
			}
		}
		return true;
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