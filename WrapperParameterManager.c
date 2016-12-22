#include <jni.h>
#include <stdio.h>
#include "WrapperParameterManager.h"
#include "ParameterManager.h"

/*Global ParameterManger variable.*/
ParameterManager *p;

/*
 * Class:     WrapperParameterManager
 * Method:    create
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL
Java_WrapperParameterManager_create(JNIEnv *env, jobject this, jint size) {

	if (!(p = PM_create(10))) {
		#ifdef DEBUG
		printf("Did not created ParameterManager succesful\n");
		#endif

		return false;
	}
	else {
		#ifdef DEBUG
		printf("Created ParameterManager\n");
		printf("Size of table is %d\n", p->size);
		#endif

		return true;
	}
}

/*
 * Class:     WrapperParameterManager
 * Method:    create
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL
Java_WrapperParameterManager_destroy(JNIEnv *env, jobject this) {

	if (!(PM_destroy(p))) {
		#ifdef DEBUG
		printf("Not destroyed\n");
		#endif
		return false;
	}
	else {
		#ifdef DEBUG
		printf("\nSuccesfully destroyed\n");
		#endif
		return true;	
	}
}

/*
 * Class:     WrapperParameterManager
 * Method:    parseFrom
 * Signature: (Ljava/lang/String;C)Z
 */
JNIEXPORT jboolean JNICALL
Java_WrapperParameterManager_parseFrom(JNIEnv *env, jobject this, jstring fileName, jchar comment) {
	const char *str= (*env)->GetStringUTFChars(env,fileName,0);

	if (!str) {
		#ifdef DEBUG
		printf("OutOfMemoryException Error\n");
		#endif

		return false;
	}

	#ifdef DEBUG
	printf("File name is: %s\n", str);
	#endif

	FILE *fp = fopen((char*)str, "r");
	/*if (fp == NULL) {
		return false;
	}*/

	if (PM_parseFrom(p, fp, comment)) {
		#ifdef DEBUG
		printf("\nParsing finished succesfully\n");
		#endif

		(*env)->ReleaseStringUTFChars(env, fileName, str);
		return true;
	}
	else {
		#ifdef DEBUG
		printf("Parsing failed\n");
		#endif 

		fclose(fp);
		return false;
	}
}

/*
 * Class:     WrapperParameterManager
 * Method:    manage
 * Signature: (Ljava/lang/String;II)Z
 */
JNIEXPORT jboolean JNICALL
Java_WrapperParameterManager_manage(JNIEnv *env, jobject this, jstring parameterName, jint parameterType, jint required) {
	const char *str= (*env)->GetStringUTFChars(env,parameterName,0);

	if (!str) {
		#ifdef DEBUG
		printf("OutOfMemoryException Error\n");
		#endif

		//return NULL;
	}
	#ifdef DEBUG
	printf("Parameter is %s\n", str);
	#endif

	if (PM_manage(p, (char*)str, parameterType, required)) {
		#ifdef DEBUG
		printf("Parameter managed\n");
		#endif

		(*env)->ReleaseStringUTFChars(env, parameterName, str);
		return true; 
	}
	else {
		#ifdef DEBUG
		printf("Parameter not managed\n");
		#endif

		return false;
	}
}

