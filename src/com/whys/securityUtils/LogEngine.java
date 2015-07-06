package com.whys.securityUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import com.vaadin.server.VaadinSession;

public class LogEngine {
	private File file;
	private FileWriter fw;
	private BufferedWriter bw;
	private boolean isWindows;
	
	public LogEngine(String type){
		
		isWindows = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
		if(!isWindows)
		{
			File whys = new File("/var/log/whys/");
			if(!whys.exists()){
				whys.mkdir();
			}		
			file = new File("/var/log/whys/"+type+".log");
			
			if (!file.exists()) {
				try {
					file.createNewFile();
					fw = new FileWriter(file.getAbsoluteFile(),true);
					bw = new BufferedWriter(fw);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				 try {
					fw = new FileWriter(file.getAbsoluteFile(),true);
					bw = new BufferedWriter(fw);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void log(String content, boolean withIP, boolean withUsername){
		if(!isWindows)
		{
			String string = new Date()+" ---- "+content;
			if (withIP){
				String ip = VaadinSession.getCurrent().getAttribute("ipaddr").toString();
				string += " ---- ip : "+ip;
				
			}
			if (withUsername){
				string += " ---- username : "+VaadinSession.getCurrent().getAttribute("pseudo").toString();
			}
			try {
				bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile(),true));
				string += System.lineSeparator();
				bw.write(string);
				System.out.println(string);
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
