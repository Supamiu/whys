package com.whys.views.parts;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;
import com.whys.data.User;
import com.whys.main.WhysUI;
import com.whys.modals.Confirmation;
import com.whys.modals.ErrorReport;
import com.whys.utils.BugReport;
import com.whys.views.adminPanel.AdminPanel;

public class Header {
	
	private HorizontalLayout hl = new HorizontalLayout();

	public Header(){
		HorizontalLayout socials = new HorizontalLayout();
		HorizontalLayout titre = new HorizontalLayout();
		
		Label fb = new Label(FontAwesome.FACEBOOK.getHtml(), ContentMode.HTML);
		fb.addStyleName("sociallabel");
		Label twitter = new Label(FontAwesome.TWITTER.getHtml(), ContentMode.HTML);
		twitter.addStyleName("sociallabel");
		socials.addComponents(fb,twitter);
		socials.setSizeUndefined();
		socials.setMargin(true);
		socials.setSpacing(true);
		
		Label whys = new Label("WHYS");
		Label version = new Label("<a style='text-decoration:none' href='http://whys.fr/forum/index.php/topic,4.msg5.html#new'>"+WhysUI.VERSION+"</a>",ContentMode.HTML);
		version.setDescription("Cliquez ici pour accéder à la rubrique patch-notes du forum");
		titre.setSpacing(true);
		whys.setStyleName(ValoTheme.LABEL_H1);
		whys.addStyleName(ValoTheme.LABEL_BOLD);
		version.setStyleName(ValoTheme.LABEL_LIGHT);
		whys.setSizeUndefined();
		titre.addComponent(whys);
		titre.addComponent(version);
		hl.setHeight("95px");
		hl.setWidth("100%");
		
		titre.setHeight("95px");
		titre.setComponentAlignment(whys, Alignment.MIDDLE_RIGHT);
		titre.setComponentAlignment(version, Alignment.MIDDLE_LEFT);
		MenuBar mb = buildMenuBar();
		
		hl.addComponents(socials,titre,mb);
		hl.setComponentAlignment(mb, Alignment.BOTTOM_RIGHT);
		hl.setComponentAlignment(titre, Alignment.MIDDLE_RIGHT);
		hl.setExpandRatio(titre, 1.1f);
		hl.setExpandRatio(mb, 1f);
	}
	
	
	@SuppressWarnings({"serial", "unused"})
	private MenuBar buildMenuBar(){
		MenuBar mb = new MenuBar();
		
		if(new User(true).isAdmin()){
			UI.getCurrent().getNavigator().addView(AdminPanel.NAME,AdminPanel.class);
			
			MenuBar.Command admin = new MenuBar.Command() {				
				@Override
				public void menuSelected(MenuItem selectedItem) {
					UI.getCurrent().getNavigator().navigateTo(AdminPanel.NAME);
				}
			};
			
			mb.addItem("Zone 51",FontAwesome.TERMINAL,admin);
		}
		
		MenuBar.Command deco = new MenuBar.Command() {
			@Override
			public void menuSelected(MenuItem selectedItem) {
				new Confirmation("Confirmation", "Êtes-vous sûr de vouloir vous déconnecter?"){
					@Override
					public void onConfirm() {
						new User().disconnect();
					}
				};
			}
		};
		
		MenuBar.Command dev = new MenuBar.Command() {			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				Notification notif = new Notification("Non Disponible", "Cette fonctionnalité est en cours de développement", Type.WARNING_MESSAGE);
				notif.setDelayMsec(3000);
				notif.show(Page.getCurrent());
			}
		};
		
		MenuBar.Command signaler = new MenuBar.Command() {			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				new ErrorReport(new BugReport());
			}
		};
		
		MenuItem report = mb.addItem("Signaler un bug",FontAwesome.BUG,signaler);
		
		mb.setStyleName(ValoTheme.MENUBAR_BORDERLESS);
		
		MenuItem options= mb.addItem("Options",FontAwesome.COGS,dev);
		MenuItem profil = mb.addItem(new User(true).getPseudo(),FontAwesome.MALE,dev);
		MenuItem deconnexion = mb.addItem("Déconnexion",FontAwesome.LOCK,deco);
		
		mb.setSizeUndefined();
		return mb;
	}
	
	public HorizontalLayout getLayout(){
		return this.hl;
	}
}
