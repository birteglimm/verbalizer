package org.semanticweb.cogExp.GentzenTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.semanticweb.cogExp.inferencerules.AdditionalDLRules;
import org.semanticweb.cogExp.core.SequentInferenceRule;
import org.semanticweb.cogExp.OWLAPIVerbaliser.VerbalisationManager;
import org.semanticweb.cogExp.OWLFormulas.OWLFormula;
import org.semanticweb.cogExp.OWLFormulas.OWLSymb;
import org.semanticweb.cogExp.PrettyPrint.PrettyPrintOWLAxiomVisitor;
import org.semanticweb.cogExp.inferencerules.INLG2012NguyenEtAlRules;

public class GentzenTree {
	
	private List<OWLFormula> formulas = new ArrayList<OWLFormula>();
	private HashMap<Integer,GentzenStep> treesteps = new HashMap<Integer,GentzenStep>();
	private HashMap<Integer,Integer> conclusions = new HashMap<Integer,Integer>(); // points towards the step that introduces the conclusion
	private List<Integer> axioms = new ArrayList<Integer>();
	private int last_key = 0;
	
	public int getLastKey(){
		return last_key;
	}
	
	public List<OWLFormula> getFormulas(){
		return formulas;
	}
	
	public boolean isAxiom(OWLFormula form){
		// System.out.println("axioms : " + axioms);
		// System.out.println("form : " + form);
		if (formulas.contains(form)){
			// System.out.println("contains");
			// System.out.println("index  " + formulas.indexOf(form));
			if (axioms.contains(formulas.indexOf(form))){
				// System.out.println("gotit");
					return true;
			}
		}
		return false;
	}
	
	public HashMap<Integer,GentzenStep> getTreesteps(){
		return treesteps;
	}
	
	public GentzenStep getStepByID(int id){
		return treesteps.get(id);
	}
	
	public GentzenStep getLastStep(){
		return treesteps.get(last_key-1);
	}
	
	public List<OWLFormula> idsToFormulas(List<Integer> intlist){
		List<OWLFormula> resultformulas = new ArrayList<OWLFormula>();
		for (int i:intlist){
			resultformulas.add(formulas.get(i));
		}
		return resultformulas;
	}
	
	public void insertStep(List<OWLFormula> premises, OWLFormula conclusion, SequentInferenceRule infrule){
		// start by identifying or inserting premise formulas
		List<Integer> premiseids = new ArrayList<Integer>();
		List<Integer> axiompremiseids = new ArrayList<Integer>();
		for (OWLFormula prem: premises){
			// make sure the formula is known
			// System.out.println("DEBUG -- considering premise formulas " + prem);
			/*
			if (infrule.equals(AdditionalDLRules.ONLYSOME))
				System.out.println("DEBUG -- considering premise formulas " + prem.prettyPrint());
				*/
			if (!formulas.contains(prem))
				formulas.add(prem);
			int id = formulas.indexOf(prem);
			premiseids.add(formulas.indexOf(prem));
			// find out if it is a conclusion, otherwise treat it as an axiom
			if (!conclusions.containsKey(id)){ 
				// System.out.println("Adding axiom " + id);
				if (!axioms.contains(id)) 
					axioms.add(id);
				}
			axiompremiseids.add(id);
		}
		formulas.add(conclusion);
		int conclusionid = formulas.indexOf(conclusion);
		// now insert the actual step
		// System.out.println("Inserting " + premiseids + " " +  conclusionid + " "+ infrule);
		/*
		if (infrule.equals(AdditionalDLRules.ONLYSOME))
			 System.out.println("Inserting " + premiseids + " " +  conclusionid + " "+ infrule);
		*/
		GentzenStep step = new GentzenStep(premiseids,axiompremiseids, conclusionid,infrule);
		// System.out.println("insert step key " + last_key);
		treesteps.put(last_key,step);
		// System.out.println("enter conclusion with: " + conclusion + " " + last_key);
		conclusions.put(conclusionid, last_key);
		last_key = last_key + 1;
		return;
	}
	
	// returns list with id of steps
	public List<Integer> computePresentationOrder(){
		// we start with what we assume is the root
		List<Integer> result = computePresentationOrder(last_key-1);
		// Collections.reverse(result);
		return result;
	}
	
