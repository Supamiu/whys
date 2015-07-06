package com.whys.scans;

import com.whys.data.Scan;
import com.whys.listeners.ScanEndListener;
import com.whys.parsers.NmapParser;

public class Nmap extends Scan {
	public final static String NAME = "nmap";
	public final static String CMD = "nmap -sS -sU -T4 -v -Pn -A ";
	public final static String HUMANIZED_NAME = "détails serveur";
	public final static String TPS = "moins de 2 heures.";
	public final static String DESCRITPION = "Ce scan trouve les ports ouverts sur le serveur, ainsi que les processus qui les écoutent.<br/>"
			+ "Il peut également trouver le système d'exploitation et sa version, ainsi que l'Uptime du serveur.";
	
	public Nmap(){
		super(NAME,CMD,HUMANIZED_NAME);
		this.duration = 200;
		this.addScanEndListener(new ScanEndListener() {			
			@Override
			public void onScanTerminated() {
				Parse();
			}
		});
	}
	
	private void Parse(){
		NmapParser np = new NmapParser(this);
		np.saveReport();
	}
}
