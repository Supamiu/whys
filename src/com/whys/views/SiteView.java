package com.whys.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.themes.ValoTheme;
import com.whys.data.Site;
import com.whys.views.parts.Footer;
import com.whys.views.parts.Header;
import com.whys.views.tabs.SiteAgressiveScans;
import com.whys.views.tabs.SiteCompleteScan;
import com.whys.views.tabs.SiteDetails;
import com.whys.views.tabs.SitePerfs;

@SuppressWarnings("serial")
public class SiteView extends CustomComponent implements View{
	
	public static final String NAME = "site";
	
	private AbsoluteLayout absLayout = new AbsoluteLayout();
	private TabSheet main;
	private Site site;
	
	public SiteView(){
		this.site = (Site) VaadinSession.getCurrent().getAttribute("Site");
		setSizeFull();
		setCompositionRoot(buildMainLayout());	
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
	}
	
	private AbsoluteLayout buildMainLayout(){
		main = buildMainContent();
		absLayout.addComponent(new Header().getLayout(),"top:0px;right:0px;left:0px");
		absLayout.addComponent(main,"top:110px;bottom:80px");
		absLayout.addComponent(new Footer().getLayout(),"bottom:0px;");
		absLayout.setImmediate(true);
		absLayout.setSizeFull();
		return absLayout;		
	}
	
	
	private TabSheet buildMainContent(){
		TabSheet panel = new TabSheet();
		panel.setSizeFull();
		panel.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
		panel.addStyleName(ValoTheme.TABSHEET_FRAMED);
		panel.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
		TabSheet ts = new TabSheet();
		ts.setStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
		ts.setSizeFull();
		
		//Onglet Détails sur le site
		Layout details = new SiteDetails(site).getLayout();
		Layout perfs = new SitePerfs().getLayout();
		Layout agressive = new SiteAgressiveScans().getLayout();
		Layout complete = new SiteCompleteScan().getLayout();
		
		ts.addTab(details, "Détails");
		ts.addTab(perfs, "Performances");
		ts.addTab(agressive,"Scans de sécurité");
		ts.addTab(complete,"Scans de failles");
		
		panel.addTab(ts,this.site.name);
		return panel;
	}
}
