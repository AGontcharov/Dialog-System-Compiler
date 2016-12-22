#Macros for Makefile
CC 	= gcc
CFLAGS = -Wall -g -c
VFLAGS = --leak-check=full --show-reachable=yes --track-origins=yes -v
OUTPUT = ./output/test.java ./output/testFieldEdit.java output/IllegalFieldValueException.java ./output/*.class

all: yadc

y.tab.c: yadc.y
	yacc -d -y yadc.y -v

y.tab.h: yadc.y
	yacc -d -y yadc.y -v

lex.yy.c: yadc.l y.tab.h
	lex yadc.l

list.o: list.c list.h y.tab.h
	$(CC) $(CFLAGS) list.c

lex.yy.o: lex.yy.c
	$(CC) $(CFLAGS) lex.yy.c

y.tab.o: y.tab.c
	$(CC) $(CFLAGS) y.tab.c

yadc: lex.yy.o y.tab.o list.o
	$(CC) lex.yy.o y.tab.o list.o -o yadc -lfl

run: yadc
	./yadc /home/alexander/Documents/CIS/CIS2750/Assignment/a3/yadc/test.config test /home/alexander/Documents/CIS/CIS2750/Assignment/a3/yadc/output

clean: 
	rm -rf yadc lex.yy.c y.tab.c y.tab.h y.output *.o *.class testP1.java testP1FieldEdit.java list test.java testFieldEdit.java IllegalFieldValueException.java $(OUTPUT)

valgrind: 
	valgrind $(VFLAGS) ./yadc /home/alexander/Documents/CIS/CIS2750/Assignment/a3/yadc/testP1.config testP1

valgrind2: 
	valgrind $(VFLAGS) ./yadc /home/alexander/Documents/CIS/CIS2750/Assignment/a3/yadc/test.config test