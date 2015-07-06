package com.whys.components;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

public abstract class ScanStartButton extends Button {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ScanStartButton(){
		this.setIcon(FontAwesome.PLAY);
		this.setImmediate(true);
		this.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				onClick();
			}
		});
		this.setCaption("Lancer le scan");
		
		this.setStyleName(ValoTheme.BUTTON_FRIENDLY);
	}
	
	public abstract void onClick();

}
