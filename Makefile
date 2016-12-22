#Macros for <jni.h> path
JPATH1 = /usr/lib/jvm/java-1.6.0-openjdk/include
JPATH2 = /usr/lib/jvm/java-1.6.0-openjdk/include/linux
JPATH3 = /usr/local/java/jdk1.8.0_05/include
JPATH4 = /usr/local/java/jdk1.8.0_05/include/linux

all: Dialogc

WrapperParameterManager.class: WrapperParameterManager.java
	javac WrapperParameterManager.java

WrapperParameterManager.h: WrapperParameterManager.class
	javah WrapperParameterManager

ParameterManager.o: ParameterManager.c ParameterManager.h
	gcc -c -fPIC ParameterManager.c

WrapperParameterManager.o: WrapperParameterManager.c WrapperParameterManager.h
	gcc -c -fPIC WrapperParameterManager.c -I$(JPATH3) -I$(JPATH4)

libJNIpm.so: WrapperParameterManager.o ParameterManager.o
	gcc -shared -Wl,-soname,libJNIpm.so -I$(JPATH3) -I$(JPATH4) -o libJNIpm.so WrapperParameterManager.o ParameterManager.o

class: Dialogc.java DialogcMenuBar.java ConfirmWindow.java MainWindow.java CompileListener.java CompileGUICode.java
	javac Dialogc.java DialogcMenuBar.java ConfirmWindow.java MainWindow.java CompileListener.java CompileGUICode.java

yadcMake: 
	$(MAKE) -C ./yadc

Dialogc: class libJNIpm.so yadcMake
	export LD_LIBRARY_PATH=. && java Dialogc

CompiledGUICode.class: CompiledGUICode.javac
	javac CompiledGUICode.java

Test.java: CompiledGUICode.class
	java CompiledGUICode

clean:
	rm -rf *.class *.o *.so *.log Example Guification Reserved Reserved2 Reserved3 && $(MAKE) -C ./yadc clean
