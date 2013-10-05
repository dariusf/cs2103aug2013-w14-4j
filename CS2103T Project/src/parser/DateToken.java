package parser;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

public class DateToken extends Token {

	private static final String REGEX_DATE = "(0?[1-9]|[12][0-9]|3[01])([/-])(1[012]|0?[1-9])(\\2((19|20)?\\d\\d))?";
	private static Pattern datePattern = Pattern.compile(REGEX_DATE);

	int day;
	int month;
	int year;
	
	public DateToken(String contents) {
		super(contents);
		Matcher matcher = datePattern.matcher(contents); 
		matcher.find();

		day = Integer.parseInt(matcher.group(1));
		month = Integer.parseInt(matcher.group(3));
		String yearString = matcher.group(5);
		if (yearString == null) {
			year = Calendar.getInstance().get(Calendar.YEAR);
		}
		else {
			year = Integer.parseInt(yearString);
			if (year < 100) {
				if (year > 50) {
					year += 1900;
				}
				else {
					year += 2000;
				}
			}
		}
	}

	public String dateString() {
		return day + "/" + month + "/" + Integer.toString(year).substring(2);
	}
	
	public String toString() {
		return "Date " + super.toString();
	}
	
	public DateTime mergeInto(DateTime currentDateTime) {
		return currentDateTime.withDate(year, month, day);
	}

	public DateTime toDateTime(boolean start) {
		return mergeInto(new DateTime()).withTime(start ? 0 : 23, start ? 0 : 59, 0, 0);
	}
}