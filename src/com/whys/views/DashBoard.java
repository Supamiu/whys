package com.whys.views;


import java.util.ArrayList;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.themes.ValoTheme;
import com.whys.database.Neo4j;
import com.whys.views.layouts.SiteLayout;
import com.whys.data.Site;
import com.whys.modals.SiteCreation;
import com.whys.views.parts.Footer;
import com.whys.views.parts.Header;

@SuppressWarnings("serial")
public class DashBoard extends CustomComponent implements View{

	// on déclare le nom de la vue dans un static public pour pouvoir 'lassigner facielement dans le controleur de vues
	public static final String NAME = "dashboard";
	
	// ensuite on prépare le layout, une couche de données dans notre vue
	private AbsoluteLayout absLayout = new AbsoluteLayout();
	// un onglet pour écrire "liste des sites"
	private TabSheet main;
	
	public DashBoard(){
		// on rend notre vue fullscreen (ça revient à faire setWidth("100%"); puis setHeight("100%");
		setSizeFull();
		// puis on place notre absLayout en composant racine de la fenêtre
		setCompositionRoot(buildMainLayout());	
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
	}
	
	private AbsoluteLayout buildMainLayout(){
		
		main = buildMainContent();
		//on ajoute le header à notre layout de base
		absLayout.addComponent(new Header().getLayout(),"top:0px;right:0px;left:0px");
		// puis le contenu généré par buildMainContent()
		absLayout.addComponent(main,"top:110px;bottom:80px");
		// puis le footer
		absLayout.addComponent(new Footer().getLayout(),"bottom:0px;");
		// on rend le layout full et on rend nos modifications immédiates
		absLayout.setImmediate(true);
		absLayout.setSizeFull();
		return absLayout;		
	}
	
	private TabSheet buildMainContent(){
		// on créé notre onglet pour la page "Liste des sites"
		TabSheet panel = new TabSheet();
		// qu'on rend full
		panel.setSizeFull();
		// on y ajoute des éléments de style appartenant au thème Valo
		panel.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
		panel.addStyleName(ValoTheme.TABSHEET_FRAMED);
		panel.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
		//On prépare ensuite la grille des sites
		GridLayout grid = new GridLayout(5, 2);
		grid.setSizeFull();
		grid.setMargin(new MarginInfo(true, true, true, true));
		grid.setSpacing(true);
		grid.setImmediate(true);
		panel.setImmediate(true);
		
		// on créé le bouton d'ajout de site
		Button addSite = new Button(FontAwesome.PLUS);
		addSite.setStyleName(ValoTheme.BUTTON_HUGE);
		addSite.addStyleName(ValoTheme.BUTTON_QUIET);
		addSite.setSizeFull();
		
		// sur lequel on place un listener pour le click, qui ouvre une modale de création de site
		addSite.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				new SiteCreation(){
					@Override
					public void onExit() {
						refreshGrid();
					}
				};
			}
		});
		
		//On récupère la liste des sites de notre utilisateur
		ArrayList<Site> sites = Neo4j.getInstance().getSites();
		int c;
		int i;
		// puis on les place dans notre grille
		for(i=0;i<sites.size();i++)
		{
			c = i>4?i-5:i;
			int j = i>4?1:0;
			
			Site site = sites.get(i);
			SiteLayout siteLayout = new SiteLayout(site){
				@Override
				public void onDeletion() {
					refreshGrid();
				}};
			// via cette méthode
			grid.addComponent(siteLayout.getLayout(), c, j);
		}
		
		//si on a moins de 10 sites
		if(Neo4j.getInstance().countSites()<10)
		{
			c = i>4?i-5:i;
			int k = i>4?1:0;
			// on ajoute le bouton addSite à la dernière case disponible
			grid.addComponent(addSite, c, k);
		}		
		
		// une fois notre onglet terminé, on l'ajoute au panel qui gère tout ça
		panel.addTab(grid,"Liste des Sites",FontAwesome.LIST);		
		return panel;
	}
	
	/**
	 * méthode pour rafraîchir le contenu de la grille de sites
	 */
	public void refreshGrid(){
		absLayout.removeComponent(main);
		main = buildMainContent();
		absLayout.addComponent(main,"top:110px;bottom:80px");
	}
	

}
