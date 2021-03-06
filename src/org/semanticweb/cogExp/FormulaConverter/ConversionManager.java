package org.semanticweb.cogExp.FormulaConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.cogExp.OWLAPIVerbaliser.OWLAPIManagerManager;
import org.semanticweb.cogExp.OWLAPIVerbaliser.VerbalisationManager;
import org.semanticweb.cogExp.OWLFormulas.OWLAtom;
import org.semanticweb.cogExp.OWLFormulas.OWLClassName;
import org.semanticweb.cogExp.OWLFormulas.OWLDataRan;
import org.semanticweb.cogExp.OWLFormulas.OWLFormula;
import org.semanticweb.cogExp.OWLFormulas.OWLIndividualName;
import org.semanticweb.cogExp.OWLFormulas.OWLInteger;
import org.semanticweb.cogExp.OWLFormulas.OWLLiteralType;
import org.semanticweb.cogExp.OWLFormulas.OWLLiteralValue;
import org.semanticweb.cogExp.OWLFormulas.OWLRoleName;
import org.semanticweb.cogExp.OWLFormulas.OWLDataPropertyName;
import org.semanticweb.cogExp.OWLFormulas.OWLObjectPropertyName;
import org.semanticweb.cogExp.OWLFormulas.OWLSymb;
import org.semanticweb.cogExp.PrettyPrint.PrettyPrintOWLObjectVisitor;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

public enum ConversionManager {
	INSTANCE;
	
	private static ConvertOWLObjectToOWLFormulaVisitor fromOWLAPIvisitor = new ConvertOWLObjectToOWLFormulaVisitor();
	
	/** Translates OWLAPI OWLObjects to (internal) OWLFormula representation
	 * 
	 * @param one OWLObject from OWLAPI
	 * @return the corresponding OWLFormula object
	 */
	public static OWLFormula fromOWLAPI(OWLObject owlobject){
		return owlobject.accept(fromOWLAPIvisitor);
	}
	
