package com.whys.modals;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

public abstract class Confirmation {
	Window window;
	VerticalLayout vl = new VerticalLayout();
	@SuppressWarnings("serial")
	public Confirmation(String title, String text){
		
		window = new Window(title);
		final Label label = new Label(text,ContentMode.HTML);
		
		HorizontalLayout buttons = new HorizontalLayout();
		
		Button ok = new Button("Oui");
		ok.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				onConfirm();
				UI.getCurrent().removeWindow(window);
			}
		});
		ok.setImmediate(true);
		ok.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		
		Button nope = new Button("Non");
		nope.addClickListener(new ClickListener() {			
			@Override
			public void buttonClick(ClickEvent event) {
				UI.getCurrent().removeWindow(window);
			}
		});
		nope.setStyleName(ValoTheme.BUTTON_DANGER);
		buttons.setSpacing(true);
		buttons.addComponents(ok,nope);
		
		
		vl.addComponents(label, buttons);
		vl.setComponentAlignment(buttons, Alignment.MIDDLE_CENTER);
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
	
	public abstract void onConfirm();
}
