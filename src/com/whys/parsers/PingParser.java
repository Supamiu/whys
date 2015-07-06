package com.whys.parsers;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.whys.data.Info;
import com.whys.data.Parser;
import com.whys.scans.Ping;

public class PingParser extends Parser{
	private static final Pattern p = Pattern.compile("(time=)(\\d+.\\d*)");
	ArrayList<Float> temps = new ArrayList<Float>();
	
	public PingParser(Ping ping){
		String[] inputs = ping.getCleanData().split("#~#");
		int i = 0;
		for(String str : inputs){
			Matcher m = p.matcher(str);
			while(m.find()){
				i++;
				this.infos.add(new Info("paquet "+i, m.group(2),ping));
				temps.add(Float.valueOf(m.group(2)));
			}
		}
		float average = 0;
		float buffer = 0;
		for(Float t : temps){
			buffer += t;
		}
		average = temps.size()==0 ? 0 : buffer/temps.size();
		String data = Float.toString(average);
		Info info = new Info("average",data,ping);
		this.infos.add(info);
	}
}
