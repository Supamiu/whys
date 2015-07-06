package com.whys.views;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.whys.database.Neo4j;
import com.whys.main.WhysUI;
import com.whys.modals.Information;
import com.whys.securityUtils.LogEngine;
import com.whys.staticDatas.CGU;
import com.whys.utils.MailEngine;
import com.whys.validators.UserValidator;

@SuppressWarnings("serial")
public class SignIn extends CustomComponent implements View{
	public static final String NAME = "inscription";
	
	//On prépare nos inputs
	private final TextField user = new TextField();
	private final TextField name = new TextField();
	private final TextField lastName = new TextField();
	private final TextField cellNum = new TextField();
	private final TextField email = new TextField();
	private final TextField emailV = new TextField();
	private final PasswordField passwd = new PasswordField();
	private final PasswordField passwdV = new PasswordField();
	private final Button confirm = new Button("S'inscrire");
	private boolean valid = false;
	
	private Navigator navigator = UI.getCurrent().getNavigator();
	private LogEngine logs = new LogEngine("inscriptions");
	
	public SignIn(){
		//On prépare le titre de la page
		Label whys = new Label("<div align='center'><h1>WHYS</h1>"+WhysUI.VERSION+"</div>", ContentMode.HTML);
		whys.setSizeFull();		
		//Des verticallayout pour mettre tout ça en deux colonnes
		VerticalLayout left = new VerticalLayout();
		VerticalLayout right = new VerticalLayout();
		HorizontalLayout fields = new HorizontalLayout();
		
		left.addComponents(name,user,email,passwd);
		right.addComponents(lastName,cellNum,emailV,passwdV);
		
		left.setSpacing(true);
		left.setMargin(new MarginInfo(true, false, true, true));
		left.setStyleName("fixed-width-caption");
		
		right.setSpacing(true);
		right.setMargin(new MarginInfo(true, false, true, false));
		right.setStyleName("fixed-width-caption");
		
		fields.setSpacing(true);
		fields.addComponents(left,right);
		
		setSizeFull();
		VerticalLayout vl = new VerticalLayout();
		Panel panel = new Panel("Inscription");
		VerticalLayout form = new VerticalLayout();
		form.setImmediate(true);
		vl.setImmediate(true);
		panel.setContent(form);
		panel.setImmediate(true);
		panel.setSizeUndefined();
		form.setMargin(true);
		form.setSpacing(true);
		vl.addComponent(whys);
		vl.addComponent(panel);		
		vl.setComponentAlignment(panel, Alignment.TOP_CENTER);		
		vl.setHeight("500px");
		vl.setWidth("100%");
		setCompositionRoot(vl);
		
		user.setInputPrompt("Pseudo");
		user.setRequired(true);
		name.setInputPrompt("Prénom");
		name.setRequired(true);
		lastName.setInputPrompt("Nom");
		lastName.setRequired(true);
		cellNum.setInputPrompt("Téléphone");
		email.setInputPrompt("Email");
		email.setRequired(true);
		emailV.setInputPrompt("Confirmation Email");
		emailV.setRequired(true);
		passwd.setInputPrompt("Mot de Passe");
		passwd.setRequired(true);
		passwdV.setInputPrompt("Mot de Passe");
		passwdV.setRequired(true);
		
		confirm.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				String message = "erreur, veuillez nous contacter";
				try{
					name.validate();
					lastName.validate();
					passwdV.validate();
					passwd.validate();
					email.validate();
					valid = true;
				}catch(InvalidValueException e)
				{
					message = e.getMessage();
					valid = false;
				}
				if(valid){
					Neo4j.getInstance().addUser(user.getValue(), passwd.getValue(), name.getValue(), lastName.getValue(), cellNum.getValue(), email.getValue());
					MailEngine.sendNoreplyMail("alpha@whys.fr", "Inscription d'un utilisateur", "L'utilisateur "+user.getValue()+"s'est inscrit avec l'email "+email.getValue());
					logs.log(user.getValue()+" inscrit", true, false);
					new Information("Succès","Votre compte a été enregistré avec succès,<br/>Whys étant en phase d'alpha, un administrateur devra valider votre compte manuellement pour éviter toute inscription non voulue."){
						public void onConfirm() {
							navigator.navigateTo(Login.NAME);
						};
					};
				}else{
					message = message == "" ? "Merci de remplir tous les champs requis" : message;
					
					Notification error = new Notification(message,Type.ERROR_MESSAGE);
					error.setDelayMsec(3000);
					error.show(Page.getCurrent());
				}
			}
		});	
		confirm.setEnabled(false);
		confirm.setDescription("Vous devez d'abord lire les CGU");
		
		Button cgu = new Button("lire les CGU");
		cgu.addClickListener(new ClickListener() {			
			@Override
			public void buttonClick(ClickEvent event) {
				new Information("Condition Générales D'utilisation", CGU.DATA) {
					
					@Override
					public void onConfirm() {
						confirm.setDescription(null);
						confirm.setEnabled(true);
					}
				};
			}
		});
		cgu.setStyleName(ValoTheme.BUTTON_QUIET);
		
		setupValidators();
		setTabIndex();
		form.addComponents(fields,confirm,cgu);
		form.setComponentAlignment(confirm, Alignment.MIDDLE_CENTER);
		form.setComponentAlignment(cgu, Alignment.MIDDLE_CENTER);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}
	
	private void setupValidators(){
		user.addValidator(new UserValidator());
		
		email.addValidator(new EmailValidator("Vous devez entrer une adresse email valide"));
		
		email.addTextChangeListener(new FieldEvents.TextChangeListener() {
			public void textChange(TextChangeEvent event) {				
				//Si le mail existe
				if (!Neo4j.getInstance().checkEmailAvailability(event.getText())) {
					//Indique une erreur
					email.setComponentError(new UserError("Email déjà utilisé"));
					valid = false;
					//sinon
				} else {
					//efface l'indicateur d'erreur 
					email.setComponentError(null);
				}
			}
		});
		
		emailV.addTextChangeListener(new FieldEvents.TextChangeListener() {
			public void textChange(TextChangeEvent event) {				
				//Si le mail correspond
				if (!event.getText().equals(email.getValue().toString())) {
					//Indique une erreur
					emailV.setComponentError(new UserError("Les emails ne correspondent pas"));
					valid = false;
					//sinon
				} else {
					//efface l'indicateur d'erreur 
					emailV.setComponentError(null);
				}
			}
		});
		
		passwd.addValidator(new RegexpValidator("(?=.*\\d).{6,}","Votre mot de passe doit faire au minimum 6 caractères avec au moins un chiffre"));
		
		passwdV.addTextChangeListener(new FieldEvents.TextChangeListener() {
			public void textChange(TextChangeEvent event) {				
				//Si le mot de passe correspond
				if (!event.getText().equals(passwd.getValue().toString())) {
					//Indique une erreur
					passwdV.setComponentError(new UserError("Les mots de passe ne correspondent pas"));
					//sinon
				} else {
					//efface l'indicateur d'erreur 
					passwdV.setComponentError(null);
				}
			}
		});	
	}
	
	private void setTabIndex(){
		name.setTabIndex(1);
		lastName.setTabIndex(2);
		user.setTabIndex(3);
		cellNum.setTabIndex(4);
		email.setTabIndex(5);
		emailV.setTabIndex(6);
		passwd.setTabIndex(7);
		passwdV.setTabIndex(8);		
	}
}
