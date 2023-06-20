package com.milkit.app.common.exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.milkit.app.common.ErrorCodeEnum;

@SuppressWarnings("serial")
public class ServiceException extends RuntimeException {

	private String code = ErrorCodeEnum.ServiceException.getCode();
	
	private String[] objects;
	

	public ServiceException() {
		this(ErrorCodeEnum.ServiceException.getCode());
	}
	
	public ServiceException(String code) {
		this.code = code;
	}
	
	public ServiceException(String code, String[] objects) {
		this.code = code;
		this.objects = objects;
	}
	
	
    public ServiceException(String code, String message) {
    	super(message);
        this.code = code;
    }
    
	public ServiceException(String code, String message, String[] objects) {
		super( message );
		this.code = code;
		this.objects = objects;
	}
    
    public ServiceException(String code, String message, Throwable cause) {
    	super(message, cause);
    	this.code = code;
    }
    
    public ServiceException(Throwable cause) {
    	super(cause.getMessage(), cause);
    }
    
    public ServiceException(Throwable cause, String code) {
    	super(cause.getMessage(), cause);
		this.code = code;
    }
    
    public void setCode(String code) {
    	this.code = code;
    }
    public String getCode() {
		return this.code;
	}
    
	public String[] getObjects() {
		return objects;
	}
	public void setObjects(String[] objects) {
		this.objects = objects;
	}
    
    public String getMessage() {
    	String errMessage;
    	if (getObjects() != null) {
       		errMessage = ErrorCodeEnum.getMessage(getCode(), getObjects());
    	} else {
    		String argMessage = ErrorCodeEnum.getMessage(getCode());
    		if(argMessage != null && !argMessage.equals("")) {
    			errMessage = argMessage;
    		} else {
    			errMessage = super.getMessage();
    		}
    	}

    	return errMessage;
    }

	public String getCauseMessage() {
		String retMessage = "[" + this.getCode() + "]";
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		super.printStackTrace(ps);
		
		retMessage += baos.toString().replaceAll("\n", " ");
		
		return retMessage;
	}
}
