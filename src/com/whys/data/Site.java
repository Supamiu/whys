package com.whys.data;

import com.whys.database.Neo4j;

public class Site {
	
	public String name;
	public String url;
	public boolean confirmed;
	public int id;
	public int nbScans = 0;
	public String type = "N/A";
		
	public Site(String name, String url, boolean confirmed, int id){
		this.name = name;
		this.url = url;
		this.confirmed = confirmed;
		this.id = id;
		this.type = Neo4j.getInstance().getSiteType(this);
	}
	
	public String getType(){
		return this.type;
	}
	
	public void refresh(){
		this.type = Neo4j.getInstance().getSiteType(this);
	}
	
	public boolean isConfirmed(){
		return this.confirmed;
	}
}
