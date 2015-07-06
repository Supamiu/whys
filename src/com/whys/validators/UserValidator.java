package com.whys.validators;

import com.vaadin.data.Validator;
import com.whys.database.Neo4j;

public class UserValidator implements Validator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private final static Neo4j neo = Neo4j.getInstance();
	
	@Override
	public void validate(Object value) throws InvalidValueException {
		if(!neo.checkUsernameAvailability(value.toString())){
			throw new InvalidValueException("Pseudo déjà utilisé");
		}
	}

}
