package org.apache.stanbol.rules.manager;

import org.apache.stanbol.rules.base.api.SPARQLObject;

public class SPARQLTriple implements SPARQLObject {

	
	private String triple;
	
	public SPARQLTriple(String triple) {
		this.triple = triple;
	}
	
	@Override
	public String getObject() {
		return triple;
	}

	
}