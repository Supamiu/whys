package com.whys.views.layouts;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import com.whys.database.Neo4j;
import com.whys.data.Site;
import com.whys.modals.Confirmation;
import com.whys.views.SiteView;

public abstract class SiteLayout {


	private VerticalLayout vl = new VerticalLayout();
	
	@SuppressWarnings("serial")
	public SiteLayout(final Site site){
		
		
		Panel panel = new Panel(site.name);
		
		panel.setSizeFull();
		VerticalLayout siteInfos = new VerticalLayout();
		siteInfos.setImmediate(true);
		
		Label url = new Label("<div align='center'>http://"+site.url+"</div>",ContentMode.HTML);
		Label nbFailles = new Label("<div align='center'>0 Failles - 0 Fixées</div>",ContentMode.HTML);
		Label confirmed = new Label();
		Button delete = new Button();
		delete.setIcon(FontAwesome.TRASH_O);
		final int idSite = site.id;
		delete.setDescription("Supprimer le site");
		delete.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				new Confirmation("Confirmation","Êtes-vous sûr de vouloir supprimer le site "+site.name+" ?"){
					@Override
					public void onConfirm() {
						Neo4j.getInstance().deleteSite(idSite);
						onDeletion();
					}};
			}
		});
		delete.setImmediate(true);
		delete.setStyleName(ValoTheme.BUTTON_DANGER);
		delete.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		delete.setSizeUndefined();
		
		if(site.confirmed == true)
		{
			confirmed.setValue("Confirmé");
			confirmed.setStyleName(ValoTheme.LABEL_SUCCESS);
		}else{
			confirmed.setValue("Non confirmé");
			confirmed.setStyleName(ValoTheme.LABEL_FAILURE);
		}
		
		HorizontalLayout hl = new HorizontalLayout();
		
		hl.addComponents(confirmed,delete);
		hl.setExpandRatio(confirmed, 1f);
		hl.setComponentAlignment(delete, Alignment.MIDDLE_CENTER);
		hl.setSizeFull();
		hl.setHeightUndefined();
		hl.setSpacing(true);
		
		siteInfos.setMargin(true);
		siteInfos.setSizeFull();
		
		siteInfos.addComponent(url);
		siteInfos.addComponent(nbFailles);
		siteInfos.setMargin(new MarginInfo(true, true, true, true));
		
		siteInfos.addComponent(hl);
		
		panel.setStyleName(ValoTheme.PANEL_WELL);
		panel.addStyleName("sitepanel");
		panel.setContent(siteInfos);
		vl.addComponent(panel);
		vl.addLayoutClickListener(new LayoutClickListener() {
			@Override
			public void layoutClick(LayoutClickEvent event) {
				VaadinSession.getCurrent().setAttribute("Site", site);
				UI.getCurrent().getNavigator().navigateTo(SiteView.NAME);
			}
		});
		
		vl.setSizeFull();
		vl.setImmediate(true);
	}
	
	public abstract void onDeletion();
	
	public VerticalLayout getLayout(){
		return vl;
	}
}
