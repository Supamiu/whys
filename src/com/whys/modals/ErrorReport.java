package com.whys.modals;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.whys.utils.BugReport;
import com.whys.utils.ErrorHandler;

@SuppressWarnings("serial")
public class ErrorReport extends Window{

	private Window window = this;
	private final Label text_eh = new Label("Une erreur a été causée par votre dernière action,<br/>"
			+ "L'application étant en cours d'alpha test, cette erreur doit être signalée à l'équipe de développement afin de pouvoir être corrigée.<br/>"
			+ "Merci d'expliquer dans le champ texte ci-dessous les circonstances et l'action qui ont provoqué cette erreur.",ContentMode.HTML);
	
	private final Label text_br = new Label("Veuillez écrire ci-dessous l'objet de votre signalement, "
			+ "un message sera transmis à l'équipe pour régler le problème dans les plus brefs délais");
	
	public ErrorReport(final ErrorHandler eh){
		VerticalLayout vl = new VerticalLayout();
		
		this.setCaption("Erreur");
		this.setHeightUndefined();
		this.setWidth("600px");
		
		final TextArea explication = new TextArea();
		explication.setSizeFull();
		
		Button submit = new Button("Envoyer");
		
		submit.addClickListener(new ClickListener() {			
			@Override
			public void buttonClick(ClickEvent event) {
				eh.setUserText(explication.getValue());
				eh.report();
				UI.getCurrent().removeWindow(window);
			}
		});
		
		vl.setMargin(true);
		vl.setSpacing(true);
		vl.addComponent(text_eh);
		vl.addComponent(explication);
		vl.addComponent(submit);
		vl.setComponentAlignment(submit,Alignment.MIDDLE_CENTER);
		
		this.setClosable(true);
		this.setModal(true);
		this.setDraggable(false);
		this.center();
		this.setResizable(false);
		this.setContent(vl);
		UI.getCurrent().addWindow(window);
	}

	public ErrorReport(final BugReport br) {
		VerticalLayout vl = new VerticalLayout();
		
		this.setCaption("Signalement");
		this.setHeightUndefined();
		this.setWidth("600px");
		
		final TextArea explication = new TextArea();
		explication.setSizeFull();
		explication.setRows(5);
		
		Button submit = new Button("Envoyer");
		submit.setSizeUndefined();
		
		submit.addClickListener(new ClickListener() {			
			@Override
			public void buttonClick(ClickEvent event) {
				br.setUserText(explication.getValue());
				br.report();
				UI.getCurrent().removeWindow(window);
			}
		});
		
		vl.setMargin(true);
		vl.setSpacing(true);
		vl.addComponents(text_br,explication,submit);
		vl.setComponentAlignment(submit,Alignment.MIDDLE_CENTER);
		
		this.setClosable(true);
		this.setModal(true);
		this.setDraggable(false);
		this.center();
		this.setResizable(false);
		this.setContent(vl);
		UI.getCurrent().addWindow(window);
	}
	
}
