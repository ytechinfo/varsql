package com.varsql.web.exception;

import com.varsql.core.common.code.VarsqlAppCode;
import com.vartech.common.constants.CodeEnumValue;

/**
*
* @FileName  : FileNotFoundException.java
* @Date      : 2020. 11. 12.
* @작성자      : ytkim
* @변경이력 :
* @프로그램 설명 : file not found exception
*/
public class FileUploadException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private CodeEnumValue errorCode;
	private String errorMessage;
	private String messageCode;

	/**
	 *
	 */
	public FileUploadException() {
		super();
	}
	/**
	 * @param s java.lang.String
	 */
	public FileUploadException(String s) {
		super(s);
	}
	/**
	 * @param s java.lang.String
	 */
	public FileUploadException(String s , Exception exeception) {
		super(s,exeception);
	}


	public FileUploadException(CodeEnumValue errorCode,Exception exeception) {
		this(errorCode,null, exeception);
	}

	public FileUploadException(CodeEnumValue errorCode, String messageCode ,Exception exeception) {
		this(errorCode, messageCode , null, exeception);
	}
	public FileUploadException(CodeEnumValue errorCode,String messageCode,	String errorMessage, Exception exeception) {
		super(errorMessage, exeception);
		this.errorCode=errorCode ;
		this.messageCode=messageCode;
		this.errorMessage = errorMessage;
	}

	public CodeEnumValue getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(CodeEnumValue errorCode) {
		this.errorCode = errorCode != null? errorCode : VarsqlAppCode.COMM_FILE_UPLOAD_ERROR;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}