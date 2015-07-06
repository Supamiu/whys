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
import com.whys.data.Info;
import com.whys.exceptions.ScanNotFoundException;
import com.whys.listeners.ScanEndListener;
import com.whys.modals.ScanEndNotifByEmail;
import com.whys.scans.Nmap;

public class NmapLayout extends ScanLayout implements ScanLayoutInterface {

	private Nmap nmap = new Nmap();
	Panel panel = new Panel("Détails du serveur");
	
	public NmapLayout(){
		panel.setSizeFull();
		panel.addStyleName("panel-centered-title");
		//Le listener pour si on lance le scan d'ici
		nmap.addScanEndListener(new ScanEndListener() {			
			@Override
			public void onScanTerminated() {
				try {
					scan = neo.getScanOfSite(site, Nmap.NAME);
				} catch (ScanNotFoundException e) {
					e.printStackTrace();
				}
				panel.setContent(resultLayout());
			}
		});
		
		try{
			this.scan = neo.getScanOfSite(site, Nmap.NAME);
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
		
		layout.addComponent(panel);
	}
	
	@Override
	public Layout resultLayout(){
		addResetButton();
		VerticalLayout pl = new VerticalLayout();
		pl.setMargin(true);
		pl.setSpacing(true);
		Label ports = new Label("Ports Ouverts : ");
		pl.addComponent(ports);
		for(Info i : neo.getScanInfosByName(scan, "openPort")){
			Label port = new Label(" -- "+i.data);
			pl.addComponent(port);
		}
		
		VerticalLayout OS_uptime = new VerticalLayout();
		OS_uptime.setMargin(true);
		OS_uptime.setSpacing(true);
		Label os = new Label("Système d'exploitation : "+neo.getScanInfo(scan, "os").data);
		Label uptime = new Label("Uptime : "+neo.getScanInfo(scan, "uptime").data+ " jour(s)");
		OS_uptime.addComponents(os,uptime);
		
		pl.addComponent(OS_uptime);
		
		return pl;
	}
	
	@Override
	public Layout startLayout(){
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		vl.setSpacing(true);
		Label tps = new Label("Temps de scan estimé : "+Nmap.TPS);
		Label desc = new Label(Nmap.DESCRITPION,ContentMode.HTML);
		HorizontalLayout hl = new HorizontalLayout();
		hl.setMargin(true);
		hl.setSpacing(true);
		
		@SuppressWarnings("serial")
		Button start = new ScanStartButton() {			
			@Override
			public void onClick() {
				new ScanEndNotifByEmail(nmap);
				nmap.start(site);
				panel.setContent(runningLayout());
			}
		};
		hl.addComponent(start);
		hl.setSizeUndefined();
		vl.addComponents(desc,tps,hl);
		vl.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
		
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
	public void resetScan() {
		neo.deleteScan(scan);
		nmap.start(site);
		panel.setContent(runningLayout());
	}

}
