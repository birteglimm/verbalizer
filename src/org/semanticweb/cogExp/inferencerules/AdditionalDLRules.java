package org.semanticweb.cogExp.inferencerules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.semanticweb.cogExp.core.AbstractSequentPositions;
import org.semanticweb.cogExp.core.InferenceApplicationService;
import org.semanticweb.cogExp.core.JustificationNode;
import org.semanticweb.cogExp.core.Pair;
import org.semanticweb.cogExp.core.ProofNode;
import org.semanticweb.cogExp.core.ProofTree;
import org.semanticweb.cogExp.core.RuleApplicationResults;
import org.semanticweb.cogExp.core.RuleBinding;
import org.semanticweb.cogExp.core.RuleKind;
import org.semanticweb.cogExp.core.Sequent;
import org.semanticweb.cogExp.core.SequentInferenceRule;
import org.semanticweb.cogExp.core.SequentList;
import org.semanticweb.cogExp.core.SequentPart;
import org.semanticweb.cogExp.core.SequentPosition;
import org.semanticweb.cogExp.core.SequentSinglePosition;
import org.semanticweb.cogExp.OWLFormulas.OWLAtom;
import org.semanticweb.cogExp.OWLFormulas.OWLFormula;
import org.semanticweb.cogExp.OWLFormulas.OWLSymb;

