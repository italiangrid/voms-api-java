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
import java.util.List;

/**
 * 
 * A strategy for looking up a set of VOMS Attribute Certificates from a
 * certificate chain.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSACLookupStrategy {

  /**
   * This method defines how a set of VOMS Attribute Certificates is looked for
   * in a certificate chain.
   * 
   * @param certChain
   *        the certificate chain that will be searched for VOMS attribute 
   *        certificates
   * 
   * @return a {@link List} of {@link ACParsingContext} that describes the
   *         lookup outcome
   */
  public List<ACParsingContext> lookupVOMSAttributeCertificates(
    X509Certificate[] certChain);

}
