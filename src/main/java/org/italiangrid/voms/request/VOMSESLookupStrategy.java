/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2014.
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
package org.italiangrid.voms.request;

import java.io.File;
import java.util.List;

/**
 * An strategy for building a list of {@link File} objects which will provide access
 * to the local trusted VOMS server contact information.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSESLookupStrategy {

	/**
	 * @return a {@link List} of {@link File} objects that can be 
	 * used to parse VOMSES information.
	 */
	public List<File> lookupVomsesInfo();
	
	/**
	 * @return a {@link List} of the paths that have been looked up to find
	 * {@link File} objects that can be used to parse VOMSES information.
	 */
	public List<String> searchedPaths();
}
