package ui;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import common.Constants;

//@ A0097282W
public class TextFormatter {
	
	/**
	 * A class that defines a (tiny) subset of markdown for styling SWT StyledText objects.
	 * It exposes one public method, setFormattedText, which sets the text of the StyledText
	 * object to the text string, with formatting applied and formatting characters removed.
	 * 
	 * Formatting types supported:
	 * 
	 * *bold*
	 * _underline_
	 * 
	 */
	
	private static final int UNDERLINE = 0;
	private static final int BOLD = 1;
	
	private static class FormatRange {
		public int start;
		public int end;
		public int formatType;
	}
	
	public static void setFormattedText(StyledText styledText, String text) {
		ArrayList<FormatRange> ranges = getFormatRanges(text);
		styledText.setText(removeFormattingCharacters(text));
		applyFormatting(styledText, text, ranges);
	}
	
	private static String removeFormattingCharacters(String text) {
		return text.replaceAll(Constants.FORMATTING_REGEX_UNDERLINE, "$1").replaceAll(Constants.FORMATTING_REGEX_BOLD, "$1");
	}

	private static void applyFormatting(StyledText styledText, String helpString, ArrayList<FormatRange> formatRanges) {
		for (FormatRange formatRange : formatRanges) {
			StyleRange sr = new StyleRange();
			sr.start = formatRange.start;
			sr.length = formatRange.end - sr.start;
			int formatType = formatRange.formatType;

			if (formatType == UNDERLINE) {
				sr.underline = true;
			}
			else {
				assert formatType == BOLD;
				sr.fontStyle = SWT.BOLD;
			}
			styledText.setStyleRange(sr);
		}
	}

	private static ArrayList<FormatRange> getFormatRanges(String text) {
		
		Pattern pattern = Pattern.compile(Constants.FORMATTING_REGEX_UNDERLINE + "|" + Constants.FORMATTING_REGEX_BOLD);
		Matcher matcher = pattern.matcher(text);

		ArrayList<FormatRange> result = new ArrayList<FormatRange>();
		
		int offset = 0;
		while (matcher.find()) {
			FormatRange formatRange = new FormatRange();
			
			formatRange.start = matcher.start() - offset;
			formatRange.end = matcher.end() - 2 - offset;
			offset += 2;
			
			if (matcher.group(1) != null) {
				formatRange.formatType = UNDERLINE;
			} else {
				assert matcher.group(2) != null;
				formatRange.formatType = BOLD;
			}
			
			result.add(formatRange);
		}
		
		return result;
	}
}
