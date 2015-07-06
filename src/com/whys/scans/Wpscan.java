package com.whys.scans;

import com.whys.data.Scan;


public class Wpscan extends Scan{
	public final static String NAME = "wpscan";
	public final static String CMD = "wpscan";
	public final static String HUMANIZED_NAME = "vulnérabilités WordPress";
	public final static String TPS = "moins de 2 heures";
	public final static String DESCRITPION = "Ce scan liste et vérifie tous vos plugins WordPress en utilisant une base de données pour déterminer si l'un de vos plugins est faillible.";
	
	public Wpscan()
	{
		super(NAME,CMD,HUMANIZED_NAME);
	}
	
	public void onResult(String data){
		raw_data += data+"<br/>";
	}
}
