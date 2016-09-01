package org.semanticweb.cogExp.ProofBasedExplanation;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.cogExp.OWLAPIVerbaliser.WordNetQuery;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

import uk.ac.manchester.cs.jfact.JFactFactory;

public class StringServer {
    private final ServerSocket server;
    private final Socket socket;
    private ClusterExplanationService service = null;
    
    public StringServer(int port, ClusterExplanationService service) throws IOException {
    	String tmpdir = "";
		try {
			tmpdir = org.semanticweb.wordnetdicttmp.WordnetTmpdirManager.makeTmpdir();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WordNetQuery.INSTANCE.setDict(tmpdir);
        server = new ServerSocket(port);
        this.service = service;
        socket = server.accept();
        Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
            try {
                socket.close();
                System.out.println("The server is shut down!");
            } catch (IOException e) { /* failed */ }
        }});
        
       
        
    }
    
    

    private void connect() {

        while (true) {
            // Socket socket = null;
            try {
                handle(socket);
            }

            catch (IOException e) {
                e.printStackTrace();
            } 
        }
    }

    private void handle(Socket socket) throws IOException {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket
                .getInputStream()));
        PrintStream outputStream = new PrintStream(socket.getOutputStream());
        String s;
        
        // while(inputReader.ready()) {
        int idlecount = 0;
        while (socket!=null && socket.isConnected()){
        	if(inputReader.ready()){
        		s = inputReader.readLine();
        		if (s==null){
        			System.out.println("We've lost connection");
        			break;
        		}
        			
        		System.out.println("[Reading input: " +s + "]");
        		String output = "";
        		try {
        			output = service.handleCluster1Request(s,outputStream);
        		} catch (OWLOntologyCreationException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		// outputStream.println(output);
        		// System.out.println("input reader ready? " + inputReader.ready() + " socket:  " + socket + " is closed "+ socket.isClosed());
        		System.out.println("[Waiting]");
        	} else {
        		// System.out.println("idlecount " + idlecount);
        		try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		/*
        		idlecount++;
        		if (idlecount>1000){
        			outputStream.println("PROBING THE CONNECTION!!!");
        			if (outputStream.checkError()){
        				idlecount = 0;
        				break;
        				}
        			}
        			*/
        	}
        	
        }
        System.out.println("Socket connection lost.");
        inputReader.close();
        outputStream.close();
        socket.close();
    }

    public static void main(String[] args) throws IOException {
    	int port = 3111;
    	System.out.println("[Server preparation. Please wait.]");
    	if (args.length>0 && args[0]!=null){
    		port = Integer.parseInt(args[0]);
    	}
    	OWLOntology ontology = null;
    	OWLReasonerFactory reasonerFactory = null;
    	OWLReasoner reasoner = null;
    	if (args.length>1 && args[1]!=null){
    		String ontologyfile = args[1];
    		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    		java.io.File file = new java.io.File(ontologyfile);
    		FileDocumentSource source = new FileDocumentSource(file);
    		OWLOntologyLoaderConfiguration loaderconfig = new OWLOntologyLoaderConfiguration(); 
    		loaderconfig.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
    		loaderconfig = loaderconfig.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.valueOf("SILENT"));
    					
    		String result = "";
    		try {
    			ontology = manager.loadOntologyFromOntologyDocument(source,loaderconfig);
    			 reasonerFactory = new JFactFactory();
    			 SimpleConfiguration configuration = new SimpleConfiguration(50000);
    		     reasoner = reasonerFactory.createReasoner(ontology,configuration);
    		} catch (Exception e){
    			System.out.println("Failed to load ontology at " + ontologyfile);
    		}
    	}
    	
    	ClusterExplanationService clusterExplanationService = new ClusterExplanationService(ontology,reasoner,reasonerFactory);
    	// if (args.length>1 && args[1]!=null){
    	// 	clusterExplanationService.precomputeAxioms();
    	// }
    	
        
        try {
			clusterExplanationService.handleCluster1Request("precompute", null);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("[Explanation generator listening on port " + port + ".]");
        StringServer server = new StringServer(port,clusterExplanationService);
        server.connect();
    }
} 