/*
 * ------------------------------------------------------------------
 *  yadc.y - This provides the grammer rules for parsing a config file
 *           also including the compilation of the files.
 *
 *  CIS*2750 - Software Systems Development and Integration - W15
 *  School of Computer Science
 *  University of Guelph
 *  Author: Alexander Gontcharov
 *  Student ID: 0814685
 * ------------------------------------------------------------------
 */

%{
	#include <stdlib.h>
	#include <stdio.h>
	#include <string.h>
	#include "list.h"

    int yylex ();
    int yyerror(char *s);
	extern char * yytext;
    extern FILE *yyin;

    stringList *title = NULL;                      /*List for title.*/
	stringList *fields = NULL;                     /*List for fields.*/
	stringList *buttons = NULL;                    /*List for buttons.*/
    stringList *fieldTypes = NULL;                 /*List for field types.*/
    stringList *actionListeners = NULL;            /*List for action listeners.*/
	stringList *buffer = NULL;                     /*List buffer to hold values.*/
%}

%union {
	char *sval;
}

%token TITLE
%token FIELDS
%token BUTTONS
%token EQUALS
%token SEMICOLON
%token QUOTATION
%token COMMA 
%token OPBRACKET
%token CLBRACKET
%token <sval> IDENTIFIER
%type <sval> titleString
%type <sval> string

%%

STATEMENT 	: expression 	{ }
			| STATEMENT expression { }
			;

expression	: TITLE EQUALS QUOTATION titleString QUOTATION SEMICOLON {
                /*GUI title has not been managed.*/
                if (!title) {
                    title = buffer;
                    title = destroyLastNode(title);
                    buffer = NULL;
                }
                else {
                    fprintf(stderr, "title already managed.\n");
                    exit(-1);
                }
			}
            | TITLE EQUALS QUOTATION QUOTATION SEMICOLON {
                fprintf(stderr, "Title is empty\n");
                exit(-1);
            }
			| FIELDS EQUALS OPBRACKET CLBRACKET SEMICOLON {
				#ifdef DEBUG
                printf("Empty fields.\n");
                #endif
			}
			| BUTTONS EQUALS OPBRACKET CLBRACKET SEMICOLON {
				#ifdef DEBUG
                printf("Empty fields.\n");
                #endif 
			}
			| FIELDS EQUALS OPBRACKET litem CLBRACKET SEMICOLON {
                /*Fields has not been managed.*/
                if (!fields) {
    				fields = buffer;
    				buffer = NULL;
                }
                /*Parse Error.*/
                else {
                    fprintf(stderr, "fields already managed.\n");
                    exit(-1);
                }
			}
			| BUTTONS EQUALS OPBRACKET litem CLBRACKET SEMICOLON {
                /*Buttons has not been managed.*/
                if (!buttons) {
                    buttons = buffer;
                    buffer = NULL;
                }
                /*Parse Error.*/
                else {
                    fprintf(stderr, "buttons already managed.\n");
                    exit(-1);
                }
			}
			| IDENTIFIER EQUALS string SEMICOLON {
                /*This is field types.*/
                if (matchString(fields, $1)) {
                    #ifdef DEBUG
                    printf("Field type: %s\n", $3);
                    #endif

                    fieldTypes = addToBack(fieldTypes, $3);
                }
                /*This is action listeners for buttons.*/
                else if (matchString(buttons, $1)) {
                    #ifdef DEBUG
                    printf("Action Listener: %s\n", $3);
                    #endif

                    actionListeners = addToBack(actionListeners, $3);
                }
                /*This is a parameter that has not been managed.*/
                else {
                    fprintf(stderr, "%s has not been managed.\n", $1);
                    exit(-1);
                }
			}
			;

titleString : IDENTIFIER {
                #ifdef DEBUG
                printf("Title string: %s\n", $1);
                #endif

                buffer = addToBack(buffer, $1);
                buffer = addToBack(buffer," ");
            }
            | titleString IDENTIFIER {
                #ifdef DEBUG
                printf("Title string: %s\n", $2);
                #endif

                buffer = addToBack(buffer, $2);
                buffer = addToBack(buffer," ");
            }
            ;

string 		: QUOTATION IDENTIFIER QUOTATION {
				$$ = $2;
			}
			;

