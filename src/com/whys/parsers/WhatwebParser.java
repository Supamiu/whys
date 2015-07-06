package com.whys.parsers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.whys.data.Info;
import com.whys.data.Parser;
import com.whys.data.Site;
import com.whys.scans.WhatWeb;


public class WhatwebParser extends Parser{
	
	String type = "N/A";
	String[] types = {"WordPress", "Joomla", "Drupal"};
	String[] inputs;
	String url;
	Site site;
	
	public WhatwebParser(WhatWeb scan){
		this.site = scan.getTarget();
		String input = scan.getCleanData().replaceAll("#~#","");
		inputs = input.split("],");
		int i=0;
		for(String str : inputs){
			String newStr = str;
			try {
				newStr = URLDecoder.decode(str.replaceFirst("\\[", " : ").replaceAll("\\]\\[", " ").replaceFirst("\\]", " ").replaceFirst("\\[", ""),"UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
			checkIfType(newStr);
			String name;
			String data;
			try{
				name = newStr.split(":")[0];
				data = newStr.split(":")[1];
			}catch(IndexOutOfBoundsException e){
				name = newStr.split(":")[0];
				data = "Problème avec le site, merci de nous contacter";
			}
			Info info = new Info(name,data,scan);
			this.infos.add(info);
			inputs[i]=newStr;
			i++;
		}
	}
	
	private void checkIfType(String input){
		for(String t : types){
			if(input.contains(t)){
				neo.setSiteType(site, t);
			}
		}
	}
}
