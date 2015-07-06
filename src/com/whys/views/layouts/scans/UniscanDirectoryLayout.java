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
import com.whys.scans.UniscanDirectory;

public class UniscanDirectoryLayout extends ScanLayout implements ScanLayoutInterface{

	private UniscanDirectory uni = new UniscanDirectory();
	Panel panel = new Panel("Dossiers accessibles");
	
	
	public UniscanDirectoryLayout(){
		panel.setSizeFull();
		panel.addStyleName("panel-centered-title");
		//Le listener pour si on lance le scan d'ici
		uni.addScanEndListener(new ScanEndListener() {			
			@Override
			public void onScanTerminated() {
				try {
					scan = neo.getScanOfSite(site, UniscanDirectory.NAME);
				} catch (ScanNotFoundException e) {
					e.printStackTrace();
				}
				panel.setContent(resultLayout());
			}
		});
		
		try{
			this.scan = neo.getScanOfSite(site, UniscanDirectory.NAME);
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
		for(Info i : neo.getScanInfos(scan)){
			Label temp = new Label(i.data);
			pl.addComponent(temp);
		}
		
		return pl;
	}
	
	@Override
	public Layout startLayout(){
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		vl.setSpacing(true);
		Label tps = new Label("Temps de scan estimé : "+UniscanDirectory.TPS);
		Label desc = new Label(UniscanDirectory.DESCRITPION,ContentMode.HTML);
		HorizontalLayout hl = new HorizontalLayout();
		hl.setMargin(true);
		hl.setSpacing(true);
		
		@SuppressWarnings("serial")
		Button start = new ScanStartButton() {			
			@Override
			public void onClick() {
				uni.start(site);
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
		uni.start(site);
		panel.setContent(runningLayout());
	}

}