	public List<Integer> computePresentationOrder(int i){
		// System.out.println("compute presentation order " + i);
		List<Integer> results = new ArrayList<Integer>();
		// find premises
		// System.out.println("find key " + i);
		GentzenStep step = treesteps.get(i);
		List<Integer> premises = step.getPremises();
		SequentInferenceRule infrule = step.getInfrule();
		// Reorder the premises according to rules
		// Reordering for SUBCLANDEQUIVELIM
		if (infrule.equals(AdditionalDLRules.SUBCLANDEQUIVELIM)){
			OWLFormula firstformula = formulas.get(premises.get(0));
			if (firstformula.getHead().equals(OWLSymb.EQUIV)){
				Collections.reverse(premises);
				// System.out.println("I JUST REORDERED PREMISES FOR SUBCLANDEQUIVELIM");
				// System.out.println(premises);
			} else {
				// System.out.println("I JUST DECIDED NOT TO REORDER PREMISES FOR SUBCLANDEQUIVELIM");
				// System.out.println(premises);			
			}
		}
		// Reordering for ONLYSOME
				if (infrule.equals(AdditionalDLRules.ONLYSOME)){
					// System.out.println("Gentzen Tree DEBUG ONLYSOME " + premises.size());
					/* 
					int id1 = premises.get(0);
					OWLFormula formula1 = formulas.get(premises.get(0));
					int f1id;
					int id2 = premises.get(1);
					OWLFormula formula2 = formulas.get(premises.get(1));
					int f2id;
					*/
					int f1id = -1000;
					int f2id = -1000;
					// determine which ones of the two premises are the onlysome formulas
					OWLFormula conclusion = formulas.get(step.getConclusion());
					// System.out.println("conclusion " + conclusion.prettyPrint());
					// System.out.println(premises);
					// figure out which is the "subclass" formula
					OWLFormula subclassOnlysome = null;
					OWLFormula superclassOnlysome = null;
					// find subclassOnlysome
					for (int subi=0; subi<premises.size();subi++ ){
						// System.out.println("subi " + subi);
						OWLFormula prem = formulas.get(premises.get(subi));
						// System.out.println("prem (" + premises.get(subi) + ") : "  + prem.prettyPrint());
						if (prem.getArgs().get(0).equals(conclusion.getArgs().get(0))){
							subclassOnlysome = prem;
							f1id=premises.get(subi);
						}
						if (prem.getArgs().get(1).equals(conclusion.getArgs().get(1))){
							superclassOnlysome = prem;
							f2id=premises.get(subi);
						}
					}
					
					// System.out.println(premises);
					/*
					for (int k : premises){
						System.out.println(VerbalisationManager.prettyPrint(formulas.get(k)));
					}
					*/
					// System.out.println(conclusion.getArgs().get(0));
					// System.out.println(formula2.getArgs().get(0));
					/*
					if (conclusion.getArgs().get(0).equals(formula2.getArgs().get(0))){
						subclassOnlysome = formula2;
						superclassOnlysome = formula1;
						f1id= id2;
						f2id= id1;
					}
					else{
						subclassOnlysome = formula1;
						superclassOnlysome = formula2;
						f1id= id1;
						f2id= id2;
						// premises.remove(1);
						// premises.add(premises.size(),id2); //<-- put the second formula in last position (the second onlysome formula)
					}
					*/
					// System.out.println("debug GentzenTree: " + premises);
					// System.out.println("ONLYSOME subclassOnlysome :" + subclassOnlysome.prettyPrint());
					// System.out.println("ONLYSOME superclassOnlysome :" + superclassOnlysome.prettyPrint());
					List<OWLFormula> expressions = AdditionalDLRules.detectOnlysome(subclassOnlysome.getArgs().get(1));
					// System.out.println("expressions: " + expressions);
					List<Integer> forms = new ArrayList<Integer>();
					for (OWLFormula form :expressions){
						for (int j: premises){
							OWLFormula premform = formulas.get(j);
							if (premform.getArgs().get(0).equals(form)){
								forms.add(j);
							}
						}
					}
					premises.removeAll(premises);
					// System.out.println("id1 " + f1id);
					premises.add(f1id); 
					// System.out.println("forms " + forms);
					premises.addAll(forms);
					// System.out.println("id2 " + f2id);
					premises.add(f2id); 
					// System.out.println("debug GentzenTree (2): " + premises);
					// System.out.println("checking " + step.getPremises());
					
					
				}
		// Reordering for RULE12
		if (infrule.equals(INLG2012NguyenEtAlRules.RULE12)){
			OWLFormula firstformula = formulas.get(premises.get(0));
			OWLFormula secondformula = formulas.get(premises.get(1));
			if (firstformula.getArgs().get(0).equals(secondformula.getArgs().get(1))){
				Collections.reverse(premises);
				// System.out.println("I JUST REORDERED PREMISES FOR RULE12");
				// System.out.println(premises);
			} else{
				// System.out.println("I JUST DECIDED NOT TO REORDER PREMISES FOR RULE12");
				// System.out.println(premises);
			}
		}
		// Reordering for RULE15
		if (infrule.equals(INLG2012NguyenEtAlRules.RULE15)){
			OWLFormula firstformula = formulas.get(premises.get(0));
			OWLFormula secondformula = formulas.get(premises.get(1));
			// System.out.println("FIRSTFORMULA : " + firstformula);
			// System.out.println("SECONDFORMULA : " + secondformula);
			// System.out.println("secondformula head : " + secondformula.getArgs().get(1).getHead());
			// System.out.println(secondformula.getArgs().get(1).getHead().equals(OWLSymb.EXISTS));
			// if (secondformula.getArgs().get(1).getHead().equals(OWLSymb.EXISTS))
			//       System.out.println("secondformula content : " + secondformula.getArgs().get(1).getArgs().get(1));
			// System.out.println("cond 1 " + secondformula.getArgs().get(1).getArgs());
		    // System.out.println("cond 2 " + secondformula.getArgs().get(1).getArgs().get(1));
			// System.out.println("cond 3 " + firstformula.getArgs().get(0));
			if (secondformula.getArgs().get(1).getArgs()!=null &&
				secondformula.getArgs().get(1).getArgs().size()>0 &&
				secondformula.getArgs().get(1).getHead().equals(OWLSymb.EXISTS) &&
				secondformula.getArgs().get(1).getArgs().get(1).equals(firstformula.getArgs().get(0))){
				//  System.out.println("I JUST DECIDED NOT TO REORDER PREMISES FOR RULE15");
				Collections.reverse(premises);
				// System.out.println("I JUST REORDERED PREMISES FOR RULE15");
				// System.out.println(premises);
			} else{
				// Collections.reverse(premises);
				// System.out.println("I JUST REORDERED PREMISES FOR RULE15");
				// System.out.println("I JUST DECIDED NOT TO REORDER PREMISES FOR RULE15");
				// System.out.println(premises);
			}
		}
		// System.out.println("made it");
		// Reordering for UNIONINTRO
				if (infrule.equals(AdditionalDLRules.UNIONINTRO)){
					OWLFormula firstformula = formulas.get(premises.get(0));
					OWLFormula secondformula = formulas.get(premises.get(1));
					// System.out.println("FIRSTFORMULA : " + firstformula);
					// System.out.println("SECONDFORMULA : " + secondformula);
					// System.out.println("secondformula head : " + secondformula.getArgs().get(1).getHead());
					// System.out.println("secondformula content : " + secondformula.getArgs().get(1).getArgs().get(1));
					if (secondformula.getArgs().get(1).getArgs().size()>0 &&
						secondformula.getArgs().get(1).getHead().equals(OWLSymb.EXISTS) 
						){
						Collections.reverse(premises);
						// System.out.println("I JUST REORDERED PREMISES FOR RULE15");
						// System.out.println(premises);
					} else{
						// System.out.println("I JUST DECIDED NOT TO REORDER PREMISES FOR RULE15");
						// System.out.println(premises);
					}
				}
		// Now do the actual recursive call
	    // System.out.println("At step " + i + " rule " + infrule.getName());
		for (int premi: premises){
			// System.out.println("examining premise " + premi);
		    // System.out.println("axioms: " + axioms);
			if (!axioms.contains(premi)){
				// if premise is not an axiom, recurse
				// System.out.println("find step for conclusion" + formulas.get(premi));
				int stepi = conclusions.get(premi);
				// System.out.println("step no " + stepi + "introduced this conclusion");
				List<Integer> recursiveorder = computePresentationOrder(stepi);
				for (int record : recursiveorder){
					if (!results.contains(record))
					results.add(record);
				}
				// System.out.println("resultorder " + results);
			}
		}
		results.add(i);
		return results;
	};
	
	
	
