package com.whys.securityUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.whys.database.Neo4j;
import com.whys.data.Site;

public class ConfirmSite {


	private FileWriter fw;
	private BufferedWriter bw;
	String confirmation;
	Neo4j neo4j = Neo4j.getInstance();
	Site target;
	Document doc;
	
	public ConfirmSite(Site target){
		this.target = target;
		String token = neo4j.getActivationToken(target);
		String filename = target.name.replaceAll(" ", "_");
		File whys = new File("/var/whys/tmp/"+filename+".whys");
		whys.delete();
		try {
			whys.createNewFile();
			fw = new FileWriter(whys.getAbsoluteFile(),true);
			bw = new BufferedWriter(fw);
			bw.write("<!DOCTYPE html><meta http-equiv='Content-Type' content='text/html;Charset=UTF-8'><html><div id='confirmation'>"+token+"</div></html>");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean confirm(){
		try {
			String targetName = target.name.replaceAll("\\s", "_");
			 doc = Jsoup.connect("http://"+target.url+"/"+targetName+".whys").ignoreContentType(true).get();
			 confirmation = doc.getElementById("confirmation").html();
		} catch (IOException | NullPointerException e) {
			return false;
		}
		
		if(confirmation.equals(neo4j.getActivationToken(target))){
			neo4j.confirmSite(target);
			target.confirmed = true;
			return true;
		}
		return false;
	}
}
