/*
 * ------------------------------------------------------------------
 *  list.c - This library provides a type (list) and a set of operations defined on this type.
 *
 *  CIS*2750 - Software Systems Development and Integration - W15
 *  School of Computer Science
 *  University of Guelph
 *  Author: Alexander Gontcharov
 *  Student ID: 0814685
 * ------------------------------------------------------------------
 */

#include "list.h"

/*Create a struct containing the string*/
stringList *createListNode(char *string) {
	stringList *newNode = NULL;

	#ifdef DEBUG
	printf("In createListNode function.\n");
	#endif

	if (!(newNode = malloc(sizeof(stringList)))) {
		fprintf(stderr, "Error: Memory allocation failure on line 9\n");
		exit(1);
	}

	if (!(newNode->string = malloc(strlen(string) + 1))) {
		fprintf(stderr, "Error: Memory allocation failure on line 16\n");
		exit(1);
	}

	#ifdef DEBUG
	printf("Succesfully malloc'd.\n");
	printf("String parameter is: %s\n", string);
	#endif

	strcpy(newNode->string, string);
	newNode->checked = 0;
	newNode->next = NULL;

	return newNode;
}

/*Add string to the back of the list*/
stringList *addToBack(stringList *headList, char *string) {
	stringList *tempList = headList;

		#ifdef DEBUG
		printf("\nIn addToBack function.\n");
		#endif

	/*The head is empty*/
	if (!tempList) {
		headList = createListNode(string);
		return headList;
	}
	/*The head is not empty.*/
	else {
		while (tempList->next) {
			tempList = tempList->next;
		}
		tempList->next = createListNode(string);
		return headList;	
	} 
}

/*Get the string at the current index of the list.*/
char *getStringAt(stringList * headList, int index) {
	int count = 0;

	while (headList) {
		if (count == index) {
			return headList->string;
		}
		headList = headList->next;
		count++;
	}
	return NULL;
}

/*Get the size of the list.*/
int getListSize(stringList * headList) {
	int size = 1;

	if(!headList) {
		return 0;
	}

	while (headList->next) {
		headList = headList->next;
		size++;
	}
	return size;
}

/*Check if string is already in list.*/
int matchString(stringList * headList, char * string) {

	while (headList) {
		if (strcmp(headList->string, string) == 0 && headList->checked != 1) {
			headList->checked = 1;
			return 1;		
		}
		headList = headList->next;
	}
	return 0;
}

/*Destroy the last item in the list.*/
stringList *destroyLastNode(stringList *headList) {
	stringList *tempList = headList;
	stringList *beforeTempList = NULL;

	while (tempList->next) {
		beforeTempList = tempList;
		tempList = tempList->next;
	}

	#ifdef DEBUG
	printf("This is: %s\n", tempList->string);
	#endif

	free(tempList->string);
	free(tempList);

	#ifdef DEBUG
	printf("This is after free:%send\n", tempList->string);
	#endif

	tempList = NULL;
	beforeTempList->next = NULL;

	return headList;
}

/*Print the list*/
void printList(stringList *headList) {
	while (headList) {
		#ifdef DEBUG
		printf("\nIn printList function.\n");
		#endif

		printf("Printing: %s\n", headList->string);
		printf("Printing: %d\n", headList->checked);
		headList = headList->next;
	}

	#ifdef DEBUG
	printf("End of printList function.\n");
	#endif
}

/*Destroy the list freeing the string and struct*/
void destroyList(stringList **headList) {
	stringList *tempList = NULL;

	#ifdef DEBUG
	printf("\nIn destroyList function\n");
	printf("Start of list is %s\n", headList->string);
	#endif

	while (*headList) {
		tempList = *headList;
		#ifdef DEBUG
		printf("Freeing: %s\n", *headList->string);
		#endif

		*headList = (*headList)->next;
		free(tempList->string);
		free(tempList);
	}

	*headList = NULL;

	#ifdef DEBUG
	printf("End of destroyList function.\n");
	#endif
}