	public OWLFormula stepGetConclusionFormula(int stepid){
		GentzenStep step = treesteps.get(stepid);
		return formulas.get(step.getConclusion());
	};
	
	public OWLFormula stepGetConclusionFormula(GentzenStep step){
		return formulas.get(step.getConclusion());
	};
	
	public List<OWLFormula> stepGetPremiseFormulas(int stepid){
		GentzenStep step = treesteps.get(stepid);
		return idsToFormulas(step.getPremises());
	};
	
	public List<OWLFormula> stepGetPremiseFormulas(GentzenStep step){
		return idsToFormulas(step.getPremises());
	};
	
	public String stepGetRuleName(GentzenStep step){
		return step.getInfrule().getName();
	};
	
	public List<GentzenStep> getStepsInOrder(){
		List<Integer> order = computePresentationOrder();
		List<GentzenStep> steps = new ArrayList<GentzenStep>();
		for(int i:order){
			steps.add(treesteps.get(i));
		}
		return steps;
	}
	
	public List<SequentInferenceRule> getInfRules(){
		Set<Integer> keys =  treesteps.keySet();
		List<SequentInferenceRule> allsteps = new ArrayList<SequentInferenceRule>();
		for (int i: keys){
			allsteps.add(treesteps.get(i).getInfrule());
		}
		return allsteps;
	}
	
