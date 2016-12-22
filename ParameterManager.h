/*
 * ------------------------------------------------------------------
 *  ParameterManager.h - Definition of the ParameterManager object and its functions on it.
 *
 *  CIS*2750 - Software Systems Development and Integration - W15
 *  School of Computer Science
 *  University of Guelph
 *  Author: Alexander Gontcharov
 *  Student ID: 0814685
 * ------------------------------------------------------------------
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>

#define BUFFERSIZE 1025 /*Buffer size for parameter name and value.*/
/*#define DEBUG*/ /*Set debugging on or off*/


/*A data type for the supported parameter types.*/
typedef enum parameterTypes {
	INT_TYPE,
	REAL_TYPE,
	BOOLEAN_TYPE,
	STRING_TYPE,
	LIST_TYPE
}param_t;

/*A boolean date type with the domain { true, false}.*/
typedef enum Boolean {
	false,
	true
}Boolean;

/*A sequence of individual values (string type) that taken together constitute the value assigned to a sinle parameter.*/
typedef struct ParameterList {
	char *item;								/*An item in the list.*/
	struct ParameterList *next;				/*Pointer to the next item ParameterList.*/
	struct ParameterList *current;			/*Pointer to the current parameterList.*/
}ParameterList;

/*Allows the value of a parameter to be stored in the same field regardless of type.*/
union param_value {
    int           int_val;
    float         real_val;
    Boolean       bool_val;  
    char          *str_val;
    ParameterList *list_val;  
}; 

/*Date type for storing the parameters in the hashtable.*/
typedef struct parameter_s {
	char          *pname;					/*Name of the parameter.*/
	param_t       ptype;					/*Type of the parameter.*/
	int           required;					/*Priority of the parameter.*/
	int 		  hasValue;					/*Flag for if the parameter has a value assigned.*/
	union         param_value *value;		/*Value of the parameter as a union.*/
	struct        parameter_s *next;		/*Pointer to the next parameter_t in the list.*/
} parameter_t;

/*Date type for ParameterManager in the form of a hashtable.*/
typedef struct ParameterManager_s {
	int           size;						/*Size of the hashtable.*/
	int 		  errFlag;
	parameter_t   **table; 					/*The actual table.*/
}ParameterManager;

/*Creates a new parameter manager objet.
 *PRE: size is a positive integer.
 *POST: Returns a new parameter manager object initialized to be empty (i.e. managing no parameters) on success, 
 *NULL otherwise (memory allocation failure).
 */
ParameterManager * PM_create(int size);

/*Destroys a parameter manger objet.
 *PRE: n/a.
 *POST: all memory associated with parameter manager p is freed; returns 1 on success, 0 otherwise.
 */
int PM_destroy(ParameterManager *p);

/*Extract values for parameters from an input stream.
 *PRE: fp is a valid input stream ready for reading that contains the desired parameters.
 *POST: All required parameters, and those optional parameters present, are assigned values that are consumed from fp, 
 *respecting comment as a "start of comment to end of line" character if not nul ('\0'); 
 *returns non-zero on success, 0 otherwise (parse error,memory allocation failure).
 */
int PM_parseFrom(ParameterManager *p, FILE *fp, char comment);

/*Set's the parameter value based on the type of that parameter.
 *PRE: n/a.
 *POST: Returns 1 on success, 0 otherwise (memory allocation failure, parsing error).
 */	
int setParameterValue(ParameterManager **p, parameter_t **currentParam, char *value);

/*Register parameter for management.
 *PRE: pname does not duplicate the name of a parameter already managed, doest not contain space, or is an empty string.
 *POST: Parameter named pname of type ptype is registered with p as a parameter; 
 *if required is zero the parameter will be considered optional, otherwise it will be considered required; 
 *returns 1 on success, 0 otherwise (duplicate name, memory allocation failure)
 */
int PM_manage(ParameterManager *p, char *pname, param_t ptype, int required);

/*Test if a parameter has been assigned a value.
 *PRE: pname is currently managed by p. 
 *POST: Returns 1 if pname has been assigned a value, 0 otherwise (no value, unknown parameter).
 */
int PM_hasValue(ParameterManager *p, char *pname);

/*Obtain the value assigned to pname.
 *PRE: pname is currently managed by p and has been assigned a value.
 *POST: Returns the value assigned to pname; result is undefined if pname has not been assigned a value or is unknown.
 */
union param_value PM_getValue(ParameterManager *p, char *pname);

/*Hash function.
 *PRE: n/a.
 *POST: Returns the the index of the pname stored in the table.
 */
unsigned int PM_hash(ParameterManager *p, char *pname);

/*Create a ParameterList struct and store the token inside it.
 *PRE: n/a.
 *POST: Returns back the pointer to the newly created ParameterList on sucess otherwise NULL (memory allocation failure).
 */
ParameterList * PL_create(char *token);

/*Add a ParameterList struct to the back of the list.
 *PRE: n/a.
 *POST: n/a.
 */
void PL_addBack(ParameterList *head, ParameterList *l);

/*Obtain the next item in a parameter list.
 *PRE: n/a.
 *POST: Returns the next item in parameter list l, NULL if there are no items remaining in the list.
 */
char * PL_next(ParameterList *l);