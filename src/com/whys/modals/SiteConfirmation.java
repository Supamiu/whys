package com.whys.modals;

import java.io.File;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;
import com.whys.data.Site;
import com.whys.securityUtils.ConfirmSite;
import com.whys.views.DashBoard;

public class SiteConfirmation {

	Window window;
	VerticalLayout vl = new VerticalLayout();
	Site site;
	
	@SuppressWarnings("serial")
	public SiteConfirmation(final Site site){
		this.site = site;

		String filename = site.name.replaceAll(" ", "_");
		final ConfirmSite confirm = new ConfirmSite(site);
		Label firstPart = new Label("Premièrement, veuillez télécharger le fichier "+filename+".whys"
				+ "\nEnsuite, copiez le fichier "+filename+".whys à la racine de votre site web (de manière à pouvoir le voir sur http://"+site.url+"/"+filename+".whys");
		Button download = new Button(FontAwesome.DOWNLOAD);
		Label secondPart = new Label("Ensuite, cliquez sur le bouton Valider pour confirmer votre site, un robot ira vérifier le fichier pour confirmer votre site.");
		download.setCaption("Télécharger "+site.name+".whys");
		download.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		
		Resource res = new FileResource(new File("/var/whys/tmp/"+filename+".whys"));
		FileDownloader fd = new FileDownloader(res);
		fd.extend(download);
		
		Button confirmer = new Button("Vérifier");
		confirmer.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		confirmer.addClickListener(new ClickListener() {			
			@Override
			public void buttonClick(ClickEvent event) {
				if(confirm.confirm()){
					UI.getCurrent().removeWindow(window);
					UI.getCurrent().getNavigator().navigateTo(DashBoard.NAME);
				}else{
					new Notification("Erreur, le fichier est introuvable ou a été modifié.",Type.ERROR_MESSAGE).show(Page.getCurrent());
				}
			}
		});
		window = new Window("Confirmation de votre site");
		
		vl.addComponents(firstPart,download,secondPart,confirmer);
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
