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

import java.security.cert.X509Certificate;
import java.util.List;

/**
 * The VOMS LSC information.
 * @author Andrea Ceccanti
 *
 */
public interface LSCInfo {
	
	
	/**
	 * Sets the name of the file from where this LSC info was parsed from.
	 * 
	 * @param filename the name of the file from where this LSC info was parsed from.
	 */
	public void setFilename(String filename);
	
	
	/** 
	 * Returns the name of file from where this LSC info was parsed from.
	 * 
	 * @return the name of the file from where this LSC info was parsed from.
	 */
	public String getFilename();
	
	/**
	 * Returns the VO name this LSC info applies to.
	 * 
	 * @return the VO name this LSC info applies to
	 */
	public String getVOName();
	
	/**
	 * Returns the host name of the VOMS AA this LSC info applies to.
	 * 
	 * @return the host name of the VOMS AA this LSC info applies to
	 */
	public String getHostname();
	
	/**
	 * Returns the certificate chain description of the VOMS AA for the
	 * given VO and hostname.
	 * 
	 * The certificate chain description is a list of X.500 distinguished names encoded as strings 
	 * according to the OpenSSL slash-separated format, as in:
	 * <verbatim>
	 * /C=IT/O=INFN/CN=INFN CA
	 * </verbatim>
	 * 
	 * The first element in the description is the leaf certificate, while the last is the CA certificate.
	 * @return the certificate chain description of the VOMS AA for the given VO and hostname.
	 */
	public List<String> getCertificateChainDescription();
	
	/**
	 * Checks if the certificate chain description maintained in the LSC information
	 * matches the certificate chain passed as argument.
	 * @param certChain
	 * @return <code>true</code> if the description matches, <code>false</code> otherwise
	 */
	public boolean matches(X509Certificate[] certChain);
	
}
