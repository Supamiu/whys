package com.whys.scans;

import com.whys.data.Scan;
import com.whys.listeners.ScanEndListener;
import com.whys.parsers.UniscanDirectoryParser;

public class UniscanDirectory extends Scan{

	public final static String NAME = "uniscan -q";
	public final static String CMD = "uniscan -q -u";
	public final static String HUMANIZED_NAME = "Détection répertoires";
	public final static String TPS = "environ 15 minutes";
	public final static String DESCRITPION = "Ce scan exécute environ 5000 requêtes sur le site web afin de détecter les répertoires qui renvoie un "
			+ "code 200 (OK)";
	
	public UniscanDirectory() {
		super(NAME,CMD,HUMANIZED_NAME);
		this.duration = 15;
		this.addScanEndListener(new ScanEndListener() {			
			@Override
			public void onScanTerminated() {
				Parse();
			}
		});
	}
	
	private void Parse(){
		UniscanDirectoryParser up = new UniscanDirectoryParser(this);
		up.saveReport();
	}
	
}