	/** converts the OWLFormula to OWL-API formula 
	 * 
	 * @return		the formula as (OWL-API) OWLObject
	 */
	public static OWLObject toOWLAPI(OWLFormula formula){
		OWLAtom head = formula.getHead();
		List<OWLFormula> tail = formula.getArgs();
		// System.out.println("TRANSFORMING:  " + this);
		OWLObject result;
		// OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory dataFactory=OWLAPIManagerManager.INSTANCE.getDataFactory();
		if (head.isSymb()){
			switch ((OWLSymb) head){
			case EQUIV:  	
				OWLClassExpression class1e = (OWLClassExpression) toOWLAPI(tail.get(0));	
				OWLClassExpression class2e = (OWLClassExpression) toOWLAPI(tail.get(1));
				result = dataFactory.getOWLEquivalentClassesAxiom(class1e, class2e);
				break;
			case SUBCL:  	
				OWLClassExpression class1 = (OWLClassExpression) toOWLAPI(tail.get(0));
				// System.out.println(class1);
				OWLClassExpression class2 = (OWLClassExpression) toOWLAPI(tail.get(1));
				// System.out.println(class2);
				result = dataFactory.getOWLSubClassOfAxiom(class1, class2);
				break;
			case INT: 
				// System.out.println("case inter!");
				// System.out.println("head " + head + "   tail: " + tail);
				// OWLClassExpression class11 = (OWLClassExpression) tail.get(0).toOWLAPI();
				// OWLClassExpression class12 = (OWLClassExpression) tail.get(1).toOWLAPI();
				// result = dataFactory.getOWLObjectIntersectionOf(class11, class12);
				Set<OWLClassExpression> exprs = new TreeSet<OWLClassExpression>();
				for (int i = 0; i<tail.size();i++){
					OWLClassExpression ce = (OWLClassExpression) toOWLAPI(tail.get(i));
					exprs.add(ce);
				}
				result = dataFactory.getOWLObjectIntersectionOf(exprs);
				break;
			case UNION:  
				// System.out.println("case union!");
				// OWLClassExpression class21 = (OWLClassExpression) tail.get(0).toOWLAPI();
				// OWLClassExpression class22 = (OWLClassExpression) tail.get(1).toOWLAPI();
				// result = dataFactory.getOWLObjectUnionOf(class21, class22);
				Set<OWLClassExpression> exprs2 = new TreeSet<OWLClassExpression>();
				for (int i = 0; i<tail.size();i++){
					OWLClassExpression ce = (OWLClassExpression) toOWLAPI(tail.get(i));
					exprs2.add(ce);
				}
				result = dataFactory.getOWLObjectUnionOf(exprs2);
				break;	
			case EXISTS:
				// System.out.println("Propertytail: " + tail.get(0));
				// System.out.println("Expressiontail" + tail.get(1));
				// System.out.println("Propertytail translated: " + toOWLAPI(tail.get(0)));
				// System.out.println("Expressiontail translated: " +  toOWLAPI(tail.get(1)));
				OWLObject propertyobject = toOWLAPI(tail.get(0));
				// System.out.println("Propertyobject: " +  propertyobject);
				OWLObjectProperty rname1 = (OWLObjectProperty) propertyobject;
				OWLObject classobject = toOWLAPI(tail.get(1));
				// System.out.println("Classobject: " +  classobject);
				OWLClassExpression cname1 = (OWLClassExpression) classobject;
				result = dataFactory.getOWLObjectSomeValuesFrom(rname1, cname1);
				// System.out.println(" Result " + result);
				// System.out.println("got there!");
				break;	
			case FORALL:
				// System.out.println("case forall!");
				OWLObjectProperty rname2 = (OWLObjectProperty) toOWLAPI(tail.get(0));
				OWLClassExpression cname2 = (OWLClassExpression) toOWLAPI(tail.get(1));
				result = dataFactory.getOWLObjectAllValuesFrom(rname2, cname2);
				break;	
			case TOP:
				result = dataFactory.getOWLThing();
				break;	
			case TRANSITIVE:
				OWLObjectProperty rname3 = (OWLObjectProperty) toOWLAPI(tail.get(0));
				result = dataFactory.getOWLTransitiveObjectPropertyAxiom(rname3);
				break;
			case FUNCTIONAL:
				OWLObjectProperty rname4= (OWLObjectProperty) toOWLAPI(tail.get(0));
				result = dataFactory.getOWLFunctionalObjectPropertyAxiom(rname4);
				break;
			case FUNCTIONALDATA:
				OWLDataProperty rname5= (OWLDataProperty) toOWLAPI(tail.get(0));
				result = dataFactory.getOWLFunctionalDataPropertyAxiom(rname5);
				break;
			case NEG:
				// System.out.println("case neg");
				OWLClassExpression classexp = (OWLClassExpression) toOWLAPI(tail.get(0));
				result = classexp.getObjectComplementOf();
				break;	
			case DATAHASVALUE:
				OWLFormula propname = (OWLFormula) tail.get(0);
				OWLDataPropertyExpression prop = (OWLDataPropertyExpression) toOWLAPI((OWLDataPropertyName) propname.getHead());
				OWLAtom tailhead = tail.get(1).getHead();
				// System.out.println("tailhead " + tailhead);
				OWLLiteralValue literalvalue = (OWLLiteralValue) tailhead;
				OWLLiteral lit = (OWLLiteral) toOWLAPI(literalvalue);
				OWLDataHasValue expr = dataFactory.getOWLDataHasValue(prop,lit);
				result = expr;
				break;
			case OBJECTHASVALUE:
				OWLFormula propname2 = (OWLFormula) tail.get(0);
				// System.out.println("DEBUG " + propname2);
				OWLObjectPropertyExpression prop2 = (OWLObjectPropertyExpression) toOWLAPI(((OWLObjectPropertyName) propname2.getHead()));
				// System.out.println("DEBUG (2) " + prop2);
				OWLIndividualName indiv = (OWLIndividualName) tail.get(1).getHead();
				OWLIndividual ni = (OWLIndividual) toOWLAPI(indiv);
				OWLObjectHasValue expr2 = dataFactory.getOWLObjectHasValue(prop2,ni);
				result = expr2;
				break;
			case DISJ:
				// TODO: Attention, this only handles the binary case!
				// System.out.println("case neg");
				OWLClassExpression classexp1 = (OWLClassExpression) toOWLAPI(tail.get(0));
				OWLClassExpression classexp2 = (OWLClassExpression) toOWLAPI(tail.get(1));
				result = dataFactory.getOWLDisjointClassesAxiom(classexp1,classexp2);
				break;
			case DOMAIN:
				OWLObjectPropertyExpression propD = (OWLObjectPropertyExpression) toOWLAPI(tail.get(0));
				OWLClassExpression classD = (OWLClassExpression) toOWLAPI(tail.get(1));
				result = dataFactory.getOWLObjectPropertyDomainAxiom(propD,classD);
				break;	
			case RANGE:
				OWLObjectPropertyExpression propR = (OWLObjectPropertyExpression) toOWLAPI(tail.get(0));
				OWLClassExpression classR = (OWLClassExpression) toOWLAPI(tail.get(1));
				result = dataFactory.getOWLObjectPropertyRangeAxiom(propR,classR);
				break;	
			case OBJECTEXACTCARDINALITY:
				OWLObjectPropertyExpression propEx = (OWLObjectPropertyExpression) toOWLAPI(tail.get(1));
				OWLFormula owlintform = tail.get(0);
				OWLInteger owlint = (OWLInteger) owlintform.getHead();
				int cardinality = owlint.getValue();
				result = dataFactory.getOWLObjectExactCardinality(cardinality, propEx);
				break;
			case CLASSASSERTION:
				OWLClassExpression classexpCA = (OWLClassExpression) toOWLAPI(tail.get(0));
				OWLNamedIndividual indivCA = (OWLNamedIndividual) toOWLAPI(tail.get(1));
				result = dataFactory.getOWLClassAssertionAxiom(classexpCA, indivCA);
				break;
			case SUBPROPERTYOF:
				if (tail.get(0).getHead().equals(OWLSymb.SUBPROPERTYCHAIN)){
					List<OWLObjectPropertyExpression> props = new ArrayList<OWLObjectPropertyExpression>();
					for (OWLFormula form : tail.get(0).getArgs()){
						OWLObjectPropertyExpression sub = (OWLObjectPropertyExpression) toOWLAPI(form);
						props.add(sub);
					}
					OWLObjectPropertyExpression subp2 = (OWLObjectPropertyExpression) toOWLAPI(tail.get(1));
					result = dataFactory.getOWLSubPropertyChainOfAxiom(props, subp2);
				}
				else{
					OWLObjectPropertyExpression subp1 = (OWLObjectPropertyExpression) toOWLAPI(tail.get(0));
					OWLObjectPropertyExpression subp2 = (OWLObjectPropertyExpression) toOWLAPI(tail.get(1));
					result = dataFactory.getOWLSubObjectPropertyOfAxiom(subp1,subp2);
				}
				break;		
			default: 
				result = dataFactory.getOWLNothing();
			}
		} else {
			result = toOWLAPI(head);
		}
		return result;
	}
	
