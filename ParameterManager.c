/*
 * ------------------------------------------------------------------
 *  ParameterManager.c - This library provides a type (ParameterManager) and a set of operations defined on this type.
 *
 *  CIS*2750 - Software Systems Development and Integration - W15
 *  School of Computer Science
 *  University of Guelph
 *  Author: Alexander Gontcharov
 *  Student ID: 0814685
 * ------------------------------------------------------------------
 */

#include "ParameterManager.h"

/*Creates a new parameter manager objet.
 *PRE: size is a positive integer.
 *POST: Returns a new parameter manager object initialized to be empty (i.e. managing no parameters) on success, 
 *NULL otherwise (memory allocation failure).
 */
ParameterManager * PM_create(int size) {
	ParameterManager * hashTable = NULL;
	int i = 0;

	/*Invalid hashtable size.*/
	if (size < 1)  {
		fprintf(stderr, "Error: Size of table is less than 1.\n");
		return NULL;
	}

	/*Malloc memory for the hashtable.*/
	if ( (hashTable = malloc(sizeof(ParameterManager)) ) == NULL ) {
		fprintf(stderr, "Error: Memory allocation failure on line 31.\n");
		PM_destroy(hashTable);
		return NULL;
	}

	/*Malloc memory for the table.*/
	if ( (hashTable->table = malloc(sizeof(parameter_t*) * size) ) == NULL) {
		fprintf(stderr, "Error: Memory allocation failure on line 38.\n");
		PM_destroy(hashTable);
		return NULL;
	}

	/*Initialize the entries for the table.*/
	for (i = 0; i < size; i++) {
		hashTable->table[i] = NULL;
	}

	/*Update hashtable size and return a pointer to it.*/
	hashTable->size = size;
	hashTable->errFlag = 0;
	return hashTable;
}

/*Destroys a parameter manger objet.
 *PRE: n/a.
 *POST: all memory associated with parameter manager p is freed; returns 1 on success, 0 otherwise.
 */
int PM_destroy(ParameterManager *p) {
	int i = 0;
	parameter_t *currentParam = NULL;
	parameter_t *tempParam = NULL;
	ParameterList *currentPList = NULL;
	ParameterList *tempPList = NULL;

	/*Check if the hashtable is already freed.*/
	if (p == NULL) {
		return 1;
	}

	/*Check if the table is already freed.*/
	if (p->table != NULL) {

		/*Normal destroy. Free everything.*/
		if (p->errFlag == 0) {

			/*Free all the entries inside the table.*/
			for (i = 0; i < p->size; i++) {
				currentParam = p->table[i];

				/*Check if current parameter is already freed.*/
				while (currentParam != NULL) {
					tempParam = currentParam;
					currentParam = currentParam->next;
					free(tempParam->pname);

					/*Check if STRING_TYPE has been malloced in union param_value.*/
					if ((tempParam->ptype == STRING_TYPE) && (tempParam->hasValue == 1)) {
						free(tempParam->value->str_val);
					}

					/*Check if LIST_TYPE has been malloced in union param_value.*/
					if ((tempParam->ptype == LIST_TYPE) && (tempParam->hasValue == 1)) {

						/*Free all the ParameterList structs after the head pointer.*/
						currentPList = tempParam->value->list_val->next;
						while (currentPList != NULL) {
							tempPList = currentPList;
							currentPList = currentPList->next;
							free(tempPList->item);
							free(tempPList);
						}

						/*Free the ParameterList head struct.*/
						free(tempParam->value->list_val->item);
						free(tempParam->value->list_val);
					}

					/*Free the union param_value and parameter data type.*/
					if (tempParam->value != NULL) {
						free(tempParam->value);	
					}
					free(tempParam);
				}
			}
		}
		/*Parse error flag. Delete all values for every parameter.*/
		else if (p->errFlag == 1) {

			/*Free all the entries inside the table.*/
			for (i = 0; i < p->size; i++) {
				currentParam = p->table[i];

				/*Check if current parameter is already freed.*/
				while (currentParam != NULL) {
					tempParam = currentParam;
					currentParam = currentParam->next;

					/*Check if STRING_TYPE has been malloced in union param_value.*/
					if ((tempParam->ptype == STRING_TYPE) && (tempParam->hasValue == 1)) {
						free(tempParam->value->str_val);
					}

					/*Check if LIST_TYPE has been malloced in union param_value.*/
					if ((tempParam->ptype == LIST_TYPE) && (tempParam->hasValue == 1)) {

						/*Free all the ParameterList structs after the head pointer.*/
						currentPList = tempParam->value->list_val->next;
						while (currentPList != NULL) {
							tempPList = currentPList;
							currentPList = currentPList->next;
							free(tempPList->item);
							free(tempPList);
						}

						/*Free the ParameterList head struct.*/
						free(tempParam->value->list_val->item);
						free(tempParam->value->list_val);
					}

					/*Free the union param_value and set it NULL and set hasValue to 0*/
					tempParam->hasValue = 0;
					free(tempParam->value);
					tempParam->value = NULL;
				}
			}

			/*Reset parse error flag in order to do a normal destroy.*/
			p->errFlag = 0;
			return 1;
		}
		else {
			/*Some sort of error occured during the invocation of PM_destroy().*/
			return 0;	
		} 
		free(p->table);
	}
	free(p);
	return 1;
}

