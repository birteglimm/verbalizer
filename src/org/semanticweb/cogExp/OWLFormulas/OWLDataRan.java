package org.semanticweb.cogExp.OWLFormulas;



public enum OWLDataRan implements OWLAtom{
	BOOLEAN, DOUBLE, FLOAT, INTEGER, STRING; 
	
	
	@Override
	public boolean isSymb() {
		return false;
	}

	@Override
	public boolean isVar() {
		return false;
	}

	@Override
	public boolean isClassName() {
		return false;
	}

	@Override
	public boolean isIndividual() {
		return false;
	}
	
}
