package com.whys.modals;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Dialog {
	Window window;
	VerticalLayout vl = new VerticalLayout();
	public Dialog(String title, String text){
		
		window = new Window(title);
		final Label label = new Label(text,ContentMode.HTML);
		
		vl.addComponents(label);
		vl.setSpacing(true);
		vl.setMargin(true);		
		window.setClosable(true);
		window.setModal(true);
		window.setDraggable(false);
		window.center();
		window.setResizable(false);
		window.setContent(vl);
		UI.getCurrent().addWindow(window);
	}
}
