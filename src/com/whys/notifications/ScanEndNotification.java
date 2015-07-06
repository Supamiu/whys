package com.whys.notifications;

import com.vaadin.ui.Notification;
import com.whys.data.Scan;

@SuppressWarnings("serial")
public class ScanEndNotification extends Notification{

	public ScanEndNotification(Scan scan) {
		super("Scan terminé","<div style='color:black'>Le scan "+scan.getHumanizedName()+" vient de se terminer</div>", Type.TRAY_NOTIFICATION, true);
		this.setDelayMsec(2000);
	}


}
