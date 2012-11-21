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
package org.italiangrid.voms;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.glite.voms.FQAN;


/**
 * The VOMS attributes information. This interface provides access to all the information 
 * available in a VOMS attribute certificate.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSAttribute {
	
	/**
	 * @return The name of the VO this VOMS attributes are about
	 */
	public String getVO();
	
	/**
	 * @return The name of the host where the VOMS AA that signed these attributes lives
	 */
	public String getHost();
	
	/**
	 * @return The port on which the VOMS AA that signed these attributes listens for requests
	 */
	public int getPort();
	
	/**
	 * @return The subject of the holder of these VOMS attributes
	 */
	public X500Principal getHolder();
	
	/**
	 * @return The serial number of the holder certificate
	 */
	public BigInteger getHolderSerialNumber();
	
	/**
	 * @return The subject of the VOMS AA that signed these attributes
	 */
	public X500Principal getIssuer();
	
	/**
	 * @return The attributes' validity start time 
	 */
	public Date getNotBefore();
	
	/**
	 * @return The attributes' validity end time
	 */
	public Date getNotAfter();
	
	/**
	 * @return The {@link List} of VOMS fully qualified attribute names
	 */
	public List<String> getFQANs();
	
	/**
	 * @return The primary VOMS fully qualified attribute name
	 */
	public String getPrimaryFQAN();
	
	/**
	 * @return The signature of this VOMS attributes
	 */
	public byte[] getSignature();
	
	/**
	 * @return The VOMS generic attributes
	 */
	public List<VOMSGenericAttribute> getGenericAttributes();
	
	/**
	 * @return The targets for this VOMS attributes
	 */
	public List<String> getTargets();
	
	/**
	 * @return The VOMS AA certificate chain
	 */
	public X509Certificate[] getAACertificates();
	
	/**
	 * This method checks whether the attributes are valid in the
	 * current instant of time. No validation is performed on the attributes.
	 * @return <code>true</code> if valid, <code>false</code> otherwise
	 */
	public boolean isValid();
	
	/**
	 * This method checks whether the attributes are valid in a given time
	 * passed as argument. No validation is performed on the attributes.
	 *  
	 * @param time 
	 * @return <code>true</code> if valid, <code>false</code> otherwise
	 */
	public boolean validAt(Date time);
	
	/**
	 * 
	 * @return the underlying bouncycastle object for the VOMS attribute certificate.
	 */
	public X509AttributeCertificateHolder getVOMSAC();
	
	/**
	 * @return a possibly-empty list of {@link FQAN} objects
	 * @deprecated use {@link #getFQANs()} instead
	 */
	public List<FQAN> getListOfFQAN();
	
	/**
	 * 
	 * @return a possibly-empty list of FQAN strings
	 * @deprecated use {@link #getFQANs()} instead
	 */
	public List<String> getFullyQualifiedAttributes();
	
	
	
}
