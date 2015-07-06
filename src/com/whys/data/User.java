package com.whys.data;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.whys.database.Neo4j;
import com.whys.exceptions.LoginException;
import com.whys.views.DashBoard;
import com.whys.views.SiteView;


public class User {
	
	private String name;
	private String lastName;
	private String pseudo;
	private String email;
	private boolean admin = false;
	private String cell;
	private boolean isActive;
	private int id;
	@SuppressWarnings("unused")
	private String sessionID;
	
	public User(){
		
	}
	
	public User(String name, String lastname, String pseudo, String email, String cell,
			int id, boolean isActive, boolean isAdmin){
		this.name = name;
		this.lastName = lastname;
		this.pseudo = pseudo;
		this.email = email;
		this.cell = cell;
		this.id = id;
		this.isActive = isActive;
		this.admin = isAdmin;	
	}
	
	public User(boolean isLoggedIn){
		name = VaadinSession.getCurrent().getAttribute("name").toString();
		setLastName(VaadinSession.getCurrent().getAttribute("lastName").toString());
		pseudo = VaadinSession.getCurrent().getAttribute("pseudo").toString();
		email = VaadinSession.getCurrent().getAttribute("email").toString();
		id = Integer.valueOf(VaadinSession.getCurrent().getAttribute("id").toString());
		admin = Neo4j.getInstance().isUserAdmin(id);
	}
	
	public void checkLogin(){
		if(VaadinSession.getCurrent().getAttribute("SessionID")!=null)
		{
			try {
				Neo4j.getInstance().loginUserWithSID(VaadinSession.getCurrent().getAttribute("SessionID").toString());
				if(VaadinSession.getCurrent().getAttribute("Site")!=null){
					try{
						UI.getCurrent().getNavigator().navigateTo(SiteView.NAME);
					}catch(NullPointerException e){
						this.checkLogin();
					}
				}else
				{
					UI.getCurrent().getNavigator().navigateTo(DashBoard.NAME);					
				}	
			} catch (LoginException e) {
				disconnect();
			}
		}
	}
	
	public void disconnect(){
		VaadinService.getCurrentRequest().getWrappedSession().invalidate(); 
		Page.getCurrent().reload();
	}

	public String getPseudo() {
		return this.pseudo;
	}

	public String getName() {
		return this.name;
	}
	
	public String getEmail(){
		return this.email;
	}
	
	/**
	 * enregistre la vue en cours pour pouvoir y retourner en cas de refresh
	 * 
	 * @param newName le nom de la vue à enregistrer
	 */
	public void setCurrentView(String newName){
		VaadinSession.getCurrent().setAttribute("currentView", newName);
	}

	public boolean isAdmin() {
		return admin;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getId() {
		return id;
	}

	public boolean isActive() {
		return isActive;
	}

	public String getCell() {
		return cell;
	}
}

