package com.whys.views.adminPanel;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.whys.views.parts.Footer;
import com.whys.views.parts.Header;

public class AdminPanel extends CustomComponent implements View{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "Zone51";
	
	private final Navigator navigator = UI.getCurrent().getNavigator();

	public AdminPanel(){
		setSizeFull();
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setSizeFull();
		Layout head = new Header().getLayout();
		Layout foot = new Footer().getLayout();
		
		GridLayout menu= getMenu();
		
		layout.addComponent(head,"top:0px;right:0px;left:0px");
		layout.addComponent(menu,"top:110px;bottom:80px");
		layout.addComponent(foot,"bottom:0px;");
		setCompositionRoot(layout);
		
		navigator.addView(Users.NAME,Users.class);
		navigator.addView(Logs.NAME, Logs.class);
		navigator.addView(Utils.NAME,Utils.class);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {		
	}
	
	private GridLayout getMenu(){
		GridLayout menu = new GridLayout(2, 2);
		menu.setMargin(true);
		menu.setSpacing(true);
		menu.setSizeFull();
		
		VerticalLayout users = new VerticalLayout();
		users.addStyleName("adminItem");
		Label user = new Label(FontAwesome.USER.getHtml()+" Utilisateurs",ContentMode.HTML);
		user.addStyleName(ValoTheme.LABEL_H1);
		user.addStyleName("adminItem");
		user.setSizeUndefined();
		users.setSizeFull();
		users.addComponent(user);
		users.setComponentAlignment(user, Alignment.MIDDLE_CENTER);
		users.addLayoutClickListener(new LayoutClickListener() {			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void layoutClick(LayoutClickEvent event) {
				navigator.navigateTo(Users.NAME);
			}
		});
		
		menu.addComponent(users,0,0);
		
		VerticalLayout logs = new VerticalLayout();
		logs.addStyleName("adminItem");
		Label log = new Label(FontAwesome.FILE_TEXT_O.getHtml()+" Logs",ContentMode.HTML);
		log.addStyleName(ValoTheme.LABEL_H1);
		log.addStyleName("adminItem");
		log.setSizeUndefined();
		logs.setSizeFull();
		logs.addComponent(log);
		logs.setComponentAlignment(log, Alignment.MIDDLE_CENTER);
		logs.addLayoutClickListener(new LayoutClickListener() {			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void layoutClick(LayoutClickEvent event) {
				navigator.navigateTo(Logs.NAME);
			}
		});
		menu.addComponent(logs,1,0);
		
		VerticalLayout utils = new VerticalLayout();
		utils.addStyleName("adminItem");
		Label util = new Label(FontAwesome.WRENCH.getHtml()+" Outils",ContentMode.HTML);
		util.addStyleName(ValoTheme.LABEL_H1);
		util.addStyleName("adminItem");
		util.setSizeUndefined();
		utils.setSizeFull();
		utils.addComponent(util);
		utils.setComponentAlignment(util, Alignment.MIDDLE_CENTER);
		utils.addLayoutClickListener(new LayoutClickListener() {			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void layoutClick(LayoutClickEvent event) {
				navigator.navigateTo(Utils.NAME);
			}
		});
		menu.addComponent(utils,0,1);
		
		
		return menu;
	}

}
