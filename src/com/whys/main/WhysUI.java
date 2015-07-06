package com.whys.main;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.whys.utils.ErrorHandler;
import com.whys.views.Welcome;

@SuppressWarnings("serial")
@Theme("whys")
@Push
@PreserveOnRefresh
public class WhysUI extends UI {
	@Override
	protected void refresh(VaadinRequest request) {
	  if (getPushConnection() != null && getPushConnection().isConnected()) {
	    getPushConnection().disconnect();
	  }
	  super.refresh(request);
	}
	
	public static final String VERSION = "alpha v0.4.5";
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = true, ui = WhysUI.class, widgetset = "com.whys.main.widgetset.WhysWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		//Ici on ajoute les méthodes qu'on veut tester à l'instanciation de l'appli.
		String ipAddr = request.getHeader("X-FORWARDED-FOR"); // si le client est derrière un proxy, on récupère le XFF
		if(ipAddr==null)
		{
			ipAddr = request.getRemoteAddr(); // sinon, on récupère l'addresse de l'expéditeur de la requête
		}
		
		VaadinSession.getCurrent().setAttribute("ipaddr",ipAddr);
		//On ajoute le titre de la page
		getPage().setTitle("Whys - White Hack Yourself");
		
		//On met en place le gestionnaire d'erreurs
		new ErrorHandler().init();
		
		Navigator navigator = new Navigator(this,this);
		navigator.addView(Welcome.NAME, new Welcome());
		navigator.navigateTo(Welcome.NAME);		
	}
}