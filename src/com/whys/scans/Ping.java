package com.whys.scans;

import com.whys.data.Scan;
import com.whys.listeners.ScanEndListener;
import com.whys.parsers.PingParser;


public class Ping extends Scan{
	public final static String NAME = "ping";
	public final static String CMD = "ping -c 20";
	public final static String HUMANIZED_NAME = "temps de réponse";
	public final static String TPS = "moins de 2 minutes.";
	public final static String DESCRITPION = "Ce scan envoi 20 paquets en direction du serveur et analyse le temps de réponse pour en calculer la moyenne.";
	
	public Ping() {
		super(NAME,CMD,HUMANIZED_NAME);
		this.duration = 5;
		this.addScanEndListener(new ScanEndListener() {			
			@Override
			public void onScanTerminated() {
				Parse();
			}
		});
	}
	
	private void Parse(){
		PingParser pp = new PingParser(this);
		pp.saveReport();
	}
}

