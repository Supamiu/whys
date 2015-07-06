package com.whys.views.layouts.scans;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;
import com.whys.components.ScanStartButton;
import com.whys.exceptions.ScanNotFoundException;
import com.whys.listeners.ScanEndListener;
import com.whys.scans.Wget;

public class WgetLayout extends ScanLayout implements ScanLayoutInterface{

	private Wget wget = new Wget();
	Panel panel = new Panel("Temps de chargement du site");
	
	public WgetLayout(){
		panel.setSizeFull();
		panel.addStyleName("panel-centered-title");
		//Le listener pour si on lance le scan d'ici
		wget.addScanEndListener(new ScanEndListener() {			
			@Override
			public void onScanTerminated() {
				try {
					scan = neo.getScanOfSite(site, Wget.NAME);
				} catch (ScanNotFoundException e) {
					e.printStackTrace();
				}
				panel.setContent(resultLayout());
			}
		});
		
		try{
			this.scan = neo.getScanOfSite(site, Wget.NAME);
		}catch(ScanNotFoundException e){
		}
		if(scan != null){
			if(scan.isFinished()){
				panel.setContent(resultLayout());
			}
			else if(scan.isRunning()){
				panel.setContent(runningLayout());
			}
		}else{
			panel.setContent(startLayout());
		}
		
		this.layout.addComponent(panel);
	}
	
	@Override
	public Layout resultLayout() {
		addResetButton();
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		vl.setSpacing(true);
		Label nbItems;
		Label tps;
		if(neo.getScanInfo(scan, "nbItems").data != "nullData"){
			nbItems = new Label("Nombre d'éléments dans la page : "+neo.getScanInfo(scan, "nbItems").data);
			tps = new Label("Temps pour charger la page : "+neo.getScanInfo(scan, "temps").data+"s");	
		}else{
			nbItems = new Label("Nombre d'éléments dans la page : 0");
			tps = new Label("Temps pour charger la page : Inexistant");	
		}
		
		vl.setSpacing(true);
		vl.addComponents(nbItems,tps);
		return vl;
	}

	@Override
	public Layout runningLayout() {
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setMargin(true);
		ProgressBar pb = new ProgressBar();
		pb.setIndeterminate(true);
		Label running = new Label("Scan en Cours");
		hl.setSizeFull();
		hl.setSpacing(true);
		hl.addComponents(pb,running);
		hl.setSizeUndefined();
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		vl.addComponent(hl);
		vl.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
		return vl;
	}

	@Override
	public Layout startLayout() {
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		vl.setSpacing(true);
		Label tps = new Label("Temps de scan estimé : "+Wget.TPS);
		Label desc = new Label(Wget.DESCRITPION,ContentMode.HTML);
		HorizontalLayout hl = new HorizontalLayout();
		hl.setMargin(true);
		hl.setSpacing(true);
		@SuppressWarnings("serial")
		Button start = new ScanStartButton() {			
			@Override
			public void onClick() {
				wget.start(site);
				panel.setContent(runningLayout());
			}
		};
		hl.addComponents(start);
		hl.setSizeUndefined();
		vl.addComponents(desc,tps,hl);
		vl.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
		return vl;

	}
	
	@Override
	public void resetScan() {
		neo.deleteScan(scan);
		wget.start(site);
		panel.setContent(runningLayout());
	}

}
