package parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import common.Constants;

//@author A0097282W
public class TimeToken extends Token {

    public static Pattern aliasTimePattern = Pattern.compile(Constants.PARSER_REGEX_ALIAS_TIME, Pattern.CASE_INSENSITIVE);
    private static Pattern timePattern = Pattern.compile(Constants.PARSER_REGEX_TIME, Pattern.CASE_INSENSITIVE);
    
    // Time is internally represented in 24-hour format
    int hour;
    int minute;
    
    public TimeToken(String contents) {
        super(contents);

        if (matchTime(contents)) return;
        else if (matchAliasTime(contents)) return;
        else assert false : Constants.PARSER_ASSERTION_ERROR_TIMETOKEN_CONTENTS_DID_NOT_MATCH_ANYTHING;
    }

    private boolean matchAliasTime(String contents) {
        Matcher matcher = aliasTimePattern.matcher(contents); 
        if (!matcher.find()) return false;
        
        // Capturing groups:
        
        // 1: whole
        
        String matchedWord = matcher.group(1);

        switch (matchedWord) {
        case Constants.PARSER_TIME_MIDNIGHT:
            hour = 0;
            break;
        case Constants.PARSER_TIME_NOON:
            hour = 12;
            break;
        case Constants.PARSER_TIME_MORNING:
            hour = 8;
            break;
        case Constants.PARSER_TIME_AFTERNOON:
            hour = 13;
            break;
        case Constants.PARSER_TIME_EVENING:
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
            
            if (period.equalsIgnoreCase(Constants.PARSER_TIME_PERIOD_PM)) {
                if (hour != 12) {
                    hour += 12;
                }
            } else {
                assert period.equalsIgnoreCase(Constants.PARSER_TIME_PERIOD_AM);
                if (hour == 12) {
                    hour = 0;
                }
            }
        }
        return true;
    }
    
    public String timeString() {
        String period = Constants.PARSER_TIME_PERIOD_AM;
        int hour12 = hour;
        
        if (hour > 12) {
            period = Constants.PARSER_TIME_PERIOD_PM;
            hour12 -= 12;
        }
        
        return String.format(Constants.PARSER_TIMESTRING, hour12, (minute < 10 ? "0" + minute : minute), period);
    }
    
    public String toString() {
        return Constants.PARSER_TOKEN_PREFIX_TIME + contents;
    }
    
    public DateTime mergeInto(DateTime currentDateTime) {
        return currentDateTime.withTime(hour, minute, 0, 0);
    }
    
    public DateTime toDateTime() {
        return mergeInto(new DateTime());
    }
}