package org.semanticweb.cogExp.examples;
import java.io.File;
import java.io.IOException;
import org.semanticweb.cogExp.GentzenTree.GentzenTree;
import org.semanticweb.cogExp.OWLAPIVerbaliser.VerbaliseTreeManager;
import org.semanticweb.cogExp.OWLAPIVerbaliser.WordNetQuery;
import org.semanticweb.cogExp.ProofBasedExplanation.ProofBasedExplanationService;
import org.semanticweb.cogExp.ProofBasedExplanation.WordnetTmpdirManager;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import uk.ac.manchester.cs.jfact.JFactFactory;


// import org.semanticweb.owlapi.inference

public class IndividualsExample {

	public static void main(String[] args) throws OWLOntologyCreationException {
		/**
		 * this is the very smallest example one can think of
		 * 
		 */

		String tmpdir = "";
		try {
			tmpdir = WordnetTmpdirManager.makeTmpdir();
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
		WordNetQuery.INSTANCE.setDict(tmpdir);
		
		
		
		// OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		String root = "/Users/marvin/work/ki-ulm-repository/miscellaneous/Bosch-intern/ontologies/";
		String path = root+"simple-tools.owl";
		File ontologyfile = new java.io.File(path);
		
		OWLOntologyManager specialOntologyManager = getImportKnowledgeableOntologyManger();
		
		OWLOntology boschOntology = 
				specialOntologyManager.loadOntologyFromOntologyDocument(ontologyfile);	
		// OWLReasoner reasoner = reasonerFactory.createReasoner(boschOntology);
		
		
		
		// Reasoner hermit=new Reasoner(boschOntology);
		// OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
		// OWLReasoner hermit = reasonerFactory.createReasoner(boschOntology);
		// System.out.println(hermit.getReasonerName());
		
		/*
		Configuration config = new Configuration();
		Reasoner hermit = new Reasoner(config,boschOntology);
		OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
		System.out.println(hermit.getReasonerName());
		*/
		// System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		
		// OWLReasoner reasoner=new Reasoner.ReasonerFactory().createReasoner(boschOntology);
		// OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
		
		// String a = "BirchWood";
		// String b = "DecidiousWood";
		// String result = getResult(a, b, path, reasonerFactory, hermit );
		
		OWLReasonerConfiguration config = new SimpleConfiguration(50000);
		OWLReasonerFactory reasonerFactory = new JFactFactory();
		OWLReasoner jfact = reasonerFactory.createReasoner(boschOntology,config);
		
		// System.out.println("Birds : \n");
		
		// System.out.println(result);
		
		// Classaxiom
		OWLDataFactory dataFactory=OWLManager.createOWLOntologyManager().getOWLDataFactory();
		// String ontologyuri = "http://www.semanticweb.org/marvin/ontologies/2016/10/untitled-ontology-772#";
		String ontologyuri = "http://www.semanticweb.org/powertools#";
	  	
		/*
		OWLClass correctlyDrillingInWood = dataFactory.getOWLClass(IRI.create(ontologyuri + "CorrectlyDrillingInWood"));
	  	OWLNamedIndividual drillingEvent1 = dataFactory.getOWLNamedIndividual(IRI.create(ontologyuri + "drillingEvent1"));
	  	OWLClassAssertionAxiom classAxiom = dataFactory.getOWLClassAssertionAxiom(correctlyDrillingInWood, drillingEvent1);
		*/
		
		OWLClass drillingInWoodWithTooMuchSpeed = dataFactory.getOWLClass(IRI.create(ontologyuri + "DrillingInWoodWithRotationalSpeedAbove1000Umin"));
	  	OWLNamedIndividual drillingEvent2 = dataFactory.getOWLNamedIndividual(IRI.create(ontologyuri + "drillingEvent2"));
	  	OWLClassAssertionAxiom classAxiom = dataFactory.getOWLClassAssertionAxiom(drillingInWoodWithTooMuchSpeed, drillingEvent2);
		
		String resulttext = ProofBasedExplanationService.getExplanationResult(classAxiom, jfact, reasonerFactory,  boschOntology, false, 1000, 50000, "OP", false, false);
		System.out.println(resulttext);
		
		
		
		
//		
//		OWLOntologyManager knowledgeableManager = getImportKnowledgeableOntologyManger();
//		OWLOntology ExampleOntology = createOntology(knowledgeableManager);
//	
//		
//		
//		OWLReasoner reasoner2 = reasonerFactory.createReasoner(ExampleOntology);
//		
//		String result5 = getResult("BirchWood", "DecidiousWood", "/home/fpaffrath/Dokumente/gatheredOntologies/bosch-reworked.owl", reasonerFactory, reasoner2);
//		String result6 = getResult("DrillHoleInWood", "DrillHole", "/home/fpaffrath/Dokumente/gatheredOntologies/bosch-reworked.owl", reasonerFactory, reasoner2);
//		String result9 = getResult("CutPaper", "CutOut", "/home/fpaffrath/Dokumente/gatheredOntologies/bosch-reworked.owl", reasonerFactory, reasoner2);
//		String result7 = getResult("DrillEntryHoleWithJigsaw", "CutOut", "/home/fpaffrath/Dokumente/gatheredOntologies/bosch-reworked.owl", reasonerFactory, reasoner2);
//		String result8 = getResult("WoodTwistDrillBit", "DrillBit", "/home/fpaffrath/Dokumente/gatheredOntologies/bosch-reworked.owl", reasonerFactory, reasoner2);
//
//		
//		System.out.println("\nTools : \n");
//		System.out.println(result5);
//		System.out.println(result6);
//		System.out.println(result9);		
//		System.out.println(result7);
//		System.out.println(result8);
		
		
	}
	
	
	
	
	
	
	

	private static String getResult(String a, String b, String path, OWLReasonerFactory reasonerFactory,
			OWLReasoner reasoner) {
		// TODO Auto-generated method stub
		GentzenTree tree = ProofBasedExplanationService.computeTree(a, 
				b, 
				path, 
				reasonerFactory, 
				reasoner);
		// GentzenTree tree2 = ProofBasedExplanationService.computeTree("CoalTit", "Tit", "/Users/marvin/work/ki-ulm-repository/miscellaneous/Bosch/Ontologien/ornithology.owl", reasonerFactory, reasoner);
		
		System.out.println("\n");
		String result = VerbaliseTreeManager.verbaliseNL(tree, false, false, null);
		return (result);
		
	}
	
	public static OWLOntologyManager getImportKnowledgeableOntologyManger(){
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		// String root = "file:///home/fpaffrath/Dokumente/gatheredOntologies";
		String root = "file:////Users/marvin/marvin_work_ulm/b/resources/";
		manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create("http://www.semanticweb.org/marvin/ontologies/2016/10/untitled-ontology-772"),
		           IRI.create(root+"bosch-reworked02032017.owl")));
		manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create("http://www.semanticweb.org/marvin/ontologies/2016/10/ontologyExport_PTGreenProducts.rdf"),
		           IRI.create(root+"ontologyExport_PTGreenProducts.rdf")));
		
		manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create("http://www.bosch.com/MaterialsOntology"),
		           IRI.create(root+"ontologyExport_MaterialsOntology.rdf")));
		manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create("http://www.bosch.com/ActivitiesOntology"),
		           IRI.create(root+"ontologyExport_ActivitiesOntology.rdf")));
		manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create("http://www.bosch.com/PTGreenProducts"),
		           IRI.create(root+"ontologyExport_PTGreenProducts.rdf")));
		
		return manager;
	}
	
	public static OWLOntology createOntology(OWLOntologyManager manager){
		OWLOntology ontology = null;
		try {
			ontology = manager.createOntology();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ontology;
}


}