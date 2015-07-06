package com.whys.scans;

import com.whys.data.Scan;
import com.whys.listeners.ScanEndListener;
import com.whys.parsers.WgetParser;

public class Wget extends Scan{
	public final static String NAME = "wget";
	public final static String CMD = "wget -p -H -nv --delete-after ";
	public final static String HUMANIZED_NAME = "temps de chargement";
	public final static String TPS = "moins de 5 minutes";
	public final static String DESCRITPION = "Ce scan charge votre page d'acceuil comme le ferait tout utilisateur, en chronomètrant le temps mis et en comptant les éléments chargés.";
	
	public Wget() {
		super(NAME,CMD,HUMANIZED_NAME);
		this.duration = 15;
		addErrorInData();
		onlySaveAfter("FINISHED");
		this.addScanEndListener(new ScanEndListener() {			
			@Override
			public void onScanTerminated() {
				Parse();
			}
		});
	}
	
	private void Parse(){
		WgetParser wp = new WgetParser(this);
		wp.saveReport();
	}
	
}
