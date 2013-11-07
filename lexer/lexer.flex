
package parser;

/**
   This is a lexical analyser generated by JFlex 1.4.3.
   We use it to tokenize input strings.

   The specification for the lexer can be found in the
   root folder of the project.
*/

@SuppressWarnings("unused")

%%

%class Lexer
%function nextToken
%type Token
%ignorecase

%unicode

%{
  String name;
%}

TimeQualifier = ((from|at)[ ]+)?
DateQualifier = ((from|on)[ ]+)?

StandardDate = (0?[1-9]|[12][0-9]|3[01])[-/](1[012]|0?[1-9])([-/]((19|20)?[0-9][0-9]))?
RelativeDate = (next[ ]+)(week|year|month|fortnight)
RelativeDayDate = ((this|next|last)[ ]+)?(((mon|tues|wednes|thurs|fri|satur|sun)day)|mon|tues|tue|wed|thurs|thu|fri|sat|sun)
MixedDate = ((0?[1-9]|[12][0-9]|3[01])[ ]*(january|february|march|april|may|june|july|august|september|october|november|december|jan|feb|mar|apr|jun|jul|aug|sep|oct|nov|dec)[ ]*((19|20)?[0-9][0-9])?)
AliasDate = (today|tonight|tomorrow|tmrw|tmr|halloween|christmas)

Date = {DateQualifier} ({StandardDate}|{RelativeDate}|{RelativeDayDate}|{AliasDate}|{MixedDate})
Time = {TimeQualifier} (((1[012]|[1-9])([:.]([0-5][0-9]))?[ ]*(am|pm))|((2[0-3]|[01]?[0-9])[:.]?([0-5][0-9])))

Word = [-:./'!?a-zA-Z0-9]+
Tag = #{Word}
QuotedWords = "\""[- :./'!?a-zA-Z0-9]+"\""

%%

{Time} {
	String contents = yytext();
	String[] split = contents.split("\\s+", 2);

	// Handles the optional qualifier
	if (split.length == 2 && (split[0].equalsIgnoreCase("at") || split[0].equalsIgnoreCase("from"))) {
		return new TimeToken(split[1].trim());
	}
	else {
		return new TimeToken(yytext());
	}
}
{Date} {
	String contents = yytext();
	String[] split = contents.split("\\s+", 2);

	// Handles the optional qualifier
	if (split.length == 2 && (split[0].equalsIgnoreCase("on") || split[0].equalsIgnoreCase("from"))) {
		return new DateToken(split[1].trim());
	}
	else {
		return new DateToken(yytext());
	}
}
{Word} {return new WordToken(yytext());}
{Tag} {return new TagToken(yytext());}
{QuotedWords} {
    String contents = yytext();
    return new WordToken(contents.substring(1, contents.length()-1));
}

.|\n {}
