package com.whys.views;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.whys.database.Neo4j;
import com.whys.exceptions.LoginException;
import com.whys.main.WhysUI;
import com.whys.securityUtils.Hash;
import com.whys.securityUtils.LogEngine;
import com.whys.utils.OnEnterKeyHandler;

@SuppressWarnings("serial")
public class Login extends CustomComponent implements View {

	public static final String NAME = "login";
	
	private final TextField user = new TextField();
	private final PasswordField passwd = new PasswordField();
	private final Button confirm = new Button("Login");
	private final Button inscription = new Button("Inscription");
	private Navigator navigator = UI.getCurrent().getNavigator();
	private LogEngine logs = new LogEngine("connections");
	
	
	public Login(){
		Label whys = new Label("<div align='center'><h1>WHYS</h1>"+WhysUI.VERSION+"</div>", ContentMode.HTML);
		whys.setHeight("200px");
		
		setSizeFull();
		VerticalLayout vl = new VerticalLayout();
		Panel panel = new Panel("Identification");
		panel.addStyleName("login");
		VerticalLayout form = new VerticalLayout();
		form.setImmediate(true);
		vl.setImmediate(true);
		panel.setContent(form);
		panel.setImmediate(true);
		panel.setSizeUndefined();
		form.setMargin(true);
		form.setSpacing(true);
		Panel news = staffMessagePanel();
		
		CustomComponent droite = new CustomComponent();
		
		HorizontalLayout mid = new HorizontalLayout();
		mid.setSizeUndefined();
		mid.setWidth("100%");
		mid.setMargin(true);
		mid.setSpacing(true);
		mid.addComponents(droite,panel,news);
		panel.addStyleName("center");
		mid.setComponentAlignment(news,Alignment.MIDDLE_CENTER);
		mid.setComponentAlignment(panel,Alignment.MIDDLE_CENTER);
		
		vl.addComponent(whys);
		vl.addComponent(mid);
		vl.setComponentAlignment(mid, Alignment.TOP_CENTER);
		vl.setWidth("100%");
		vl.setMargin(true);
		setCompositionRoot(vl);
		
		user.setInputPrompt("Pseudo");
		passwd.setInputPrompt("password");
		
		OnEnterKeyHandler onEnter = new OnEnterKeyHandler() {			
			@Override
			public void onEnterKeyPressed() {
				confirm();				
			}
		};
		
		onEnter.installOn(passwd);
		onEnter.installOn(user);
		
		confirm.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				confirm();
			}
		});
		
		inscription.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				navigator.navigateTo(SignIn.NAME);
			}
		});
		inscription.setStyleName(ValoTheme.BUTTON_QUIET);
		confirm.setStyleName(ValoTheme.BUTTON_PRIMARY);
		form.addComponents(user,passwd,confirm,inscription);
		form.setComponentAlignment(confirm, Alignment.MIDDLE_CENTER);
		form.setComponentAlignment(inscription, Alignment.MIDDLE_CENTER);
	}
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		user.focus();
	}
	
	private void confirm(){
		try{
			Neo4j.getInstance().loginUser(user.getValue(), passwd.getValue());
			new Hash();
			logs.log(user.getValue()+" s'est connecté",true,false);
			VaadinSession.getCurrent().setAttribute("pseudo", user.getValue());
			navigator.addView(DashBoard.NAME, DashBoard.class);
			navigator.addView(SiteView.NAME, SiteView.class);
			navigator.navigateTo(DashBoard.NAME);
		}
		catch(LoginException e)
		{
			Notification notif = new Notification("Erreur de login", e.getMessage(), Type.ERROR_MESSAGE);
			notif.setDelayMsec(500);
			notif.show(Page.getCurrent());
		}	
	}
	
	public Panel staffMessagePanel(){
		Panel panel = new Panel("Les news");
		Label content = new Label(Neo4j.getInstance().getNews(),ContentMode.HTML);
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		panel.setHeight("400px");
		panel.setWidth("300px");
		vl.addComponent(content);
		panel.setContent(vl);
		return panel;
	}

}
