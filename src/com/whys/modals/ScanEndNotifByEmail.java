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
import com.whys.data.Scan;

@SuppressWarnings("serial")
public class ScanEndNotifByEmail extends Window{
	
	final Window window = this;
	public ScanEndNotifByEmail(final Scan scan){
		VerticalLayout vl = new VerticalLayout();
		
		this.setCaption("Notification");
		final Label label = new Label("Ce scan est un scan de longue durée,<br/>"
				+ "souhaitez-vous reçevoir un email pour vous informer une fois celui-ci terminé?",ContentMode.HTML);
		
		HorizontalLayout buttons = new HorizontalLayout();
		
		Button ok = new Button("Oui");
		ok.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				scan.setEmailNotif(true);
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
		
		this.setClosable(true);
		this.setModal(true);
		this.setDraggable(false);
		this.center();
		this.setResizable(false);
		this.setContent(vl);
		UI.getCurrent().addWindow(window);
	}
}
