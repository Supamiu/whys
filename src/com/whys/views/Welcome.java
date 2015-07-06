package com.whys.views;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.whys.database.Neo4j;

@SuppressWarnings("serial")
public class Welcome  extends CustomComponent implements View{
	
	public static final String NAME = "welcome";
	Navigator navigator = UI.getCurrent().getNavigator();
	public Welcome(){
		checkStatus();
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		checkStatus();
	}
	
	private void checkStatus(){
		Neo4j neo4j = Neo4j.getInstance();
		String error = neo4j.getServerStatus();
		VerticalLayout vl = new VerticalLayout();
		setCompositionRoot(vl);
		setSizeFull();
		Label errorLabel = new Label();
		if(error.equals("ok"))
		{
			navigator.addView(Login.NAME, Login.class);
			navigator.addView(SignIn.NAME, SignIn.class);
			navigator.navigateTo(Login.NAME);
		}
		else
		{
			errorLabel.setValue("<h1>"+error+"</h1>");
		}
		errorLabel.setContentMode(ContentMode.HTML);
		errorLabel.setWidth(null);
		vl.setSizeFull();
		vl.addComponent(errorLabel);
		vl.setComponentAlignment(errorLabel, Alignment.MIDDLE_CENTER);
	}
}
