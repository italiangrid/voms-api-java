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
package org.italiangrid.voms.store;

import java.io.File;
import java.io.InputStream;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.store.impl.LSCFile;

/**
 * This interface defines a parser for VOMS LSC files.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface LSCFileParser {

	/**
	 * Parses an LSC file.
	 * 
	 * @param vo the name of the VO this LSC file is about
	 * @param hostname the name of host this LSC file is about
	 * @param file the LSC file
	 * @return an {@link LSCFile} object
	 * @throws VOMSError in case of parsing errors 
	 */
	public LSCFile parse(String vo, 
			String hostname,			
			File file);
	
	
	/**
	 * Parses an LSC file from a generic input stream.
	 * 
	 * @param vo the name of the VO this LSC file is about
	 * @param hostname the name of host this LSC file is about
	 * @param is an {@link InputStream} that contains the LSC information
	 * 
	 * @return an {@link LSCFile} object
	 * @throws VOMSError in case of parsing errors
	 */
	public LSCFile parse(String vo,
			String hostname,
			InputStream is);
	
	
	
}
