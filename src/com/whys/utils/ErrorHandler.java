package com.whys.utils;

import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.ui.UI;
import com.whys.data.User;
import com.whys.modals.ErrorReport;

public class ErrorHandler {

	private ErrorHandler me = this;
	private String cause;
	private String userText;
	private String title;
	
	public ErrorHandler(){
	}
	
	public void init(){
		UI.getCurrent().setErrorHandler(new DefaultErrorHandler() {
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			
			@Override
		    public void error(com.vaadin.server.ErrorEvent event) {
		        cause = "<b>Stack de l'erreur :</b><br/><br/>";
		        
		        for (Throwable t = event.getThrowable(); t != null;
		                t = t.getCause())
		               if (t.getCause() == null)
		                   title = t.getClass().getName();
		        
		        StackTraceElement[] stack = event.getThrowable().getStackTrace();
		        for(StackTraceElement s : stack){
		        	cause += s.toString() + "<br/>";
		        }
		        
		        event.getThrowable().printStackTrace();
		        new ErrorReport(me);
		    }
		});
	}
	
	public void setUserText(String text){
		this.userText = text;
	}
	
	public void report(){
		MailEngine.sendMailFromUser("alpha@whys.fr", "rapport de bug - "+title, userText + "<br/><br/><br/>"+cause, new User(true).getEmail());
	}	
	
}
