package com.whys.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.*;

public class parserWP {

	private Pattern pattern;
	private Matcher matcher;
	private ArrayList<String> minor;
	private ArrayList<String> plugin;
	private ArrayList<String> rift;
	private ArrayList<String> ref;
	private ArrayList<String> fixed;
	private ArrayList<ArrayList<String>> riftName;
	private ArrayList<ArrayList<String>> riftNb;
	private int riftFoundByRift;
	
	parserWP(String text) {
		minor = new ArrayList<String>();
		plugin = new ArrayList<String>();
		rift = new ArrayList<String>();
		ref = new ArrayList<String>();
		fixed = new ArrayList<String>();
		riftName = new ArrayList<ArrayList<String>>();
		riftNb = new ArrayList<ArrayList<String>>();
		riftFoundByRift = 0;

		/*
		 * dictionnaire de donn�es nom de faille
		 */
		
		File file = new File("C:/Users/Loic/Desktop/parserWP.config");
		try {
			InputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line;
			while ((line = br.readLine()) != null) {
				ArrayList<String> correspondanceRiftName = new ArrayList<String>();
				ArrayList<String> correspondanceRiftNb = new ArrayList<String>();
				String name = line.split(":")[0];
				String value = line.split(":")[1];
				correspondanceRiftName.add(name);
				correspondanceRiftNb.add(name);
				if (!riftNb.contains(correspondanceRiftNb))
					riftNb.add(correspondanceRiftNb);
				correspondanceRiftName.add(value);
				riftName.add(correspondanceRiftName);
			}
			br.close();
		} catch (Exception e) {
			System.err.println("Error: Target File Cannot Be Read");
		}

		/*
		 * Parser
		 */
		pattern = Pattern.compile("(\\x{1B}\\[33m\\[!\\]\\x{1B}\\[\\dm(.*)|\\x{1B}\\[31m\\[!\\]\\x{1B}\\[\\dm(.*)((\\n[\\x{20}]{4}.*)*)(\\n\\x{1B}\\[34m\\[i\\]\\x{1B}\\[\\dm(.*))?)");
		matcher = pattern.matcher(text);
		while (matcher.find()) {
			riftFoundByRift = 0;

			if (matcher.group(2) != null) {
				minor.add(matcher.group(2).substring(1));
			} else {
				if (matcher.group(3) != null) {
					String temp = matcher.group(3).substring(8);
					if (temp.split("-").length > 1) {
						temp = temp.substring(temp.indexOf("-"));
						for (int i = 0; i < riftName.size(); i++) {
							ArrayList<String> test = riftName.get(i);
							if (temp.contains(test.get(1))) {
								for (int j = 0; j < riftNb.size();j++){
									if (test.get(1).equals(riftNb.get(j).get(0))){
										if (riftNb.get(j).size() == 2)
											riftNb.get(j).set(1,String.valueOf(Integer.parseInt(riftNb.get(j).get(1)) + 1));
										else
											riftNb.get(j).add("1");
									}
								}
								plugin.add(matcher.group(3).substring(8).split("-")[0]);
								rift.add(test.get(1));
								riftFoundByRift++;
							}
						}
					}
					if (matcher.group(4) != null) {
						for (int i = 0; i < riftFoundByRift; i++) {
							ref.add(matcher
									.group(4)
									.substring(1)
									.replaceAll(
											"(\\x{20}){4}Reference:\\x{20}", ""));
						}
					} else {
						for (int i = 0; i < riftFoundByRift; i++) {
							ref.add(null);
						}
					}
					if (matcher.group(7) != null) {
						for (int i = 0; i < riftFoundByRift; i++) {
							fixed.add(matcher.group(7).substring(11));
						}
					} else {
						for (int i = 0; i < riftFoundByRift; i++) {
							fixed.add(null);
						}
					}
				}
			}
		}

	}

	/*
	 * Methodes de r�cup�ration
	 */
	
	/**
	 * @param index
	 * @return Retourne l'am�lioration possible
	 */
	public String getMinor(int index) {
		return minor.get(index);
	}

	/**
	 * @param index
	 * @return Retourne le nom de la faille
	 */
	public String getPlugin(int index) {
		return plugin.get(index);
	}

	/**
	 * @param index
	 * @return Retourne le nom de la faille
	 */
	public String getRift(int index) {
		return rift.get(index);
	}

	/**
	 * @param index
	 * @return Retourne les liens permettant de corriger la faille
	 */
	public String getRef(int index) {
		return ref.get(index);
	}

	/**
	 * @param index
	 * @return Retourne un String contenant le num�ro de la version qui corrige
	 *         la faille, sinon null
	 */
	public String getFix(int index) {
		return fixed.get(index);
	}

	/**
	 * @return Retourne le nombre de faille d�tect�es
	 */
	public int getNbRift() {
		return plugin.size();
	}

	/**
	 * @return Retourne le nombre d'am�lioration d�tect�es
	 */
	public int getNbMinor() {
		return minor.size();
	}

	/**
	 * @return Retourne le nombre de type diff�rents
	 */
	public int getNbTypes() {

		return riftNb.size();
	}

	/**
	 * @return Retourne le nom du type
	 */
	public String getType(int index) {

		return riftNb.get(index).get(0);
	}

	/**
	 * @return Retourne le nombre d'occurence du type
	 */
	public int getTypeNb(int index) {

		return Integer.parseInt(riftNb.get(index).get(1));
	}
}
