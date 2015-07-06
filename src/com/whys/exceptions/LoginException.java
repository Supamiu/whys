package com.whys.exceptions;

@SuppressWarnings("serial")
public class LoginException extends Exception {
	private final String message;
	public LoginException(String message){
		this.message = message;
	}
	
	public String getMessage(){
		return this.message;
	}
}
