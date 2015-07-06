package com.whys.components;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;

public abstract class InfoButton extends Button{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InfoButton(String caption){
		this.setIcon(FontAwesome.INFO);
		this.setCaption(caption);
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
	}
	
	public abstract void onClick();
}
