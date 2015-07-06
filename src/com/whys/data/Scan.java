package com.whys.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringEscapeUtils;

import com.vaadin.server.Page;
import com.whys.database.Neo4j;
import com.whys.listeners.ScanEndListener;
import com.whys.notifications.ScanEndNotification;
import com.whys.scans.ScriptEngine;
import com.whys.securityUtils.LogEngine;
import com.whys.utils.MailEngine;

/**
 * Superclasse de laquelle héritent tous les scans
 * 
 * @author Miu
 *
 */
public class Scan {
	
	protected String NAME;
	protected String CMD;
	protected String HUMANIZED_NAME;
	protected String TPS;
	protected String raw_data = "";
	protected boolean notifyByEmail = false;
	protected boolean enCours;
	protected String splitStringStart = null;
	protected String splitStringEnd = null;
	protected boolean addData = false;
	protected int id;
	protected Site target;
	protected LogEngine logs = new LogEngine("scans");
	protected int duration;
	protected User user;
	protected Neo4j neo = Neo4j.getInstance();
	protected boolean needErrorInData = false;
	private ArrayList<String> saveWithout = new ArrayList<String>();
	private Scan me = this;
	protected final Collection<ScanEndListener> scanEndListeners = new ArrayList<ScanEndListener>();
	ScriptEngine se = new ScriptEngine() {
		@Override
		public void onStandardOutput(String data) {
			//à decommenter pour tester de nouveaux scans
			//System.out.println(data);
			if(addData == false){
				if(splitStringStart == null){
					if(splitStringEnd == null){
						addData = true;
						raw_data+="#~#"+data;
					}else if(data.contains(splitStringEnd)){
						addData = false;
					}
				}else if(data.contains(splitStringStart)){
					addData = true;
					raw_data += data;
				}
			}else{
				if(splitStringEnd != null){
					if(data.contains(splitStringEnd)){
						addData = false;
					}
				}else{
					raw_data+="#~#"+data;
				}
			}
		}
		@Override
		public void onErrorOutput(String data) {
			//à décommenter pour tester de nouveaux scans
			//System.out.println("ERROR : "+data);			
			if(addData == false){
				if(splitStringStart == null){
					if(splitStringEnd == null){
						addData = true;
						raw_data+="#~#"+data;
					}else if(data.contains(splitStringEnd)){
						addData = false;
					}
				}else if(data.contains(splitStringStart)){
					addData = true;
					raw_data += data;
				}
			}else{
				if(splitStringEnd != null){
					if(data.contains(splitStringEnd)){
						addData = false;
					}
				}else{
					raw_data+="#~#"+data;
				}
			}
		}
		@Override
		public void onScriptEnd() {
			if(duration > 120){
				notifyByEmail();
			}
			saveResults();			
		}
	};
	
	/**
	 * Constructeur pour les scans qui vont être lancés
	 * 
	 * @param NAME Le nom du scan
	 * @param CMD La commande pour lancer le scan
	 */
	public Scan(String NAME, String CMD,String HUMANIZED_NAME){
		this.user = new User(true);
		this.NAME = NAME;
		this.CMD = CMD;
		this.HUMANIZED_NAME = HUMANIZED_NAME;
	}
	
	/**
	 * Vérifie si l'utilisateur a demande à reçevoir un email, et l'envoi si besoin est
	 */
	private void notifyByEmail() {
		if(this.notifyByEmail){
			MailEngine.sendNoreplyMail(user.getEmail(), "WHYS - Scan terminé", "Le scan "+this.HUMANIZED_NAME+" sur le site "+target.name+" vient de se terminer");
		}
	}

	/**
	 * Constructeur pour les scans terminés récupérés de la bdd ou à 
	 * sauvegarder dans celle-cy
	 * 
	 * @param NAME Le nom du scan
	 * @param raw_data Les données qui ont été renvoyées par le scan
	 */
	public Scan(String NAME, String raw_data, int id){
		this.NAME = NAME;
		this.raw_data = raw_data;
		this.id = id;
	}
	
	/**
	 * 
	 * @return le nom du scan
	 */
	public String getName(){
		return this.NAME;
	}
	
