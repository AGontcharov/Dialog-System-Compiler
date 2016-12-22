public class WrapperParameterManager {

	static final int INT_TYPE = 0;
	static final int REAL_TYPE = 1;
	static final int BOOLEAN_TYPE = 2;
	static final int STRING_TYPE = 3;
	static final int LIST_TYPE = 4;

	//Don't think I need this
	/*public static String fileName;
	public static char comment;*/

	//Or this
	/*public void setFileStream(String fileName) {
		this.fileName = fileName;
	}

	public void setCharComment(char comment) {
		this.comment = comment;
	}*/

	//Any way to make this part of the constructor or maybe initialize in the wrapper library?*/
	/*
	 * Class:     WrapperParameterManager
	 * Method:    create
	 * Signature: (I)Z
	 */
	public native boolean create(int size);

	/*
	 * Class:     WrapperParameterManager
	 * Method:    destroy
	 * Signature: ()Z
	 */
	public native boolean destroy();

	/*
	 * Class:     WrapperParameterManager
	 * Method:    parseFrom
	 * Signature: (Ljava/lang/String;C)Z
	 */
	public native boolean parseFrom(String fileName, char comment);

	/*
	 * Class:     WrapperParameterManager
	 * Method:    manage
	 * Signature: (Ljava/lang/String;II)Z
	 */
	public native boolean manage(String parameterName, int parameterType, int required);

	/*
	 * Class:     WrapperParameterManager
	 * Method:    hasValue
	 * Signature: (Ljava/lang/String;)Z
	 */
	public native boolean hasValue(String parameterName);

	/*
	 * Class:     WrapperParameterManager
	 * Method:    getInt
	 * Signature: (Ljava/lang/String;)I
	 */
	public native int getInt(String parameterName);

	/*
	 * Class:     WrapperParameterManager
	 * Method:    getReal
	 * Signature: (Ljava/lang/String;)F
	 */
	public native float getReal(String parameterName);
	/*
	 * Class:     WrapperParameterManager
	 * Method:    getBoolean
	 * Signature: (Ljava/lang/String;)Z
	 */
	public native boolean getBoolean(String parameterName);
	/*
	 * Class:     WrapperParameterManager
	 * Method:    getString
	 * Signature: (Ljava/lang/String;)Ljava/lang/String;
	 */
	public native String getString(String parameterName);

	/*
	 * Class:     WrapperParameterManager
	 * Method:    getList
	 * Signature: (Ljava/lang/String;)Ljava/lang/String;
	 */
	public native String getList(String parameterName);

	//Load the library at compile time
	static {
		System.loadLibrary("JNIpm");
	}
}