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
package org.italiangrid.voms.ac;

import java.security.cert.X509Certificate;

import org.italiangrid.voms.VOMSAttribute;

/**
 * The strategy implemented to perform the validation of a VOMS attribute certificate.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSACValidationStrategy {

	/**
	 * Validates a VOMS Attribute Certificate
	 * @param attributes the parsed VOMS attributes
	 * @param theChain the certificate chain from which the attributes were parsed
	 * @return a {@link VOMSValidationResult} object describing the outcome of the validation
	 */
	public VOMSValidationResult validateAC(VOMSAttribute attributes, X509Certificate[] theChain);
	
	
	/**
	 * Validates VOMS attributes not extracted from a certificate chain (e.g., as returned
	 * from the VOMS server)
	 * 
	 * @param attributes the VOMS attributes
	 * @return a {@link VOMSValidationResult} object describing the outcome of the validation
	 */
	public VOMSValidationResult validateAC(VOMSAttribute attributes);
}
