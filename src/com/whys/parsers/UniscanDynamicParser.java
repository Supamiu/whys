package com.whys.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.whys.data.Flaw;
import com.whys.data.Info;
import com.whys.data.Parser;
import com.whys.scans.UniscanDynamic;

public class UniscanDynamicParser extends Parser{
	
	private static final Pattern p_SQLi = Pattern.compile("Vul \\[SQL-i\\]([^#~#]+)"); 
	private static final Pattern p_Emails = Pattern.compile("E-mail Found: ([^#]+)");
	private static final Pattern p_LFI = Pattern.compile("Vul \\[LFI\\]([^#~#]+)"); 
	private static final Pattern p_RCE = Pattern.compile("Vul \\[RCE\\]([^#~#]+)"); 
	private static final Pattern p_XSS = Pattern.compile("Vul \\[XSS\\]([^#~#]+)");
	private static final Pattern p_RFI = Pattern.compile("Vul \\[RFI\\]([^#~#]+)"); 
	private static final Pattern p_BSQLi = Pattern.compile("Vul \\[RFI\\]([^#~#]+)"); 
	private static final Pattern p_FCK = Pattern.compile("\\[\\+\\] (http:\\/\\/[^#]+)");
	private static final Pattern p_Thim = Pattern.compile("\\[\\+\\] Timthumb \\w+: ([^#]+)");
	private static final Pattern p_Shell = Pattern.compile("\\[\\+\\] Possible WebShell : ([^#]+)");
	private static final Pattern p_CGI = Pattern.compile("\\[\\+\\] Vul:([^#]+)");
	private static final Pattern p_SCD = Pattern.compile("Source Code Found.?:([^#]+)");
	
	
	public UniscanDynamicParser(UniscanDynamic uni){
		String input = uni.getCleanData();
		Matcher m_SQLinj = p_SQLi.matcher(input);
		while(m_SQLinj.find()){
			Flaw flaw = new Flaw("Injection SQL", m_SQLinj.group(1), uni);
			flaws.add(flaw);
		}
		
		Matcher m_BSQLinj = p_BSQLi.matcher(input);
		while(m_BSQLinj.find()){
			Flaw flaw = new Flaw("Injection SQL Blind", m_BSQLinj.group(1), uni);
			flaws.add(flaw);
		}
		
		Matcher m_Shell = p_Shell.matcher(input);
		while(m_Shell.find()){
			Flaw flaw = new Flaw("Shell possible", m_Shell.group(1), uni);
			flaws.add(flaw);
		}
		
		Matcher m_SCD = p_SCD.matcher(input);
		while(m_SCD.find()){
			Flaw flaw = new Flaw("Code source", m_SCD.group(1), uni);
			flaws.add(flaw);
		}
		
		Matcher m_CGI = p_CGI.matcher(input);
		while(m_CGI.find()){
			Flaw flaw = new Flaw("CGI", m_CGI.group(1), uni);
			flaws.add(flaw);
		}
		
		Matcher m_LFI = p_LFI.matcher(input);
		while(m_LFI.find()){
			Flaw flaw = new Flaw("Local File Inclusion",m_LFI.group(1),uni);
			flaws.add(flaw);
		}
		
		Matcher m_RCE = p_RCE.matcher(input);
		while(m_RCE.find()){
			Flaw flaw = new Flaw("Remote Code Execution",m_RCE.group(1),uni);
			flaws.add(flaw);
		}
		
		Matcher m_XSS = p_XSS.matcher(input);
		while(m_XSS.find()){
			Flaw flaw = new Flaw("Cross Site Scipting",m_XSS.group(1),uni);
			flaws.add(flaw);
		}
		
		Matcher m_RFI = p_RFI.matcher(input);
		while(m_RFI.find()){
			Flaw flaw = new Flaw("Remote File Include",m_RFI.group(1),uni);
			flaws.add(flaw);
		}
		
		Matcher m_FCK = p_FCK.matcher(input);
		while(m_FCK.find()){
			Flaw flaw = new Flaw("FCKEditor exploit", m_FCK.group(1), uni);
			flaws.add(flaw);
		}
		
		Matcher m_Thim = p_Thim.matcher(input);
		while(m_Thim.find()){
			Flaw flaw = new Flaw("Timthumb", m_Thim.group(1), uni);
			flaws.add(flaw);
		}	
		
		
		Matcher m_Emails = p_Emails.matcher(input);
		String emails = "";
		while(m_Emails.find()){
			emails += m_Emails.group(1);
		}
		if(emails!=""){
			Info info = new Info("Emails", emails, uni);
			infos.add(info);
		}
	}
}
