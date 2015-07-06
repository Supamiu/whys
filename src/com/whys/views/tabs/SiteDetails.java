package com.whys.views.tabs;

import java.util.ArrayList;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;
import com.whys.database.Neo4j;
import com.whys.exceptions.ScanNotFoundException;
import com.whys.listeners.ScanEndListener;
import com.whys.data.Site;
import com.whys.modals.SiteConfirmation;
import com.whys.data.Info;
import com.whys.data.Scan;
import com.whys.scans.WhatWeb;
import com.whys.views.DashBoard;

public class SiteDetails {
	
	private Site site;
	private final VerticalLayout detailsContainer = new VerticalLayout();
	WhatWeb wat = new WhatWeb();
	Label type;
	Label nbScans;
	
	
	
	public SiteDetails(final Site site){
		this.site = site;
		wat.addScanEndListener(new ScanEndListener() {
			@Override
			public void onScanTerminated() {
				detailsContainer.removeAllComponents();
				try {
					addWhatWebScan(Neo4j.getInstance().getScanOfSite(site,WhatWeb.NAME));
				} catch (ScanNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Créé le layout de l'onglet Détails
	 * 
	 * @return HorizontalLayout terminé.
	 */
	@SuppressWarnings("serial")
	public HorizontalLayout getLayout() {
		HorizontalLayout hl = new HorizontalLayout();
		VerticalLayout vl = new VerticalLayout();
		//Dans vlRight, j'ajouterais les informations détaillées sur le site (serveur, redirections, headers,...)
		VerticalLayout vlRight = new VerticalLayout();
		vlRight.setMargin(true);
		
		//On gère d'abord la génération de toute la partie de gauche
		vl.setMargin(true);
		vl.setSpacing(true);
		Button retour = new Button(FontAwesome.BACKWARD);
		retour.setCaption("Retour");
		retour.setStyleName(ValoTheme.BUTTON_QUIET);
		
		retour.addClickListener(new ClickListener() {			
			@Override
			public void buttonClick(ClickEvent event) {
				VaadinSession.getCurrent().setAttribute("Site", null);
				UI.getCurrent().getNavigator().navigateTo(DashBoard.NAME);
			}
		});
		Label url = new Label("<a style='text-decoration: none;' href='http://"+site.url+"' target='_blank'>Accéder au site</a>",ContentMode.HTML);
		nbScans = new Label("Scans effectués : "+Neo4j.getInstance().countScansOfSite(site.id));
		String typeStr = site.type;
		
		if(typeStr.toLowerCase() == "wordpress")
		{
			type = new Label("Type de site : "+FontAwesome.WORDPRESS.getHtml() +" "+typeStr,ContentMode.HTML);
		}else{
			type = new Label("Type de site : "+typeStr);
		}
		type.setSizeUndefined();
		Label nbFailles = new Label("Failles détectées : "+Neo4j.getInstance().countFlaws(site));
		nbFailles.setWidthUndefined();
		HorizontalLayout confirmation = new HorizontalLayout();
		Button confirm = new Button(FontAwesome.CHECK);
		confirm.addClickListener(new ClickListener() {			
			@Override
			public void buttonClick(ClickEvent event) {
				new SiteConfirmation(site);
			}
		});
		confirm.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		confirm.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		Label confirmed = new Label();
		confirmation.addComponent(confirmed);
		confirmation.setSpacing(true);
		if(site.confirmed == true)
		{
			confirmed.setValue("Confirmé");
			confirmed.setStyleName(ValoTheme.LABEL_SUCCESS);
		}else{
			confirmed.setValue("Non confirmé");
			confirmed.setStyleName(ValoTheme.LABEL_FAILURE);
			confirm.setDescription("Confirmer le site");
			confirmation.addComponent(confirm);
			confirmation.setSizeUndefined();
			confirmation.setComponentAlignment(confirm, Alignment.MIDDLE_CENTER);
		}
		url.setWidthUndefined();
		nbScans.setWidthUndefined();
		confirmed.setWidthUndefined();	
		type.setWidthUndefined();
		
		//Puis on attaque la génération de la partie de droite
		//d'abord on conçoit les différentes couches
		Panel details = new Panel("Informations détaillées");
		details.setStyleName(ValoTheme.PANEL_WELL);
		details.addStyleName("detailedinformations");
		
		detailsContainer.setMargin(true);
		detailsContainer.setSizeFull();
		detailsContainer.setImmediate(true);
		details.setContent(detailsContainer);
		details.setSizeFull();
		//Une fois les couches conçues, on peut y ajouter nos informations
		
		//Si l'utilisateur a déjà fait un scan Whatweb (le scan d'informations)
		Scan scan = null;
		try{
			scan = Neo4j.getInstance().getScanOfSite(site,WhatWeb.NAME);
		}catch(ScanNotFoundException e){
			//si on est la, il n'a pas de scan whatweb associé au site ^^
		}
		//Si il a un scan, on ajoute son résultat à notre layout.
		if(scan != null){
			addWhatWebScan(scan);
		}
		else
		{
			//si le site n'a pas de scan de type informations, on demande à l'utilisateur d'en lancer un.
			final Label nope = new Label("Lancez le scan pour remplir cette partie.");
			final Button startScan = new Button(FontAwesome.PLAY);
			startScan.setImmediate(true);
			startScan.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					//Si le scan de type informations est en cours, on laisse l'utilisateur patienter un peut.
					wat.start(site);
					setupLoadingLayout();
				}
			});
			startScan.setCaption("Lancer le scan");
			
			//Une fois les composants prêts, on y applique les styles désirés
			startScan.setStyleName(ValoTheme.BUTTON_FRIENDLY);
			nope.setSizeUndefined();
			startScan.setSizeUndefined();
		
			//Pour finir on ajoute nos composants dans le layout qui les affichera
			detailsContainer.addComponents(nope,startScan);
			
			//Puis on y applique nos règles de style
			detailsContainer.setComponentAlignment(nope, Alignment.MIDDLE_CENTER);
			detailsContainer.setComponentAlignment(startScan, Alignment.MIDDLE_CENTER);
		}
		
		//Et pour finir, on positionne notre Panel dans vlRight pour l'accrocher à la partie droite de l'écran
		vlRight.addComponent(details);
		
		vlRight.setSizeFull();
		vl.setSizeFull();
		vl.addComponents(retour,url,nbScans,nbFailles,type,confirmation);
		vl.setComponentAlignment(retour, Alignment.TOP_LEFT);
		vl.setComponentAlignment(url, Alignment.MIDDLE_CENTER);
		vl.setComponentAlignment(nbScans, Alignment.MIDDLE_CENTER);
		vl.setComponentAlignment(confirmation, Alignment.MIDDLE_CENTER);
		vl.setComponentAlignment(type, Alignment.MIDDLE_CENTER);
		vl.setComponentAlignment(nbFailles, Alignment.MIDDLE_CENTER);
		
		hl.setSizeFull();
		hl.addComponents(vl, vlRight);

		return hl;
	}
	
	/**
	 * ajoute au layout de droite les détails extraits du scan Whatweb donné;
	 * 
	 * @param scan
	 */
	private void addWhatWebScan(Scan scan){
		ArrayList<Info> infos = Neo4j.getInstance().getScanInfos(scan);
		for(Info i : infos){
			HorizontalLayout hl = new HorizontalLayout();
			hl.addComponents(new Label(i.name+" : "),new Label(i.data));
			nbScans.setValue("Scans effectués : "+Neo4j.getInstance().countScansOfSite(site.id));
			this.site.refresh();
			type.setValue("Type de site : "+this.site.type);
			detailsContainer.addComponent(hl);
		}		
	}
	
	/**
	 * prépare et met en place la partie "scan en cours, blablabla'
	 */
	private void setupLoadingLayout(){
		//Si le scan de type informations est en cours, on laisse l'utilisateur patienter un peut.
		detailsContainer.removeAllComponents();
		Label label = new Label("Scan en cours, Veuillez patienter");
		label.setSizeUndefined();
		detailsContainer.addComponent(label);
		detailsContainer.setComponentAlignment(label, Alignment.BOTTOM_CENTER);
		Label spinner = new Label();
		spinner.setSizeUndefined();
		spinner.setStyleName(ValoTheme.LABEL_SPINNER);
		detailsContainer.addComponent(spinner);
		detailsContainer.setComponentAlignment(spinner, Alignment.TOP_CENTER);
	}
	
	/**
	 * change le site utilisé pour le layout en cours.
	 *  
	 * @param site le site qui va remplacer celui actuellement utilisé
	 */
	public void setSite(Site site){
		this.site=site;
	}
}
