package org.semanticweb.cogExp.OWLAPIVerbaliser;

import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTextPane;

public class NoRulesElement extends TextElement{
	/**
	 * Special TextElement that indicates that there's nothing to explain. 
	 * It comes in action if e.g. there is no SequentInferenceRule for 
	 * the axiom.
	 * It is supposed to facilitate coloring and such like.
	 */
	
	/*TODO maybe an extra "Failure!" Element should be implemented
	 * 
	 */

	/**
	 * this constructor loads the TextElement with the string
	 * "That's already stated in the ontology. " and adds an explanation at the end. 
	 */
	/**
	 * @param explanation is added at the end
	 */
	public NoRulesElement(String explanation) {
		this.content = "That's already stated in the ontology. " + explanation;
	}
	

	/* (non-Javadoc)
	 * @see org.semanticweb.cogExp.OWLAPIVerbaliser.TextElement#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	

	/* (non-Javadoc)
	 * @see org.semanticweb.cogExp.OWLAPIVerbaliser.TextElement#toHTML()
	 */
	@Override
	public String toHTML() {
		// TODO Auto-generated method stub
		return super.toHTML();
	}
	

	/* (non-Javadoc)
	 * @see org.semanticweb.cogExp.OWLAPIVerbaliser.TextElement#setContent(java.lang.String)
	 */
	@Override
	public void setContent(String content) {
		// TODO Auto-generated method stub
		super.setContent(content);
	}
	

	/* (non-Javadoc)
	 * @see org.semanticweb.cogExp.OWLAPIVerbaliser.TextElement#addToDocument(javax.swing.JTextPane)
	 */
	@Override
	public void addToDocument(JTextPane textPane) {
		// TODO Auto-generated method stub
		super.addToDocument(textPane);
	}
	

	/* (non-Javadoc)
	 * @see org.semanticweb.cogExp.OWLAPIVerbaliser.TextElement#toJLabel()
	 */
	@Override
	public List<JLabel> toJLabel() {
		// TODO Auto-generated method stub
		return super.toJLabel();
	}

}