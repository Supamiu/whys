package com.whys.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.whys.data.Flaw;
import com.whys.data.Parser;
import com.whys.scans.Joomscan;

public class JoomscanParser extends Parser{

	private final static Pattern p_vuln = Pattern.compile("(Vulnerable\\?\\s)([\\w/]+)");// Vulnérable = groupe 2
	private final static Pattern p_name = Pattern.compile("(Info -> \\w+: )([^#~#]+)");//Composant vulnérable = groupe 2
	private final static Pattern p_data = Pattern.compile("(Exploit: )([^#~#]+)");
	
	public JoomscanParser(Joomscan js){
		String input = js.getCleanData();
		String[] tests = input.split("#\\s\\d+");
		for(String test : tests){
			String name=null;
			String data=null;
			
			Matcher m_vuln = p_vuln.matcher(test);
			while(m_vuln.find()){
				if(m_vuln.group(2).contains("Yes")){
					Matcher m_name = p_name.matcher(test);
					while(m_name.find()){
						name = m_name.group(2);
					}
					Matcher m_data = p_data.matcher(test);
					while(m_data.find()){
						data = m_data.group(2);
					}
					if(data != null && name != null){
						Flaw flaw = new Flaw(name, data, js);
						this.flaws.add(flaw);
					}
				}
			}
		}
	}
}
