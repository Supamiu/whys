package com.whys.modals;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class LongText {
	Window window;
	VerticalLayout vl = new VerticalLayout();
	public LongText(String title, String text){
		
		Panel panel = new Panel();
		window = new Window(title);
		final Label label = new Label(text,ContentMode.HTML);
		panel.setStyleName(ValoTheme.PANEL_BORDERLESS);
		panel.setContent(label);
		
		vl.addComponents(panel);
		vl.setSpacing(true);
		vl.setMargin(true);		
		window.setWidth("50%");		
		window.setClosable(true);
		window.setModal(true);
		window.setDraggable(false);
		window.center();
		window.setResizable(false);
		window.setContent(vl);
		UI.getCurrent().addWindow(window);
	}
}
