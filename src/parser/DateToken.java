package parser;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public class DateToken extends Token {

	public static void main(String[] args){
		java.util.Scanner scanner = new java.util.Scanner(System.in);
		while(true) {
			DateToken randomDateToken = new DateToken("12 Oct");
			String input = scanner.nextLine();
			System.out.println(randomDateToken.isValidDateString(input));
		}
	}
	
	private static DateTime nowStub = null; // for testing purposes

	private static final String REGEX_STANDARD_DATE = "(0?[1-9]|[12][0-9]|3[01])[-/](1[012]|0?[1-9])([-/]((19|20)?[0-9][0-9]))?";
	private static Pattern standardDate = Pattern.compile(REGEX_STANDARD_DATE, Pattern.CASE_INSENSITIVE);

	private static final String REGEX_RELATIVE_DAY_DATE = "((this|next|last)[ ]+)?(((mon|tues|wednes|thurs|fri|satur|sun)day)|mon|tues|tue|wed|thurs|thu|fri|sat|sun)";
	private static Pattern relativeDayDate = Pattern.compile(REGEX_RELATIVE_DAY_DATE, Pattern.CASE_INSENSITIVE);

	private static final String REGEX_ALIAS_DATE = "(today|tomorrow|tmrw|tmr|halloween)";
	private static Pattern aliasDate = Pattern.compile(REGEX_ALIAS_DATE, Pattern.CASE_INSENSITIVE);
	
	private static final String REGEX_MIXED_DATE = "((0?[1-9]|[12][0-9]|3[01])[ ]*(january|february|march|april|may|june|july|august|september|october|november|december|jan|feb|mar|apr|jun|jul|aug|sep|oct|nov|dec)[ ]*((19|20)?[0-9][0-9])?)";
	private static Pattern mixedDate = Pattern.compile(REGEX_MIXED_DATE, Pattern.CASE_INSENSITIVE);

	int day;
	int month;
	int year;
	
	public DateToken(String contents) {
		super(contents);
		
		if (matchStandardDate(contents)) return;
		else if (matchRelativeDayDate(contents)) return;
		else if (matchAliasDate(contents)) return;
		else if (matchMixedDate(contents)) return;
		else assert false : "Date token contents did not match anything; possibly a regex bug in either DateToken or lexer";
	}

	public boolean isValidDateString(String contents){
		return matchStandardDate(contents) || matchRelativeDayDate(contents) || matchAliasDate(contents) || matchMixedDate(contents);
	}
	
	private boolean matchMixedDate(String contents) {

		Matcher matcher = mixedDate.matcher(contents);
		
		if (!matcher.find()) return false;
				
		// Capturing groups:
		
		// 1: everything
		// 2: day
		// 3: month string
		// 4: year
		// 5: <fragment>
		
		day = Integer.parseInt(matcher.group(2));
		month = monthNumber(matcher.group(3));
		DateTime now = nowStub == null ? new DateTime() : nowStub;
		year = matcher.group(4) == null ? now.getYear() : checkYear(Integer.parseInt(matcher.group(4)));

		return true;
	}
	
	private boolean matchAliasDate(String contents) {

		Matcher matcher = aliasDate.matcher(contents);
		
		if (!matcher.find()) return false;
				
		// Capturing groups:
		
		// 1: the alias
		
		String dateString = matcher.group(1).toLowerCase();
		DateTime now = nowStub == null ? new DateTime() : nowStub;
		
		switch (dateString) {
		case "today":
			year = now.getYear();
			month = now.getMonthOfYear();
			day = now.getDayOfMonth();
			break;
		case "tomorrow":
		case "tmr":
		case "tmrw":
			now = now.plusDays(1);
			year = now.getYear();
			month = now.getMonthOfYear();
			day = now.getDayOfMonth();
			break;
		case "halloween":
			year = now.getYear();
			month = 10;
			day = 31;
			break;
		default:
			assert false : "Error in DateToken logic";
		}

		return true;
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
			year = checkYear(Integer.parseInt(yearString));
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
	
	private int checkYear (int year) {
		if (year < 100) {
			if (year > 50) {
				return year + 1900;
			}
			else {
				return year + 2000;
			}
		}
		return year;
	}
	
    private int monthNumber(String monthString) {
        monthString = monthString.toLowerCase();
        switch (monthString) {
        case "january":
        case "jan":
            return 1;
        case "february":
        case "feb":
            return 2;
        case "march":
        case "mar":
            return 3;
        case "april":
        case "apr":
            return 4;
        case "may":
            return 5;
        case "june":
        case "jun":
            return 6;
        case "july":
        case "jul":
            return 7;
        case "august":
        case "aug":
            return 8;
        case "september":
        case "sep":
            return 9;
        case "october":
        case "oct":
            return 10;
        case "november":
        case "nov":
            return 11;
        case "december":
        case "dec":
            return 12;
                    default:
            throw new IllegalArgumentException("Invalid month");
        }
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
}