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
package org.italiangrid.voms.ac.impl;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.ac.ACLookupListener;
import org.italiangrid.voms.ac.ACParsingContext;
import org.italiangrid.voms.ac.VOMSACLookupStrategy;
import org.italiangrid.voms.asn1.VOMSACUtils;
import org.italiangrid.voms.asn1.VOMSConstants;
import org.italiangrid.voms.util.NullListener;

import eu.emi.security.authn.x509.proxy.ProxyUtils;

/**
 * This strategy returns the leaf VOMS Attribute Certificate in a certificate
 * chain, i.e. the Attribute Certificate found in the latest delegation in the
 * chain.
 * 
 * @author Andrea Ceccanti
 *
 */
public class LeafACLookupStrategy implements VOMSACLookupStrategy,
  VOMSConstants {

  private ACLookupListener listener;

  public LeafACLookupStrategy(ACLookupListener l) {

    this.listener = l;
  }

  public LeafACLookupStrategy() {

    this(NullListener.INSTANCE);
  }

  public List<ACParsingContext> lookupVOMSAttributeCertificates(
    X509Certificate[] certChain) {

    List<ACParsingContext> parsedACs = new ArrayList<ACParsingContext>();

    if (certChain == null || certChain.length == 0)
      throw new VOMSError(
        "Cannot extract VOMS Attribute Certificates from a null or empty certificate chain!");

    for (int index = 0; index < certChain.length; index++) {

      X509Certificate cert = certChain[index];

      listener.notifyACLookupEvent(certChain, index);

      try {

        if (ProxyUtils.isProxy(cert)) {

          List<AttributeCertificate> vomsACs = VOMSACUtils
            .getACsFromCertificate(cert);

          // Break at the first AC found from the top of the chain
          if (!vomsACs.isEmpty()) {

            listener.notifyACParseEvent(certChain, index);

            ACParsingContext ctx = new ACParsingContext(vomsACs, index,
              certChain);
            parsedACs.add(ctx);
            break;
          }
        }

      } catch (IOException e) {
        throw new VOMSError(e.getMessage(), e);
      }
    }

    return parsedACs;
  }
}
