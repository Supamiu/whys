package com.whys.utils;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public abstract class OnEnterKeyHandler {

	final ShortcutListener enterShortCut = new ShortcutListener(
                "EnterOnTextAreaShorcut", ShortcutAction.KeyCode.ENTER, null) {
                    @Override
                    public void handleAction(Object sender, Object target) {
                        onEnterKeyPressed();
                    }
                };

     public void installOn(final PasswordField password)
     {
        password.addFocusListener(
                new FieldEvents.FocusListener() {

                    @Override
                    public void focus(FieldEvents.FocusEvent event
                    ) {
                        password.addShortcutListener(enterShortCut);
                    }

                }
        );
     }
    public void installOn(final TextField field)
    {
    	field.addFocusListener(
               new FieldEvents.FocusListener() {

                   @Override
                   public void focus(FieldEvents.FocusEvent event
                   ) {
                	   field.addShortcutListener(enterShortCut);
                   }

               }
       );

    	field.addBlurListener(
                new FieldEvents.BlurListener() {

                    @Override
                    public void blur(FieldEvents.BlurEvent event
                    ) {
                    	field.removeShortcutListener(enterShortCut);
                    }

                }
        );
     }

     public abstract void onEnterKeyPressed();

}
