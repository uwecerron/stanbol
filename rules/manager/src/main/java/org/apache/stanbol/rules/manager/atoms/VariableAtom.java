package org.apache.stanbol.rules.manager.atoms;

import java.net.URI;

import org.apache.stanbol.rules.base.api.URIResource;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import org.apache.stanbol.rules.base.SWRL;

public class VariableAtom implements URIResource {

	private URI uri;
	private boolean negative;
	
	public VariableAtom(URI uri, boolean negative) {
		this.uri = uri;
		this.negative = negative;
	}

	@Override
	public URI getURI() {
		return uri;
	}

		@Override
	public Resource createJenaResource(Model model) {
		return model.createResource(uri.toString(), SWRL.Variable);
	}
		
	@Override
	public String toString() {
		
		return uri.toString();
		
	}
	
	public boolean isNegative() {
		return negative;
	}
	
}