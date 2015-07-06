package com.whys.data;

import java.util.ArrayList;

import com.whys.database.Neo4j;

public class Parser {
	protected ArrayList<Info> infos = new ArrayList<Info>();
	protected ArrayList<Flaw> flaws = new ArrayList<Flaw>();
	protected Neo4j neo = Neo4j.getInstance();
	
	public void saveReport(){
		for(Info i : infos){
			neo.addInfo(i);
		}
		for(Flaw f : flaws){
			neo.addFlaw(f);
		}
	}
}