/*Extract values for parameters from an input stream.
 *PRE: fp is a valid input stream ready for reading that contains the desired parameters.
 *POST: All required parameters, and those optional parameters present, are assigned values that are consumed from fp, 
 *respecting comment as a "start of comment to end of line" character if not nul ('\0'); 
 *returns non-zero on success, 0 otherwise (parse error,memory allocation failure).
 */
int PM_parseFrom(ParameterManager *p, FILE *fp, char comment) {
	parameter_t *currentParam = NULL; 		/*Parameter to iterate through the list.*/
	char *lexemeBuffer = NULL;				/*Buffer for lexemes.*/
	int readingLexeme = 0;					/*Flag if buffer is crrently reading.*/
	char c;									/*Character from stream.*/
	int stringFlag = 0; 					/*Flag for string types.*/	
	int listFlag = 0;						/*Flag for list types.*/
	int equalFlag = 0;						/*Equal flag.*/
	int i = 0;								/*Counter.*/
	int hasval = 0;							/*Hastable index for key.*/

	if (fp == NULL) {
		printf("Stream null");
		return 0;
	}
	
	/*Malloc and initialize the lexeme buffer.*/
	if ((lexemeBuffer = (char *)malloc(BUFFERSIZE)) == NULL) {
		fprintf(stderr, "Error: Memory allocation failure on line 190.\n");
		return 0;
	}
	memset(lexemeBuffer, 0, BUFFERSIZE);

	#ifdef DEBUG
	printf("In PM_parseFrom function.\n");
	#endif

	while ((c = fgetc(fp)) != EOF) {

		/*Whitespace state. Set readingLexeme to 0. Otherwise continue.*/
		if ((isspace(c)) && (!stringFlag)) {

			/*There is something stored in the buffer.*/
			if (readingLexeme) {
				readingLexeme = 0;

				/*listFlag state. Ignore all whitespace.*/
				if (listFlag == 1) {
					continue;
				}

				while ((c = fgetc(fp)) != EOF) {
					#ifdef DEBUG
					printf("State 2 character is: %c\n", c);
					printf("Equal Flag is set to: %d\n", equalFlag);
					#endif

					/*This is a lexeme for a name.*/
					if ((c == '=') && (!equalFlag)) {
						ungetc(c, fp);
						break;
					}

					/*This is a lexeme for a value.*/
					else if ((c == ';') && (equalFlag)) {
						ungetc(c, fp);
						break;
					}

					/*Space. Ignore it.*/
					else if (isspace(c)) {
						continue;
					}

					/*Comment. Read until end of line.*/
					else if (c == comment) {
						ungetc(c, fp);
						break;
					}

					/*This has to be a parse error.*/
					else {
						fprintf(stderr, "Parse Error: Expected format is violated.\n");
						free(lexemeBuffer);
						p->errFlag = 1;
						PM_destroy(p);
						return 0;
					}
				}

				/*Did not finish reading parameter.*/
				if (c == EOF) {
					fprintf(stderr, "Parse Error: End of file before parsing finished.\n");
					free(lexemeBuffer);
					p->errFlag = 1;
					PM_destroy(p);
					return 0;
				}
			}

			/*Nothing stored in buffer.*/
			else {
				continue;
			}
		}

		/*Comment state. Read until end of line.*/
		else if ((c == comment) && (!stringFlag)) {
			while (c != '\n' && c != EOF) {
				c = fgetc(fp);
			}
		}

		/*Value state. Find the index of the parameter. Otherwise ignore it.*/
		else if ((c == '=') && (!stringFlag))  {
			
			/*Equal flag has been set when it should not have been.*/
			if (equalFlag == 1) {
				fprintf(stderr, "Parse Error: mulitiple equal.\n");
				free(lexemeBuffer);
				p->errFlag = 1;
				PM_destroy(p);
				return 0;
			}
			equalFlag = 1;

			#ifdef DEBUG
			printf("\tEqual sign found.\n");
			printf("\tlexemeBuffer contains: %s\n", lexemeBuffer);
			#endif

			hasval = PM_hash(p, lexemeBuffer);

			/*Traverse the list until you find the registered parameter or you reach the end of the list.
	 		 *Also check that it doens't have a value in order not to overwrite it.*/
			for(currentParam = p->table[hasval]; currentParam != NULL; currentParam = currentParam->next) {
				if ((strcmp(lexemeBuffer, currentParam->pname) == 0) && (currentParam->hasValue == 0))  {
					break;
				}
			}

			memset(lexemeBuffer, 0 , BUFFERSIZE);
			readingLexeme = 0;
			i = 0;
		}

		/*End of parameter state. Insert to hash table. Resert counter and buffers.*/
		else if ((c == ';') && (!stringFlag)) {
			#ifdef DEBUG
			printf("\tSemicolon cound.\n");
			printf("\tlexemeBuffer contains: %s\n", lexemeBuffer);
			#endif

			/*Set the parameter value based on it's type.*/
			if (!setParameterValue(&p, &currentParam, lexemeBuffer)) {
				free(lexemeBuffer);
				p->errFlag = 1;
				PM_destroy(p);
				return 0;
			}

			/*Reset counter, flags and buffers.*/
			memset(lexemeBuffer, 0, BUFFERSIZE);
			i = 0;
			equalFlag = 0;
			readingLexeme = 0;
		}

		/*Store character in buffer.*/
		else {
			readingLexeme = 1;

			#ifdef DEBUG
			printf("Current Lexeme character is: %c\n", c);
			printf("readingLexeme is: %d\n", readingLexeme);
			#endif

			/*String type state. String flag alternates between 1 and 0.*/
			if (c == '"') {
				stringFlag = 1 - stringFlag;
			}

			/*List type state.  flag alternates between 1 and 0.*/
			if ((c == '{') || (c == '}')) {
				listFlag = 1 - listFlag;
			}

			/*Store character in the buffer and increment the counter.*/
			lexemeBuffer[i] = c;
			i++;
		}
	}

	#ifdef DEBUG
	printf("Before checking required.\n");
	printf("c is: %d\n", c);
	printf("readingLexeme is: %d\n", readingLexeme);
	#endif

	/*Parsing is still trying to read but reach EOF.*/
	if (readingLexeme == 1) {
		fprintf(stderr, "Parse Error: End of file before parsing finished (371).\n");
		p->errFlag = 1;
		free(lexemeBuffer);
		PM_destroy(p);
		return 0;
	}

	/*Check if a value has been stored for a required parameter.*/
	for(i = 0; i < p->size; i++) {
		for(currentParam = p->table[i]; currentParam != NULL; currentParam = currentParam->next) {
			if (currentParam->required != 0 && (PM_hasValue(p, currentParam->pname)) == 0) {
				fprintf(stderr, "Parse Error: Parameter %s is not registered.\n", currentParam->pname);
				p->errFlag = 1;
				free(lexemeBuffer);
				PM_destroy(p);
				return 0;
			}	
		}
	}
	free(lexemeBuffer);

	#ifdef DEBUG
	printf("End of parsing function.\n");
	#endif

	return 1;
}

