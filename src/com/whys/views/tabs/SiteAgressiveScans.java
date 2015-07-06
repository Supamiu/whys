package com.whys.views.tabs;

import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.whys.data.Site;
import com.whys.views.layouts.scans.JoomscanLayout;
import com.whys.views.layouts.scans.NmapLayout;
import com.whys.views.layouts.scans.UniscanDirectoryLayout;

public class SiteAgressiveScans {	
	
	
	public SiteAgressiveScans(){
	}
	
	public Layout getLayout(){
		
		Site site = (Site) VaadinSession.getCurrent().getAttribute("Site");
		
		if(!Boolean.valueOf(site.isConfirmed())){
			VerticalLayout base = new VerticalLayout();
			base.setSizeFull();
			VerticalLayout nope = new VerticalLayout();
			Label explication = new Label("Vous ne pouvez pas accéder à cette partie tant que vous n'avez pas confirmé que ce site vous appartient."
					+ "Si vous avez le moindre problème pour confirmer votre site,<a href='mailto:contact@whys.fr'>contactez nous</a>",ContentMode.HTML);
			nope.setHeightUndefined();
			nope.setWidth("50%");
			nope.addComponent(explication);
			nope.setComponentAlignment(explication, Alignment.MIDDLE_CENTER);
			base.addComponent(nope);
			base.setComponentAlignment(nope, Alignment.MIDDLE_CENTER);
			return base;
		}
	
		GridLayout layout = new GridLayout(3,1);
		layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);
		
		layout.addComponent(new NmapLayout().getLayout());
		layout.addComponent(new JoomscanLayout().getLayout());
		layout.addComponent(new UniscanDirectoryLayout().getLayout());
		
		return layout;
	}
}
