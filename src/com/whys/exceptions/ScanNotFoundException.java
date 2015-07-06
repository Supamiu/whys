package com.whys.exceptions;

@SuppressWarnings("serial")
public class ScanNotFoundException extends Exception {
	private final String message;
	public ScanNotFoundException(){
		this.message = "Le scan est introuvable";
	}
	
	public String getMessage(){
		return this.message;
	}
}
