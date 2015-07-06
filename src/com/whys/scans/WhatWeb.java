package com.whys.scans;

import com.whys.data.Scan;
import com.whys.listeners.ScanEndListener;
import com.whys.parsers.WhatwebParser;


public class WhatWeb extends Scan{
	public final static String NAME = "whatweb";
	public final static String CMD = "whatweb ";
	public final static String HUMANIZED_NAME = "détails du site";
	
	public WhatWeb() {
		super(NAME,CMD,HUMANIZED_NAME);
		this.duration = 10;
		this.addScanEndListener(new ScanEndListener() {			
			@Override
			public void onScanTerminated() {
				Parse();
			}
		});
	}
	
	private void Parse(){
		WhatwebParser wwp = new WhatwebParser(this);
		wwp.saveReport();
	}
}
