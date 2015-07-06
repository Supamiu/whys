package com.whys.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.whys.data.Info;
import com.whys.data.Parser;
import com.whys.scans.UniscanDirectory;

public class UniscanDirectoryParser extends Parser {
	private static final Pattern p = Pattern.compile("CODE: 200 URL: ([^#~#]+)");

	public UniscanDirectoryParser(UniscanDirectory uniscan){
		String input = uniscan.getCleanData();
		Matcher m = p.matcher(input);
		
		while(m.find()){
			Info info = new Info("Dossier trouvé", m.group(1), uniscan);
			this.infos.add(info);
		}
	}
}
