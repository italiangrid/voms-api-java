/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2012.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.italiangrid.voms.util;

import java.util.ArrayList;
import java.util.List;

import org.glite.voms.FQAN;

/**
 * FQAN handling utilities
 * 
 * @author Andrea Ceccanti
 *
 */
public class FQANHelper {

	public static List<FQAN> convert(List<String> fqanStrings){
		if (fqanStrings == null)
			return null;
		
		List<FQAN> fqanList = new ArrayList<FQAN>();
		for (String s: fqanStrings)
			fqanList.add(new FQAN(s));
		
		return fqanList;
	}
}
