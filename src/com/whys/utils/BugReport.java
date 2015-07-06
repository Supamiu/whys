package com.whys.utils;

import com.whys.data.User;

public class BugReport {

	private String userText;
	
	public void init(){
		
	}
	
	public void setUserText(String text){
		this.userText = text;
	}
	
	public void report(){
		MailEngine.sendMailFromUser("alpha@whys.fr", "Rapport de bug - Utilisateur", userText, new User(true).getEmail());
	}
}
