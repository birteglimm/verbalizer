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

package org.semanticweb.cogExp.core;

import java.util.List;

public abstract class SequentPosition extends AbstractSequentPositions{
	
	SequentPart sequentpart;

	public abstract SequentPart getSequentPart();
	
	public SequentPosition(SequentPart part, List<Integer> positions){};
	
	public SequentPosition(SequentPart part, int position){};
	
	public SequentPosition(){};
	
	public boolean isNotNull(){return true;};
	

}