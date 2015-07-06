package com.whys.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.whys.data.Flaw;
import com.whys.data.Parser;
import com.whys.scans.UniscanStatic;

public class UniscanStaticParser extends Parser{
	
	private static final Pattern p_LFI = Pattern.compile("Vul \\[LFI\\]([^#~#]+)");
	private static final Pattern p_RCE = Pattern.compile("Vul \\[RCE\\]([^#~#]+)");
	private static final Pattern p_RFI = Pattern.compile("Vul \\[RFI\\]([^#~#]+)");
	
	public UniscanStaticParser(UniscanStatic uni){
		String input = uni.getCleanData();
		Matcher m_LFI = p_LFI.matcher(input);
		Matcher m_RCE = p_RCE.matcher(input);
		Matcher m_RFI = p_RFI.matcher(input);
		
		while(m_LFI.find()){
			Flaw flaw = new Flaw("Local File Include", m_LFI.group(1), uni);
			flaws.add(flaw);
		}
		while(m_RCE.find()){
			Flaw flaw = new Flaw("Remote Code Execution", m_RCE.group(1), uni);
			flaws.add(flaw);
		}
		while(m_RFI.find()){
			Flaw flaw = new Flaw("Remote File Include", m_RFI.group(1), uni);
			flaws.add(flaw);
		}
	}
}
