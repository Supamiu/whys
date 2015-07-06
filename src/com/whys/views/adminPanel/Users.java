package com.whys.views.adminPanel;

import java.util.ArrayList;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.whys.data.User;
import com.whys.database.Neo4j;
import com.whys.modals.Confirmation;
import com.whys.utils.MailEngine;
import com.whys.views.parts.Footer;
import com.whys.views.parts.Header;

public class Users extends CustomComponent implements View{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String NAME = "Users";
	private static final Neo4j neo = Neo4j.getInstance();
	
	public Users(){
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
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}
	
	@SuppressWarnings("unchecked")
	private VerticalLayout getMain(){
		VerticalLayout main = new VerticalLayout();
		
		Table users = new Table();
		users.addContainerProperty("ID", Integer.class, null);
		users.addContainerProperty("nom", String.class, null);
		users.addContainerProperty("prénom", String.class, null);
		users.addContainerProperty("pseudo", String.class, null);
		users.addContainerProperty("email", String.class, null);
		users.addContainerProperty("portable", String.class, null);
		users.addContainerProperty("actif", CheckBox.class, null);
		users.addContainerProperty("admin", CheckBox.class, null);
		users.addContainerProperty("delete", Button.class, null);
		
		ArrayList<User> userList = neo.getUsers();
		
		for(final User u : userList){
			Object newItemId = users.addItem();
			Item row = users.getItem(newItemId);
			CheckBox actif = new CheckBox();
			actif.setValue(u.isActive());
			actif.addValueChangeListener(new ValueChangeListener() {				
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					neo.setUserActive(u, Boolean.valueOf(event.getProperty().getValue().toString()));
					if(Boolean.valueOf(event.getProperty().getValue().toString())){
						MailEngine.sendNoreplyMail(u.getEmail(), "WHYS - Votre compte a été activé", "Bonjour,<br/><br/>"
								+ "Vous reçevez ce mail car votre compte WHYS vient d'être activé par un administrateur, vous pouvez désormais vous identifier sur http://app.whys.fr");
					}
					new Notification("Utilisateur "+u.getPseudo()+" modifié",Type.TRAY_NOTIFICATION).show(Page.getCurrent());
				}
			});
			
			Button delete = new Button(FontAwesome.TRASH_O);
			delete.addClickListener(new ClickListener() {				
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					new Confirmation("Confirmation","Supprimer l'utilisateur "+u.getPseudo()+" ?") {
						
						@Override
						public void onConfirm() {
							neo.deleteUser(u);							
						}
					};
				}
			});
			
			
			CheckBox admin = new CheckBox();
			admin.setValue(u.isAdmin());
			admin.setReadOnly(true);
			row.getItemProperty("ID").setValue(u.getId());
			row.getItemProperty("nom").setValue(u.getLastName());
			row.getItemProperty("prénom").setValue(u.getName());
			row.getItemProperty("pseudo").setValue(u.getPseudo());
			row.getItemProperty("email").setValue(u.getEmail());
			row.getItemProperty("portable").setValue(u.getCell());
			row.getItemProperty("actif").setValue(actif);
			row.getItemProperty("admin").setValue(admin);
			row.getItemProperty("delete").setValue(delete);
		}
		
		users.setSizeFull();
		main.addComponent(users);
		main.setSizeFull();
		return main;
	}

}
