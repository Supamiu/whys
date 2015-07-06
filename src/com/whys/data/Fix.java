package com.whys.data;

import com.whys.database.Neo4j;

public class Fix {
	
	public boolean isUpdate;
	public String data;
	public Flaw flaw;
	
	
	/**
	 * Constructeur pour créer un fix depuis une flaw ou un scan
	 * 
	 * @param isUpdate si c'est un fix de type "fixed in X.X.X"
	 * @param update la donnée du fix
	 * @param flaw la faille qu'il fix
	 */
	public Fix(boolean isUpdate,String update,Flaw flaw){
		this.isUpdate = isUpdate;
		this.data = update;
		this.flaw = flaw;
	}
	
	/**
	 * Constructeur pour créer un fix depuis neo4j, ne pas utiliser en dehors de neo4j.
	 * 
	 * @param isUpdate si c'est un fix de type "fixed in X.X.X"
	 * @param update la donnée du fix
	 */
	public Fix(boolean isUpdate,String update){
		this.isUpdate = isUpdate;
		this.data = update;
	}
	
	
	/**
	 * sauvegarde le fix dans la base de données.
	 */
	public void save(){
		Neo4j.getInstance().addFix(this);
	}
	
}
