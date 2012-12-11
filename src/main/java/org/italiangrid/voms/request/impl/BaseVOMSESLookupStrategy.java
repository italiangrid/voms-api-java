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
package org.italiangrid.voms.request.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.italiangrid.voms.request.VOMSESLookupStrategy;

/**
 * A base VOMSES lookup strategy which just run a existence check on a given
 * list of paths.
 * 
 * @author Andrea Ceccanti
 * 
 */
public class BaseVOMSESLookupStrategy implements VOMSESLookupStrategy {

	private final List<String> checkedPaths;

	
	public BaseVOMSESLookupStrategy() {
		checkedPaths = new ArrayList<String>();
	}
	
	public BaseVOMSESLookupStrategy(List<String> checkedPaths) {
		if (checkedPaths == null)
			throw new NullPointerException("Please provide a non-null list of paths.");
		
		this.checkedPaths = checkedPaths;
	}

	public List<File> lookupVomsesInfo() {

		List<File> vomsesPaths = new ArrayList<File>();

		for (String p : checkedPaths) {
			File f = new File(p);
			if (f.exists())
				vomsesPaths.add(f);
		}

		return vomsesPaths;
	}

	public List<String> searchedPaths() {
		return checkedPaths;
	}
	
	public void addPath(String vomsesPath){
		checkedPaths.add(vomsesPath);
	}
}
