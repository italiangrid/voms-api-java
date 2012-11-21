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
package org.italiangrid.voms.store;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

/**
 * The VOMS trust store takes care of parsing local trusted information (being certificates or LSC files)
 * for known VOMS servers.
 *  
 * @author Andrea Ceccanti
 *
 */
public interface VOMSTrustStore {
	
	/**
	 * Returns the locally trusted directories where VOMS trust information (being VOMS server certificates or
	 * LSC files) are searched for.
	 * 
	 * @return a {@link List} of local paths
	 */
	public List<String> getLocalTrustedDirectories();
	
	/**
	 * Returns the list of VOMS Attribute Authority certificates held in this {@link VOMSTrustStore}.
	 * @return the collection of VOMS Attribute Authority certificates held in this {@link VOMSTrustStore}, an empty list
	 * if no certificate was found.
	 */
	public List<X509Certificate> getLocalAACertificates();
	
	/**
	 * Returns the VOMS Attribute Authority certificate held in this {@link VOMSTrustStore} whose subject
	 * matches the subject passed as argument.
	 * 
	 * @param aaCertSubject a certificate subject
	 * @return the VOMS AA {@link X509Certificate} that matches the subject passed as argument or null if no matching
	 * 	certificate is found in this store 
	 */
	public X509Certificate getAACertificateBySubject(X500Principal aaCertSubject);
	
	
	/**
	 * Returns the LSC information held in this {@link VOMSTrustStore} for the vo and hostname passed as arguments.
	 * 
	 * @param voName the name of the VO for which the LSC applies
	 * @param hostname the name of the host for which the LSC applies
	 * @return a {@link LSCInfo} object, or null if no LSC  matching the arguments was found
	 */
	public LSCInfo getLSC(String voName, String hostname);
	
	/**
	 * Returns all the LSC information held in this {@link VOMSTrustStore}.
	 * The returned {@link Map} is keyed by VO name.
	 * @return a possibly empty map {@link LSCInfo} objects 
	 */
	public Map<String,Set<LSCInfo>> getAllLSCInfo();
	
	/**
	 * Loads trust information from the sources configured for this trust store.
	 */
	public void loadTrustInformation();
	
}
