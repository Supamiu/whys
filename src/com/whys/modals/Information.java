package com.whys.modals;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public abstract class Information {
	Window window;
	VerticalLayout vl = new VerticalLayout();
	@SuppressWarnings("serial")
	public Information(String title, String text){
		
		window = new Window(title);
		final Label label = new Label(text,ContentMode.HTML);
		
		Button ok = new Button("ok");
		ok.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				onConfirm();
				UI.getCurrent().removeWindow(window);
			}
		});
		ok.setImmediate(true);
		ok.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		
		vl.addComponents(label, ok);
		vl.setComponentAlignment(ok, Alignment.MIDDLE_CENTER);
		vl.setSpacing(true);
		vl.setMargin(true);
		window.setWidth("60%");
		window.setClosable(true);
		window.setModal(true);
		window.setDraggable(false);
		window.center();
		window.setResizable(false);
		window.setContent(vl);
		UI.getCurrent().addWindow(window);
		ok.focus();
	}
	
	public abstract void onConfirm();
}
