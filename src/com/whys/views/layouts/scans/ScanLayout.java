package com.whys.views.layouts.scans;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;
import com.whys.data.Scan;
import com.whys.data.Site;
import com.whys.database.Neo4j;

public abstract class ScanLayout {

	protected Neo4j neo = Neo4j.getInstance();
	protected Site site;
	protected Scan scan = null;
	protected AbsoluteLayout absl = new AbsoluteLayout();
	protected VerticalLayout layout = new VerticalLayout();
	private Button reset = new Button(FontAwesome.REPEAT);

	public ScanLayout(){
		this.site = (Site) VaadinSession.getCurrent().getAttribute("Site");
		absl.setSizeFull();
		
		reset.addClickListener(new ClickListener() {			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				resetScan();
			}
		});
		reset.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
		reset.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		absl.addComponent(layout);
		layout.setSizeFull();
	}
	
	public AbsoluteLayout getLayout(){
		return this.absl;
	}
	
	protected void addResetButton(){
		absl.addComponent(reset,"top: 1px; left: 1px");
	}
	
	public abstract void resetScan();
}