	public String toDOT(){
		String result = "";
		// System.out.println("to dot called ");
		List<Integer> order = computePresentationOrder();
		// System.out.println("computed presentation order " + order);
		for (Integer index :order ){
			GentzenStep step = getStepByID(index);
			int conclusion = step.getConclusion();
			String stepname = "index_" + index + step.getInfrule().getShortName();
			if (stepname.contains("-"))
				stepname = stepname.replaceAll("-", "minus");
			List<Integer> premises = step.getPremises();
			String rulelabel = step.getInfrule().getShortName();
			for (Integer prem : premises){
				String nodelabel = VerbalisationManager.prettyPrint(formulas.get(prem));
				nodelabel = nodelabel.replace(Character.toString(PrettyPrintOWLAxiomVisitor.SUBCLSYMB), "&#x2291;");
				nodelabel = nodelabel.replace(Character.toString(PrettyPrintOWLAxiomVisitor.EQUIVSYMB), "&#x2261;");
				nodelabel = nodelabel.replace(Character.toString(PrettyPrintOWLAxiomVisitor.INTSYMB), "&#x2293;");
				nodelabel = nodelabel.replace(Character.toString(PrettyPrintOWLAxiomVisitor.UNIONSYMB), "&#x2294;");
				nodelabel = nodelabel.replace(Character.toString(PrettyPrintOWLAxiomVisitor.EXISTSSYMB), "&#x2203;");
				nodelabel = nodelabel.replace(Character.toString(PrettyPrintOWLAxiomVisitor.FORALLSYMB), "&#x2200;");
				nodelabel = nodelabel.replace(Character.toString(PrettyPrintOWLAxiomVisitor.CIRCSYMB), "&#x25CB;");
				if (nodelabel.length()>30){
					int i = 0;
					int skip = 25;
					while(i<nodelabel.length()){
						int tmp1 = nodelabel.indexOf("&#x2291;",i+2);
						int tmp2 = nodelabel.indexOf("&#x2261;",i+2);
						int tmp3 = nodelabel.indexOf("&#x2293;",i+2);
						int tmp4 = nodelabel.indexOf("&#x2294;",i+2);
						if (tmp1<skip) 
							tmp1=5000;
						if (tmp2<skip) 
							tmp2=5000;
						if (tmp3<skip) 
							tmp3=5000;
						if (tmp4<skip) 
							tmp4=5000;
						int min = Math.min(tmp1,Math.min(tmp2,Math.min(tmp3,tmp4)));
						if (min<5000 && min>skip){
							nodelabel = nodelabel.substring(0, min) + "\n" +  nodelabel.substring(min, nodelabel.length());
						}
						i = min;
					}
				}
					
				
				
				result +=  "n" + prem.toString() + " -> " + stepname + "R" + conclusion + ";\n"; 
				//
				result += "n" + prem.toString() + "[fontname=fixedsys,shape=rectangle,charset=\"utf-8\",label=\"" 
				+ nodelabel  + "\"];\n";
			}
			result +=  stepname + "R" + conclusion + " -> " + "n" + conclusion + ";\n";
			result += "n" 
			+ conclusion + "[fontname=fixedsys,shape=rectangle,charset=\"utf-8\",label=\"" 
					+ VerbalisationManager.prettyPrint(formulas.get(conclusion))  + "\"];\n";
			result += stepname + "R" 
					+ conclusion + "[color=lightblue,style=filled,label=\"" 
					+ rulelabel +  "\"];\n";
		}
		// return "";
		// return "digraph " + VerbalisationManager.prettyPrint(formulas.get(getStepByID(order.get(order.size()-1)).getConclusion())) + "\n { ratio=compress \n" + result + "}";
		return "digraph proof" + "\n { fontname=calibri ratio=compress \n" + result + "}";
	}
	
}
