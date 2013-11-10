package parser;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

//@author A0097282W
public class DateToken extends Token {
	
	private static DateTime nowStub = null; // for testing purposes

	private static final String REGEX_STANDARD_DATE = "(0?[1-9]|[12][0-9]|3[01])[-/](1[012]|0?[1-9])([-/]((19|20)?[0-9][0-9]))?";
	private static Pattern standardDate = Pattern.compile(REGEX_STANDARD_DATE, Pattern.CASE_INSENSITIVE);

	private static final String REGEX_RELATIVE_DATE = "(next[ ]+)(week|year|month|fortnight)";
	private static Pattern relativeDate = Pattern.compile(REGEX_RELATIVE_DATE, Pattern.CASE_INSENSITIVE);

	private static final String REGEX_RELATIVE_DAY_DATE = "((this|next|last)[ ]+)?(((mon|tues|wednes|thurs|fri|satur|sun)day)|mon|tues|tue|wed|thurs|thu|fri|sat|sun)";
	private static Pattern relativeDayDate = Pattern.compile(REGEX_RELATIVE_DAY_DATE, Pattern.CASE_INSENSITIVE);

	private static final String REGEX_ALIAS_DATE = "(yesterday|today|tonight|tomorrow|tmrw|tmr|halloween|christmas)";
	private static Pattern aliasDate = Pattern.compile(REGEX_ALIAS_DATE, Pattern.CASE_INSENSITIVE);
	
	private static final String REGEX_MIXED_DATE = "((0?[1-9]|[12][0-9]|3[01])[ ]*(january|february|march|april|may|june|july|august|september|october|november|december|jan|feb|mar|apr|jun|jul|aug|sep|oct|nov|dec)[ ]*((19|20)?[0-9][0-9])?)";
	private static Pattern mixedDate = Pattern.compile(REGEX_MIXED_DATE, Pattern.CASE_INSENSITIVE);

	int day;
	int month;
	int year;
	
	public DateToken(String contents) {
		super(contents);
		
		if (matchStandardDate(contents)) return;
		else if (matchRelativeDate(contents)) return;
		else if (matchRelativeDayDate(contents)) return;
		else if (matchAliasDate(contents)) return;
		else if (matchMixedDate(contents)) return;
		else assert false : "Date token contents did not match anything; possibly a regex bug in either DateToken or lexer";
	}
	
	private boolean matchRelativeDate(String contents) {

		Matcher matcher = relativeDate.matcher(contents);
		
		if (!matcher.find()) return false;
				
		// Capturing groups:
		
		// 1: next
		// 2: year/week
		
		DateTime now = nowStub == null ? new DateTime() : nowStub;
		String duration = matcher.group(2);
		
		if (duration.equalsIgnoreCase("year")) {
			now = now.plusYears(1);
		}
		else if (duration.equalsIgnoreCase("month")) {
			now = now.plusMonths(1);
		}
		else if (duration.equalsIgnoreCase("fortnight")) {
			now = now.plusWeeks(2);
		}
		else {
			assert duration.equalsIgnoreCase("week") : "Invalid duration " + duration + "; possibly a regex bug in either DateToken or lexer";
			now = now.plusWeeks(1);
		}

		int day = now.getDayOfMonth();
		int month = now.getMonthOfYear();
		int year = now.getYear();
		
		setDate(day, month, year);

		return true;
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
		
		int day = Integer.parseInt(matcher.group(2));
		int month = monthNumber(matcher.group(3));
		DateTime now = nowStub == null ? new DateTime() : nowStub;
		int year = matcher.group(4) == null ? now.getYear() : checkYear(Integer.parseInt(matcher.group(4)));

		setDate(day, month, year);
		
		return true;
	}
	