	/**
	 * 
	 * @return l'id du scan, seulement si il existe dans la base de données
	 */
	public int getId(){
		return this.id;
	}
	
	/**
	 * Démarre le scan sur le site target
	 * 
	 * @param target Site qui est la cible du scan
	 */
	public void start(Site target){
		this.addScanEndListener(new ScanEndListener() {			
			@Override
			public void onScanTerminated() {
				ScanEndNotification end = new ScanEndNotification(me);
				end.show(Page.getCurrent());
			}
		});
		this.target = target;
		logs.log("scan "+this.NAME+" lancé sur le site "+target.name, true, true);
		this.id = neo.startScan(target, this);
		try {
			se.execute(this.CMD + " "+target.url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return le nom du scan tel qu'il doit être affiché à l'utilisateur
	 */
	public String getHumanizedName(){
		return this.HUMANIZED_NAME;
	}
	
	/**
	 * Retire les caractères coorespondants à des codes couleur Linux
	 * inscrire les résultats dans la base de données
	 * 
	 * Puis lance l'énènement scanTerminate sur les listeners qui lui sont associés
	 */
	private void saveResults() {
		String clean_data = getCleanData();
		neo.scanTerminated(this, clean_data);
		notifyListeners();
	}
	
	/**
	 * 
	 * @return les données brut de sortie du scan
	 */
	public String getRawData(){
		return raw_data;
	}
	
	/**
	 * Permet d'ajouter un listener sur la fin d'un scan
	 * 
	 * @param e le listener à ajouter
	 */
	public void addScanEndListener(ScanEndListener e){
		this.scanEndListeners.add(e);
	}
	
	/**
	 * notifie tous les listeners que le scan en cours est terminé
	 */
	private void notifyListeners(){
		for(ScanEndListener sl : scanEndListeners){
			sl.notified();
		}
		logs.log("scan "+this.NAME+" sur le site "+target.name+" terminé ", true, true);
	}
	
	/**
	 * 
	 * @return les données clean à partir du raw_data
	 * 
	 */
	public String getCleanData(){
		String clean_data = raw_data.replaceAll("\\[\\d+m", "");
		clean_data = clean_data.replaceAll("\\\\", " ");
		clean_data = StringEscapeUtils.escapeEcmaScript(clean_data);
		clean_data = clean_data.replaceAll("\\\\/", "/");
		for(String regex : saveWithout){
			clean_data = clean_data.replaceAll(regex, "");
		}
		return clean_data;
	}
	
	/**
	 * Définit les chaines de caractères qui doivent être exclues de la sauvegarde dans la BDD
	 * @param regex La RegEx qui correspond à la donnée qui ne doit pas être sauvée 
	 */
	protected void saveWithout(String regex){
		this.saveWithout.add(regex);
	}
	
	/**
	 * 
	 * @return true si le scan est en cours, false sinon
	 */
	public boolean isRunning(){
		return neo.isScanRunning(this);
	}
	
	/**
	 * 
	 * @return true si le scan est fini, false sinon
	 */
	public boolean isFinished(){
		return neo.isScanFinished(this);
	}

	/**
	 * 
	 * @return la cible du scan
	 */
	public Site getTarget(){
		return this.target;
	}
	
	/**
	 * ajoute la sortie error au raw_data
	 */
	public void addErrorInData(){
		this.needErrorInData = true;
	}
	
	/**
	 * ne récupère la sortie qu'après la chaine donnée
	 * @param splitString la chaine de caractère pour split
	 */
	public void onlySaveAfter(String splitString){
		this.splitStringStart = splitString;
	}
	
	/**
	 * ne récupère la sortie qu'avant la chaine donnée 
	 * @param splitString la chaine de caractère pour split
	 */
	public void onlySaveBefore(String splitString){
		this.splitStringEnd = splitString;
	}

	/**
	 * définit si l'utilisateur doit reçevoir un email une fois le scan terminé
	 * @param b
	 */
	public void setEmailNotif(boolean b) {
		this.notifyByEmail = true;		
	}
	
	/**
	 * 
	 * @return la durée estimée du scan (pour les notifications d'envoi email)
	 */
	public int getDuration(){
		return this.duration;
	}

}