/*Test if a parameter has been assigned a value.
 *PRE: pname is currently managed by p. 
 *POST: Returns 1 if pname has been assigned a value, 0 otherwise (no value, unknown parameter).
 */
int PM_manage(ParameterManager *p, char *pname, param_t ptype, int required) {
	parameter_t *newParam = NULL;
	parameter_t *currentParam = NULL;

	#ifdef DEBUG
	printf("Inside Manage, String is %s.\n", pname);
	printf("Size of table is %d\n", p->size);
	#endif

	unsigned int hasval = PM_hash(p, pname);
	int i = 0;

	if (strlen(pname) == 0) {
		fprintf(stderr, "Error: Parameter %s is empty.\n", pname);
		return 0;
	}

	/*Check if parameter name contains space.*/
	for (i = 0; i < strlen(pname); i++) {
		if (isspace(pname[i])) {
			fprintf(stderr, "Error: Parameter %s contains space.\n", pname);
			return 0;
		}
	}

	/*Check if parameter name is already managed.*/
	for(currentParam = p->table[hasval]; currentParam != NULL; currentParam = currentParam->next) {
		if (strcmp(pname, currentParam->pname) == 0) {
			fprintf(stderr, "Error: Duplicate parameter name for %s.\n", currentParam->pname);
			return 0;
		}
	}

	/*Check if given the appropriate ptype of the parameter.*/
	if ((ptype > 4) || (ptype < 0)) {
		fprintf(stderr, "Error: Unknown parameter type for %s\n", pname);
		return 0;
	}

	if ( (newParam = malloc(sizeof(parameter_t))) == NULL) {
		fprintf(stderr, "Error: Memory allocation failure on line 437.\n");
		PM_destroy(p);
		return 0;
	}

	if ( (newParam->pname = malloc(strlen(pname) + 1)) == NULL) {
		fprintf(stderr, "Error: Memory allocation failure on line 443.\n");
		PM_destroy(p);
		return 0;
	}

	if ( (newParam->value = malloc(sizeof(union param_value))) == NULL) {
		fprintf(stderr, "Error: Memory allocation failure on line 449.\n"); 
		PM_destroy(p);
		return 0;
	}

	/*Manage the parameters.*/
	strcpy(newParam->pname, pname);
	newParam->ptype = ptype;
	newParam->required = required;
	newParam->hasValue = 0;
	newParam->next = NULL;

	/*Chain.*/
	newParam->next = p->table[hasval];
	p->table[hasval] = newParam;
	return 1;
}

