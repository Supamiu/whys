package com.whys.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.whys.main.WhysUI;

@SuppressWarnings("serial")
public class Presentation extends CustomComponent implements View{

	public static final String NAME = "prez";

	@Override
	public void enter(ViewChangeEvent event) {
	}
	
	public Presentation(){
		AbsoluteLayout layout = new AbsoluteLayout();
		
		layout.addComponent(buildHeader());
		
		setCompositionRoot(layout);
	}
	
	private HorizontalLayout buildHeader(){
		HorizontalLayout titre = new HorizontalLayout();
		Label whys = new Label("WHYS");
		Label version = new Label(WhysUI.VERSION);
		titre.setSpacing(true);
		whys.setStyleName(ValoTheme.LABEL_H1);
		whys.addStyleName(ValoTheme.LABEL_BOLD);
		version.setStyleName(ValoTheme.LABEL_LIGHT);
		whys.setSizeUndefined();
		titre.addComponent(whys);
		titre.addComponent(version);
		
		titre.setHeight("95px");
		titre.setComponentAlignment(whys, Alignment.MIDDLE_RIGHT);
		titre.setComponentAlignment(version, Alignment.MIDDLE_LEFT);
		return titre;
	}
}
