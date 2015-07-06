package com.whys.modals;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import com.whys.database.Neo4j;

public abstract class SiteCreation {
	@SuppressWarnings("serial")
	public SiteCreation()
	{
		
		int count = Neo4j.getInstance().countSites();
		
		if(count>9)
		{
			Notification notif = new Notification("Création impossible","Vous ne pouvez pas créer d'autres sites car vous en possédez déjà 10<br/>Veuillez en supprimer ou nous contacter si vous avez réellement besoin de plus de places pour vos sites.",Type.HUMANIZED_MESSAGE,true);
			notif.setDelayMsec(2000);
			notif.setPosition(Position.TOP_CENTER);
			notif.show(Page.getCurrent());
		}
		else
		{
			final Window window;
			VerticalLayout vl = new VerticalLayout();
			
			window = new Window("Ajout d'un site");
			
			final TextField name = new TextField();
			final TextField url = new TextField();	
			
			name.setRequired(true);
			url.setRequired(true);
			name.setInputPrompt("Nom du site");
			url.setInputPrompt("Adresse du site");
			Button confirm = new Button("Ajouter le site");
			confirm.setStyleName(ValoTheme.BUTTON_PRIMARY);
			confirm.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try{
						name.validate();
						url.validate();
						Neo4j.getInstance().createNewSite(name.getValue(), url.getValue());
						UI.getCurrent().removeWindow(window);
						onExit();
					}catch(InvalidValueException e){
						Notification notif = new Notification("Erreur","Merci de remplir les deux champs",Type.ERROR_MESSAGE);
						notif.setDelayMsec(2000);
						notif.show(Page.getCurrent());
					}					
				}
			});
			
			vl.addComponents(name,url,confirm);
			vl.setComponentAlignment(confirm, Alignment.MIDDLE_CENTER);
			vl.setSpacing(true);
			vl.setMargin(true);
			vl.setSizeUndefined();
			
			window.setClosable(true);
			window.setModal(true);
			window.setDraggable(false);
			window.center();
			window.setResizable(false);
			window.setContent(vl);
			UI.getCurrent().addWindow(window);
			name.focus();
		}
	}
	
	public abstract void onExit();
}
