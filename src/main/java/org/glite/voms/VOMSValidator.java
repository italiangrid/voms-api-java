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
package org.glite.voms;

import java.security.cert.X509Certificate;

import org.italiangrid.voms.ac.impl.LegacyVOMSValidatorAdapter;

/**
 * This class is deprecated, and is provided for partial backwards compatibility with
 * existing users of the VOMS Java APIs.
 * 
 * @author Andrea Ceccanti
 * @deprecated
 *
 */
public class VOMSValidator extends LegacyVOMSValidatorAdapter {

	public VOMSValidator(X509Certificate cert) {
		super(cert);
		
	}

	public VOMSValidator(X509Certificate[] certChain) {
		super(certChain);
	}
}
