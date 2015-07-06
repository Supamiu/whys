package com.whys.scans;

import com.whys.data.Scan;
import com.whys.listeners.ScanEndListener;
import com.whys.parsers.JoomscanParser;

public class Joomscan extends Scan{

	public final static String NAME = "joomscan";
	public final static String CMD = "joomscan -u";
	public final static String HUMANIZED_NAME = "Vulnérabilités Joomla";
	public final static String TPS = "moins d'une heure.";
	public final static String DESCRITPION = "Ce scan détecte la version de Joomla installée, et test toutes les failles"
			+ " de sécurité qui y ont été signalées pour savoir si votre site Joomla y est vulnérable.";
	
	public Joomscan(){
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
		JoomscanParser jp = new JoomscanParser(this);
		jp.saveReport();
	}
	
	
}