	private static OWLObject toOWLAPI(OWLAtom atom){
		OWLObject result = null;
		if (atom instanceof OWLClassName)
			result = toOWLAPI((OWLClassName) atom);
		if (atom instanceof OWLSymb)
			result = toOWLAPI((OWLSymb) atom);
		if (atom instanceof OWLRoleName)
			result = toOWLAPI((OWLRoleName) atom);
		if (atom instanceof OWLIndividualName)
			result = toOWLAPI((OWLIndividualName) atom);
		if (atom instanceof OWLDataPropertyName)
			result = toOWLAPI((OWLDataPropertyName) atom);
		if (atom instanceof OWLObjectPropertyName)
			result = toOWLAPI((OWLObjectPropertyName) atom);
		return result;
	}
	
	
	private static OWLObject toOWLAPI(OWLSymb symb){
		OWLDataFactory dataFactory=OWLAPIManagerManager.INSTANCE.getDataFactory();
		return dataFactory.getOWLThing();
		
	}
	
	
	private static OWLObject toOWLAPI(OWLClassName classname){
		OWLDataFactory dataFactory=OWLAPIManagerManager.INSTANCE.getDataFactory();
		return dataFactory.getOWLClass(IRI.create(classname.getOntologyname()));
	}
	
	private static OWLObject toOWLAPI(OWLRoleName rolename){
		OWLDataFactory dataFactory=OWLAPIManagerManager.INSTANCE.getDataFactory();
		// if (dataproperty)
		//	return dataFactory.getOWLDataProperty(IRI.create(ontologyname));
		// else 
		return dataFactory.getOWLObjectProperty(IRI.create(rolename.getOntologyname()));
	}
	
