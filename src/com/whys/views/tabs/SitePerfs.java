package com.whys.views.tabs;

import com.vaadin.ui.GridLayout;
import com.whys.views.layouts.scans.PingLayout;
import com.whys.views.layouts.scans.WgetLayout;

public class SitePerfs {

	public SitePerfs(){
	}
	
	public GridLayout getLayout(){
		GridLayout layout = new GridLayout(2,1);
		layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);
		
		layout.addComponent(new PingLayout().getLayout());
		layout.addComponent(new WgetLayout().getLayout());
		return layout;
	}
	
}
