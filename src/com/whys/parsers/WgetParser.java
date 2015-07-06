package com.whys.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.whys.data.Info;
import com.whys.data.Parser;
import com.whys.scans.Wget;

public class WgetParser extends Parser{
	private static final Pattern p = Pattern.compile("(#~#Downloaded:.)(\\d+)(.files,.)(\\d+,?\\d*\\w{1})(.in.)(\\d+\\,?\\d+)(s)");
	
	public WgetParser(Wget wget){
		String input = wget.getCleanData();
		Matcher m = p.matcher(input);
		while(m.find()){
			Info nb = new Info("nbItems",m.group(2),wget);
			this.infos.add(nb);
			Info size = new Info("size",m.group(4),wget);
			this.infos.add(size);
			Info temps = new Info("temps",m.group(6),wget);
			this.infos.add(temps);
		}
			
		
	}
}