	private boolean matchAliasDate(String contents) {

		Matcher matcher = aliasDate.matcher(contents);
		
		if (!matcher.find()) return false;
				
		// Capturing groups:
		
		// 1: the alias
		
		String dateString = matcher.group(1).toLowerCase();
		DateTime now = nowStub == null ? new DateTime() : nowStub;
		int day = 0, month = 0, year = 0;
		
		switch (dateString) {
		case "yesterday":
			day = now.minusDays(1).getDayOfMonth();
			month = now.getMonthOfYear();
			year = now.getYear();
			break;
		case "today":
		case "tonight":
			day = now.getDayOfMonth();
			month = now.getMonthOfYear();
			year = now.getYear();
			break;
		case "tomorrow":
		case "tmr":
		case "tmrw":
			now = now.plusDays(1);
			day = now.getDayOfMonth();
			month = now.getMonthOfYear();
			year = now.getYear();
			break;
		case "halloween":
			day = 31;
			month = 10;
			year = now.getYear();
			break;
		case "christmas":
			day = 25;
			month = 12;
			year = now.getYear();
			break;
		default:
			assert false : "Error in DateToken logic";
		}
		
		setDate(day, month, year);

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
		
		// Scheme for day qualifiers:
		
		// Let x be the current day
		// last = x-7 to x-1
		// this = x+1 to x+7
		// next = x+7 to x+13 (1 day overlap with 'this')
		
		DateTime date = now.withDayOfWeek(dayIndex);
		if (date.isBefore(now) || date.isEqual(now)) {
			date = date.plusWeeks(1);
		}
		
		if (qualifier != null) {
			if (qualifier.equalsIgnoreCase("next")) {
				if (!date.minusDays(7).equals(now)) {
					date = date.plusWeeks(1);
				}
			}
			else if (qualifier.equalsIgnoreCase("last")) {
				date = date.minusWeeks(1);
				if (date.isEqual(now)) {
					date = date.minusWeeks(1);
				}
			}
			// 'this' is not caught; the logic for it is the same as that for
			// having no qualifier
		}
		
		
		int day = date.getDayOfMonth();
		int month = date.getMonthOfYear();
		int year = date.getYear();

		setDate(day, month, year);

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

		int day = Integer.parseInt(matcher.group(1));
		int month = Integer.parseInt(matcher.group(2));
		int year;
		String yearString = matcher.group(4);
		if (yearString == null) {
			year = Calendar.getInstance().get(Calendar.YEAR);
		}
		else {
			year = checkYear(Integer.parseInt(yearString));
		}
		
		setDate(day, month, year);

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

	public DateTime toDateTime() {
		return mergeInto(new DateTime());//.withTime(start ? 0 : 23, start ? 0 : 59, 0, 0);
	}

	public static void setNowStub(DateTime now) {
		nowStub = now;
	}
	
	private void setDate(int day, int month, int year) {
		assert year > 999 && year < 10000 : "Year " + year + " should only be 4 digits long";
		assert day >= 1 && day <= 31 : "Day " + day + " should be within [1, 31]";
		assert month >= 1 && month <= 12 : "Month " + month + " should be within [1, 12]";
		
		boolean valid = true;
		
		switch (month) {
		case DateTimeConstants.JANUARY:
		case DateTimeConstants.MARCH:
		case DateTimeConstants.MAY:
		case DateTimeConstants.JULY:
		case DateTimeConstants.AUGUST:
		case DateTimeConstants.OCTOBER:
		case DateTimeConstants.DECEMBER:
			valid = day >= 1 && day <= 31;
			break;
		case DateTimeConstants.FEBRUARY:
			boolean isLeapYear = new DateTime(year, 1, 1, 0, 0, 0, 0).year().isLeap();
			int lastDayOfMonth = isLeapYear ? 29 : 28;
			valid = day >= 1 && day <= lastDayOfMonth;
			break;
		case DateTimeConstants.APRIL:
		case DateTimeConstants.JUNE:
		case DateTimeConstants.SEPTEMBER:
		case DateTimeConstants.NOVEMBER:
			valid = day >= 1 && day <= 30;
			break;
		default:
			assert false : "Invalid month of year " + month;
		}
		
		if (!valid) throw new IllegalDateException("Invalid day " + day + " of month " + month);
		
		this.day = day;
		this.month = month;
		this.year = year;
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