/**
   This is the input to JFlex 1.4.3. It generates a lexical analyser that
   we use to tokenize input strings.
*/

%%

%public
%class Lexer
%function nextToken
%type Token

%unicode

%{
  String name;
%}

Date = (0?[1-9]|[12][0-9]|3[01])([/])(0?[1-9]|1[012])([/]((19|20)?[0-9][0-9]))?
Time = ((1[012]|[1-9]):([0-5][0-9])[ ]*(am|pm))|(([01]?[0-9]|2[0-3]):([0-5][0-9])[ ]*)
AddKeyword = "add"
OnKeyword = "on"
AtKeyword = "at"
Word = [a-z]+
QuotedWords = '[a-z0-9 :/]+'

%%

{OnKeyword} {return new KeywordToken(yytext());}
{AddKeyword} {return new KeywordToken(yytext());}
{AtKeyword} {return new KeywordToken(yytext());}
{Time} {return new TimeToken(yytext());}
{Date} {return new DateToken(yytext());}
{Word} { return new WordToken(yytext()); }
{QuotedWords} { return new WordToken(yytext()); }

.|\n {}
