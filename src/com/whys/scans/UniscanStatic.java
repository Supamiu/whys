package com.whys.scans;

import com.whys.data.Scan;
import com.whys.listeners.ScanEndListener;
import com.whys.parsers.UniscanStaticParser;


public class UniscanStatic extends Scan {
	public final static String NAME = "uniscan -s";
	public final static String CMD = "uniscan -s -u";
	public final static String HUMANIZED_NAME = "Scan statique de failles Web";
	public final static String TPS = "moins de 2 heures.";
	public final static String DESCRITPION = "Ce scan va exécuter une liste de requêtes pré établies sur votre site pour en tirer "
			+ "des failles génériques (Local File Include, Remote Command Execution, Remote File Include).";
	
	public UniscanStatic(){
		super(NAME,CMD,HUMANIZED_NAME);
		this.duration = 200;
		this.saveWithout("Remaining tests: [^#]+ #~#\\[\\*\\] ");
		this.addScanEndListener(new ScanEndListener() {			
			@Override
			public void onScanTerminated() {
				Parse();
			}
		});
	}
	
	private void Parse(){
		UniscanStaticParser usp = new UniscanStaticParser(this);
		usp.saveReport();
	}
}