	private static OWLObject toOWLAPI(OWLIndividualName name){
		OWLDataFactory dataFactory=OWLAPIManagerManager.INSTANCE.getDataFactory();
		return dataFactory.getOWLNamedIndividual(IRI.create(name.getOntologyname()));	
	}
	
	private static OWLObject toOWLAPI(OWLDataPropertyName name){
		OWLDataFactory dataFactory=OWLAPIManagerManager.INSTANCE.getDataFactory();
		return dataFactory.getOWLDataProperty(IRI.create(name.getOntologyname()));
	}
	
	private static OWLObject toOWLAPI(OWLObjectPropertyName name){
		OWLDataFactory dataFactory=OWLAPIManagerManager.INSTANCE.getDataFactory();
		return dataFactory.getOWLObjectProperty(IRI.create(name.getOntologyname()));
	}
	
	private static OWLObject toOWLAPI(OWLLiteralValue val){
		OWLDataFactory dataFactory=OWLAPIManagerManager.INSTANCE.getDataFactory();
		OWLLiteral lit = dataFactory.getOWLLiteral(val.getValue(),ConversionManager.literaltypeToDatatype(val.getType()));
		return lit;	
	}
	
	public static OWLDataRan datarangeToDataran(OWLDataRange range){
		OWLDatatype datatype = range.asOWLDatatype();
		if (datatype.isBoolean()){
			return OWLDataRan.BOOLEAN;
		}
		if (datatype.isDouble()){
			return OWLDataRan.DOUBLE;
		}
		if (datatype.isFloat()){
			return OWLDataRan.FLOAT;
		}
		if (datatype.isInteger()){
			return OWLDataRan.INTEGER;
		}
		return OWLDataRan.STRING;
	}
	
	public static OWLLiteralType datatypeToLiteraltype(OWLDatatype datatype){
		if (datatype.isBoolean()){
			return OWLLiteralType.BOOLEAN;
		}
		if (datatype.isDouble()){
			return OWLLiteralType.DOUBLE;
		}
		if (datatype.isFloat()){
			return OWLLiteralType.FLOAT;
		}
		if (datatype.isInteger()){
			return OWLLiteralType.INTEGER;
		}
		return OWLLiteralType.STRING;
	}
	
	public static OWLDatatype literaltypeToDatatype(OWLLiteralType littype){
		OWLDataFactory dataFactory=OWLAPIManagerManager.INSTANCE.getDataFactory();
		if (littype.equals(OWLLiteralType.BOOLEAN)){
			return dataFactory.getBooleanOWLDatatype();
		}
		if (littype.equals(OWLLiteralType.DOUBLE)){
			return dataFactory.getDoubleOWLDatatype();
		}
		if (littype.equals(OWLLiteralType.FLOAT)){
			return dataFactory.getFloatOWLDatatype();
		}
		if (littype.equals(OWLLiteralType.INTEGER)){
			return dataFactory.getIntegerOWLDatatype();
		}
		// OWLDatatype string = dataFactory.getOWLDatatype(XSDVocabulary.parseShortName("xsd:string"));
		OWLDatatype string = dataFactory.getOWLDatatype(XSDVocabulary.STRING.getIRI());
		return string;
	}
	
	
}
