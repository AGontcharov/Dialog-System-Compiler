/*
 * ------------------------------------------------------------------
 *  list.h - Definition of a string list.
 *
 *  CIS*2750 - Software Systems Development and Integration - W15
 *  School of Computer Science
 *  University of Guelph
 *  Author: Alexander Gontcharov
 *  Student ID: 0814685
 * ------------------------------------------------------------------
 */

#ifndef _LIST_H_
#define _LIST_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
/*#define DEBUG*/ /*Set debugging on or off.*/

/*Struct for storing strings.*/
typedef struct stringList {
	int checked;
	char *string;
	struct stringList *next;
}stringList;

/*Create a struct containing the string.*/
stringList *createListNode(char *string);

/*Add string to the back of the list.*/
stringList *addToBack(stringList * headList, char *string);

/*Get the string at the current index of the list.*/
char *getStringAt(stringList * headList, int index);

/*Get the size of the list.*/
int getListSize(stringList * headList);

/*Check if string is already in list.*/
int matchString(stringList * headList, char * string);

/*Destroy the last item in the list.*/
stringList *destroyLastNode(stringList *headList);

/*Print the list.*/
void printList(stringList *headList);

/*Destroy the list freeing the string and struct.*/
void destroyList(stringList **headList);

#endif