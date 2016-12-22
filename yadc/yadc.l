/*
 * ------------------------------------------------------------------
 *  yadc.l - This provides the regex for all the tokens.
 *
 *  CIS*2750 - Software Systems Development and Integration - W15
 *  School of Computer Science
 *  University of Guelph
 *  Author: Alexander Gontcharov
 *  Student ID: 0814685
 * ------------------------------------------------------------------
 */

%{
	#include "y.tab.h" 
	int fileno(FILE *stream);
	char *strdup(const char *s);
%}

IDENTIFIER		[a-zA-Z_]+[0-9a-zA-Z_]*
EQUALS			=
COMMA			,
OPBRACKET		\{
CLBRACKET		\}
SEMICOLON		;
QUOTATION		"\""
SPACE			[ \t]
NEWLINE			\n
COMMENT 		#(.)*
%option noinput
%option nounput

%%

{IDENTIFIER} {
	#ifdef DEBUG
	printf("%s: is an IDENTIFIER\n", yytext);
	#endif
	 
	yylval.sval = strdup(yytext);

	if (strcmp(yytext, "title") == 0) {
		return TITLE;
	}
	else if (strcmp(yytext, "fields") == 0) {
		return FIELDS;
	}
	else if (strcmp(yytext, "buttons") == 0) {
		return BUTTONS;
	}
	else {
		return IDENTIFIER;
	}
}

{EQUALS} {
	#ifdef DEBUG
	printf("%s: is an EQUALS\n", yytext);
	#endif

	return (EQUALS);
}

{COMMA}	{
	#ifdef DEBUG
	printf("%s: is a COMMA\n", yytext);
	#endif

	return (COMMA);
}

{OPBRACKET}	{
	#ifdef DEBUG
	printf("%s: is an OPBRACKET\n", yytext);
	#endif 

	return (OPBRACKET);
}

{CLBRACKET}	{
	#ifdef DEBUG
	printf("%s: is a CLBRACKET\n", yytext);
	#endif 

	return (CLBRACKET);
}

{SEMICOLON}	{
	#ifdef DEBUG
	printf("%s: is a SEMICOLON\n", yytext);
	#endif

	return (SEMICOLON);
}

{QUOTATION} {
	#ifdef DEBUG
	printf("%s: is a QUOTATION\n", yytext);
	#endif 

	return (QUOTATION);
}

{COMMENT} { 
	#ifdef DEBUG
	printf("%s: is a COMMENT\n", yytext);
	#endif 

	/*ignore comments.*/
}

{SPACE} {
	/*ignore spaces.*/
}

{NEWLINE} {
	/*ignore new lines.*/
}

%%