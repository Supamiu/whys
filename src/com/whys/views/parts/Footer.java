package com.whys.views.parts;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;
import com.whys.modals.LongText;
import com.whys.staticDatas.EthicalRules;

public class Footer {
	
	private HorizontalLayout hl = new HorizontalLayout();
	
	@SuppressWarnings("serial")
	public Footer(){
		hl.setHeight("100px");
		hl.setWidth("100%");
		hl.setMargin(true);
		hl.setSpacing(true);
		hl.addStyleName("footerbackground");
		
		VerticalLayout contact = new VerticalLayout();
		Label contact_title = new Label("Contact");
		contact_title.setStyleName(ValoTheme.LABEL_BOLD);
		Label mail = new Label("<a style='text-decoration:none' href='mailto:contact@whys.fr'> Email</a>",ContentMode.HTML);
		mail.setStyleName(ValoTheme.LABEL_LIGHT);
		
		VerticalLayout informations = new VerticalLayout();
		Label informations_title = new Label("Informations");
		informations_title.setStyleName(ValoTheme.LABEL_BOLD);
		
		Button ethique = new Button("Nos règles éthiques");
		ethique.addClickListener(new ClickListener() {		
			@Override
			public void buttonClick(ClickEvent event) {
				new LongText("Nos règles éthique",EthicalRules.DATA);
			}
		});
		ethique.setStyleName(ValoTheme.BUTTON_BORDERLESS);
		ethique.addStyleName(ValoTheme.BUTTON_SMALL);
		ethique.addStyleName(ValoTheme.BUTTON_LINK);
		
		informations.addComponents(informations_title,ethique);		
		
		contact.addComponents(contact_title,mail);		
		
		hl.addComponents(contact,informations);	
	}
	
	public HorizontalLayout getLayout(){
		return this.hl;
	}
}