/*
 * Class:     WrapperParameterManager
 * Method:    hasValue
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL
Java_WrapperParameterManager_hasValue(JNIEnv *env, jobject this, jstring parameterName) {
	const char *str= (*env)->GetStringUTFChars(env, parameterName, 0);

	if (!str) {
		#ifdef DEBUG
		printf("OutOfMemoryException Error\n");
		#endif

		return false;
	}
	#ifdef DEBUG
	printf("Parameter name is: %s\n", str);
	#endif

	if (PM_hasValue(p, (char*)str)) {
		#ifdef DEBUG
		printf("Value is stored for parameter\n");
		#endif

		(*env)->ReleaseStringUTFChars(env, parameterName, str);
		return true;
	}
	else {
		#ifdef DEBUG
		printf("Value is not stored for parameter\n");
		#endif

		return false;
	}
}

/*
 * Class:     WrapperParameterManager
 * Method:    getInt
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL
Java_WrapperParameterManager_getInt(JNIEnv *env, jobject this, jstring parameterName) {
	int myInt = 0;
	const char *str = (*env)->GetStringUTFChars(env, parameterName, 0);

	if (!str) {
		#ifdef DEBUG
		printf("OutOfMemoryException Error\n");
		#endif

		return false;
	}
	#ifdef DEBUG
	printf("Parameter name is: %s\n", str);
	#endif

	/*Get the integer from the union.*/
	myInt = PM_getValue(p, (char*)str).int_val;
	(*env)->ReleaseStringUTFChars(env, parameterName, str);
	return myInt;
}

/*
 * Class:     WrapperParameterManager
 * Method:    getReal
 * Signature: (Ljava/lang/String;)F
 */
JNIEXPORT jfloat JNICALL
Java_WrapperParameterManager_getReal(JNIEnv *env, jobject this, jstring parameterName) {
	float myReal = 0;
	const char *str = (*env)->GetStringUTFChars(env, parameterName, 0);

	if (!str) {
		#ifdef DEBUG
		printf("OutOfMemoryException Error\n");
		#endif

		return false;
	}
	#ifdef DEBUG
	printf("Parameter name is: %s\n", str);
	#endif

	/*Get the real from the union.*/
	myReal = PM_getValue(p, (char*)str).real_val;
	(*env)->ReleaseStringUTFChars(env, parameterName, str);
	return myReal;
}

/*
 * Class:     WrapperParameterManager
 * Method:    getBoolean
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL
Java_WrapperParameterManager_getBoolean(JNIEnv *env, jobject this, jstring parameterName) {
	const char *str = (*env)->GetStringUTFChars(env, parameterName, 0);

	if (!str) {
		#ifdef DEBUG
		printf("OutOfMemoryException Error\n");
		#endif
		//return NULL;
	}
	#ifdef DEBUG
	printf("Parameter name is: %s\n", str);
	#endif

	/*Get the boolean from the union.*/
	jboolean myBoolean = PM_getValue(p, (char*)str).bool_val;
	(*env)->ReleaseStringUTFChars(env, parameterName, str);
	return myBoolean; 
}

/*
 * Class:     WrapperParameterManager
 * Method:    getString
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL 
Java_WrapperParameterManager_getString(JNIEnv *env, jobject this, jstring parameterName) {
	const char *myString = NULL;
	const char *str = (*env)->GetStringUTFChars(env, parameterName, 0);

	if (!str) {
		#ifdef DEBUG
		printf("OutOfMemoryException Error\n");
		#endif

		return NULL;
	}
	#ifdef DEBUG
	printf("Parameter name is: %s\n", str);
	#endif

	/*Get the string from the union and created a jstring.*/
	myString = PM_getValue(p, (char*)str).str_val;
	jstring newString = (*env)->NewStringUTF(env, myString);
	(*env)->ReleaseStringUTFChars(env, parameterName, str);

	return newString;
}

/*
 * Class:     WrapperParameterManager
 * Method:    getList
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_WrapperParameterManager_getList(JNIEnv *env, jobject this, jstring parameterName) {
	ParameterList * headPList = NULL;
	const char *listString = NULL;
	jstring newString = NULL;
	const char *str = (*env)->GetStringUTFChars(env, parameterName, 0);

	if (!str) {
		#ifdef DEBUG
		printf("OutOfMemoryException Error\n");
		#endif

		return NULL;
	}
	#ifdef DEBUG
	printf("Parameter name is: %s\n", str);
	#endif

	/*Get the head of the list and pass it to PL_next to get the list items.*/
	headPList = PM_getValue(p, (char*)str).list_val;
	listString = PL_next(headPList);
	newString = (*env)->NewStringUTF(env, listString);
	(*env)->ReleaseStringUTFChars(env, parameterName, str);

	return newString;
}