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
package org.italiangrid.voms;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.X509AttributeCertificateHolder;

/**
 * The VOMS attributes information. This interface provides access to all the
 * information available in a VOMS attribute certificate.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSAttribute {

  /**
   * This method returns the name of the VO this VOMS attributes are about
   * 
   * @return The name of the VO this VOMS attributes are about
   */
  public String getVO();

  /**
   * This method returns the host where the VOMS Attribute Authority (AA) that
   * signed these attribute lives
   * 
   * @return The name of the host where the VOMS AA that signed these attributes
   *         lives
   */
  public String getHost();

  /**
   * This method returns the port on which the VOMS Attribute Authority (AA)
   * that signed these attributes listens for requests.
   * 
   * @return The port on which the VOMS AA that signed these attributes listens
   *         for requests
   */
  public int getPort();

  /**
   * This method returns the subject (as an {@link X500Principal}) of the holder
   * of these VOMS attributes
   * 
   * @return The subject of the holder of these VOMS attributes
   */
  public X500Principal getHolder();

  /**
   * This method returns the holder certificate serial number
   * 
   * @return The serial number of the holder certificate
   */
  public BigInteger getHolderSerialNumber();

  /**
   * This method returns the subject of the VOMS Attribute Authority that signed
   * these attributes.
   * 
   * @return The subject of the VOMS AA that signed these attributes
   */
  public X500Principal getIssuer();

  /**
   * This method returns the attributes' validity start time
   * 
   * @return The attributes' validity start time
   */
  public Date getNotBefore();

  /**
   * This method returns the attributes' validity end time
   * 
   * @return The attributes' validity end time
   */
  public Date getNotAfter();

  /**
   * This method returns the list of signed Fully Qualified Attribute Names
   * (FQANs) in this {@link VOMSAttribute}.
   * 
   * @return The {@link List} of VOMS fully qualified attribute names
   */
  public List<String> getFQANs();

  /**
   * This method returns the primary FQAN (the first in the list returned by
   * {@link #getFQANs()}) in this {@link VOMSAttribute}.
   * 
   * @return The primary VOMS fully qualified attribute name
   */
  public String getPrimaryFQAN();

  /**
   * This method returns the signature on the VOMS attribute certificate as a
   * byte array.
   * 
   * @return The signature of this VOMS attributes
   */
  public byte[] getSignature();

  /**
   * This method returns the list of VOMS Generic attributes in this
   * {@link VOMSAttribute}.
   * 
   * @return The VOMS generic attributes
   */
  public List<VOMSGenericAttribute> getGenericAttributes();

  /**
   * This method returns the list of targets defined for this
   * {@link VOMSAttribute}.
   * 
   * @return The targets for this VOMS attributes
   */
  public List<String> getTargets();

  /**
   * This method returns the certificate chain of the VOMS Attribute Authority
   * (AA) that signed this {@link VOMSAttribute}.
   * 
   * @return The VOMS AA certificate chain
   */
  public X509Certificate[] getAACertificates();

  /**
   * This method checks whether the attributes are valid in the current instant
   * of time. No validation is performed on the attributes.
   * 
   * @return <code>true</code> if valid, <code>false</code> otherwise
   */
  public boolean isValid();

  /**
   * This method checks whether the attributes are valid in a given time passed
   * as argument. No validation is performed on the attributes.
   * 
   * @param time
   *          the time used for the validity check
   * @return <code>true</code> if valid, <code>false</code> otherwise
   */
  public boolean validAt(Date time);

  /**
   * This method returns the underlying VOMS Attribute certificate object.
   * 
   * @return the underlying bouncycastle object for the VOMS attribute
   *         certificate.
   */
  public X509AttributeCertificateHolder getVOMSAC();

}