litem		: string {
                #ifdef DEBUG
                printf("\nThis is a list item: %s\n", $1);
                #endif

                if (!matchString(fields, $1) && !matchString(buttons, $1)) {
                    buffer = addToBack(buffer, $1);
                }
                else {
                    fprintf(stderr, "%s already managed in a list.\n", $1);
                    exit(-1);
                }
			}
			| litem COMMA string {
                #ifdef DEBUG
                printf("\nThis is a list item: %s\n", $3);
                #endif

                if (!matchString(fields, $3) && !matchString(buttons, $3)) {
                    buffer = addToBack(buffer, $3);
                }
                else {
                    fprintf(stderr, "%s already managed in another list.\n", $3);
                    exit(-1);
                }
			}
			;
%%

int yyerror(char *s) {
    fprintf(stderr, "Syntax Error: %s\n",yytext);
    exit(-1);
}

int main (int argc, char *argv[]) {
    char *guiTitle = NULL;                     /*String for the GUI title.*/
	char *projectName = NULL;                  /*The name of the project.*/
    char *outputDiretory = NULL;               /*The output directory for the generated files.*/
	char *tempString = NULL;                   /*Temporary buffer to hold a string.*/
    char *tempString2 = NULL;                  /*Temporary buffer to hold a string.*/
	char formatter[1025] = {0};                /*Formats the string for writing to file.*/
	FILE *fp = NULL;                           /*The file stream.*/
    FILE *filePath = NULL;                     /*The path to the file to be parsed.*/
	int i = 0;                                 /*Counter.*/

	/*Open and use file stream - Change.*/
	if (argc == 3 || argc == 4) {
        filePath = fopen(argv[1], "r");
		projectName = argv[2];
        outputDiretory = argv[3];

        #ifdef DEBUG
		printf("Project name is: %s\n", projectName);
        #endif

        #ifdef DEBUG
        printf("Output directory is: %s\n", outputDiretory);
        #endif

        yyin = filePath;

		if (yyin == NULL) {
			fprintf(stderr, "Could not open or find file %s\n", argv[1]);
			return -1;
		}
	}
	else {
		fprintf(stderr, "Invalid number of arguments\n");
        fprintf(stderr, "[yadc] [file path (empty if cwd)] [name of project] [output directory (empty if cwd)]\n");
		return -1;
	}

	/*Begin parsing.*/
	while (!feof(yyin)) {
		yyparse();
	}

    #ifdef DEBUG
    printList(title);
    printf("\n");
    printList(fields);
    printf("\n");
    printList(buttons);
    printf("\n");
    printList(fieldTypes);
    printf("\n");
    printList(actionListeners);
    #endif

    /*Create and write the GUI Java interface file in the current working directory.*/
    if (!outputDiretory) { 
        sprintf(formatter, "%sFieldEdit.java", projectName);
        fp = fopen(formatter, "w");
        if (fp == NULL) {
            fprintf(stderr, "(1)Problem creating or writing to file: %s\n", formatter);
            return -1;
        }
        memset(formatter, 0, 1025);
    }
    /*Create and write the GUI Java interface file in the specified working directory.*/
    else {
        sprintf(formatter, "%s/%sFieldEdit.java", outputDiretory, projectName);
        fp = fopen(formatter, "w");
        if (fp == NULL) {
            fprintf(stderr, "(2)Problem creating or writing to file: %s\n", formatter);
            return -1;
        } 
        memset(formatter, 0, 1025);
    }

	/*Write declaration for GUI interface class.*/
    fprintf(fp, "public interface %sFieldEdit {\n\n", projectName);

    /*Write get methods for fields.*/
    fprintf(fp,"\t//Get methods for text fields\n");
    while ((tempString = getStringAt(fields, i))) {
    	fprintf(fp, "\tpublic String getDC%s();\n\n", tempString);
    	i++;
    }

    /*Write set methods for fields.*/
    i = 0;
    fprintf(fp,"\t//Set methods for text fields\n");
    while ((tempString = getStringAt(fields, i))) {
    	fprintf(fp, "\tpublic void setDC%s(String message);\n\n", tempString);
    	i++;
    }

    /*Write status method and close the file stream.*/
    fprintf(fp,"\t//Method to append messages to status area\n");
    fprintf(fp,"\tpublic void appendToStatusArea(String message);\n");
    fprintf(fp,"}\n");
    fclose(fp);

    /*Create and write the IllegalFieldValueException Java file in the current working directory.*/
    if (!outputDiretory) {
        sprintf(formatter, "IllegalFieldValueException.java");
        fp = fopen(formatter, "w");
        if (fp == NULL) {
            fprintf(stderr, "(1)Problem creating or writing to file: %s\n", formatter);
            return -1;
        }
        memset(formatter, 0, 1025);
    }
    /*Create and write the IllegalFieldValueException Java file in the specified working directory.*/
    else {
        sprintf(formatter, "%s/IllegalFieldValueException.java", outputDiretory);
        fp = fopen(formatter, "w");
        if (fp == NULL) {
            fprintf(stderr, "(2)Problem creating or writing to file: %s\n", formatter);
            return -1;
        } 
        memset(formatter, 0, 1025);
    }

    /*Write declaration of IllegalFieldValueException class.*/
    fprintf(fp, "/**\n");
    fprintf(fp, "* IllegalFieldValueException class for Dialogc.\n");
    fprintf(fp, "* @author Alexander Gontcharov\n");
    fprintf(fp, "*/\n");
    fprintf(fp, "public class IllegalFieldValueException extends RuntimeException {\n\n");

    /*Write constructor for class.*/
    fprintf(fp, "\t/**\n");
    fprintf(fp, "\t *\n");
    fprintf(fp, "\t */\n");
    fprintf(fp, "\tpublic IllegalFieldValueException(String message) {\n");
    fprintf(fp, "\t\tsuper(message);\n");
    fprintf(fp, "\t}\n");
    fprintf(fp, "}");

    /*Close the file stream.*/
    fclose(fp);

    /*Create and write the GUI Java file in the current working directory.*/
    if (!outputDiretory) {
        sprintf(formatter, "%s.java", projectName);
        fp = fopen(formatter, "w");
        if (fp == NULL) {
            fprintf(stderr, "(1)Problem creating or writing to file: %s\n", formatter);
            return -1;
        }
        memset(formatter, 0, 1025);
    }
    /*Create and write the GUI Java file in the specified working directory.*/
    else {
        sprintf(formatter, "%s/%s.java", outputDiretory, projectName);
        fp = fopen(formatter, "w");
        if (fp == NULL) {
            fprintf(stderr, "(2)Problem creating or writing to file: %s\n", formatter);
            return -1;
        } 
        memset(formatter, 0, 1025);
    }

    /*Write Java class imports.*/
    fprintf(fp, "import javax.swing.JFrame;\n");
    fprintf(fp, "import javax.swing.JPanel;\n");
    fprintf(fp, "import javax.swing.JLabel;\n");
    fprintf(fp, "import javax.swing.JScrollPane;\n");
    fprintf(fp, "import javax.swing.JTextArea;\n");
    fprintf(fp, "import javax.swing.JTextField;\n");
    fprintf(fp, "import javax.swing.border.BevelBorder;\n");
    fprintf(fp, "import javax.swing.JButton;\n");
    fprintf(fp, "import javax.swing.BorderFactory;\n");
    fprintf(fp, "import java.awt.BorderLayout;\n");
    fprintf(fp, "import java.awt.FlowLayout;\n");
    fprintf(fp, "import java.awt.GridLayout;\n\n");

    /*Write declaration of GUI class.*/
    fprintf(fp, "public class %s extends JFrame implements %sFieldEdit {\n", projectName, projectName);

    /*Write declaration of final primitive types.*/
    fprintf(fp, "\tpublic static final int WIDTH = 600;\n");
    fprintf(fp, "\tpublic static final int HEIGHT = 400;\n");
    fprintf(fp, "\tpublic static final int NUMBER_OF_CHAR = 20;\n");
    fprintf(fp, "\tpublic static final int NUMBER_OF_LINES = 10;\n\n");

    /*Write declaration of fields.*/
    i = 0;
    while ((tempString = getStringAt(fields, i))) {
    	fprintf(fp, "\tprivate JTextField %sTextField;\n", tempString);
    	i++;
    }

    /*Write declaration of buttons.*/
    i = 0;
    while ((tempString = getStringAt(buttons, i))) {
    	fprintf(fp, "\tprivate JButton %sButton;\n", tempString);
    	i++;
    }

    /*Write declarion of JComponents in use for functioins.*/
    fprintf(fp, "\tprivate JTextArea statusMessage;\n");
    fprintf(fp, "\tprivate JPanel centerPanel;\n");
    fprintf(fp, "\tprivate JPanel southPanel;\n\n");

    /*Write constructor for class.*/
    fprintf(fp, "\tpublic %s() {\n", projectName);
    fprintf(fp, "\t\tsuper();\n");
    fprintf(fp, "\t\tsetSize(WIDTH, HEIGHT);\n");

    /*Create the string for the GUI title.*/
    i = 0;
    while ((tempString = getStringAt(title, i))) {
        strcat(formatter, tempString);
        i++;
    }

    #ifdef DEBUG
    printf("New title string is: %s.\n", formatter); DEBUG this later
    #endif

    guiTitle = formatter;

    /*Write settings for GUI.*/
    fprintf(fp, "\t\tsetTitle(\"%s\");\n", guiTitle);
    memset(formatter, 0, 1025);
    fprintf(fp, "\t\tsetDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);\n");
    fprintf(fp, "\t\tsetLayout(new BorderLayout());\n");
    fprintf(fp, "\t\tsetLocationRelativeTo(null);\n\n");
    
    /*Write invocation of methods to create the GUI.*/
    fprintf(fp, "\t\t//Create the compiled window\n");
    fprintf(fp, "\t\tcreateCenterPanel();\n");
    fprintf(fp, "\t\tadd(centerPanel, BorderLayout.CENTER);\n");
    fprintf(fp, "\t\tcreateSouthPanel();\n");
    fprintf(fp, "\t\tadd(southPanel, BorderLayout.SOUTH);\n");
    fprintf(fp, "\t}\n\n");

    /*Write get methods for fields.*/
    i = 0;
    while ((tempString = getStringAt(fields, i))) {
    	fprintf(fp, "\t//Get method for %s\n", tempString);
    	fprintf(fp, "\tpublic String getDC%s() {\n", tempString);

        tempString2 = getStringAt(fieldTypes, i);

        #ifdef DEBUG
        fprintf(stderr, "Field type for %s is: %s\n", tempString, tempString2);
        #endif

        /*Integer field type.*/
        if (strcmp(tempString2, "integer") == 0) {
            fprintf(fp, "\t\tif (!%sTextField.getText().matches(\"^-?[0-9]+$\")) {\n", tempString);
            fprintf(fp, "\t\t\tthrow new IllegalFieldValueException(\"%s = incorrect type\");\n", tempString);
            fprintf(fp, "\t\t}\n\n");
        }
        /*Float field type.*/
        else if (strcmp(tempString2, "float") == 0) {
            fprintf(fp, "\t\tif (!%sTextField.getText().matches(\"^-?[0-9]+.?[0-9]*$\")) {\n", tempString);
            fprintf(fp, "\t\t\tthrow new IllegalFieldValueException(\"%s = incorrect type\");\n", tempString);
            fprintf(fp, "\t\t}\n\n");
        }
        /*String field type.*/
        else if (strcmp(tempString2, "string") == 0) {
            /*Exception here?*/
        }
        /*Uknown field type.*/
        else {
            fprintf(stderr, "%s is not an integer, float or string.\n", tempString);
            return -1;
        }

        /*Return the string from field.*/
    	fprintf(fp, "\t\treturn %sTextField.getText();\n", tempString);
    	fprintf(fp, "\t}\n\n");
    	i++;
    }

    /*Write set methods for fields.*/
	i = 0;
    while ((tempString = getStringAt(fields, i))) {
    	fprintf(fp, "\t//Set method for %s\n", tempString);
    	fprintf(fp, "\tpublic void setDC%s(String message) {\n", tempString);
    	fprintf(fp, "\t\t%sTextField.setText(message);\n", tempString);
    	fprintf(fp, "\t}\n\n");
    	i++;
    }

    /*Write append to status area method.*/
    fprintf(fp, "\tpublic void appendToStatusArea(String message) {\n");
    fprintf(fp, "\t\tstatusMessage.append(message + \"\\n\");\n");
    fprintf(fp, "\t}\n\n");

    /*Write center panel method.*/
    fprintf(fp, "\t//Create and the fields to the Center in the Center Panel\n");
    fprintf(fp, "\tprivate void createCenterPanel() {\n");
    fprintf(fp, "\t\tcenterPanel = new JPanel();\n");
    fprintf(fp, "\t\tcenterPanel.setLayout(new BorderLayout());\n\n");

    /*Write field panel*/
    fprintf(fp, "\t\tJPanel fieldPanel = new JPanel();\n");
	fprintf(fp, "\t\tfieldPanel.setLayout(new GridLayout(%d, 2));\n", getListSize(fields));
    fprintf(fp, "\t\tfieldPanel.setBorder(BorderFactory.createEmptyBorder(20,20,0,20));\n\n");

    /*Write declaration of all the field labels and text fields and add them.*/
    i = 0;
    while ((tempString = getStringAt(fields, i))) {
    	fprintf(fp, "\t\t%sTextField = new JTextField();\n", tempString);
    	fprintf(fp, "\t\tfieldPanel.add(new JLabel(\"%s\"));\n", tempString);
    	fprintf(fp, "\t\tfieldPanel.add(%sTextField);\n\n", tempString);
    	i++;
    }
    fprintf(fp, "\t\tcenterPanel.add(fieldPanel, BorderLayout.CENTER);\n\n");

    /*Write button panel*/
    fprintf(fp, "\t\t//Create and add the buttons to the SOUTH in the Center Panel\n");
    fprintf(fp, "\t\tJPanel buttonPanel = new JPanel();\n");
    fprintf(fp, "\t\tbuttonPanel.setLayout(new FlowLayout());\n\n");
    
    /*Write declaration of all the button names and action listener and add them.*/
    i = 0;
    while ((tempString = getStringAt(buttons, i))) {
    	fprintf(fp, "\t\t%sButton = new JButton(\"%s\");\n", tempString, tempString);
        tempString2 = getStringAt(actionListeners, i);
    	fprintf(fp, "\t\t%sButton.addActionListener(new %s(this));\n", tempString, tempString2);
    	fprintf(fp, "\t\tbuttonPanel.add(%sButton);\n\n", tempString);
    	i++;
    }
    fprintf(fp, "\t\tcenterPanel.add(buttonPanel, BorderLayout.SOUTH);\n");
    fprintf(fp, "\t}\n\n");

    /*Write South panel method.*/
    fprintf(fp, "\tprivate void createSouthPanel() {\n");
    fprintf(fp, "\t\tsouthPanel = new JPanel();\n");
    fprintf(fp, "\t\tsouthPanel.setLayout(new BorderLayout());\n\n");

    /*Write status panel.*/
    fprintf(fp, "\t\t//Create and add editor bar to north in South panel\n");
    fprintf(fp, "\t\tJPanel statusPanel = new JPanel();\n");
    fprintf(fp, "\t\tstatusPanel.setBorder(new BevelBorder (BevelBorder.LOWERED));\n");
    fprintf(fp, "\t\tJLabel editor = new JLabel(\"Status\");\n");
    fprintf(fp, "\t\tstatusPanel.add(editor);\n");
    fprintf(fp, "\t\tsouthPanel.add(statusPanel, BorderLayout.NORTH);\n\n");

    /*Write status area and add it.*/
    fprintf(fp, "\t\t//Create and add text area to Center in South panel\n");
    fprintf(fp, "\t\tstatusMessage = new JTextArea(NUMBER_OF_LINES, NUMBER_OF_CHAR);\n");
    fprintf(fp, "\t\tstatusMessage.setEditable(true);\n");
    fprintf(fp, "\t\tJScrollPane scrolledText = new JScrollPane(statusMessage);\n");
    fprintf(fp, "\t\tscrolledText.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);\n");
    fprintf(fp, "\t\tscrolledText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);\n");
    fprintf(fp, "\t\tsouthPanel.add(scrolledText, BorderLayout.CENTER);\n");
    fprintf(fp, "\t}\n\n");

    /*Write Main.*/
    fprintf(fp, "\tpublic static void main(String[] args) {\n");
	fprintf(fp, "\t\t%s compiledGUI = new %s();\n", projectName, projectName);
    fprintf(fp, "\t\tcompiledGUI.setVisible(true);\n");
    fprintf(fp, "\t}\n");
    fprintf(fp, "}");

    /*Close the file stream and destroy all the lists.*/
    fclose(filePath);
    fclose(fp);
    destroyList(&title);
    destroyList(&fields);
    destroyList(&buttons);
    destroyList(&fieldTypes);
    destroyList(&actionListeners);

    //Success
    fprintf(stdout, "Files succesfully compiled.\n");

	return 0;
}