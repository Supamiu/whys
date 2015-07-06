package com.whys.securityUtils;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;

public class SessionManager {
	
	public SessionManager(){
		try{
			System.out.println(VaadinSession.getCurrent().getAttribute("SessionID").toString());
		}catch(NullPointerException npe)
		{
			VaadinService.getCurrentRequest().getWrappedSession().invalidate();
			Page.getCurrent().reload();
		}
	}
}
