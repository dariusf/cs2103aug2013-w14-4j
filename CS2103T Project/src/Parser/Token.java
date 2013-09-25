package Parser;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CORBA.PRIVATE_MEMBER;

public class Token {
	String thing = "";

	public Token(String name) {
		thing = name;
	}

	public String toString() {
		return thing;
	}
}

class DateToken extends Token {

	public DateToken(String name) {
		super(name);
	}

	public String toString() {
		return "Date " + super.toString();
	}

}

class TimeToken extends Token {

	private static final String REGEX_TIME = "(1[012]|[1-9]):([0-5][0-9])\\s?(?i)(am|pm)|([01]?[0-9]|2[0-3]):([0-5][0-9])";
	private static Pattern timePattern = Pattern.compile(REGEX_TIME);

	// TODO: get rid of this
	private enum Period {
		AM, PM
	}
	
	int hour;
	int minute;
	Period period;
	
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
			period = matcher.group(3).equalsIgnoreCase("am") ? Period.AM : Period.PM;
			int a = 1;
		}
	}

	private void setPeriod() {
		if (hour - 12 < 0) {
			period = Period.AM;
		}
		else {
			period = Period.PM;
			hour -= 12;
		}
	}
	
	public String timeString() {
		return hour + ":" + (minute < 10 ? "0" + minute : minute) + " " + (period == Period.AM ? "am" : "pm");
	}
	
	public String toString() {
		return "Time " + thing;
	}
}

class WordToken extends Token {

	public WordToken(String name) {
		super(name);
	}

	public String toString() {
		return "Word " + super.toString();
	}

}

class KeywordToken extends Token {

	public KeywordToken(String name) {
		super(name);
	}


	public String toString() {
		return "Keyword " + super.toString();
	}


}