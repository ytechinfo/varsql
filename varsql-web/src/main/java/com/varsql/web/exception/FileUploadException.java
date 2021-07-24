package com.varsql.web.exception;

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
	
	private int errorCode;
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
	
	
	public FileUploadException(int errorCode,Exception exeception) {
		this(errorCode,null, exeception);
	}
	
	public FileUploadException(int errorCode, String messageCode ,Exception exeception) {
		this(errorCode, messageCode , null, exeception);
	}
	public FileUploadException(int errorCode,String messageCode,	String errorMessage, Exception exeception) {
		super(errorMessage, exeception);
		this.errorCode=errorCode ;
		this.messageCode=messageCode;
		this.errorMessage = errorMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
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
