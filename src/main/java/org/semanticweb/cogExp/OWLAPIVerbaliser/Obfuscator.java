/*
 *     Copyright 2012-2018 Ulm University, AI Institute
 *     Main author: Marvin Schiller, contributors: Felix Paffrath, Chunhui Zhu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.semanticweb.cogExp.OWLAPIVerbaliser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.cogExp.core.Pair;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public class Obfuscator {

	private HashMap<String,String> names = new HashMap<String,String>();
	private HashMap<String,String> roles = new HashMap<String,String>();
	
	public Obfuscator(){
	}
	
	public Obfuscator(Set<OWLAxiom> axioms, List<String> rolenames, List<String> classnames){
		// Classnames
		Set<String> original_classnames = new HashSet<String>();
		for (OWLAxiom ax: axioms){
			Set<OWLClass> classes = ax.getClassesInSignature();
			// System.out.println("Obf debug " + classes.toString());
			for (OWLClass cl : classes){
				original_classnames.add(cl.getIRI().getFragment());
			}
		}
		// System.out.println("Obf debug " + original_classnames.toString());
		if (original_classnames.size()<=classnames.size()){
			int i = 0;
			for (String origname : original_classnames){
				names.put(origname,classnames.get(i));
				i++;
			}
		}
		// System.out.println("Obf debug " + names.toString());
		// Role Names
		Set<String> original_rolenames = new HashSet<String>();
		for (OWLAxiom ax: axioms){
			Set<OWLObjectProperty> props = ax.getObjectPropertiesInSignature();
			for (OWLObjectProperty prop : props){
				original_rolenames.add(prop.getIRI().getFragment());
			}
		}
		// System.out.println("Obf debug " + original_rolenames.toString());
		if (original_rolenames.size()<=rolenames.size()){
			int i = 0;
			for (String origname : original_rolenames){
				roles.put(origname,rolenames.get(i));
				i++;
			}
		}
		// System.out.println("Obf debug " + roles.toString());
	}
	
	public void addName(String originalname, String newname){
		names.put(originalname,newname);
	}
	
	public void addRole(String originalname, String newname){
		roles.put(originalname,newname);
	}
	
	public String obfuscateName(String name){
		if (names.containsKey(name)){
			return names.get(name);
		}
		else return name;
	}
	
	public String obfuscateRole(String name){
		if (roles.containsKey(name)){
			return roles.get(name);
		}
		else return name;
	}
	
}