public enum AdditionalDLRules implements SequentInferenceRule{
	
SIMPLETERMINATION{	
	
	@Override
	public java.lang.String getName(){return "SimpleTermination";};
	@Override
	public java.lang.String getShortName(){return "ST";};
	
	@Override
	public List<RuleBinding> findRuleBindings(Sequent s){
		List<RuleBinding> results = new ArrayList<RuleBinding>();
		HashSet<OWLFormula> succs = s.getAllSuccedentOWLFormulas();
		List<OWLFormula> succslist = new ArrayList<OWLFormula>(succs);
		if (succs.size()==1){
			OWLFormula target = succslist.get(0);
			HashSet<OWLFormula> ants = s.getAllAntecedentOWLFormulas();
			for(OWLFormula ant : ants){
				if (ant.equals(target)){
				List<Integer> list = new ArrayList<Integer>();
				list.add(s.antecedentFormulaGetID(ant));
				SequentPosition position1 = new SequentSinglePosition(SequentPart.ANTECEDENT, list);
				RuleBinding binding = new RuleBinding();
			    binding.insertPosition("A1", position1);
				results.add(binding);
				}
			}
		}
		
		return results;
	}
	
	
	@Override
	public SequentList computePremises(Sequent sequent, RuleBinding binding) throws Exception {
		// TODO Auto-generated method stub
		ArrayList sequentlist = new ArrayList();
		Sequent newsequent = new Sequent();
		sequentlist.add(newsequent);
		return SequentList.makeANDSequentList(sequentlist);
	}

},
	



R0{
		
		@Override
		public java.lang.String getName(){return "R0";};
		@Override
		public java.lang.String getShortName(){return "R0";};
		

		@Override
		public List<RuleBinding> findRuleBindings(Sequent s){
			List<RuleBinding> results = new ArrayList<RuleBinding>();
			Set<OWLFormula> subexprs = s.getAllSubExprs();
			for (OWLFormula subexpr : subexprs){
				if (subexpr.isClassExpression()){ 
					OWLFormula conclusion = OWLFormula.createFormula(OWLSymb.SUBCL,subexpr,subexpr);
					if (!s.alreadyContainedInAntecedent(conclusion) 
							){
						   // System.out.println("R0 DEBUG adds " + conclusion);
						   RuleBinding binding = new RuleBinding(conclusion,null);
						    results.add(binding);
					}
				}
			}
			// trivial one, also necessary sometimes!
			OWLFormula trivial_conclusion = OWLFormula.createFormula(OWLSymb.SUBCL,OWLFormula.createFormulaBot(),OWLFormula.createFormulaBot());
			if (!s.alreadyContainedInAntecedent(trivial_conclusion) 
					){
				   // System.out.println("R0 DEBUG adds " + conclusion);
				   RuleBinding binding = new RuleBinding(trivial_conclusion,null);
				    results.add(binding);
			}
			// System.out.println("R0 DEBUG results " + results);
			return new ArrayList<RuleBinding>(results);
		}
		
		@Override
		public List<RuleBinding> findRuleBindings(Sequent s, boolean ... saturate) {
			return findRuleBindings(s);
		}
		
		@Override
		public SequentList computePremises(Sequent sequent, RuleBinding binding) throws Exception{
			
			List<RuleApplicationResults> results = computeRuleApplicationResults(sequent, binding);
			List<Sequent> sequents =  ruleApplicationResultsAsSequent(results);
			// System.out.println("Rule 1 neo DEBUG sequents " + sequents);
			return SequentList.makeANDSequentList(sequents);
		}
		
		
		@Override
		public List<RuleApplicationResults> computeRuleApplicationResults(Sequent sequent, RuleBinding binding) throws Exception{
			List<RuleApplicationResults> results = new ArrayList<RuleApplicationResults>();
			// OWLSubClassOfAxiom axiom3;
			OWLFormula conclusionformula = null;
			// System.out.println("DEBUG -- newantecedent " + binding.getNewAntecedent());
			if (binding.getNewAntecedent()!=null){ // fast track
				conclusionformula = binding.getNewAntecedent();
				// System.out.println(" rule topintro is adding " + conclusionformula);
			} 
			RuleApplicationResults result = new RuleApplicationResults();
			result.setOriginalFormula(sequent);
			result.addAddition("A1",conclusionformula);
			results.add(result);
			result.setMaxFormulaDepth(InferenceApplicationService.computeRuleBindingMaxDepth(sequent, binding));
			return results;
		}
		
		
	}, // END R0
	
	
TOPINTRO{
		
		@Override
		public java.lang.String getName(){return "Topintro";};
		@Override
		public java.lang.String getShortName(){return "toI";};
		

		@Override
		public List<RuleBinding> findRuleBindings(Sequent s){
			List<RuleBinding> results = new ArrayList<RuleBinding>();
			Set<OWLFormula> subexprs = s.getAllSubExprs();
			for (OWLFormula subexpr : subexprs){
					OWLFormula conclusion = OWLFormula.createFormula(OWLSymb.SUBCL,subexpr,OWLFormula.createFormulaTop());
					if (!subexpr.isTop() && subexpr.isClassExpression() &&!s.alreadyContainedInAntecedent(conclusion) 
							){
							RuleBinding binding = new RuleBinding(conclusion,null);
						    results.add(binding);
							
				}
			}
			// System.out.println("Rule 1neo DEBUG results " + results);
			return new ArrayList<RuleBinding>(results);
		}
		
		@Override
		public List<RuleBinding> findRuleBindings(Sequent s, boolean ... saturate) {
			return findRuleBindings(s);
		}
		
		@Override
		public SequentList computePremises(Sequent sequent, RuleBinding binding) throws Exception{
			
			List<RuleApplicationResults> results = computeRuleApplicationResults(sequent, binding);
			List<Sequent> sequents =  ruleApplicationResultsAsSequent(results);
			// System.out.println("Rule 1 neo DEBUG sequents " + sequents);
			return SequentList.makeANDSequentList(sequents);
		}
		
		
		@Override
		public List<RuleApplicationResults> computeRuleApplicationResults(Sequent sequent, RuleBinding binding) throws Exception{
			List<RuleApplicationResults> results = new ArrayList<RuleApplicationResults>();
			// OWLSubClassOfAxiom axiom3;
			OWLFormula conclusionformula = null;
			// System.out.println("DEBUG -- newantecedent " + binding.getNewAntecedent());
			if (binding.getNewAntecedent()!=null){ // fast track
				conclusionformula = binding.getNewAntecedent();
				// System.out.println(" rule topintro is adding " + conclusionformula);
			} 
			RuleApplicationResults result = new RuleApplicationResults();
			result.setOriginalFormula(sequent);
			result.addAddition("A1",conclusionformula);
			results.add(result);
			result.setMaxFormulaDepth(InferenceApplicationService.computeRuleBindingMaxDepth(sequent, binding));
			return results;
		}
		
		
	}, // END TOPINTRO

BOTINTRO{
		
		@Override
		public java.lang.String getName(){return "Botintro";};
		@Override
		public java.lang.String getShortName(){return "boI";};
		

		@Override
		public List<RuleBinding> findRuleBindings(Sequent s){
			List<RuleBinding> results = new ArrayList<RuleBinding>();
			Set<OWLFormula> subexprs = s.getAllSubExprs();
			for (OWLFormula subexpr : subexprs){
					OWLFormula conclusion = OWLFormula.createFormula(OWLSymb.SUBCL,OWLFormula.createFormulaBot(),subexpr);
					if (subexpr.isClassExpression() &&!s.alreadyContainedInAntecedent(conclusion) 
							){
							RuleBinding binding = new RuleBinding(conclusion,null);
						    results.add(binding);
							
				}
			}
			// System.out.println("Rule 1neo DEBUG results " + results);
			return new ArrayList<RuleBinding>(results);
		}
		
		@Override
		public List<RuleBinding> findRuleBindings(Sequent s, boolean ... saturate) {
			return findRuleBindings(s);
		}
		
		@Override
		public SequentList computePremises(Sequent sequent, RuleBinding binding) throws Exception{
			
			List<RuleApplicationResults> results = computeRuleApplicationResults(sequent, binding);
			List<Sequent> sequents =  ruleApplicationResultsAsSequent(results);
			// System.out.println("Rule 1 neo DEBUG sequents " + sequents);
			return SequentList.makeANDSequentList(sequents);
		}
		
		
		@Override
		public List<RuleApplicationResults> computeRuleApplicationResults(Sequent sequent, RuleBinding binding) throws Exception{
			List<RuleApplicationResults> results = new ArrayList<RuleApplicationResults>();
			// OWLSubClassOfAxiom axiom3;
			OWLFormula conclusionformula = null;
			// System.out.println("DEBUG -- newantecedent " + binding.getNewAntecedent());
			if (binding.getNewAntecedent()!=null){ // fast track
				conclusionformula = binding.getNewAntecedent();
				// System.out.println(" rule topintro is adding " + conclusionformula);
			} 
			RuleApplicationResults result = new RuleApplicationResults();
			result.setOriginalFormula(sequent);
			result.addAddition("A1",conclusionformula);
			results.add(result);
			result.setMaxFormulaDepth(InferenceApplicationService.computeRuleBindingMaxDepth(sequent, binding));
			return results;
		}
		
		
	}, // END TOPINTRO
	
	
	ELEXISTSMINUS{	@Override
	public java.lang.String getName(){return "ELEXISTSMINUS";};
	@Override
	public java.lang.String getShortName(){return "EE-";};
	
	private final OWLFormula prem = OWLFormula.createFormula(OWLSymb.SUBCL, 
			OWLFormula.createFormulaVar("v1"), 
			OWLFormula.createFormula(OWLSymb.EXISTS, 
					OWLFormula.createFormulaRoleVar("r1"),
					OWLFormula.createFormulaVar("v2")));

	
	@Override
	public List<RuleBinding> findRuleBindings(Sequent s){
		return findRuleBindings(s,false);
	}
	
	
	
	@Override
	public List<RuleBinding> findRuleBindings(Sequent s, boolean... one_suffices){
		// System.out.println("exists minus: find Rule bindings called ");
		ArrayList<RuleBinding> results = new ArrayList<RuleBinding>();
		List<OWLFormula> candidates = s.findMatchingFormulasInAntecedent(prem);
		// System.out.println("matching formulas length: " + candidates.size());
		for (OWLFormula candidate : candidates){
				// build resulting formula and check if contained
				OWLFormula resultformula = OWLFormula.createFormula(OWLSymb.SUBCL, candidate.getArgs().get(1).getArgs().get(1),candidate.getArgs().get(1).getArgs().get(1));
				// System.out.println(" this would be the result " + resultformula);
				if (!s.alreadyContainedInAntecedent(resultformula)){
					 List<Integer> list = new ArrayList<Integer>();
					 list.add(s.antecedentFormulaGetID(candidate));
					 SequentPosition position1 = new SequentSinglePosition(SequentPart.ANTECEDENT, list);
					 RuleBinding binding = new RuleBinding();
					 binding.insertPosition("A1", position1);
					 results.add(binding);
					 if (one_suffices.length>0 && one_suffices[0]==true){return results;}
						}
		}
		return results;
	}	
		
	
	@Override
	public List<RuleApplicationResults> computeRuleApplicationResults(Sequent sequent, RuleBinding binding) throws Exception{
		List<RuleApplicationResults> results = new ArrayList<RuleApplicationResults>();
		// System.out.println(" NGUYEN 2 called" + sequent  + binding.get("A1"));
		SequentPosition position1 = binding.get("A1");
		if (!(position1 instanceof SequentSinglePosition)){throw new Exception("unexpected position for rule2");}
		SequentSinglePosition pos1 = (SequentSinglePosition) position1;
		// attention, order of formulas can get swapped when cloning.
		OWLFormula formula = sequent.antecedentGetFormula(pos1.getToplevelPosition());
		// System.out.println("formula " + formula);
		OWLFormula formula3 = OWLFormula.createFormula(OWLSymb.SUBCL, formula.getArgs().get(1).getArgs().get(1), formula.getArgs().get(1).getArgs().get(1));
		// OWLSubClassOfAxiom axiom3 = (OWLSubClassOfAxiom) formula3.toOWLAPI();
			if (!sequent.alreadyContainedInAntecedent(formula3)){
				RuleApplicationResults result = new RuleApplicationResults();
				result.setOriginalFormula(sequent);
				result.addAddition("A1",formula3);
				results.add(result);
				result.setMaxFormulaDepth(InferenceApplicationService.computeRuleBindingMaxDepth(sequent, binding));
			    // System.out.println(" ELEXISTSMINUS computed consequence! " + axiom3);
			}
			// System.out.println("check after Nguyen2; sequent: " +sequent);
			// System.out.println("check after Nguyen2; s: " + s);
		return results;
	}
	
	@Override
	public SequentList computePremises(Sequent sequent, RuleBinding binding) throws Exception{
		List<RuleApplicationResults> results = computeRuleApplicationResults(sequent, binding);
		List<Sequent> sequents =  ruleApplicationResultsAsSequent(results);
		return SequentList.makeANDSequentList(sequents);
	}
	
	@Override
	public List<RuleKind> qualifyRule(){
		RuleKind[] a = {RuleKind.FORWARD};
		return Arrays.asList(a);
	}
	
}, // ELEXISTSMINUS

FORALLMINUS{	@Override
public java.lang.String getName(){return "FORALLMINUS";};
@Override
public java.lang.String getShortName(){return "F-";};

private final OWLFormula prem = OWLFormula.createFormula(OWLSymb.SUBCL, 
		OWLFormula.createFormulaVar("v1"), 
		OWLFormula.createFormula(OWLSymb.FORALL, 
				OWLFormula.createFormulaRoleVar("r1"),
				OWLFormula.createFormulaVar("v2")));


@Override
public List<RuleBinding> findRuleBindings(Sequent s){
	return findRuleBindings(s,false);
}



@Override
public List<RuleBinding> findRuleBindings(Sequent s, boolean... one_suffices){
	// System.out.println("exists minus: find Rule bindings called ");
	ArrayList<RuleBinding> results = new ArrayList<RuleBinding>();
	List<OWLFormula> candidates = s.findMatchingFormulasInAntecedent(prem);
	// System.out.println("matching formulas length: " + candidates.size());
	for (OWLFormula candidate : candidates){
			// build resulting formula and check if contained
			OWLFormula resultformula = OWLFormula.createFormula(OWLSymb.SUBCL, candidate.getArgs().get(1).getArgs().get(1),candidate.getArgs().get(1).getArgs().get(1));
			// System.out.println(" this would be the result " + resultformula);
			if (!s.alreadyContainedInAntecedent(resultformula)){
				 List<Integer> list = new ArrayList<Integer>();
				 list.add(s.antecedentFormulaGetID(candidate));
				 SequentPosition position1 = new SequentSinglePosition(SequentPart.ANTECEDENT, list);
				 RuleBinding binding = new RuleBinding();
				 binding.insertPosition("A1", position1);
				 results.add(binding);
				 if (one_suffices.length>0 && one_suffices[0]==true){return results;}
					}
	}
	return results;
}	
	


@Override
public List<RuleApplicationResults> computeRuleApplicationResults(Sequent sequent, RuleBinding binding) throws Exception{
	List<RuleApplicationResults> results = new ArrayList<RuleApplicationResults>();
	// System.out.println(" NGUYEN 2 called" + sequent  + binding.get("A1"));
	SequentPosition position1 = binding.get("A1");
	if (!(position1 instanceof SequentSinglePosition)){throw new Exception("unexpected position for forall minus");}
	SequentSinglePosition pos1 = (SequentSinglePosition) position1;
	// attention, order of formulas can get swapped when cloning.
	OWLFormula formula = sequent.antecedentGetFormula(pos1.getToplevelPosition());
	// System.out.println("formula " + formula);
	OWLFormula formula3 = OWLFormula.createFormula(OWLSymb.SUBCL, formula.getArgs().get(1).getArgs().get(1), formula.getArgs().get(1).getArgs().get(1));
	// OWLSubClassOfAxiom axiom3 = (OWLSubClassOfAxiom) formula3.toOWLAPI();
		if (!sequent.alreadyContainedInAntecedent(formula3)){
			RuleApplicationResults result = new RuleApplicationResults();
			result.setOriginalFormula(sequent);
			result.addAddition("A1",formula3);
			results.add(result);
			result.setMaxFormulaDepth(InferenceApplicationService.computeRuleBindingMaxDepth(sequent, binding));
		    // System.out.println(" ELEXISTSMINUS computed consequence! " + axiom3);
		}
		// System.out.println("check after Nguyen2; sequent: " +sequent);
		// System.out.println("check after Nguyen2; s: " + s);
	return results;
}

@Override
public SequentList computePremises(Sequent sequent, RuleBinding binding) throws Exception{
	List<RuleApplicationResults> results = computeRuleApplicationResults(sequent, binding);
	List<Sequent> sequents =  ruleApplicationResultsAsSequent(results);
	return SequentList.makeANDSequentList(sequents);
}

@Override
public List<RuleKind> qualifyRule(){
	RuleKind[] a = {RuleKind.FORWARD};
	return Arrays.asList(a);
}

}, // ELEXISTSMINUS

// union minus applies to unions on the right
UNIONMINUS{	@Override
public java.lang.String getName(){return "UNIONMINUS";};
@Override
public java.lang.String getShortName(){return "F-";};

private final OWLFormula prem = OWLFormula.createFormula(OWLSymb.SUBCL, 
		OWLFormula.createFormulaVar("v1"), 
		OWLFormula.createFormula(OWLSymb.UNION, 
				OWLFormula.createFormulaRoleVar("r1"),
				OWLFormula.createFormulaVar("v2")));


@Override
public List<RuleBinding> findRuleBindings(Sequent s){
	return findRuleBindings(s,false);
}



@Override
public List<RuleBinding> findRuleBindings(Sequent s, boolean... one_suffices){
	// System.out.println("exists minus: find Rule bindings called ");
	ArrayList<RuleBinding> results = new ArrayList<RuleBinding>();
	List<OWLFormula> candidates = s.findMatchingFormulasInAntecedent(prem);
	// System.out.println("matching formulas length: " + candidates.size());
	for (OWLFormula candidate : candidates){
			// build first resulting formula and check if contained
			OWLFormula resultformula1 = OWLFormula.createFormula(OWLSymb.SUBCL, candidate.getArgs().get(1).getArgs().get(0),candidate.getArgs().get(1).getArgs().get(0));
			// System.out.println(" this would be the result " + resultformula);
			if (!s.alreadyContainedInAntecedent(resultformula1)){
				 List<Integer> list = new ArrayList<Integer>();
				 list.add(s.antecedentFormulaGetID(candidate));
				 SequentPosition position1 = new SequentSinglePosition(SequentPart.ANTECEDENT, list);
				 RuleBinding binding = new RuleBinding();
				 binding.insertPosition("A1", position1);
				 results.add(binding);
				 if (one_suffices.length>0 && one_suffices[0]==true){return results;}
					}
			// build second resulting formula and check if contained
		OWLFormula resultformula2 = OWLFormula.createFormula(OWLSymb.SUBCL, candidate.getArgs().get(1).getArgs().get(1),candidate.getArgs().get(1).getArgs().get(1));
		// System.out.println(" this would be the result " + resultformula);
		if (!s.alreadyContainedInAntecedent(resultformula1)){
			List<Integer> list = new ArrayList<Integer>();
			list.add(s.antecedentFormulaGetID(candidate));
			SequentPosition position1 = new SequentSinglePosition(SequentPart.ANTECEDENT, list);
			RuleBinding binding = new RuleBinding();
			binding.insertPosition("A1", position1);
			results.add(binding);
			if (one_suffices.length>0 && one_suffices[0]==true){return results;}
		}
	}
	return results;
}	
	


@Override
public List<RuleApplicationResults> computeRuleApplicationResults(Sequent sequent, RuleBinding binding) throws Exception{
	List<RuleApplicationResults> results = new ArrayList<RuleApplicationResults>();
	// System.out.println(" NGUYEN 2 called" + sequent  + binding.get("A1"));
	SequentPosition position1 = binding.get("A1");
	if (!(position1 instanceof SequentSinglePosition)){throw new Exception("unexpected position for forall minus");}
	SequentSinglePosition pos1 = (SequentSinglePosition) position1;
	// attention, order of formulas can get swapped when cloning.
	OWLFormula formula = sequent.antecedentGetFormula(pos1.getToplevelPosition());
	// System.out.println("formula " + formula);
	OWLFormula formula3 = OWLFormula.createFormula(OWLSymb.SUBCL, formula.getArgs().get(1).getArgs().get(1), formula.getArgs().get(1).getArgs().get(1));
	// OWLSubClassOfAxiom axiom3 = (OWLSubClassOfAxiom) formula3.toOWLAPI();
		if (!sequent.alreadyContainedInAntecedent(formula3)){
			RuleApplicationResults result = new RuleApplicationResults();
			result.setOriginalFormula(sequent);
			result.addAddition("A1",formula3);
			results.add(result);
			result.setMaxFormulaDepth(InferenceApplicationService.computeRuleBindingMaxDepth(sequent, binding));
		    // System.out.println(" ELEXISTSMINUS computed consequence! " + axiom3);
		}
		// System.out.println("check after Nguyen2; sequent: " +sequent);
		// System.out.println("check after Nguyen2; s: " + s);
	return results;
}

@Override
public SequentList computePremises(Sequent sequent, RuleBinding binding) throws Exception{
	List<RuleApplicationResults> results = computeRuleApplicationResults(sequent, binding);
	List<Sequent> sequents =  ruleApplicationResultsAsSequent(results);
	return SequentList.makeANDSequentList(sequents);
}

@Override
public List<RuleKind> qualifyRule(){
	RuleKind[] a = {RuleKind.FORWARD};
	return Arrays.asList(a);
}

}, // ELEXISTSMINUS


// for a formula of the form "A subcl B union C" in the antecedent, create two premise sequents where the original axiom is 
// replaced by "A subcl B" and "A subcl C", respectively
UNIONELIM{	@Override
public java.lang.String getName(){return "UNIONELIM";};
@Override
public java.lang.String getShortName(){return "U-";};

// Formula of the form "A subcl B union C"
private final OWLFormula prem = OWLFormula.createFormula(OWLSymb.SUBCL, 
		OWLFormula.createFormulaVar("v1"), 
		OWLFormula.createFormula(OWLSymb.UNION, 
				OWLFormula.createFormulaVar("v2"), 
				OWLFormula.createFormulaVar("v3")));


@Override
public List<RuleBinding> findRuleBindings(Sequent s){
	return findRuleBindings(s,false);
}

@Override
public List<RuleBinding> findRuleBindings(Sequent s, boolean... one_suffices){
	// System.out.println("union minus: find Rule bindings called ");
	ArrayList<RuleBinding> results = new ArrayList<RuleBinding>();
	List<OWLFormula> candidates = s.findMatchingFormulasInAntecedent(prem);
	// System.out.println("matching formulas length: " + candidates.size());
	for (OWLFormula candidate : candidates){
				 // first try to find situations where application of this rule is not desirable
				 // (i) If the left-hand-side of the subsumption contains a disjunction at the top level
				 //     -- this does not restrict completeness - the union on the left side can always be decomposed
				 if (candidate.getArgs().get(0).getHead().equals(OWLSymb.UNION)){
					 continue;
				 }
				 // System.out.println("DEBUG -- candidate: " + candidate);
				 List<Integer> list = new ArrayList<Integer>();
				 list.add(s.antecedentFormulaGetID(candidate));
				 SequentPosition position1 = new SequentSinglePosition(SequentPart.ANTECEDENT, list);
				 RuleBinding binding = new RuleBinding();
				 binding.insertPosition("A1", position1);
				 results.add(binding);
				 if (one_suffices.length>0 && one_suffices[0]==true){return results;}
	}
	return results;
}	
	

@Override
public List<RuleApplicationResults> computeRuleApplicationResults(Sequent sequent, RuleBinding binding) throws Exception{
	List<RuleApplicationResults> results = new ArrayList<RuleApplicationResults>();
	// System.out.println(" NGUYEN 2 called" + sequent  + binding.get("A1"));
	SequentPosition position1 = binding.get("A1");
	if (!(position1 instanceof SequentSinglePosition)){throw new Exception("unexpected position for rule2");}
	SequentSinglePosition pos1 = (SequentSinglePosition) position1;
	OWLFormula formula = sequent.antecedentGetFormula(pos1.getToplevelPosition());
	
	OWLFormula subA = formula.getArgs().get(0); 
	OWLFormula subB = formula.getArgs().get(1).getArgs().get(0); 
	OWLFormula subC = formula.getArgs().get(1).getArgs().get(1); 
	
	OWLFormula formula1 = OWLFormula.createFormula(OWLSymb.SUBCL, subA,subB);	
	OWLFormula formula2 = OWLFormula.createFormula(OWLSymb.SUBCL, subA,subC);
	
	// System.out.println("DEBUG -- union elim formula1: " + formula1);
	// System.out.println("DEBUG -- union elim formula2: " + formula2);
	
	// OWLSubClassOfAxiom axiom3 = (OWLSubClassOfAxiom) formula3.toOWLAPI();
    // two premise sequents are produced by generating a "result" for each of them
	RuleApplicationResults result1 = new RuleApplicationResults();
	result1.setOriginalFormula(sequent);
	result1.addAddition("A1",formula1);
	result1.addDeletion("A1",formula);
	results.add(result1);
	result1.setMaxFormulaDepth(InferenceApplicationService.computeRuleBindingMaxDepth(sequent, binding));
	
	RuleApplicationResults result2 = new RuleApplicationResults();
	result2.setOriginalFormula(sequent);
	result2.addAddition("A1",formula2);
	result2.addDeletion("A1",formula);
	results.add(result2);
	result2.setMaxFormulaDepth(InferenceApplicationService.computeRuleBindingMaxDepth(sequent, binding));
		    // System.out.println(" ELEXISTSMINUS computed consequence! " + axiom3);
		
		// System.out.println("check after Nguyen2; sequent: " +sequent);
		// System.out.println("check after Nguyen2; s: " + s);
	return results;
}

@Override
public SequentList computePremises(Sequent sequent, RuleBinding binding) throws Exception{
	List<RuleApplicationResults> results = computeRuleApplicationResults(sequent, binding);
	List<Sequent> sequents =  ruleApplicationResultsAsSequent(results);
	return SequentList.makeANDSequentList(sequents);
}

@Override
public List<RuleKind> qualifyRule(){
	RuleKind[] a = {RuleKind.FORWARD};
	return Arrays.asList(a);
}

}, // ELEXISTSMINUS


EQUIVEXTRACT{	@Override
public java.lang.String getName(){return "EQUIVEXTRACT";};
@Override
public java.lang.String getShortName(){return "EE-";};

private final OWLFormula prem1 = OWLFormula.createFormula(OWLSymb.EQUIV, 
		OWLFormula.createFormulaVar("v1"), 
		OWLFormula.createFormula(OWLSymb.INT,OWLFormula.createFormulaVar("v2"), OWLFormula.createFormulaVar("v3")));

@Override
public List<RuleBinding> findRuleBindings(Sequent s){
	return findRuleBindings(s,false);
}

public List<SequentSinglePosition> collectAllConjunctPositions(OWLFormula formula, SequentSinglePosition currentposition){
	List<SequentSinglePosition> results = new ArrayList<SequentSinglePosition>();
	if (formula.getHead().equals(OWLSymb.INT)){ // RECURSIVE CASE
			List<OWLFormula> subterms = formula.getArgs();
			// for (OWLFormula subterm : subterms){
			for (int i = 0; i<subterms.size();i++){
				SequentSinglePosition newcurrentposition = new SequentSinglePosition(currentposition.getSequentPart(),currentposition.getPosition());
				newcurrentposition.getPosition().add(i);
				results.addAll(collectAllConjunctPositions(subterms.get(i),newcurrentposition));
			}
	} else { // WE ARE AT A 'LEAF'
		results.add(currentposition);
	}
	return results;
}

public List<SequentSinglePosition> collectImmediateConjunctPositions(OWLFormula formula, SequentSinglePosition currentposition){
	List<SequentSinglePosition> results = new ArrayList<SequentSinglePosition>();
	if (formula.getHead().equals(OWLSymb.INT)){ // RECURSIVE CASE
			List<OWLFormula> subterms = formula.getArgs();
			for (int i = 0; i<subterms.size();i++){
				SequentSinglePosition newcurrentposition = new SequentSinglePosition(currentposition.getSequentPart(),currentposition.getPosition());
				newcurrentposition.getPosition().add(i);
				results.add(newcurrentposition);
			}
	} else { // WE ARE AT A 'LEAF'
		results.add(currentposition);
	}
	return results;
}


@Override
public List<RuleBinding> findRuleBindings(Sequent s, boolean... one_suffices){
	ArrayList<RuleBinding> results = new ArrayList<RuleBinding>();

	List<OWLFormula> candidates = s.findMatchingFormulasInAntecedent(prem1);
	
	for (int i = 0 ; i < candidates.size(); i++){
		
		List<Integer> pos1 = new ArrayList<Integer>();
		pos1.add(s.antecedentFormulaGetID(candidates.get(i)));
		pos1.add(1);
		SequentSinglePosition currentposition = new SequentSinglePosition(SequentPart.ANTECEDENT,pos1);
		List<SequentSinglePosition> foundPositions = collectAllConjunctPositions(candidates.get(i).getArgs().get(1),currentposition);
		// System.out.println("FOUND POSITIONS " + foundPositions);
		List<SequentSinglePosition> immediatePositions = collectImmediateConjunctPositions(candidates.get(i).getArgs().get(1),currentposition);
		// System.out.println("IMMEDIATE POSITIONS " + immediatePositions);
		List<SequentSinglePosition> foundPos2 = new ArrayList<SequentSinglePosition>(foundPositions);
		foundPos2.retainAll(immediatePositions);
		if(foundPos2.size()==foundPositions.size()){
			continue;
		}
		
		for (SequentSinglePosition pos : foundPositions){
			OWLFormula resultformula = OWLFormula.createFormula(OWLSymb.SUBCL, 
					candidates.get(i).getArgs().get(0),
					s.getOWLSubformula(pos));
			// System.out.println("equiv extract, checking out " + resultformula);
			if (!s.alreadyContainedInAntecedent(resultformula)){
				RuleBinding binding = new RuleBinding(resultformula,null);
				 binding.insertPosition("A1", pos);
				 results.add(binding);
		}
		}
	}
		// System.out.println("Results : " + results);
		return results;
	}
		
@Override
public List<RuleApplicationResults> computeRuleApplicationResults(Sequent sequent, RuleBinding binding) throws Exception{
	List<RuleApplicationResults> results = new ArrayList<RuleApplicationResults>();
	// System.out.println(" NGUYEN union intor called" + sequent  + binding.get("A1"));
	
	
	OWLFormula formula3 = binding.getNewAntecedent();
	// System.out.println("Formula 3 " + formula3);
		if (!sequent.alreadyContainedInAntecedent(formula3)){
			RuleApplicationResults result = new RuleApplicationResults();
			result.setOriginalFormula(sequent);
			result.addAddition("A1",formula3);
			results.add(result);
			result.setMaxFormulaDepth(InferenceApplicationService.computeRuleBindingMaxDepth(sequent, binding));
		    // System.out.println(" equiv extract computed consequence! " + formula3);
		}
		// System.out.println("check after Nguyen2; sequent: " +sequent);
		// System.out.println("check after Nguyen2; s: " + s);
	return results;
}

@Override
public SequentList computePremises(Sequent sequent, RuleBinding binding) throws Exception{
	List<RuleApplicationResults> results = computeRuleApplicationResults(sequent, binding);
	List<Sequent> sequents =  ruleApplicationResultsAsSequent(results);
	return SequentList.makeANDSequentList(sequents);
}

@Override
public List<RuleKind> qualifyRule(){
	RuleKind[] a = {RuleKind.FORWARD};
	return Arrays.asList(a);
}

}, // EQUIVEXTRACT


UNIONINTRO{	@Override
public java.lang.String getName(){return "UNIONINTRO";};
@Override
public java.lang.String getShortName(){return "EU+";};

private final OWLFormula prem1 = OWLFormula.createFormula(OWLSymb.SUBCL, 
		OWLFormula.createFormulaVar("v1"), 
		OWLFormula.createFormulaVar("v2"));

private final OWLFormula prem2 = OWLFormula.createFormula(OWLSymb.UNION, 
		OWLFormula.createFormulaVar("v2"),
		OWLFormula.createFormulaVar("v3"));

private final OWLFormula prem3 = OWLFormula.createFormula(OWLSymb.UNION, 
		OWLFormula.createFormulaVar("v3"),
		OWLFormula.createFormulaVar("v2"));


@Override
public List<RuleBinding> findRuleBindings(Sequent s){
	return findRuleBindings(s,false);
}


public OWLFormula cutDeeplyMatchedFormula(OWLFormula pattern, OWLFormula found){
	if (found==null || pattern == null){
		return null;
	}
	// System.out.println("cutting with " + pattern + " " + found);
	if (found!=null && found.getArgs()!=null && found.getArgs().size()>0){
		for(OWLFormula arg : found.getArgs()){
			if (cutDeeplyMatchedFormula(pattern,arg)!=null){
				return cutDeeplyMatchedFormula(pattern,arg);
			}
			}
	}
	if (found.getHead().equals(pattern.getHead())){
		try {
			// System.out.println("heads match, " + found.getHead() + " found: " + found + " pattern " + pattern);
			if (found!=null && found.match(pattern).size()>0){
				return found;
			}
		}
			catch (Exception e) {}
	}
	
	return null;
}

@Override
public List<RuleBinding> findRuleBindings(Sequent s, boolean... one_suffices){
	ArrayList<RuleBinding> results = new ArrayList<RuleBinding>();

	List<OWLFormula> candidates = s.findMatchingFormulasInAntecedent(prem1);
	Collections.sort(candidates,s.formulaAscComparatorAnt());
	for (int i = 0 ; i < candidates.size(); i++){
		List<Pair<OWLFormula, OWLFormula>> matcher;
		try {
			matcher = candidates.get(i).match(prem1);
			OWLFormula prem_2 = prem2.applyMatcher(matcher);
			OWLFormula prem_3 = prem3.applyMatcher(matcher);
			// System.out.println("prem2 " + prem_2);
			// System.out.println("prem3 " + prem_3);
			HashSet<OWLFormula> newcandidates1 = new HashSet<OWLFormula>();
			HashSet<OWLFormula> newcandidates2 = new HashSet<OWLFormula>();
			HashSet<OWLFormula> newcandidates = new HashSet<OWLFormula>();
			newcandidates1.addAll(s.findMatchingFormulasInAntecedentDeeply(prem_2));
			// System.out.println("newcandidates1 " + newcandidates1);
			for(OWLFormula newcandidate : newcandidates1){
				if (cutDeeplyMatchedFormula(prem_2,newcandidate)!=null){
				newcandidates.add(cutDeeplyMatchedFormula(prem_2,newcandidate));
			}
				if (cutDeeplyMatchedFormula(prem_3,newcandidate)!=null){
					newcandidates.add(cutDeeplyMatchedFormula(prem_3,newcandidate));
				}
			}
			newcandidates2.addAll(s.findMatchingFormulasInAntecedentDeeply(prem_3));
			for(OWLFormula newcandidate : newcandidates2){
				if (cutDeeplyMatchedFormula(prem_3,newcandidate)!=null){
				newcandidates.add(cutDeeplyMatchedFormula(prem_3,newcandidate));
				}
				if (cutDeeplyMatchedFormula(prem_2,newcandidate)!=null){
					newcandidates.add(cutDeeplyMatchedFormula(prem_2,newcandidate));
					}
			}
			// System.out.println("newcandidates2 " + newcandidates2);
			// System.out.println("newcandidates" + newcandidates);
			for(OWLFormula newcandidate : newcandidates){
				// System.out.println("part 1" + candidates.get(i).getArgs().get(0));
				// System.out.println("part 2" + newcandidate);
				OWLFormula resultformula = OWLFormula.createFormula(OWLSymb.SUBCL, candidates.get(i).getArgs().get(0),newcandidate);
				// System.out.println("resultformula " + resultformula);
				if (!s.alreadyContainedInAntecedent(resultformula)){
					 List<Integer> list = new ArrayList<Integer>();
					 list.add(s.antecedentFormulaGetID(candidates.get(i)));
					 SequentPosition position1 = new SequentSinglePosition(SequentPart.ANTECEDENT, list);
					 RuleBinding binding = new RuleBinding(resultformula,null);
					 binding.insertPosition("A1", position1);
					 results.add(binding);
					 // System.out.println("new antecedent from binding: " + binding.getNewAntecedent());
					 if (one_suffices.length>0 && one_suffices[0]==true){return results;}
						}	
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("prem 2 " + prem2 +  " Matcher: " + matcher);
		// now build new formula using the matcher
	}
	// System.out.println("results " + results);
	for (RuleBinding rb: results){
		// System.out.println(rb.getNewAntecedent());
	}
	return results;
}	
	

@Override
public List<RuleApplicationResults> computeRuleApplicationResults(Sequent sequent, RuleBinding binding) throws Exception{
	List<RuleApplicationResults> results = new ArrayList<RuleApplicationResults>();
	// System.out.println(" NGUYEN union intor called" + sequent  + binding.get("A1"));
	
	/*
	SequentPosition position1 = binding.get("A1");
	if (!(position1 instanceof SequentSinglePosition)){throw new Exception("unexpected position for rule union intro ");}
	SequentSinglePosition pos1 = (SequentSinglePosition) position1;
	SequentPosition position2 = binding.get("A2");
	if (!(position1 instanceof SequentSinglePosition)){throw new Exception("unexpected position for rule union intro ");}
	SequentSinglePosition pos2 = (SequentSinglePosition) position2;
	// attention, order of formulas can get swapped when cloning.
	OWLFormula formula1 = sequent.antecedentGetFormula(pos1.getToplevelPosition());
	OWLFormula formula2 = sequent.antecedentGetFormula(pos2.getToplevelPosition());
	// System.out.println("formula " + formula);
	OWLFormula formula_uni = formula2.getArgs().get(0); 
	// System.out.println("subposition " + subposition);
	// System.out.println("formula_int " + formula_int);
	// System.out.println("formula2 " + formula2);
	 * */
	// OWLFormula formula3 = OWLFormula.createFormula(OWLSymb.SUBCL, formula1.getArgs().get(0), formula_uni);
	OWLFormula formula3 = binding.getNewAntecedent();
	// System.out.println("Formula 3 " + formula3);
	// OWLSubClassOfAxiom axiom3 = (OWLSubClassOfAxiom) formula3.toOWLAPI();
		if (!sequent.alreadyContainedInAntecedent(formula3)){
			RuleApplicationResults result = new RuleApplicationResults();
			result.setOriginalFormula(sequent);
			result.addAddition("A1",formula3);
			results.add(result);
		    // System.out.println(" union intro computed consequence! " + formula3);
		}
		// System.out.println("check after Nguyen2; sequent: " +sequent);
		// System.out.println("check after Nguyen2; s: " + s);
	return results;
}

@Override
public SequentList computePremises(Sequent sequent, RuleBinding binding) throws Exception{
	List<RuleApplicationResults> results = computeRuleApplicationResults(sequent, binding);
	List<Sequent> sequents =  ruleApplicationResultsAsSequent(results);
	return SequentList.makeANDSequentList(sequents);
}

@Override
public List<RuleKind> qualifyRule(){
	RuleKind[] a = {RuleKind.FORWARD};
	return Arrays.asList(a);
}

}, // ELEXISTSMINUS


	
	
	
	

	// \forall r.\bot \subset X ^ \exists r.\top \subset X --> \top \subset X

		DEFDOMAIN{	@Override
		public java.lang.String getName(){return "AdditionalDLRules-DefinitionOfDomain";};
		@Override
		public java.lang.String getShortName(){return "dd";};
		
		private final OWLFormula domainFormula = OWLFormula.createFormula(OWLSymb.DOMAIN, 
				OWLFormula.createFormulaRoleVar("r1"),
				OWLFormula.createFormulaVar("v1"));
		
		
		@Override
		public List<RuleBinding> findRuleBindings(Sequent s){
			// System.out.println("DEFDOMAIN FIND RULE BINDING CALLED");
			ArrayList<RuleBinding> results = new ArrayList<RuleBinding>();
			List<OWLFormula> candidates = s.findMatchingFormulasInAntecedent(domainFormula);
			for (OWLFormula candidate : candidates){
				// System.out.println("DEFDOMAIN FOUND SUITABLE DOMAIN AXIOM " + domainFormula);
				OWLFormula conclusion = OWLFormula.createFormula(OWLSymb.SUBCL, 
						OWLFormula.createFormula(OWLSymb.EXISTS, 
								                candidate.getArgs().get(0),
								                OWLFormula.createFormulaTop()),
						candidate.getArgs().get(1));
				if (!s.alreadyContainedInAntecedent(conclusion)){
					RuleBinding binding = new RuleBinding(conclusion,null);	
				    List<Integer> list1 = new ArrayList<Integer>();
				    list1.add(s.antecedentFormulaGetID(candidate));
				    
					SequentPosition position1 = new SequentSinglePosition(SequentPart.ANTECEDENT, list1);
					binding.insertPosition("A1",position1);
					results.add(binding);	
				}
				
			}
			return results;
			
		}
				/* 
				public List<RuleBinding> findRuleBindings(Sequent s){
					// System.out.println("DEFDOMAIN FIND RULE BINDING CALLED");
					ArrayList<RuleBinding> results = new ArrayList<RuleBinding>();
					if (s.isEmptyP()) return results;
					ArrayList antecedent = s.getAntecedent();
					int count1 = 0;
					// System.out.println(antecedent);
					for (Object formula : antecedent){
						if (formula instanceof OWLObjectPropertyDomainAxiom){
							try {
							// System.out.println("DEFDOMAIN FOUND SUITABLE DOMAIN AXIOM " + formula);
							OWLObjectPropertyDomainAxiom axiom = (OWLObjectPropertyDomainAxiom) formula;
							OWLObjectPropertyExpression property = axiom.getProperty();	
							OWLClassExpression domain = axiom.getDomain();
							OWLDataFactory dataFactory= OWLManager.createOWLOntologyManager().getOWLDataFactory();
							OWLSubClassOfAxiom axiom3 = dataFactory.getOWLSubClassOfAxiom(dataFactory.getOWLObjectSomeValuesFrom(property,dataFactory.getOWLThing()),domain);		
							OWLFormula conclusion = OWLFormula.fromOWLAPI(axiom3);
							
							if (!antecedent.contains(axiom3)){
							RuleBinding binding = new RuleBinding(conclusion,null);	
						    List<Integer> list1 = new ArrayList<Integer>();
						    
							SequentPosition position1 = new SequentSinglePosition(SequentPart.ANTECEDENT, list1);
							binding.insertPosition("A1", position1);
							results.add(binding);
							}
							} catch (LanguageFeatureMissingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							} // end of if
						
						count1++;		
					} // end outer for loop
					System.out.println("DEFDOMAIN " + results);
					return results;
				}
				
				*/
					
				@Override
				public SequentList computePremises(Sequent sequent, RuleBinding binding) throws Exception{
					List<RuleApplicationResults> results = computeRuleApplicationResults(sequent, binding);
					List<Sequent> sequents =  ruleApplicationResultsAsSequent(results);
					return SequentList.makeANDSequentList(sequents);
				}
				
				@Override
				public List<RuleApplicationResults> computeRuleApplicationResults(Sequent sequent, RuleBinding binding) throws Exception{
					List<RuleApplicationResults> results = new ArrayList<RuleApplicationResults>();
					if (binding.getNewAntecedent()!=null){
						// System.out.println("have new conclusion");
						OWLFormula newant = binding.getNewAntecedent();
						if (!sequent.alreadyContainedInAntecedent(newant)){
							RuleApplicationResults result = new RuleApplicationResults();
							result.setOriginalFormula(sequent);
							result.addAddition("A1",newant);
							results.add(result);
							result.setMaxFormulaDepth(InferenceApplicationService.computeRuleBindingMaxDepth(sequent, binding));
							// System.out.println("DEFDOMAIN ADDING " + newant);
						}
					}
					else{
						System.out.println("ADDITIONAL RULES; THIS SHOULD NOT HAPPEN!");
					}
					return results;
				}
				
				
				@Override
				public List<RuleKind> qualifyRule(){
					RuleKind[] a = {RuleKind.PSEUDOELIMINATION, RuleKind.FORWARD};
					return Arrays.asList(a);
				}
				
			}, // END DEFDOMAIN
		
		
		
		
		SUBCLANDEQUIVELIM{ // SubCla(X,Y) and Equiv(Y,Z) --> SubCla(X,Z)  (order of equiv does not matter)
			
			@Override
			public java.lang.String getName(){return "AdditionalDLRules-SubclassAndEquiv";};
			@Override
			public java.lang.String getShortName(){return "seE";};
			
			private final OWLFormula prem1 = OWLFormula.createFormula(OWLSymb.SUBCL, 
					OWLFormula.createFormulaVar("v1"), 
					OWLFormula.createFormulaVar("v2"));
			
			private final OWLFormula prem2a = OWLFormula.createFormula(OWLSymb.EQUIV, 
					OWLFormula.createFormulaVar("v2"), 
					OWLFormula.createFormulaVar("v3"));
			
			private final OWLFormula prem2b = OWLFormula.createFormula(OWLSymb.EQUIV, 
					OWLFormula.createFormulaVar("v3"), 
					OWLFormula.createFormulaVar("v2"));
			
			@Override
			public List<RuleBinding> findRuleBindings(Sequent s){
				ArrayList<RuleBinding> results = new ArrayList<RuleBinding>();
				if (s.isEmptyP()) return results;
				// collect all equiv and subcl formulas
				List<OWLFormula> candidates = s.findMatchingFormulasInAntecedent(prem1);
				// for each subclass formula, find matching equiv formula
				for (int i = 0 ; i < candidates.size(); i++){
						List<OWLFormula> candidates2a = new ArrayList<OWLFormula>();
						List<OWLFormula> candidates2b = new ArrayList<OWLFormula>();
						try{
							List<Pair<OWLFormula,OWLFormula>> matcher = candidates.get(i).match(prem1);
							// System.out.println("matcher: " + matcher);
							OWLFormula prem2_a = prem2a.applyMatcher(matcher);
							// System.out.println("prem2_a: " + prem2_a);
							OWLFormula prem2_b = prem2b.applyMatcher(matcher);
							// System.out.println("prem2_b: " + prem2_b);
							candidates2a = s.findMatchingFormulasInAntecedent(prem2_a);
							candidates2b = s.findMatchingFormulasInAntecedent(prem2_b);
							// System.out.println("debug :found candidates 2a " + candidates2a);
							// System.out.println("debug :found candidates 2b " + candidates2b);
						}
						catch (Exception e){
							// just do nothing
							// System.out.println("--");
						}		
						// remove candidates where conclusion is already contained in sequent
						List<OWLFormula> candidates2a_tmp = new ArrayList<OWLFormula>(candidates2a);
						candidates2a = new ArrayList<OWLFormula>();
						List<OWLFormula> candidates2b_tmp = new ArrayList<OWLFormula>(candidates2b);
						candidates2b = new ArrayList<OWLFormula>();
						// System.out.println("DEBUG SUBCLEQUIV (a) : " + candidates2a_tmp);
						// System.out.println("DEBUG SUBCLEQUIV (b) : " + candidates2b_tmp);
						for (int k = 0 ; k < candidates2a_tmp.size(); k++){
							OWLFormula resultformula = OWLFormula.createFormula(OWLSymb.SUBCL, 
									candidates.get(i).getArgs().get(0),
									candidates2a_tmp.get(k).getArgs().get(1));
								// System.out.println("debug resultformula + " +  resultformula);
									if (!(candidates.get(i).getArgs().get(0).equals(candidates2a_tmp.get(k).getArgs().get(1))   // trivial case
											||	s.alreadyContainedInAntecedent(resultformula)       
											)) 
										candidates2a.add(candidates2a_tmp.get(k));
						}	
						// System.out.println("DEBUG SUBCLEQUIV (a) after throwing out " + candidates2a);
						for (int k = 0 ; k < candidates2b_tmp.size(); k++){
							OWLFormula resultformula = OWLFormula.createFormula(OWLSymb.SUBCL, 
									candidates.get(i).getArgs().get(0),
									candidates2b_tmp.get(k).getArgs().get(0));
									if (!(candidates.get(i).getArgs().get(0).equals(candidates2b_tmp.get(k).getArgs().get(0))  
										||	s.alreadyContainedInAntecedent(resultformula))) 
										candidates2b.add(candidates2b_tmp.get(k));
						}	
						candidates2a.addAll(candidates2b);
						// System.out.println("Number of candidates: " + candidates2a.size());
						// generate bindings
						for (int k = 0 ; k < candidates2a.size(); k++){
							RuleBinding binding = new RuleBinding();
							// System.out.println("subclassandequivdebug form1: " + candidates.get(i));
							// System.out.println("subclassandequivdebug form1 classname: " + candidates.get(i).getArgs().get(0).getHead());
							// System.out.println("subclassandequivdebug form2: " + candidates2a.get(k));
							SequentPosition position1 = new SequentSinglePosition(SequentPart.ANTECEDENT, s.antecedentFormulaGetID(candidates.get(i)));
							binding.insertPosition("A1", position1);
							// System.out.println("position 1 " + position1);
							SequentPosition position2 = new SequentSinglePosition(SequentPart.ANTECEDENT, s.antecedentFormulaGetID(candidates2a.get(k)));
							binding.insertPosition("A2", position2);
							// System.out.println("position 2 " + position2);
							results.add(binding);
						}
				} // end large for loop
				return results;
			}
			
			@Override
			public List<RuleApplicationResults> computeRuleApplicationResults(Sequent sequent, RuleBinding binding) throws Exception{
				List<RuleApplicationResults> results = new ArrayList<RuleApplicationResults>();
				
				SequentPosition position1 = binding.get("A1");
				SequentPosition position2 = binding.get("A2");
				Sequent s = sequent.clone();
				ArrayList newsequents = new ArrayList<Sequent>();
				
				if (!(position1 instanceof SequentSinglePosition)){throw new Exception("unexpected position for subcl and equiv");}
				SequentSinglePosition pos1 = (SequentSinglePosition) position1;
				if (!(position2 instanceof SequentSinglePosition)){throw new Exception("unexpected position for subcl and equiv");}
				SequentSinglePosition pos2 = (SequentSinglePosition) position2;
				OWLFormula formula_1 = s.antecedentGetFormula(pos1.getToplevelPosition());
				OWLFormula formula_2 = s.antecedentGetFormula(pos2.getToplevelPosition());
				OWLFormula formula_3;
				OWLFormula superclass = formula_1.getArgs().get(1);
				OWLFormula equiv1 = formula_2.getArgs().get(0);
				OWLFormula equiv2 = formula_2.getArgs().get(1);
				if (superclass.equals(equiv1)){
					formula_3 = OWLFormula.createFormula(OWLSymb.SUBCL,formula_1.getArgs().get(0),
							equiv2);
				} else{
					formula_3 = OWLFormula.createFormula(OWLSymb.SUBCL,formula_1.getArgs().get(0),
							equiv1);
				}
				// System.out.println(" --- deubg formula1 : " + formula_1.getArgs().get(0));
				// System.out.println(" --- deubg formula2a : " + equiv2);
				// System.out.println(" --- deubg formula2b : " + equiv1);
				// System.out.println(" --- deubg  subclassandequiv debug resultformula! : " + formula_3);
				if (!formula_3.getArgs().get(0).equals(formula_3.getArgs().get(1)) && !s.alreadyContainedInAntecedent(formula_3)){
					RuleApplicationResults result1 = new RuleApplicationResults();
					result1.setOriginalFormula(s);
					result1.addAddition("A1", formula_3);
					results.add(result1);
					result1.setMaxFormulaDepth(InferenceApplicationService.computeRuleBindingMaxDepth(sequent, binding));
					}
				/*
				Object formula1 = formula_1.toOWLAPI();
				Object formula2 = formula_2.toOWLAPI();
				System.out.println("formula1 " + formula1);
				System.out.println("formula2 " + formula2);
				if (formula1 instanceof OWLSubClassOfAxiom && formula2 instanceof OWLEquivalentClassesAxiom){
					OWLSubClassOfAxiom axiom3;
					OWLSubClassOfAxiom axiom1 = (OWLSubClassOfAxiom) formula1;
					OWLEquivalentClassesAxiom axiom2 = (OWLEquivalentClassesAxiom) formula2;
					OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
					OWLDataFactory dataFactory=manager.getOWLDataFactory();
					if (axiom1.getSuperClass().equals(axiom2.getClassExpressionsAsList().get(0))){
						axiom3= dataFactory.getOWLSubClassOfAxiom(axiom1.getSubClass(),axiom2.getClassExpressionsAsList().get(1));
					}else{
						axiom3= dataFactory.getOWLSubClassOfAxiom(axiom1.getSubClass(),axiom2.getClassExpressionsAsList().get(0));
					}
					System.out.println("resultformula! : " + axiom3);
					if (!antecedent.contains(axiom3)){
						RuleApplicationResults result1 = new RuleApplicationResults();
						result1.setOriginalFormula(s);
						result1.addAddition("A1", axiom3);
						results.add(result1);}
				}
				*/
				return results;
				
			}
			
			@Override
			public SequentList computePremises(Sequent sequent, RuleBinding binding) throws Exception{
				List<RuleApplicationResults> results = computeRuleApplicationResults(sequent, binding);
				List<Sequent> sequents =  ruleApplicationResultsAsSequent(results);
				return SequentList.makeANDSequentList(sequents);
			}
			
			/// TODO: THIS IS JUST COPIED, IT IS NOT IMPLEMENTED FOR THIS OBJECT!
			@Override
			public void expandTactic(ProofTree tree, ProofNode<Sequent,java.lang.String,AbstractSequentPositions> source, JustificationNode<java.lang.String,AbstractSequentPositions> justification) throws Exception{
				return;
							}
							
			
			
		}, // END RULE31
		
/*
TRANSOBJECTPROPERTY{ // transitive(rel) and SubCla(A,exists rel.B) and SubCla(B,exists rel. C) --> SubCla(A,exists rel.C)  (order of equiv does not matter)
			
			public java.lang.String getName(){return "AdditionalDLRules-SubclassAndEquiv";};
			public java.lang.String getShortName(){return "seE";};
			
			private final OWLFormula prem1 = OWLFormula.createFormula(OWLSymb.SUBCL, 
					OWLFormula.createFormulaVar("v1"), 
					OWLFormula.createFormulaVar("v2"));
			
			private final OWLFormula prem2a = OWLFormula.createFormula(OWLSymb.EQUIV, 
					OWLFormula.createFormulaVar("v2"), 
					OWLFormula.createFormulaVar("v3"));
			
			private final OWLFormula prem2b = OWLFormula.createFormula(OWLSymb.EQUIV, 
					OWLFormula.createFormulaVar("v3"), 
					OWLFormula.createFormulaVar("v2"));
			
			public List<RuleBinding> findRuleBindings(Sequent s){
				ArrayList<RuleBinding> results = new ArrayList<RuleBinding>();
				if (s.isEmptyP()) return results;
				// collect all equiv and subcl formulas
				List<OWLFormula> candidates = s.findMatchingFormulasInAntecedent(prem1);
				// for each subclass formula, find matching equiv formula
				for (int i = 0 ; i < candidates.size(); i++){
						List<OWLFormula> candidates2a = new ArrayList<OWLFormula>();
						List<OWLFormula> candidates2b = new ArrayList<OWLFormula>();
						try{
							List<Pair<OWLFormula,OWLFormula>> matcher = candidates.get(i).match(prem1);
							OWLFormula prem2_a = prem2a.applyMatcher(matcher);
							// System.out.println("prem2_a: " + prem2_a);
							OWLFormula prem2_b = prem2b.applyMatcher(matcher);
							candidates2a = s.findMatchingFormulasInAntecedent(prem2_a);
							candidates2b = s.findMatchingFormulasInAntecedent(prem2_b);
						}
						catch (Exception e){
							// just do nothing
							// System.out.println("--");
						}		
						// remove candidates where conclusion is already contained in sequent
						for (int k = 0 ; k < candidates2a.size(); k++){
							OWLFormula resultformula = OWLFormula.createFormula(OWLSymb.SUBCL, 
									candidates.get(i).getArgs().get(0),
									candidates2a.get(k).getArgs().get(1));
									if (candidates.get(i).getArgs().get(0).equals(candidates2a.get(k).getArgs().get(1))   // trivial case
											||	s.alreadyContainedInAntecedent(resultformula)       
											) 
										candidates2a.remove(candidates2a.get(k));
						}	
						for (int k = 0 ; k < candidates2b.size(); k++){
							OWLFormula resultformula = OWLFormula.createFormula(OWLSymb.SUBCL, 
									candidates.get(i).getArgs().get(0),
									candidates2b.get(k).getArgs().get(0));
									if (candidates.get(i).getArgs().get(0).equals(candidates2b.get(k).getArgs().get(0))  
										||	s.alreadyContainedInAntecedent(resultformula)) 
										candidates2b.remove(candidates2b.get(k));
						}	
						candidates2a.addAll(candidates2b);
						// System.out.println("Number of candidates: " + candidates2a.size());
						// generate bindings
						for (int k = 0 ; k < candidates2a.size(); k++){
							RuleBinding binding = new RuleBinding();
							SequentPosition position1 = new SequentSinglePosition(SequentPart.ANTECEDENT, s.antecedentFormulaGetID(candidates.get(i)));
							binding.insertPosition("A1", position1);
							// System.out.println("position 1 " + position1);
							SequentPosition position2 = new SequentSinglePosition(SequentPart.ANTECEDENT, s.antecedentFormulaGetID(candidates2a.get(k)));
							binding.insertPosition("A2", position2);
							// System.out.println("position 2 " + position2);
							results.add(binding);
						}
				} // end large for loop
				return results;
			}
			
			public List<RuleApplicationResults> computeRuleApplicationResults(Sequent sequent, RuleBinding binding) throws Exception{
				List<RuleApplicationResults> results = new ArrayList<RuleApplicationResults>();
				
				SequentPosition position1 = binding.get("A1");
				SequentPosition position2 = binding.get("A2");
				Sequent s = sequent.clone();
				ArrayList antecedent = s.getAntecedent();
				ArrayList newsequents = new ArrayList<Sequent>();
				
				if (!(position1 instanceof SequentSinglePosition)){throw new Exception("unexpected position for subcl and equiv");}
				SequentSinglePosition pos1 = (SequentSinglePosition) position1;
				if (!(position2 instanceof SequentSinglePosition)){throw new Exception("unexpected position for subcl and equiv");}
				SequentSinglePosition pos2 = (SequentSinglePosition) position2;
				OWLFormula formula_1 = s.antecedentGetFormula(pos1.getToplevelPosition());
				OWLFormula formula_2 = s.antecedentGetFormula(pos2.getToplevelPosition());
				OWLFormula formula_3;
				OWLFormula superclass = formula_1.getArgs().get(1);
				OWLFormula equiv1 = formula_2.getArgs().get(0);
				OWLFormula equiv2 = formula_2.getArgs().get(1);
				if (superclass.equals(equiv1)){
					formula_3 = OWLFormula.createFormula(OWLSymb.SUBCL,formula_1.getArgs().get(0),
							equiv2);
				} else{
					formula_3 = OWLFormula.createFormula(OWLSymb.SUBCL,formula_1.getArgs().get(0),
							equiv1);
				}
				// System.out.println("resultformula! : " + formula_3);
				if (!s.alreadyContainedInAntecedent(formula_3)){
					RuleApplicationResults result1 = new RuleApplicationResults();
					result1.setOriginalFormula(s);
					result1.addAddition("A1", formula_3);
					results.add(result1);
					result1.setMaxFormulaDepth(InferenceApplicationService.computeRuleBindingMaxDepth(sequent, binding));
					}
				
				return results;
				
			}
			
			public SequentList computePremises(Sequent sequent, RuleBinding binding) throws Exception{
				List<RuleApplicationResults> results = computeRuleApplicationResults(sequent, binding);
				List<Sequent> sequents =  ruleApplicationResultsAsSequent(results);
				return SequentList.makeANDSequentList(sequents);
			}
			
			/// TODO: THIS IS JUST COPIED, IT IS NOT IMPLEMENTED FOR THIS OBJECT!
			public void expandTactic(ProofTree tree, ProofNode<Sequent,java.lang.String,AbstractSequentPositions> source, JustificationNode<java.lang.String,AbstractSequentPositions> justification) throws Exception{
				return;
							}
							
			
			
		}, // END trans objectproperty
*/
		
			// X \sqsubset Y, y \sqsubset X --> equiv(X,Y)
		
		
		
		RULE5MULTI{	@Override
		public java.lang.String getName(){return "R5M";};
		@Override
		public java.lang.String getShortName(){return "R5M";};

		private final OWLFormula prem1 = OWLFormula.createFormula(OWLSymb.SUBCL, 
				OWLFormula.createFormulaVar("v1"), 
				OWLFormula.createFormulaVar("v2"));



		@Override
		public List<RuleBinding> findRuleBindings(Sequent s){
			return findRuleBindings(s,false);
		}


		@Override
		public List<RuleBinding> findRuleBindings(Sequent s, boolean... one_suffices){
			Set<RuleBinding> resultsPre = new HashSet<RuleBinding>();

			List<OWLFormula> candidates = s.findMatchingFormulasInAntecedent(prem1);
			// for each individual left hand side, find the set of available right hand sides
			List<OWLFormula> lefthands = new ArrayList<OWLFormula>();
			List<Set<OWLFormula>> righthands = new ArrayList<Set<OWLFormula>>();
			for(OWLFormula cand : candidates){
				OWLFormula left = cand.getArgs().get(0);
				OWLFormula right = cand.getArgs().get(1);
				if (lefthands.contains(left)){
					Set<OWLFormula> set = righthands.get(lefthands.indexOf(left));
					if (set==null){
						set = new HashSet<OWLFormula>();
					}
					set.add(right);
				} else{ // not contained in lefthands
					lefthands.add(left);
					Set<OWLFormula> set= new HashSet<OWLFormula>();
					righthands.add(set);
					set.add(right);
				}
			} // end for loop
			// now loop through the collection to see if there are large collections of righthandsides (>2)
			for (int i = 0; i< lefthands.size(); i++){
				// System.out.println("lefthandside " + lefthands.get(i));
				// System.out.println("righthandside " + righthands.get(i));
				if (righthands.get(i).size()>2){
					Set<OWLFormula> subexprs = s.getAllSubExprs();
					for (OWLFormula sub : subexprs){
						if (sub.getHead().equals(OWLSymb.INT)){
							// System.out.println("sub args " + sub.getArgs());
							boolean contained = true;
							for (OWLFormula subbie : sub.getArgs()){
								boolean found = false;
								for (OWLFormula rightie: righthands.get(i)){
									if (rightie.equals(subbie))
									found= true;
								}
								if (!found)
									contained = false;
									break;
							}
							if (contained && sub.getArgs().size()>0){ // righthands.get(i).containsAll(sub.getArgs())){
								// System.out.println("RULE5 GOT A WINNER!");
								OWLFormula resultformula = OWLFormula.createFormula(OWLSymb.SUBCL, lefthands.get(i),
										sub);
								 // System.out.println("RULE5 generated binding with formula, before checking" + resultformula);
								if (!s.alreadyContainedInAntecedent(resultformula)){
									//  System.out.println("RULE5 generated binding with formula !" + resultformula);
									 RuleBinding binding = new RuleBinding(resultformula,null);
									 // produce correct positions, needed to find premises!
									 for (int j = 0; j<sub.getArgs().size(); j++){
										 OWLFormula subbie = sub.getArgs().get(j);
										 OWLFormula target = OWLFormula.createFormula(OWLSymb.SUBCL, lefthands.get(i),subbie);
										 List<OWLFormula> candidatesTarget = s.findMatchingFormulasInAntecedent(target);
										 if (candidatesTarget.size()==0){
											 continue;
										 }
										 List<Integer> list = new ArrayList<Integer>();
										 list.add(s.antecedentFormulaGetID(candidatesTarget.get(0)));
										 SequentPosition position1 = new SequentSinglePosition(SequentPart.ANTECEDENT, list);
										 binding.insertPosition("A" + (j+1), position1);
										 if (j>1){
											 System.out.println("FEATURE! R5M found massive application opportunity!");
										 }
										 // System.out.println("DEBUG R5M inserted premise position " + (j+1) + " " + candidatesTarget.get(0));
									 }
									 // binding.insertPosition("A1", position1);
									 resultsPre.add(binding);					
								}					
							}
						}
					}
			}
			}	
			// System.out.println("RULE 5 MULTI RESULTS " + resultsPre);
			return new ArrayList<RuleBinding>(resultsPre);
		}	
			

		@Override
		public List<RuleApplicationResults> computeRuleApplicationResults(Sequent sequent, RuleBinding binding) throws Exception{
			List<RuleApplicationResults> results = new ArrayList<RuleApplicationResults>();
			// System.out.println(" add rule5 multi" + sequent  + binding.get("A1"));
			
			OWLFormula formula3 = binding.getNewAntecedent();
			
				if (!sequent.alreadyContainedInAntecedent(formula3)){
					RuleApplicationResults result = new RuleApplicationResults();
					result.setOriginalFormula(sequent);
					result.addAddition("A1",formula3);
					results.add(result);
					result.setMaxFormulaDepth(InferenceApplicationService.computeRuleBindingMaxDepth(sequent, binding));
				    // System.out.println(" add rule5 multi computed consequence! " + formula3);
				}
			return results;
		}

		@Override
		public SequentList computePremises(Sequent sequent, RuleBinding binding) throws Exception{
			List<RuleApplicationResults> results = computeRuleApplicationResults(sequent, binding);
			List<Sequent> sequents =  ruleApplicationResultsAsSequent(results);
			return SequentList.makeANDSequentList(sequents);
		}

		@Override
		public List<RuleKind> qualifyRule(){
			RuleKind[] a = {RuleKind.FORWARD};
			return Arrays.asList(a);
		}

		}, // end Rule5Multi
		
		
		FORALLUNION{ // SubCla(X,\forall r.Y or Z) and SubCla(Y,W) and SubCla(Z,W) --> SubCla(X, \forall r.W)
			
			@Override
			public java.lang.String getName(){return "Additional-Forall-Union";};
			@Override
			public java.lang.String getShortName(){return "faU";};
			
			
			private final OWLFormula prem1 = OWLFormula.createFormula(OWLSymb.SUBCL, 
					OWLFormula.createFormulaVar("v1"), 
					OWLFormula.createFormula(OWLSymb.FORALL, 
							OWLFormula.createFormulaRoleVar("r1"),
							OWLFormula.createFormula(OWLSymb.UNION,  
									OWLFormula.createFormulaVar("v2"),
									OWLFormula.createFormulaVar("v3")
									)));
									
			/*
			private final OWLFormula prem1 = OWLFormula.createFormula(OWLSymb.SUBCL, 
					OWLFormula.createFormulaVar("v1"), 
					OWLFormula.createFormula(OWLSymb.FORALL, 
							OWLFormula.createFormulaRoleVar("r1"),							
									OWLFormula.createFormulaVar("v2")
									
									));
			*/
			
			private final OWLFormula prem2 = OWLFormula.createFormula(OWLSymb.SUBCL, 
					OWLFormula.createFormulaVar("v2"), 
					OWLFormula.createFormulaVar("v4"));
			
			private final OWLFormula prem3 = OWLFormula.createFormula(OWLSymb.SUBCL, 
					OWLFormula.createFormulaVar("v3"), 
					OWLFormula.createFormulaVar("v4"));
			
			private final OWLFormula result = OWLFormula.createFormula(OWLSymb.SUBCL, 
					OWLFormula.createFormulaVar("v1"), 
					OWLFormula.createFormula(OWLSymb.FORALL, 
									OWLFormula.createFormulaRoleVar("r1"),
									OWLFormula.createFormulaVar("v4")
									));
					
			
			@Override
			public List<RuleBinding> findRuleBindings(Sequent s){
				// System.out.println("forall union find rule bindings called");
				List<RuleBinding> results = new ArrayList<RuleBinding>();
				List<OWLFormula> candidates1 = s.findMatchingFormulasInAntecedent(prem1);
				// System.out.println(candidates1);
				for (int i = 0 ; i < candidates1.size(); i++){
				    List<OWLFormula> candidates2 = new ArrayList<OWLFormula>();
					// get matcher 
					try{
					List<Pair<OWLFormula,OWLFormula>> matcher = candidates1.get(i).match(prem1);
					// System.out.println("faU: Matcher: " + matcher);
					// now build new formula using the matcher
					OWLFormula prem_2 = prem2.applyMatcher(matcher);
					// System.out.println("prem_2 " + prem_2);
					// now search for this formula in sequent
					candidates2 = s.findMatchingFormulasInAntecedent(prem_2);
					// System.out.println("candidates 2" + candidates2);
					for (OWLFormula candidate2: candidates2){
						List<Pair<OWLFormula,OWLFormula>> matcher2 = candidates1.get(i).match(prem3);
						// System.out.println("faU: Matcher2: " + matcher);
						// now build new formula using the matcher
						OWLFormula prem_3 = prem3.applyMatcher(matcher);
						// System.out.println("prem_3 " + prem_3);
						List<OWLFormula> candidates3 = new ArrayList<OWLFormula>();
						candidates3 = s.findMatchingFormulasInAntecedent(prem_3);
						for (OWLFormula candidate3: candidates3){
							List<Pair<OWLFormula,OWLFormula>> matcher_all = matcher;
							List<Pair<OWLFormula,OWLFormula>> matcher_conc = candidate3.match(prem3);
							matcher_all.addAll(matcher_conc);
							// System.out.println(matcher_all);
							OWLFormula conclusion = result.applyMatcher(matcher_all);	
							// System.out.println("conclusion: " + conclusion);
							// System.out.println(s.alreadyContainedInAntecedent(conclusion));
							if (!s.alreadyContainedInAntecedent(conclusion)){
								RuleBinding binding = new RuleBinding(conclusion,null);																						
								SequentPosition position1 = new SequentSinglePosition(SequentPart.ANTECEDENT, s.antecedentFormulaGetID(candidates1.get(i)));
								binding.insertPosition("A1", position1);
								SequentPosition position2 = new SequentSinglePosition(SequentPart.ANTECEDENT, s.antecedentFormulaGetID(candidate2));
								binding.insertPosition("A2", position2);
								SequentPosition position3 = new SequentSinglePosition(SequentPart.ANTECEDENT, s.antecedentFormulaGetID(candidate3));
								binding.insertPosition("A3", position3);
								results.add(binding);
								// System.out.println("BINDING " + binding);
							} // end if
						} // end loop candidate3 
					} // end loop candidate2
					} catch (Exception e){
					e.printStackTrace();
				}
				} // end formula 1 loop
				// System.out.println("DEBUG forallunion === results " + results);			
				return results;
			}
			
			@Override
			public List<RuleApplicationResults>  computeRuleApplicationResults(Sequent sequent, RuleBinding binding) throws Exception{
				SequentPosition position1 = binding.get("A1");
				SequentPosition position2 = binding.get("A2");
				SequentPosition position3 = binding.get("A3");
				Sequent s = sequent.clone();
				// ArrayList antecedent = s.getAntecedent();
				ArrayList newsequents = new ArrayList<Sequent>();
				
				List<RuleApplicationResults> results = new ArrayList<RuleApplicationResults>();
				
				OWLFormula conclusion = binding.getNewAntecedent();
					if (!sequent.alreadyContainedInAntecedent(conclusion)){
						RuleApplicationResults results1 = new RuleApplicationResults();
						results1.setOriginalFormula(s);
						results1.addAddition("A1", conclusion);
						results.add(results1);	
						results1.setMaxFormulaDepth(InferenceApplicationService.computeRuleBindingMaxDepth(sequent, binding));
						// System.out.println("DEBUG Forallunion === " + conclusion);
					}
				return results;
			}
			
			
			@Override
			public SequentList computePremises(Sequent sequent, RuleBinding binding) throws Exception{
				List<RuleApplicationResults> results = computeRuleApplicationResults(sequent, binding);
				List<Sequent> sequents =  ruleApplicationResultsAsSequent(results);
				return SequentList.makeANDSequentList(sequents);
			}
					
		}, // END RULE23
		
		
		
		
		ONLYSOME{	
		@Override
		public java.lang.String getName(){return "ONLYSOME";};
		@Override
		public java.lang.String getShortName(){return "OS";};
		
	
		
		@Override
		public List<RuleBinding> findRuleBindings(Sequent s){
			return findRuleBindings(s,false);
		}
		
		
		
		@Override
		public List<RuleBinding> findRuleBindings(Sequent s, boolean... one_suffices){
			ArrayList<RuleBinding> results = new ArrayList<RuleBinding>();
			HashSet<OWLFormula> antecedentformulas = s.getAllAntecedentOWLFormulas();
			HashSet<OWLFormula> prem1_candidates = new HashSet<OWLFormula>();
			HashSet<OWLFormula> prem2_candidates = new HashSet<OWLFormula>();
			// looking for first set of candidates
			for (OWLFormula form : antecedentformulas){
				// System.out.println("DEBUG --- " + form);
				if (form.getHead().equals(OWLSymb.SUBCL)){
					List<OWLFormula> args = form.getArgs();
					List<OWLFormula> expressions = detectOnlysome(args.get(1));
					// remove trivial subsumptions
					if (form.getArgs().get(0).equals((form.getArgs().get(1)))){
						continue;
					}
					// now check for onlysomes
					if (expressions!=null && expressions.size()>0){
						// System.out.println("DEBUG --- add cand1 " + form);
						prem1_candidates.add(form);
					}
					// System.out.println("DEBUG ---2--- " + form);
					expressions = detectOnlysome(args.get(0));
					if (expressions!=null && expressions.size()>0){
						// System.out.println("DEBUG --- add cand2 " + form);
						prem2_candidates.add(form);
					}
						
				}
			} // end for 
			// now really find out
			// System.out.println("DEBUG onlysome candidates1: " + prem1_candidates);
			// System.out.println("DEBUG onlysome candidates2: " + prem2_candidates);
			for (OWLFormula form1 : prem1_candidates){
				for (OWLFormula form2 : prem2_candidates){
					List<OWLFormula> expressions1 = detectOnlysome(form1.getArgs().get(1));
					List<OWLFormula> expressions2 = detectOnlysome(form2.getArgs().get(0));
					// System.out.println(" ONLYSOME DEBUG form 1 " + form1);
					// System.out.println(" ONLYSOME DEBUG form 2 " + form2);
					// now need to find all necessary subsumptions
					boolean exists = false;
					Set<OWLFormula> witness_formulas = new HashSet<OWLFormula>();
					// System.out.println(" ONLYSOME DEBUG expressions1 " + expressions1);
					for (OWLFormula exp1 : expressions1){
						for (OWLFormula exp2 : expressions2){
							if (s.alreadyContainedInAntecedent(OWLFormula.createFormula(OWLSymb.SUBCL,exp1,exp2))){
								exists = true;
								witness_formulas.add(OWLFormula.createFormula(OWLSymb.SUBCL,exp1,exp2));
							}
						}
					}
					if (!exists) 
						continue;
					exists = false;
					// System.out.println(" ONLYSOME DEBUG expressions2 " + expressions2);
					for (OWLFormula exp2 : expressions2){
						for (OWLFormula exp1 : expressions1){
							if (s.alreadyContainedInAntecedent(OWLFormula.createFormula(OWLSymb.SUBCL,exp1,exp2))){
								exists = true;
								witness_formulas.add(OWLFormula.createFormula(OWLSymb.SUBCL,exp1,exp2));
							}
						}
					}
					if (!exists) 
						continue;
					// System.out.println(" ONLYSOME DEBUG witnesses " + witness_formulas);
					// check if potential conclusion is trivial
					if (form1.getArgs().get(0).equals(form2.getArgs().get(1)))
						continue;
					// check if potential conclusion already derived 
					OWLFormula concl = OWLFormula.createFormula(OWLSymb.SUBCL,
							form1.getArgs().get(0),
							form2.getArgs().get(1));
					// System.out.println("DEBUG onlysome :::: found conclusion + " + concl + " , is contained:" + s.alreadyContainedInAntecedent(concl));
					if (s.alreadyContainedInAntecedent(concl))
						continue;
					// all checks passed. Now need to generate rule binding!
					List<Integer> list1 = new ArrayList<Integer>();
					list1.add(s.antecedentFormulaGetID(form1));
					SequentPosition position1 = new SequentSinglePosition(SequentPart.ANTECEDENT, list1);
					RuleBinding binding = new RuleBinding(concl,null);
					binding.insertPosition("A1", position1);
					
					List<Integer> list2 = new ArrayList<Integer>();
					list2.add(s.antecedentFormulaGetID(form2));
					SequentPosition position2 = new SequentSinglePosition(SequentPart.ANTECEDENT, list2);
					binding.insertPosition("A2", position2);
					
					int i = 3;
					
					System.out.println("treating witness formulas : " + witness_formulas);
					for (OWLFormula wit : witness_formulas){
						OWLFormula realwit = wit;
						for (Object potwit : s.getAllAntecedentOWLFormulas()){
							if (wit.equals(potwit))
								realwit = (OWLFormula) potwit;
						}
						List<Integer> list3 = new ArrayList<Integer>();
						// System.out.println(" DEBUG WIT " + s.alreadyContainedInAntecedent(wit) + " " + s.antecedentFormulaGetID(realwit));
						list3.add(s.antecedentFormulaGetID(realwit));
						SequentPosition position3 = new SequentSinglePosition(SequentPart.ANTECEDENT, list3);
						binding.insertPosition("A" + i, position3);
						i++;
					}
					
					
					results.add(binding);
					
				}
			} // end for 
			// System.out.println("DEBUG onlysome :::: results + " + results);
			return results;
		} // end
		
		
		@Override
		public List<RuleApplicationResults> computeRuleApplicationResults(Sequent sequent, RuleBinding binding) throws Exception {
			List<RuleApplicationResults> results = new ArrayList<RuleApplicationResults>();
			OWLFormula conclusion = binding.getNewAntecedent();
			// System.out.println("DEBUG Adding conclusion: " + conclusion);
			if (!sequent.alreadyContainedInAntecedent(conclusion)){
				RuleApplicationResults results1 = new RuleApplicationResults();
				results1.setOriginalFormula(sequent);
				results1.addAddition("A1", conclusion);
				results.add(results1);	
				results1.setMaxFormulaDepth(InferenceApplicationService.computeRuleBindingMaxDepth(sequent, binding));
			}
			// TODO Auto-generated method stub
			return results;
		}
	
		@Override
		public SequentList computePremises(Sequent sequent, RuleBinding binding) throws Exception{
			List<RuleApplicationResults> results = computeRuleApplicationResults(sequent, binding);
			List<Sequent> sequents =  ruleApplicationResultsAsSequent(results);
			return SequentList.makeANDSequentList(sequents);
		}
		
		
		}; // value entries separated by commas
	
	@Override
	public List<RuleBinding> findRuleBindings(Sequent s){
		List<SequentPosition> positions = findPositions(s);
		ArrayList<RuleBinding> bindings = new ArrayList<RuleBinding>();
		for (SequentPosition pos: positions){
			RuleBinding binding = new RuleBinding();
			binding.insertPosition("SINGLEPOS",pos);
		}
		return bindings;
	}
	
	/* Nonsense
	public SequentList computePremises(Sequent sequent){
		List<Sequent> premises = new ArrayList();
		List<SequentPosition> positions = findPositions(sequent);
		Iterator it = positions.iterator();
		while(it.hasNext()){
			try {
				premises.addAll(computePremises(sequent,(SequentSinglePosition) it.next()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return premises;
	} */
	
	
	@Override
	public List<RuleKind> qualifyRule(){
		RuleKind[] a = {RuleKind.FORWARD};
		return Arrays.asList(a);
	}
	
	@Override
	public boolean isApplicable(Sequent s){
		List<SequentPosition> positions = findPositions(s);
		if (positions==null || positions.size()==0){
		return false;	
		}
		else{return true;}
	}
	
	@Override
	public SequentList computePremises(Sequent s, SequentPosition p){
		return null;
	}
		
	@Override
	public List<SequentPosition> findPositions(Sequent s){
		return null;
	}
	
	private static List<Sequent> ruleApplicationResultsAsSequent(List<RuleApplicationResults> results){
		ArrayList<Sequent> sequents = new ArrayList();
		for (RuleApplicationResults result: results){
			// System.out.println("APPLYING A RESULT!");
			Sequent sequent = ((Sequent) result.getOriginalFormula()).clone();
			// deletions
			Map deletions = result.getDeletionsMap();
			for (Object sr : deletions.keySet()){
					java.lang.String str = (String) sr;
					Object formula = deletions.get(str);
					if (str.contains("A")){
						if (formula instanceof OWLFormula){
							sequent.removeOWLFormulaFromAntecedent((OWLFormula) formula);
						} else
						{
							System.out.println("SOMETHING WENT QUITE WRONG");
						}
						} else {
							System.out.println("SOMETHING WENT QUITE WRONG");	
						}
					} // endif
			// additions
			Map additions = result.getAdditionsMap();
			for (Object sr : additions.keySet()){
			java.lang.String str = (String) sr;
			Object formula = additions.get(str);
			if (str.contains("A")){
				if (formula instanceof OWLFormula){
					try {
						sequent.addAntecedent((OWLFormula) formula);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// depth bookkeeping
					int newdepth = result.getMaxFormulaDepth()+1;
					// System.out.println("Setting depth for formula " + ((OWLFormula) formula) +" : " + newdepth);
					sequent.setFormulaDepth(sequent.antecedentFormulaGetID((OWLFormula) formula), newdepth);
				} else{
				try {
					// sequent.addAntecedent(formula);
					System.out.println("SOMETHING WENT QUITE WRONG");	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}}
		}else {
				try {
					// sequent.addSuccedent(formula);
					System.out.println("SOMETHING WENT QUITE WRONG");	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} // endif
			sequents.add(sequent);
	} // end loop for one result
		return sequents;
}
	
	@Override
	public List<RuleApplicationResults> computeRuleApplicationResults(Sequent sequent, RuleBinding binding) throws Exception{
		return null;
}
	
	public void expandTactic(ProofTree tree, ProofNode<Sequent,java.lang.String,AbstractSequentPositions> source, JustificationNode<java.lang.String,AbstractSequentPositions> justification) throws Exception{
		return;
	}
	
	@Override
	public List<RuleBinding> findRuleBindings(Sequent s, boolean ... saturate) {
		return findRuleBindings(s);
	}
	
	
	private static List<OWLFormula> detectOnlysome(OWLFormula formula){
		// System.out.println("trying the onlysome detector on " + formula);
		if (!formula.getHead().equals(OWLSymb.INT)) 
				return null;
		List<OWLFormula> intsubformulas = new ArrayList<OWLFormula>(formula.getArgs());
		List<OWLFormula> extconcepts = new ArrayList<OWLFormula>();
		List<OWLFormula> forallconcepts_tmp = new ArrayList<OWLFormula>();
		List<OWLFormula> forallconcepts = new ArrayList<OWLFormula>();
		// idea: iterate through all subformulas and sort existential/forall expressions into the right buckets
		while(intsubformulas.size()>0){
			// System.out.println(" DEBUG! onlysomedetector: " + intsubformulas.get(0));
			OWLFormula form = intsubformulas.get(0);
			OWLAtom head = form.getHead();
			// remove from fringe
			intsubformulas.remove(form);
			// recursion
			if (head.equals(OWLSymb.INT)){
				intsubformulas.addAll(form.getArgs());
				// System.out.println(" DEBUG! onlysomedetector: intsubformulas " + intsubformulas);
				continue;
			} 
			// collect exists
			if (head.equals(OWLSymb.EXISTS)){
				extconcepts.add(form.getArgs().get(1));
				continue;
			}
			// collect foralls
			if (head.equals(OWLSymb.FORALL)){
				forallconcepts_tmp.add(form.getArgs().get(1));
				continue;
			}
			// if we get here without "continue", something is wrong...!
			return null;
		}
		// now check that all is right; first unionionfy
		while(forallconcepts_tmp.size()>0){
			OWLFormula currentformula = forallconcepts_tmp.get(0);
			OWLAtom head = currentformula.getHead();
			forallconcepts_tmp.remove(currentformula);
			if (head.equals(OWLSymb.UNION)){
				forallconcepts_tmp.addAll(currentformula.getArgs());
				continue;
			}
			forallconcepts.add(currentformula);
		}
		// System.out.println(" DEBUG! onlysomedetector: " + extconcepts);
		// System.out.println(" DEBUG! onlysomedetector: " + forallconcepts);
		// System.out.println(forallconcepts);
		if (!forallconcepts.containsAll(extconcepts) || !extconcepts.containsAll(forallconcepts)){
			// System.out.println("return null");
			return null;
		}
		return extconcepts;
	}
	
	
}