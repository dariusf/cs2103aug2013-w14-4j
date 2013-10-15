package parser;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public class DateToken extends Token {

	private static DateTime nowStub = null; // for testing purposes

	private static final String REGEX_STANDARD_DATE = "(0?[1-9]|[12][0-9]|3[01])[-/](1[012]|0?[1-9])([-/]((19|20)?[0-9][0-9]))?";
	private static Pattern standardDate = Pattern.compile(REGEX_STANDARD_DATE, Pattern.CASE_INSENSITIVE);

	private static final String REGEX_RELATIVE_DAY_DATE = "((this|next|last)[ ]+)?(((mon|tues|wednes|thurs|fri|satur|sun)day)|mon|tues|tue|wed|thurs|thu|fri|sat|sun)";
	private static Pattern relativeDayDate = Pattern.compile(REGEX_RELATIVE_DAY_DATE, Pattern.CASE_INSENSITIVE);

	int day;
	int month;
	int year;
	
	public DateToken(String contents) {
		super(contents);
		
		if (matchStandardDate(contents)) return;
		if (matchRelativeDayDate(contents)) return;
		else assert false : "Date token contents did not match anything";
	}

	private boolean matchRelativeDayDate(String contents) {
		Matcher matcher = relativeDayDate.matcher(contents);
		
		if (!matcher.find()) return false;
				
		// Capturing groups:
		
		// 1: qualifier with trailing spaces
		// 2: qualifier
		// 3: day with short forms
		// 4: day without short forms
		// 5: <fragment>
				
		String qualifier = matcher.group(2);
		String dayString = matcher.group(3);
		assert dayString != null;
		int dayIndex = dayOfWeek(dayString);
		
		DateTime now = nowStub == null ? new DateTime() : nowStub;
		DateTime date = now.withDayOfWeek(dayIndex);
			
		if (qualifier != null) {
			if (qualifier.equalsIgnoreCase("next")) {
				date = date.plusWeeks(1);
			}
			else if (qualifier.equalsIgnoreCase("last")) {
				date = date.minusWeeks(1);
			}
			// "this" is not caught; same effect as qualifier being null
		}
		
		year = date.getYear();
		month = date.getMonthOfYear();
		day = date.getDayOfMonth();

		return true;
	}

	private int dayOfWeek(String day) {
		day = day.toLowerCase();
		switch (day) {
		case "mon":
		case "monday":
			return DateTimeConstants.MONDAY;
		case "tues":
		case "tue":
		case "tuesday":
			return DateTimeConstants.TUESDAY;
		case "wed":
		case "wednesday":
			return DateTimeConstants.WEDNESDAY;
		case "thu":
		case "thurs":
		case "thursday":
			return DateTimeConstants.THURSDAY;
		case "fri":
		case "friday":
			return DateTimeConstants.FRIDAY;
		case "sat":
		case "saturday":
			return DateTimeConstants.SATURDAY;
		case "sun":
		case "sunday":
			return DateTimeConstants.SUNDAY;
		default:
			assert false : "Cannot parse date string";
		}
		return 0;
	}

	public boolean matchStandardDate(String input) {
		Matcher matcher = standardDate.matcher(contents); 
		
		if (!matcher.find()) return false;
		
		// Capturing groups:
		
		// 1: day
		// 2: month
		// 3: [-/] year
		// 4: year
		// 5: first 2 digits of year

		day = Integer.parseInt(matcher.group(1));
		month = Integer.parseInt(matcher.group(2));
		String yearString = matcher.group(4);
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

		return true;
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

	public static void setNowStub(DateTime now) {
		nowStub = now;
	}
}