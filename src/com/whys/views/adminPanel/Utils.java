package com.whys.views.adminPanel;

import com.vaadin.ui.Notification.Type;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.whys.database.Neo4j;
import com.whys.utils.MailEngine;
import com.whys.views.parts.Footer;
import com.whys.views.parts.Header;

public class Utils extends CustomComponent implements View{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String NAME = "Utils";
	private static final Neo4j neo = Neo4j.getInstance();
	
	public Utils(){
		setSizeFull();
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setSizeFull();
		Layout head = new Header().getLayout();
		Layout foot = new Footer().getLayout();
		
		VerticalLayout main = getMain();
		
		layout.addComponent(head,"top:0px;right:0px;left:0px");
		layout.addComponent(main,"top:110px;bottom:80px");
		layout.addComponent(foot,"bottom:0px;");
		setCompositionRoot(layout);
	}
	
	private VerticalLayout getMain(){
		VerticalLayout main = new VerticalLayout();
		main.setSizeFull();
		
		GridLayout layout = new GridLayout(2,2);
		layout.setSizeFull();
		layout.setSpacing(true);
		
		//Modification des news affichées
		Panel setNews = new Panel("Changer le message de news");
		setNews.setSizeFull();
		final TextArea tb = new TextArea();
		tb.setValue(neo.getNews());
		Button changeNews = new Button("Appliquer");
		changeNews.addClickListener(new ClickListener() {			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				neo.setNews(tb.getValue());
				Notification validation = new Notification("Confirmation","Message de news changé.",Type.TRAY_NOTIFICATION);
				validation.setDelayMsec(2000);
				validation.show(Page.getCurrent());
			}
		});
		VerticalLayout panel = new VerticalLayout();
		tb.setSizeFull();
		tb.setInputPrompt("Nouveau texte (html prix en charge)");
		panel.addComponents(tb,changeNews);
		panel.setMargin(true);
		panel.setSpacing(true);
		panel.setSizeFull();
		panel.setComponentAlignment(changeNews, Alignment.MIDDLE_CENTER);
		setNews.setContent(panel);
		layout.addComponent(setNews);
		//Fin des news
		
		//Envoi d'un mail de newsletter
		Panel sendNewsLetter = new Panel("Envoyer une newsletter");
		sendNewsLetter.setSizeFull();
		final TextField title = new TextField();
		title.setSizeFull();
		final TextArea content = new TextArea();
		content.setSizeFull();
		VerticalLayout newsLetterPanel = new VerticalLayout();
		title.setInputPrompt("Titre du mail");
		content.setInputPrompt("Contenu du mail (HTML supporté)");
		Button send = new Button(FontAwesome.PAPER_PLANE);
		send.setCaption("Envoyer");
		send.addClickListener(new ClickListener() {			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				MailEngine.SendNewsLetter(title.getValue(), content.getValue());
				Notification validation = new Notification("Confirmation","Mails envoyés",Type.TRAY_NOTIFICATION);
				validation.setDelayMsec(2000);
				validation.show(Page.getCurrent());
			}
		});
		
		newsLetterPanel.addComponents(title, content, send);
		newsLetterPanel.setSizeFull();
		newsLetterPanel.setMargin(true);
		newsLetterPanel.setSpacing(true);
		sendNewsLetter.setContent(newsLetterPanel);
		layout.addComponent(sendNewsLetter);
		
		main.addComponent(layout);
		return main;
	}
		
	
	@Override
	public void enter(ViewChangeEvent event) {
			
	}

}
