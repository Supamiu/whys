package com.whys.data;

import com.whys.database.Neo4j;

public class Info {
	
	public String name;
	public String data;
	public Scan scan;
	
	/**
	 * constructeur pour ajouter une info
	 * 
	 * @param name son nom
	 * @param data la donnée associée
	 * @param scan le scan qui l'a rélévée
	 */
	public Info(String name, String data, Scan scan){
		this.name = name;
		this.data = data;
		this.scan = scan;
	}
	
	/**
	 * constructeur pour récupérer unn scan de la bdd, réservé à l'utilisation dans Neo4j()
	 * @param name
	 * @param data
	 */
	public Info(String name, String data){
		this.name = name;
		this.data = data;
	}
	
	/**
	 * Ne doit pas être utilisé si l'Info a été instanciée dans scan associé !
	 */
	public void save(){
		Neo4j.getInstance().addInfo(this);
	}

	public void addData(String data) {
		this.data+=data;
	}
}
