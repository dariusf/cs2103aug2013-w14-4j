package parser;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import common.Constants;

//@author A0097282W
public class DateToken extends Token {
        

		private static DateTime nowStub = null; // for testing purposes

        private static Pattern standardDate = Pattern.compile(Constants.PARSER_REGEX_STANDARD_DATE, Pattern.CASE_INSENSITIVE);
        private static Pattern relativeDate = Pattern.compile(Constants.PARSER_REGEX_RELATIVE_DATE, Pattern.CASE_INSENSITIVE);
        private static Pattern relativeDayDate = Pattern.compile(Constants.PARSER_REGEX_RELATIVE_DAY_DATE, Pattern.CASE_INSENSITIVE);
        private static Pattern aliasDate = Pattern.compile(Constants.PARSER_REGEX_ALIAS_DATE, Pattern.CASE_INSENSITIVE);
        private static Pattern mixedDate = Pattern.compile(Constants.PARSER_REGEX_MIXED_DATE, Pattern.CASE_INSENSITIVE);

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
                else assert false : Constants.PARSER_ASSERTION_ERROR_DATE_TOKEN_CONTENTS_DID_NOT_MATCH;
        }
        
        private boolean matchRelativeDate(String contents) {

                Matcher matcher = relativeDate.matcher(contents);
                
                if (!matcher.find()) return false;
                                
                // Capturing groups:
                
                // 1: next
                // 2: year/week
                
                DateTime now = nowStub == null ? new DateTime() : nowStub;
                String duration = matcher.group(2);
                
                if (duration.equalsIgnoreCase(Constants.PARSER_DATE_YEAR)) {
                        now = now.plusYears(1);
                }
                else if (duration.equalsIgnoreCase(Constants.PARSER_DATE_MONTH)) {
                        now = now.plusMonths(1);
                }
                else if (duration.equalsIgnoreCase(Constants.PARSER_DATE_FORTNIGHT)) {
                        now = now.plusWeeks(2);
                }
                else {
                        assert duration.equalsIgnoreCase(Constants.PARSER_DATE_WEEK) : String.format(Constants.PARSER_ASSERTION_ERROR_INVALID_DURATION, duration);
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
                case Constants.PARSER_DATE_YESTERDAY:
                        day = now.minusDays(1).getDayOfMonth();
                        month = now.getMonthOfYear();
                        year = now.getYear();
                        break;
                case Constants.PARSER_DATE_TODAY:
                case Constants.PARSER_DATE_TONIGHT:
                        day = now.getDayOfMonth();
                        month = now.getMonthOfYear();
                        year = now.getYear();
                        break;
                case Constants.PARSER_DATE_TOMORROW:
                case Constants.PARSER_DATE_TMR:
                case Constants.PARSER_DATE_TMRW:
                        now = now.plusDays(1);
                        day = now.getDayOfMonth();
                        month = now.getMonthOfYear();
                        year = now.getYear();
                        break;
                case Constants.PARSER_DATE_HALLOWEEN:
                        day = 31;
                        month = 10;
                        year = now.getYear();
                        break;
                case Constants.PARSER_DATE_CHRISTMAS:
                        day = 25;
                        month = 12;
                        year = now.getYear();
                        break;
                default:
                        assert false : Constants.PARSER_ASSERTION_ERROR_DATETOKEN_LOGIC;
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
                        if (qualifier.equalsIgnoreCase(Constants.PARSER_DATE_QUALIFIER_NEXT)) {
                                if (!date.minusDays(7).equals(now)) {
                                        date = date.plusWeeks(1);
                                }
                        }
                        else if (qualifier.equalsIgnoreCase(Constants.PARSER_DATE_QUALIFIER_LAST)) {
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
                return String.format(Constants.PARSER_DATESTRING, day, month, Integer.toString(year).substring(2));
        }
        
        public String toString() {
                return Constants.PARSER_TOKEN_PREFIX_DATETOKEN + super.toString();
        }
        
        public DateTime mergeInto(DateTime currentDateTime) {
                return currentDateTime.withDate(year, month, day);
        }

        public DateTime toDateTime() {
                return mergeInto(new DateTime());
        }

        public static void setNowStub(DateTime now) {
                nowStub = now;
        }
        
        private void setDate(int day, int month, int year) {
                assert year > 999 && year < 10000 : String.format(Constants.PARSER_ASSERTION_ERROR_YEAR_4_DIGITS, year);
                assert day >= 1 && day <= 31 : String.format(Constants.PARSER_ASSERTION_ERROR_DAY_1_31, day);
                assert month >= 1 && month <= 12 : String.format(Constants.PARSER_ASSERTION_ERROR_MONTH_1_12, month);
                
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
                        assert false : String.format(Constants.PARSER_ASSERTION_ERROR_INVALID_MONTH_OF_YEAR, month);
                }
                
                if (!valid) throw new IllegalDateException(String.format(Constants.PARSER_EXCEPTION_INVALID_DAY_D_OF_MONTH, day, month));
                
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
        case Constants.PARSER_DATE_JANUARY:
        case Constants.PARSER_DATE_JAN:
            return 1;
        case Constants.PARSER_DATE_FEBRUARY:
        case Constants.PARSER_DATE_FEB:
            return 2;
        case Constants.PARSER_DATE_MARCH:
        case Constants.PARSER_DATE_MAR:
            return 3;
        case Constants.PARSER_DATE_APRIL:
        case Constants.PARSER_DATE_APR:
            return 4;
        case Constants.PARSER_DATE_MAY:
            return 5;
        case Constants.PARSER_DATE_JUNE:
        case Constants.PARSER_DATE_JUN:
            return 6;
        case Constants.PARSER_DATE_JULY:
        case Constants.PARSER_DATE_JUL:
            return 7;
        case Constants.PARSER_DATE_AUGUST:
        case Constants.PARSER_DATE_AUG:
            return 8;
        case Constants.PARSER_DATE_SEPTEMBER:
        case Constants.PARSER_DATE_SEP:
            return 9;
        case Constants.PARSER_DATE_OCTOBER:
        case Constants.PARSER_DATE_OCT:
            return 10;
        case Constants.PARSER_DATE_NOVEMBER:
        case Constants.PARSER_DATE_NOV:
            return 11;
        case Constants.PARSER_DATE_DECEMBER:
        case Constants.PARSER_DATE_DEC:
            return 12;
        default:
            assert false : Constants.PARSER_ASSERTION_ERROR_INVALID_MONTH;
            return Constants.INVALID_INDEX;
        }
    }
    
        private int dayOfWeek(String day) {
                day = day.toLowerCase();
                switch (day) {
                case Constants.PARSER_DATE_MON:
                case Constants.PARSER_DATE_MONDAY:
                        return DateTimeConstants.MONDAY;
                case Constants.PARSER_DATE_TUES:
                case Constants.PARSER_DATE_TUE:
                case Constants.PARSER_DATE_TUESDAY:
                        return DateTimeConstants.TUESDAY;
                case Constants.PARSER_DATE_WED:
                case Constants.PARSER_DATE_WEDNESDAY:
                        return DateTimeConstants.WEDNESDAY;
                case Constants.PARSER_DATE_THU:
                case Constants.PARSER_DATE_THURS:
                case Constants.PARSER_DATE_THURSDAY:
                        return DateTimeConstants.THURSDAY;
                case Constants.PARSER_DATE_FRI:
                case Constants.PARSER_DATE_FRIDAY:
                        return DateTimeConstants.FRIDAY;
                case Constants.PARSER_DATE_SAT:
                case Constants.PARSER_DATE_SATURDAY:
                        return DateTimeConstants.SATURDAY;
                case Constants.PARSER_DATE_SUN:
                case Constants.PARSER_DATE_SUNDAY:
                        return DateTimeConstants.SUNDAY;
                default:
                        assert false : Constants.PARSER_ASSERTION_ERROR_CANNOT_PARSE_DATE_STRING;
                }
                return 0;
        }
}