/*Test if a parameter has been assigned a value.
 *PRE: pname is currently managed by p. 
 *POST: Returns 1 if pname has been assigned a value, 0 otherwise (no value, unknown parameter).
 */
int PM_hasValue(ParameterManager *p, char *pname) {
	unsigned int hasval = PM_hash(p, pname);
	parameter_t *currentParam = NULL;

	/*Traverse through the linked list until parameter name is matched and check if it has a value stored.*/
	for(currentParam = p->table[hasval]; currentParam != NULL; currentParam = currentParam->next) {
		if (strcmp(pname, currentParam->pname) == 0) {
			if (currentParam->hasValue == 1) {
				return 1;
			}
		}
	}
	/*Unknown parameter or no value stored.*/
	return 0;
}

/*Obtain the value assigned to pname.
 *PRE: pname is currently managed by p and has been assigned a value.
 *POST: Returns the value assigned to pname; result is undefined if pname has not been assigned a value or is unknown.
 */
union param_value PM_getValue(ParameterManager *p, char *pname) {
	unsigned int hasval = 0;
	parameter_t *currentParam = NULL;
	hasval = PM_hash(p, pname);

	/*Traverse through the linked list until parameter name is matched and return the value.*/
	for(currentParam = p->table[hasval]; currentParam != NULL; currentParam = currentParam->next) {
		if (strcmp(pname, currentParam->pname) == 0) {
			return (*currentParam->value);	
		}
	}
	return (*currentParam->value); /*Not too sure about this. I just need to return it no matter what.*/
}

