package com.whys.scans;

import com.whys.data.Scan;
import com.whys.listeners.ScanEndListener;
import com.whys.parsers.UniscanDynamicParser;

public class UniscanDynamic extends Scan{
	
	public final static String NAME = "uniscan -d";
	public final static String CMD = "uniscan -d -u";
	public final static String HUMANIZED_NAME = "Scan dynamique de failles Web";
	public final static String TPS = "Jusqu'à une journée";
	public final static String DESCRITPION = "Ce scan cherche toutes les failles possibles sur votre site,"
			+ "il est capable de repérer des injections SQL et des champs d'upload fichiers pour tester des failles "
			+ "RFI ou LFI, il recherche également des éventuels backdoors et des CGI faillibles, ainsi que des Path "
			+ "disclosure ou encore des phpInfo(). Il expose également des informations qui peuvent aisément être "
			+ "détectées sur votre site (adresses email, liens externes).";
	
	public UniscanDynamic(){
		super(NAME,CMD,HUMANIZED_NAME);
		this.saveWithout("Crawling: \\[[^#]+(#~#)+\\| \\[\\*\\]");
		this.saveWithout("Looking for FCKeditor directories [^#]+ #~#\\| \\[\\*\\]");
		this.saveWithout("Remaining tests: [^#]+ #~#\\[\\*\\] ");
		this.saveWithout("Creating tests [^#]+ #~#\\| \\[\\*\\]");
		this.duration = 24*60;
		this.addScanEndListener(new ScanEndListener() {			
			@Override
			public void onScanTerminated() {
				Parse();
			}
		});
	}
	
	private void Parse(){
		UniscanDynamicParser up = new UniscanDynamicParser(this);
		up.saveReport();
	}

}
