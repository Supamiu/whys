package com.whys.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.whys.data.Info;
import com.whys.data.Parser;
import com.whys.scans.Nmap;

public class NmapParser extends Parser {
	private final static Pattern p_OS = Pattern.compile("(OS details:.)([^#~#]+)(Uptime)");//Type d'OS = groupe 2
	private final static Pattern p_ports = Pattern.compile("(Discovered.open.port.)(\\d+)(\\/)(\\w+)(.on)"); //port ouvert = groupe 2, protocole = groupe 4
	private final static Pattern p_uptime = Pattern.compile("(Uptime.guess:.)(\\d+\\.\\d+)(.days)");//Uptime en jours = groupe 2
	
	public NmapParser(Nmap nmap){
		String input = nmap.getCleanData();
		
		Matcher m_OS = p_OS.matcher(input);
		Info os = null;
		while(m_OS.find()){
			os = new Info("os",m_OS.group(2),nmap);
		}
		
		os=os==null?new Info("os","Non déterminé",nmap):os;
		
		this.infos.add(os);
		
		Matcher m_uptime = p_uptime.matcher(input);
		Info uptime = null;
		while(m_uptime.find()){
			uptime = new Info("uptime",m_uptime.group(2),nmap);
		}
		
		uptime=uptime==null?new Info("uptime","Non déterminé",nmap):uptime;
		
		this.infos.add(uptime);
		
		Matcher m_ports = p_ports.matcher(input);
		while(m_ports.find()){
			Info port = new Info("openPort",m_ports.group(2)+" -- "+m_ports.group(4).toString().toUpperCase(),nmap);
			//Regex pour récupérer le service qui écoute et sa version ainsi que le status du port
			Pattern p_port_complete = Pattern.compile("#~#"+m_ports.group(2)+"\\/"+m_ports.group(4)+"\\s+(\\S+)\\s+\\w+\\s+([^#]+)");
			// status du port (open ou open|filtered) = groupe 1, service qui écoute sur le port = groupe 2
			Matcher m_port_complete = p_port_complete.matcher(input);
			String data = null;
			while(m_port_complete.find()){
				data = " status : "+m_port_complete.group(1)+" -- écouté par "+m_port_complete.group(2);
			}
			//Si le regex d'info complète n'a pas marché, on se montre un poil moins gourmand.
			if(data == null){
				//Regex pour récupérer le service qui écoute sans sa version ainsi que le status du port
				Pattern p_port_partial = Pattern.compile("#~#"+m_ports.group(2)+"\\/"+m_ports.group(4)+"\\s+(\\S+)\\s+(\\w+)");
				Matcher m_port_partial = p_port_partial.matcher(input);
				while(m_port_partial.find()){
					data = " status : "+m_port_partial.group(1)+" -- écouté par "+m_port_partial.group(2);
				}
			}
			
			port.addData(data);			
			this.infos.add(port);
		}
	}
}
