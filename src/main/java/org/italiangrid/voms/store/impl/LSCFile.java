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
package org.italiangrid.voms.store.impl;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.italiangrid.voms.store.LSCInfo;

import eu.emi.security.authn.x509.impl.OpensslNameUtils;
import eu.emi.security.authn.x509.impl.X500NameUtils;

/**
 * Represents a VOMS LSC (Legacy Secure Channel) file.
 * 
 * <p>The LSC file describes the certificate chain that a VOMS attribute authority
 * uses to sign a VOMS attribute certificate. The LSC mechanism helps in solving
 * the public key distribution problem for VOMS AA certificates and is used in
 * the VOMS validation process to validate the signature on the AC. It does this by
 * extracting the VOMS AA certificate included in the VOMS extension and ensuring
 * that the chain conforms to the description in the LSC file.</p>
 * 
 * <p>Two {@link LSCFile} objects are considered equal if their VO and hostname fields match.</p>
 * 
 */
public class LSCFile implements LSCInfo {

  /** The LSC filename. */
  String filename;

  /** The VO (Virtual Organization) this LSC file is associated with. */
  String vo;

  /** The hostname that this LSC file pertains to. */
  String hostname;

  /** The certificate chain description contained in this LSC file. */
  List<String> certChainDescription = new ArrayList<>();

  /**
   * Returns the VO name.
   *
   * @return the VO name
   */
  public String getVOName() {

    return vo;
  }

  /**
   * Returns the hostname.
   *
   * @return the hostname
   */
  public String getHostname() {

    return hostname;
  }

  /**
   * Returns the certificate chain description.
   *
   * @return a list of certificate chain descriptions
   */
  public List<String> getCertificateChainDescription() {

    return certChainDescription;
  }

  /**
   * Returns the filename of the LSC file.
   *
   * @return the LSC filename
   */
  public String getFilename() {

    return filename;
  }

  /**
   * Sets the filename for this LSC file.
   *
   * @param filename the filename to set
   */
  public void setFilename(String filename) {

    this.filename = filename;
  }

  /**
   * Sets the VO name.
   *
   * @param vo the VO name to set
   */
  public void setVo(String vo) {

    this.vo = vo;
  }

  /**
   * Sets the hostname.
   *
   * @param hostname the hostname to set
   */
  public void setHostname(String hostname) {

    this.hostname = hostname;
  }

  /**
   * Sets the certificate chain description.
   *
   * @param certChainDesc the certificate chain description to set
   */
  public void setCertificateChainDescription(List<String> certChainDesc) {

    this.certChainDescription = new ArrayList<>(certChainDesc);
  }

  /**
   * Computes the hash code for this LSC file.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {

    final int prime = 31;
    int result = 1;
    result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
    result = prime * result + ((vo == null) ? 0 : vo.hashCode());
    return result;
  }

  /**
   * Determines if two {@code LSCFile} objects are equal based on their VO and hostname.
   *
   * @param obj the object to compare with
   * @return {@code true} if the objects are equal, otherwise {@code false}
   */
  @Override
  public boolean equals(Object obj) {

    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    LSCFile other = (LSCFile) obj;
    if (hostname == null) {
      if (other.hostname != null)
        return false;
    } else if (!hostname.equals(other.hostname))
      return false;
    if (vo == null) {
      if (other.vo != null)
        return false;
    } else if (!vo.equals(other.vo))
      return false;
    return true;
  }

  /**
   * Returns a string representation of this LSC file.
   *
   * @return a string representation of this LSC file
   */
  @Override
  public String toString() {

    return "LSCFile [filename=" + filename + ", vo=" + vo + ", hostname="
      + hostname + ", certChainDescription=" + certChainDescription + "]";
  }

  /**
   * Checks if the given certificate chain matches the description in this LSC file.
   *
   * @param certChain the certificate chain to verify
   * @return {@code true} if the certificate chain matches, otherwise {@code false}
   */
  public boolean matches(X509Certificate[] certChain) {

    if (certChainDescription == null || certChainDescription.isEmpty()) {
      return false;
    }

    if (certChain == null || certChain.length == 0) {
      return false;
    }

    if (certChainDescription.size() != certChain.length * 2) {
      return false;
    }

    for (int i = 0; i < certChain.length; i++) {
      if (!matches(certChain[i].getSubjectX500Principal(), certChainDescription.get(2 * i))) {
        return false;
      }
      if (!matches(certChain[i].getIssuerX500Principal(), certChainDescription.get(2 * i + 1))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if a certificate distinguished name (DN) matches the expected DN in the LSC file.
   *
   * @param certDn the certificate DN
   * @param lscDn the expected DN in the LSC file
   * @return {@code true} if the DNs match, otherwise {@code false}
   */
  @SuppressWarnings("deprecation")
  private boolean matches(X500Principal certDn, String lscDn) {
    return X500NameUtils.equal(certDn, OpensslNameUtils.opensslToRfc2253(lscDn));
  }
}
