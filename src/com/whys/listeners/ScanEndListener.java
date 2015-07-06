package com.whys.listeners;

import com.vaadin.ui.UI;

/**
 * 
 * @author Miu
 * 
 * Listener pour les évènements de fin de scan
 *
 */

public abstract class ScanEndListener {

	public void notified(){
		UI.getCurrent().access(new Runnable() {
            @Override
            public void run() {
                onScanTerminated();
            }
        });
	}
	
	public abstract void onScanTerminated();	
	
}
