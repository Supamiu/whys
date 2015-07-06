package com.whys.database;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.query.QueryEngine;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

import com.vaadin.server.VaadinSession;
import com.whys.data.Fix;
import com.whys.data.Flaw;
import com.whys.data.Info;
import com.whys.data.Scan;
import com.whys.data.User;
import com.whys.exceptions.LoginException;
import com.whys.exceptions.ScanNotFoundException;
import com.whys.data.Site;
import com.whys.securityUtils.Hash;
import com.whys.securityUtils.LogEngine;

@SuppressWarnings("unchecked")
public class Neo4j {
	private String urlRest;
	private String url = "http://localhost:7575";
	private QueryEngine<?> engine;
	private static Neo4j INSTANCE = new Neo4j();
	
	private Neo4j(){
		if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)
		{
			url = "http://127.0.0.1:7474";
		}
		urlRest = url+"/db/data";
		final RestAPI graphDb = new RestAPIFacade(urlRest);
		engine = new RestCypherQueryEngine(graphDb);
	}
	
	public static Neo4j getInstance(){
		if (INSTANCE == null)
		{ 
			INSTANCE = new Neo4j();	
		}
		return INSTANCE;
	}
	
	public void loginUser(String username, String password) throws LoginException{
		new Hash();
		String pass = Hash.sha256(password);
		QueryResult<?> result= engine.query("Match (u:User{login:'"+escapeQuotes(username)+"',password:'"+pass+"'}) return u.login as pseudo, u.name as prenom, u.lastname as nom, u.email as email, u.active as active, u._id as id", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		if(iterator.hasNext())
		{
			Object row = iterator.next();
			if(!Boolean.valueOf(((Map<String, Object>) row).get("active").toString())==true){
				throw new LoginException("Compte non activé, merci de vérifier votre boîte mail ou nous contacter");
			}
			else
			{
				VaadinSession.getCurrent().setAttribute("name", ((Map<String, Object>) row).get("prenom").toString());
				VaadinSession.getCurrent().setAttribute("lastName", ((Map<String, Object>) row).get("nom").toString());
				VaadinSession.getCurrent().setAttribute("pseudo", ((Map<String, Object>) row).get("pseudo").toString());
				VaadinSession.getCurrent().setAttribute("email", ((Map<String, Object>) row).get("email").toString());
				VaadinSession.getCurrent().setAttribute("id", ((Map<String, Object>) row).get("id").toString());
				Calendar cal = Calendar.getInstance();
				String SessionID = Hash.sha256(((Map<String, Object>) row).get("pseudo").toString()+Hash.sha256(password)+Hash.sha256(Integer.valueOf(cal.get(Calendar.HOUR_OF_DAY)).toString()));
				VaadinSession.getCurrent().setAttribute("SessionID", SessionID);
				engine.query("Match (u:User{login:'"+username+"',password:'"+pass+"'}) set u.sessionID = '"+SessionID+"'", null);
			}
			
		}
		else
		{
			throw new LoginException("Nom d'utilisateur ou mot de passe incorrect");
		}		
	}
	
	public void addUser(String login, String password, String name, String lastname, String cellNum, String email){
		int id;
		QueryResult<?> result= engine.query("Match (s:User) return max(s._id) as idmax", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		try{
			Object row = iterator.next();
			id=Integer.valueOf(((Map<String, Object>) row).get("idmax").toString())+1;
		}catch(NoSuchElementException e){
			id=0;
		}
		//On créé le futur token d'activation
		SecureRandom random = new SecureRandom();
		String userToken = new BigInteger(130, random).toString(32);
		new Hash();
		//on Hash le mot de passe en SHA-256, c'est un minimum
		String pass = Hash.sha256(password);
		engine.query("CREATE (u:User{email:'"+escapeQuotes(email)+"',login:'"+escapeQuotes(login)+"', password:'"+pass+"', name:'"+escapeQuotes(name)+"', lastname:'"+escapeQuotes(lastname)+"', cell:'"+escapeQuotes(cellNum)+"', activationToken:'"+userToken+"', active:false, _id:"+id+"})", null);
	}
	
	public ArrayList<User> getUsers(){
		ArrayList<User> users = new ArrayList<User>();
		QueryResult<?> result= engine.query("Match (u:User)"
				+ " return u.name as name, u._id as id, u.lastname as lastname, u.email as email, u.cell as cell, u.login as pseudo, u.active as active, case when u.admin is null then false else u.admin end as admin order by u._id", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		Object row;
		while(iterator.hasNext())
		{
			row = iterator.next();
			User user = new User(((Map<String, Object>) row).get("name").toString(),
					((Map<String, Object>) row).get("lastname").toString(),
					((Map<String, Object>) row).get("pseudo").toString(),
					((Map<String, Object>) row).get("email").toString(),
					((Map<String, Object>) row).get("cell").toString(),
					Integer.valueOf(((Map<String, Object>) row).get("id").toString()),
					Boolean.valueOf(((Map<String, Object>) row).get("active").toString()), 
					Boolean.valueOf(((Map<String, Object>) row).get("admin").toString()));
			users.add(user);
		}
		return users;
	}
	
	public void setUserActive(User user, boolean active){
		engine.query("Match (u:User{_id:"+user.getId()+"}) set u.active = "+active, Collections.EMPTY_MAP);
	}
	
	public void deleteUser(User u){
		engine.query("optional Match (u:User{_id:"+u.getId()+"})-[r:0..8]->(c) delete u,r,c", Collections.EMPTY_MAP);
		
	}
	
	@SuppressWarnings({ "rawtypes" })
	public String getServerStatus(){
		String status;
		QueryResult<?> result;
		result = engine.query("Match (x:serverStatus) return x.status as status", Collections.EMPTY_MAP);
		//Formate les données recuperées dans iterator
		Iterator<?> iterator=result.iterator();
		
		Object row = iterator.next();
		
		status = ((Map) row).get("status").toString();
		
		return status;
	}
	
	public void createNewSite(String name, String url){
		int id;
		//Si l'utilisateur a rentré "http://" au début de son url, vire cette partie de l'url avant de la stocker
		url = url.substring(0, 7).equals("http://")?url.substring(7):url;
		url = escapeQuotes(url).replaceAll("\\\\/", "\\/");
		try{
			name = name.substring(0, 7).equals("http://")?name.substring(7):name;
		}catch(StringIndexOutOfBoundsException e){}
		
		
		String userMail = VaadinSession.getCurrent().getAttribute("email").toString();		
		QueryResult<?> result= engine.query("Match (s:Site) return max(s._id) as idmax", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		try{
			Object row = iterator.next();
			id = Integer.valueOf(((Map<String, Object>) row).get("idmax").toString())+1;
		}catch(NoSuchElementException e){
			id = 0;
		}
		engine.query("Match (u:User{email:'"+userMail+"'}) with u"
				+ " CREATE (u)-[:OWNS]->(s:Site{url:'"+url+"', name:'"+escapeQuotes(name)+"', confirmed:false, _id:"+id+"})",Collections.EMPTY_MAP);
		new LogEngine("sites").log("site "+name+" créé avec l'id "+id, true, true);
	}
	
	public void deleteSite(int id){
		if(countScansOfSite(id)>0){
			String query = "Match (u:User{_id:"+VaadinSession.getCurrent().getAttribute("id")+"}) with u"
					+ " optional Match (u)-[r0:OWNS]->(s:Site{_id:"+id+"})-[r]->(sc)-[a]->(i) delete a,i";
			String query2 = "Match (u:User{_id:"+VaadinSession.getCurrent().getAttribute("id")+"}) with u"
					+ " optional Match (u)-[r0:OWNS]->(s:Site{_id:"+id+"})-[r]->(sc) delete r0,s,r,sc";
			engine.query(query,Collections.EMPTY_MAP);
			engine.query(query2,Collections.EMPTY_MAP);
		}
		else
		{
			String query = "Match (u:User{_id:"+VaadinSession.getCurrent().getAttribute("id")+"}) with u"
					+ " Match (u)-[r0:OWNS]->(s:Site{_id:"+id+"}) delete r0,s";
			engine.query(query,Collections.EMPTY_MAP);
		}
		
		new LogEngine("sites").log("site avec l'id "+id+" supprimé", true, true);
	}

	public boolean checkEmailAvailability(String text) {
		QueryResult<?> result= engine.query("optional Match (u:User{email:'"+text+"'}) return case when u is null then true else false end as valid", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		Object row = iterator.next();
		return Boolean.valueOf(((Map<String, Object>) row).get("valid").toString());
	}
	
	public boolean checkUsernameAvailability(String username){
		QueryResult<?> result= engine.query("optional Match (u:User{username:'"+username+"'}) return case when u is null then true else false end as valid", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		Object row = iterator.next();
		return Boolean.valueOf(((Map<String, Object>) row).get("valid").toString());
	}
	
	public String escapeQuotes(String source){
		String result = StringEscapeUtils.escapeEcmaScript(source);
		return result;
	}

	public void loginUserWithSID(String value) throws LoginException {
		QueryResult<?> result= engine.query("Match (u:User{sessionID:'"+value+"'}) return u.login as pseudo, u.name as prenom, u.lastname as nom, u.email as email, u.active as active, u._id as id", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		if(iterator.hasNext())
		{
			Object row = iterator.next();
			if(!Boolean.valueOf(((Map<String, Object>) row).get("active").toString())){
				throw new LoginException("Aucun utilisateur avec ce SessionID");
			}
			else
			{
				VaadinSession.getCurrent().setAttribute("name", ((Map<String, Object>) row).get("prenom").toString());
				VaadinSession.getCurrent().setAttribute("lastName", ((Map<String, Object>) row).get("nom").toString());
				VaadinSession.getCurrent().setAttribute("pseudo", ((Map<String, Object>) row).get("pseudo").toString());
				VaadinSession.getCurrent().setAttribute("email", ((Map<String, Object>) row).get("email").toString());
				VaadinSession.getCurrent().setAttribute("id", ((Map<String, Object>) row).get("id").toString());
				VaadinSession.getCurrent().setAttribute("SessionID", value);
			}
		}
	}

	public Site getSite(int id) {
		QueryResult<?> result= engine.query("Match (u:User{_id:"+VaadinSession.getCurrent().getAttribute("id")+"})-[:OWNS]->(s:Site{_id:"+id+"})"
				+ " return s.name as name, s.url as url, s.confirmed as confirmed, s._id as id order by s._id", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		Object row;
		row = iterator.next();
		String name = ((Map<String, Object>) row).get("name").toString();
		String url = ((Map<String, Object>) row).get("url").toString();
		boolean confirmed = Boolean.valueOf(((Map<String, Object>) row).get("confirmed").toString());
		Site site = new Site(name, url, confirmed, id);
		return site;
	}
	
	public void setSiteType(Site site, String type){
		engine.query("Match (s:Site{_id:"+site.id+"}) set s.type='"+type+"'", Collections.EMPTY_MAP);
	}
	
	public String getSiteType(Site site){
		QueryResult<?> result= engine.query("Match(s:Site{_id:"+site.id+"}) return s.type as type", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		Object row;
		row = iterator.next();
		String type;
		try{
			type = ((Map<String, Object>) row).get("type").toString();			
		}catch(NullPointerException e){
			type = "N/A";
		}
		return type;
	}
	
	public ArrayList<Site> getSites(){
		ArrayList<Site> sites = new ArrayList<Site>();
		QueryResult<?> result= engine.query("Match (u:User{_id:"+VaadinSession.getCurrent().getAttribute("id")+"})-[:OWNS]->(s:Site)"
				+ " return s.name as name, s.url as url, s.confirmed as confirmed, s._id as id order by s._id", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		Object row;
		while(iterator.hasNext())
		{
			row = iterator.next();
			String name = ((Map<String, Object>) row).get("name").toString();
			String url = ((Map<String, Object>) row).get("url").toString();
			boolean confirmed = Boolean.valueOf(((Map<String, Object>) row).get("confirmed").toString());
			int id = Integer.valueOf(((Map<String, Object>) row).get("id").toString());
			Site site = new Site(name, url, confirmed, id);
			sites.add(site);
		}		
		
		return sites;		
	}
	
	public int countSites(){
		int count = 0;
		QueryResult<?> result= engine.query("Match (u:User{_id:"+VaadinSession.getCurrent().getAttribute("id")+"})-[:OWNS]->(s:Site)"
				+ " return s", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		while(iterator.hasNext())
		{
			iterator.next();
			count++;
		}	
		return count;
	}
	
	public int countScansOfSite(int id){
		String query = "match (s:Site{_id:"+id+"})-[:SCANS]->(sc:Scan) return count(sc) as result";
		QueryResult<?> result= engine.query(query, Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		Object row = iterator.next();
		return Integer.valueOf(((Map<String, Object>) row).get("result").toString());
	}

	public ArrayList<Scan> getScansOfSite(Site site){
		ArrayList<Scan> scans = new ArrayList<Scan>();
		QueryResult<?> result= engine.query("Match (s:Site{_id:"+site.id+"})-[:SCANS]->(sc:Scan)"
				+ " return  sc.type as type, sc.rawData as data, sc.enCours as enCours, sc._id as id order by sc._id", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		Object row;
		while(iterator.hasNext())
		{
			row = iterator.next();
			String name = ((Map<String, Object>) row).get("type").toString();
			String data = ((Map<String, Object>) row).get("data").toString();
			int id = Integer.valueOf(((Map<String, Object>) row).get("id").toString());
			Scan scan = new Scan(name, data, id);
			scans.add(scan);
		}
		return scans;
	}
	
	public int countFlaws(Site site) {
		String query = "match (s:Site{_id:"+site.id+"})-[:SCANS]->(sc:Scan)-[:FOUND]->(f:Flaw) return count(f) as result";
		QueryResult<?> result= engine.query(query, Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		Object row = iterator.next();
		return Integer.valueOf(((Map<String, Object>) row).get("result").toString());
	}
/**
 * Créé un nouveau scan dans la base de données avec le propriété enCours=true, et aucun data de sortie
 * 
 * @param target Le site cible du scan
 * @param scan	Le scan qui est lancé
 * @return l'id du nouveau scan dans la base de données
 */
	public int startScan(Site target,Scan scan){
		int id;
		QueryResult<?> result= engine.query("Match (sc:Scan) return max(sc._id) as idmax", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		try{
			Object row = iterator.next();
			id = Integer.valueOf(((Map<String, Object>) row).get("idmax").toString())+1;
		}catch(NoSuchElementException e){
			id=0;
		}
		String query = "Match (s:Site{_id:"+target.id+"}) with s"
				+ " Create (s)-[:SCANS]->(sc:Scan{_id:"+id+",type:'"+scan.getName()+"',enCours:true})";
		engine.query(query, Collections.EMPTY_MAP);
		return id;
	}
	
	public void scanTerminated(Scan scan, String resultData){
		engine.query("Match (sc:Scan{_id:"+scan.getId()+"}) set sc.enCours=false, sc.raw_data='"+resultData+"'", Collections.EMPTY_MAP);
	}
	
	public Scan getScanOfSite(Site site, String scanName) throws ScanNotFoundException {
		Scan scan = null;
		QueryResult<?> result= engine.query("Match (s:Site{_id:"+site.id+"})-[:SCANS]->(sc:Scan{type:'"+scanName+"'})"
				+ " return  sc.type as type, sc.raw_data as data, sc.enCours as enCours, sc._id as id order by sc._id", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		if(iterator.hasNext())
		{
			Object row = iterator.next();
			String name = ((Map<String, Object>) row).get("type").toString();
			String data = "";
			try{
				data = ((Map<String, Object>) row).get("data").toString();
			}catch(NullPointerException e){
			}
			int id = Integer.valueOf(((Map<String, Object>) row).get("id").toString());
			scan = new Scan(name, data, id);
		}else{
			throw new ScanNotFoundException();
		}
		return scan;
	}
	
	public void confirmSite(Site target) {
		engine.query("Match (s:Site{_id:"+target.id+"}) set s.confirmed = true", Collections.EMPTY_MAP);		
	}
	
	public String getActivationToken(Site site){
		QueryResult<?> result= engine.query("Match (u:User)-[:OWNS]->(s:Site{_id:"+site.id+"}) return u.activationToken as token", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		Object row = iterator.next();
		return ((Map<String, Object>) row).get("token").toString();
	}
	
	public void addInfo(Info info){
		engine.query("Match (sc:Scan{_id:"+info.scan.getId()+"}) with sc"
				+ " Create (sc)-[:FOUND]->(i:Info{name:'"+info.name+"', data:'"+info.data+"'})", Collections.EMPTY_MAP);
	}
	
	public ArrayList<Info> getScanInfos(Scan scan){
		ArrayList<Info> infos = new ArrayList<Info>();
		QueryResult<?> result= engine.query("Match (sc:Scan{_id:"+scan.getId()+"})-[:FOUND]->(i:Info) return i.name as name, i.data as data", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		Object row;
		while(iterator.hasNext())
		{
			row = iterator.next();
			String name = ((Map<String, Object>) row).get("name").toString();
			String data = ((Map<String, Object>) row).get("data").toString();
			Info info = new Info(name,data);
			infos.add(info);
		}
		return infos;
	}
	
	public Info getScanInfo(Scan scan, String infoName){
		QueryResult<?> result= engine.query("Match (sc:Scan{_id:"+scan.getId()+"})-[:FOUND]->(i:Info{name:'"+infoName+"'}) return i.data as data", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		Info info = null;
		try{
			Object row = iterator.next();
			info = new Info(infoName,((Map<String, Object>) row).get("data").toString());
		}catch(NoSuchElementException e){
			info = new Info(infoName, "nullData");
		}
		return info;
	}
	
	public ArrayList<Info> getScanInfosByName(Scan scan, String infoName){
		ArrayList<Info> infos = new ArrayList<Info>();
		QueryResult<?> result= engine.query("Match (sc:Scan{_id:"+scan.getId()+"})-[:FOUND]->(i:Info{name:'"+infoName+"'}) return i.data as data", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		Object row;
		while(iterator.hasNext())
		{
			row = iterator.next();
			String name = infoName;
			String data = ((Map<String, Object>) row).get("data").toString();
			Info info = new Info(name,data);
			infos.add(info);
		}
		return infos;
	}
	
	public void addFlaw(Flaw flaw) {
		engine.query("Match (sc:Scan{_id:"+flaw.scan.getId()+"}) with sc"
				+ " Create (sc)-[:FOUND]->(f:Flaw{name:'"+flaw.name+"',data:'"+flaw.data+"'})",Collections.EMPTY_MAP);
	}
	
	public ArrayList<Flaw> getScanFlaws(Scan scan){
		ArrayList<Flaw> flaws = new ArrayList<Flaw>();
		QueryResult<?> result= engine.query("Match (sc:Scan{_id:"+scan.getId()+"})-[:FOUND]->(f:Flaw) return f.name as name, f.data as data", Collections.EMPTY_MAP);
		Iterator<?> iterator=result.iterator();
		Object row;
		while(iterator.hasNext())
		{
			row = iterator.next();
			String name = ((Map<String, Object>) row).get("name").toString();
			String data = ((Map<String, Object>) row).get("data").toString();
			Flaw flaw = new Flaw(name,data);
			flaws.add(flaw);
		}		
		return flaws;
	}

	public void addFix(Fix fix){
		engine.query("match (sc:Scan[{_id:"+fix.flaw.scan.getId()+"})-[:FOUND]->(fl:Flaw{_id:'"+fix.flaw+"'}) with fl"
				+ " Create (fl)-[:FIXED]->(f:Fix{isUpdate:'"+fix.isUpdate+"',data:'"+fix.data+"'})", Collections.EMPTY_MAP);
	}

	public boolean isScanRunning(Scan scan) {
		QueryResult<?> result= engine.query("optional Match (sc:Scan{_id:"+scan.getId()+"}) return case when sc.enCours is null then false else sc.enCours end as result", Collections.EMPTY_MAP);
		Object row = result.iterator().next();
		boolean res = Boolean.valueOf(((Map<String, Object>) row).get("result").toString());
		return res;
	}

	public boolean isScanFinished(Scan scan) {
		QueryResult<?> result= engine.query("optional Match (sc:Scan{_id:"+scan.getId()+"}) return case when sc.enCours=true then false when sc.enCours is null then false else true end as result", Collections.EMPTY_MAP);
		Object row = result.iterator().next();
		return Boolean.valueOf(((Map<String, Object>) row).get("result").toString());
	}

	public String getNews(){
		QueryResult<?> result= engine.query("Match (n:News) return n.content as result", Collections.EMPTY_MAP);
		Object row = result.iterator().next();
		return ((Map<String, Object>) row).get("result").toString();
	}

	public boolean isUserAdmin(int id) {
		QueryResult<?> result= engine.query("optional Match (u:User{_id:"+id+"}) return case when u.admin=true then true else false end as result", Collections.EMPTY_MAP);
		Object row = result.iterator().next();
		return Boolean.valueOf(((Map<String, Object>) row).get("result").toString());
	}
	
	public void setNews(String data){
		data = escapeQuotes(data);
		data = data.replaceAll("\\\\/", "\\/");
		engine.query("Match (n:News) set n.content = '"+data+"'",Collections.EMPTY_MAP);
	}

	public void deleteScan(Scan scan) {
		engine.query("optional Match (u:User)-[r1:SCANS]->(sc:Scan{_id:"+scan.getId()+"})-[r2:FOUND]->(a) delete r1,sc,r2,a", Collections.EMPTY_MAP);		
	}
}
