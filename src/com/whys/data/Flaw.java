package com.whys.data;

import com.whys.database.Neo4j;

public class Flaw {
	
	public String name;
	public String data;
	public Scan scan;
	public String id;
	
	/**
	 * constructeur pour ajouter une flaw
	 * 
	 * @param name son nom
	 * @param data la donnée associée
	 * @param scan le scan qui l'a rélévée
	 */
	public Flaw(String name, String data, Scan scan){
		this.name = name;
		this.data = data;
		this.scan = scan;
	}
	
	/**
	 * constructeur pour récupérer unn scan de la bdd, réservé à l'utilisation dans Neo4j()
	 * @param name
	 * @param data
	 */
	public Flaw(String name, String data){
		this.name = name;
		this.data = data;
	}
	
	/**
	 * Ne doit pas être utilisé si la flaw a été instanciée dans scan associé !
	 */
	public void save(){
		Neo4j.getInstance().addFlaw(this);
	}
}
