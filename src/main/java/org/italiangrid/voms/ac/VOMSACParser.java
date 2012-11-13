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
package org.italiangrid.voms.ac;

import java.security.cert.X509Certificate;
import java.util.List;

import org.italiangrid.voms.VOMSAttribute;

/**
 * This interface defines the behavior of a VOMS Attribute Certificate parser.
 * 
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSACParser {

	/**
	 * Looks for and parses VOMS attributes in the certificate chain passed as argument (which is assumed to be already validated).
	 * 
	 * @param validatedChain a validated X.509 certificate chain  
	 * @return a possibly empty list of {@link VOMSAttribute} objects providing access to the parsed VOMS attributes
	 */
	public List<VOMSAttribute> parse(X509Certificate[] validatedChain);
	
}