/*Set's the parameter value based on the type of that parameter.
 *PRE: n/a.
 *POST: Returns 1 on success, 0 otherwise (memory allocation failure, parsing error).
 */	
int setParameterValue(ParameterManager **p, parameter_t **currentParam, char *value) {
	ParameterList *newPList = NULL;			/*ParameterList pointer to store tokens of LIST_TYPE.*/
	char *token = NULL;						/*Pointer to the tokens of LIST_TYPE.*/
	int floatCheck = 0;						/*Counter for decimal point in REAL_TYPE.*/
	int i = 0;								/*Counter.*/

	#ifdef DEBUG
	printf("In setParameterValue function.\n");
	printf("Name is: %s\n", (*currentParam)->pname);
	printf("Value is: %s\n", value);
	printf("Parameter name is %s: and value is %s.\n", (*currentParam)->pname, value);
	#endif

	/*Unsupported parameter ignore it.*/
	if (*currentParam == NULL) {
		return 1;
	}

	switch((*currentParam)->ptype) {
		case INT_TYPE: {

			/*Check if buffer contains an alphanumerical characters.*/
			for (i = 0; i < strlen(value); i++) {
				if (((isdigit(value[i])) == 0) && (value[i] != '-')) { 
					fprintf(stderr, "Parse Error: Parameter %s is not an INT_TYPE.\n", (*currentParam)->pname);
					(*p)->errFlag = 1;
					return 0;
				}
			}

			/*Store INT_TYPE and update hasValue flag.*/
			(*currentParam)->value->int_val = atoi(value);
			(*currentParam)->hasValue = 1;
			break;
		}
		case REAL_TYPE: {

			for (i = 0; i < strlen(value); i++) {

				/*Check is buffer contains a decimal character.*/
				if (value[i] == '.') {
					floatCheck++;
				}

				/*Check if buffer contains an alphanumerical character.*/
				else if ((isdigit(value[i])) == 0 && (value[i] != '-')) {  							
					fprintf(stderr, "Parse Error: Parameter %s is not a REAL_TYPE.\n", (*currentParam)->pname);
					(*p)->errFlag = 1;
					return 0;
				}
			}

			/*There is more than one decimal point.*/
			if (floatCheck > 1) {
				fprintf(stderr, "Parse Error: Parameter %s is not a REAL_TYPE.\n", (*currentParam)->pname);
				(*p)->errFlag = 1;
				return 0;
			}

			/*Store REAL_TYPE and update hasValue flag.*/
			(*currentParam)->value->real_val = atof(value);
			(*currentParam)->hasValue = 1;
			floatCheck = 0;
			break;
		}
		case BOOLEAN_TYPE: {

			/*String compare "true" and store BOOLEAN_TYPE and update hasValue flag.*/
			if (strcmp("true", value) == 0) {
				(*currentParam)->value->bool_val = true;
				(*currentParam)->hasValue = 1;
			}

			/*String compare "false" and store BOOLEAN_TYPE and update hasValue flag.*/
			else if (strcmp("false", value) == 0)  {
				(*currentParam)->value->bool_val = false;
				(*currentParam)->hasValue = 1;
			}
			else {
				fprintf(stderr, "Parse Error: Parameter %s is not a BOOLEAN_TYPE.\n", (*currentParam)->pname);
				(*p)->errFlag = 1;
				return 0;
			}
			break;
		}
		case STRING_TYPE: {

			/*Check if first and last characters are quotation marks.*/
			if ((value[0] == '"') && (value[strlen(value) - 1] == '"')) {

				/*Malloc for str_val.*/
				if ( ((*currentParam)->value->str_val = malloc(strlen(value) + 1)) == NULL) {
					fprintf(stderr, "Error: Memory allocation failure on line 601.\n");
					return 0;
				}

				/*Strip the first and last character in the buffer and store the string.*/
				memmove(value, value + 1, strlen(value));
				value[strlen(value) - 1] = '\0';
				strcpy((*currentParam)->value->str_val, value);
				(*currentParam)->hasValue = 1;
			}
			else {
				fprintf(stderr, "Parse Error: Parameter %s is not a STRING_TYPE.\n", (*currentParam)->pname);
				(*p)->errFlag = 1;
				return 0;
			}
			break;
		}
		case LIST_TYPE: {

			/*Check if first and last characters are curly brackets.*/
			if ((value[0] == '{') && (value[strlen(value) - 1] == '}')) {

				/*Strip the first and last character in buffer.*/
				memmove(value, value + 1, strlen(value));
				value[strlen(value) - 1] = '\0';

				/*Check if buffer is empty and store the empty list.*/
				if (strlen(value) == 0) {

					/*Malloc for list_val.*/
					if (((*currentParam)->value->list_val = malloc(sizeof(ParameterList))) == NULL) {
							fprintf(stderr, "Error: Memory allocation failure on line 633.\n");
							return 0;
					}

					/*Malloc for item in ParameterList.*/
					if (((*currentParam)->value->list_val->item = malloc(strlen(value) + 1)) == NULL) {
							fprintf(stderr, "Error: Memory allocation failure on line 638.\n");
							return 0;
					}

					/*Store the head of the ParameterList and update hasValue flag.*/
					strcpy((*currentParam)->value->list_val->item, value);
					(*currentParam)->value->list_val->next = NULL;
					(*currentParam)->hasValue = 1;
					(*currentParam)->value->list_val->current = NULL;
				}
				else {

					/*Tokenize the list by comma.*/
					token = strtok(value, ",");

					/*Check if first and last character of token are quotation marks.*/
					if ((token[0] != '"') && (token[strlen(token) - 1]) != '"') {
						fprintf(stderr, "Parse Error: Parameter %s is not a LIST_TYPE.\n", (*currentParam)->pname);
						(*p)->errFlag = 1;
						return 0;
					}

					/*Strip the first and last character in token.*/
					memmove(token, token + 1, strlen(token));
					token[strlen(token) - 1] = 0;

					/*Malloc for list_val.*/
					if (((*currentParam)->value->list_val = malloc(sizeof(ParameterList))) == NULL) {
							fprintf(stderr, "Error: Memory allocation failure on line 666.\n");
							return 0;
					}

					/*Malloc for item in ParameterList.*/
					if (((*currentParam)->value->list_val->item = malloc(strlen(value) + 1)) == NULL) {
							fprintf(stderr, "Error: Memory allocation failure on line 672.\n");
							return 0;
					}

					/*Copy the first token in list. This is the head pointer.*/
					strcpy((*currentParam)->value->list_val->item, token);
					(*currentParam)->value->list_val->next = NULL;

					/*Copy the head in current for PL_next function.*/
					(*currentParam)->value->list_val->current = (*currentParam)->value->list_val;
					(*currentParam)->hasValue = 1;

					/*Tokenize the whole list and create and store in a ParameterList struct.*/
					while (token != NULL) {
						token = strtok(NULL, ",");

						/*Move on to the next token*/
						if (token != NULL) {

							if ((token[0] != '"') && (token[strlen(token) - 1]) != '"') {
								fprintf(stderr, "Parse Error: Parameter %s is not a LIST_TYPE.\n", (*currentParam)->pname);
								(*p)->errFlag = 1;
								return 0;
							}

							/*Strip the first and last character in token. Create and store it in a parameterList and add it to the back.*/
							memmove(token, token + 1, strlen(token));
							token[strlen(token) - 1] = '\0';
							newPList = PL_create(token);
							if (newPList == NULL) {
								return 0;
							}
							PL_addBack((*currentParam)->value->list_val, newPList);
						}
					}
				}
			}
			else {
				fprintf(stderr, "Parse Error: Parameter %s is not a LIST_TYPE.\n", (*currentParam)->pname);
				(*p)->errFlag = 1;
				return 0;
			}
			break;
		}
		default:
			fprintf(stderr, "Parse Error: Uknown date type for %s.\n", (*currentParam)->pname);
			(*p)->errFlag = 1;
			return 0;
	}
return 1;
}

