package com.whys.validators;

import com.vaadin.data.Validator;
import com.whys.database.Neo4j;

public class EmailFreeValidator implements Validator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static Neo4j neo = Neo4j.getInstance();
	
	@Override
	public void validate(Object value) throws InvalidValueException {
			if(neo.checkEmailAvailability(value.toString())){
				throw new InvalidValueException("Email déjà utilisé");
			}
	}

}
