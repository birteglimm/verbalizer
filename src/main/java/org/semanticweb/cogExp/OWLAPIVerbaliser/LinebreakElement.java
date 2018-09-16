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

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 * represents a line break (its content is nothing but "\n").
 * This is needed for the @see CustomJPanel, so mostly for GUI in the protege plugin.
 * @author fpaffrath
 *
 */
public class LinebreakElement extends TextElement{

		public LinebreakElement(){
			super("\n");
		}
		
		/* (non-Javadoc)
		 * @see org.semanticweb.cogExp.OWLAPIVerbaliser.TextElement#toHTML()
		 */
		public String toHTML(){
			return "<br>"; 
		}
		
		/* (non-Javadoc)
		 * @see org.semanticweb.cogExp.OWLAPIVerbaliser.TextElement#addToDocument(javax.swing.JTextPane)
		 */
		@Override
		public void addToDocument(JTextPane textPane){
			StyledDocument document = textPane.getStyledDocument();
			try {
				document.insertString(document.getLength(), content, null);
			} catch (BadLocationException e) {
				//  Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		/* (non-Javadoc)
		 * @see org.semanticweb.cogExp.OWLAPIVerbaliser.TextElement#isLinebreak()
		 */
		@Override
		public boolean isLinebreak(){
			return true;
		}
	
}