/*Hash function taking from sparknotes. Citation in README.txt.
 *PRE: n/a.
 *POST: Returns the the index of the pname stored in the table.
 */
unsigned int PM_hash(ParameterManager *p, char *pname) {
	unsigned int hasval = 0;
	int sum = 0;

	#ifdef DEBUG
	printf("Inside hash, String is %s.\n", pname);
	printf("Size of table is %d\n", p->size);
	#endif

	/*For each character multiply the old hasval by 31 and add the current.*/
	for (; *pname != '\0'; pname++) {
		hasval = *pname + (hasval << 5) - hasval;
	}

	sum = hasval % p->size;

	#ifdef DEBUG
	printf("Inside hash, hasval before return is %d.\n", hasval);
	printf("Inside hash, string before return is %s.\n", pname);
	printf("Hasval mod table size is %d\n", sum);
	#endif

	/*Hasval mod size of the table to make sure it fits the range.*/
	return (hasval % p->size);
}

/*Create a ParameterList struct and store the token inside it.
 *PRE: n/a.
 *POST: Returns back the pointer to the newly created ParameterList on sucess otherwise NULL (memory allocation failure).
 */
ParameterList * PL_create(char *token){ 
	ParameterList *newPList = NULL;

	/*Malloc for list_val.*/
	if ( (newPList = malloc(sizeof(ParameterList))) == NULL) {
		fprintf(stderr, "Error: Memory allocation failure on line 763.\n"); 
		return NULL;
	}
	
	/*Malloc for item in ParameterList.*/
	if ( (newPList->item = malloc(strlen(token) + 1)) == NULL) {
		fprintf(stderr, "Error: Memory allocation failure on line 769.\n"); 
		return NULL;
	}

	/*Store the item of the list.*/
	strcpy(newPList->item, token);
	newPList->next = NULL;
	newPList->current = NULL;

	return newPList;
}

/*Add a ParameterList to the back of the list.
 *PRE: n/a.
 *POST: n/a.
 */
void PL_addBack(ParameterList *head, ParameterList *l) {
	ParameterList *temp = head;

	/*Traverse the list until you reach the tail.*/
	while(temp->next != NULL) {
		temp = temp->next;
	}

	/*Add the list to the back.*/
	temp->next = l;
}

/*Obtain the next item in a parameter list.
 *PRE: n/a.
 *POST: Returns the next item in parameter list l, NULL if there are no items remaining in the list.
 */
char * PL_next(ParameterList *l) {
	ParameterList *currentPList = NULL;

	/*Exhausted all the items in the ParameterList.*/
	if (l->current == NULL) {
		return NULL;
	}
	
	/*Return item in the list and update the current list pointer.*/
	else {
		currentPList = l->current;
		l->current = l->current->next;
		return currentPList->item;
	}
}