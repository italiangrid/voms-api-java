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

import java.security.cert.X509Certificate;
import java.util.List;

import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.ac.ACParsingContext;
import org.italiangrid.voms.ac.VOMSACLookupStrategy;
import org.italiangrid.voms.ac.VOMSACParser;
import org.italiangrid.voms.ac.VOMSAttributesNormalizationStrategy;
import org.italiangrid.voms.util.NullListener;

/**
 * Default implementation of the VOMS attribute certificate parsing logic.
 * 
 * @author Andrea Ceccanti
 *
 */
public class DefaultVOMSACParser implements VOMSACParser {

  private final VOMSACLookupStrategy acLookupStrategy;
  private final VOMSAttributesNormalizationStrategy acNormalizationStrategy = new LeafVOMSExtensionNormalizationStrategy();

  public DefaultVOMSACParser() {

    this(new LeafACLookupStrategy(NullListener.INSTANCE));
  }

  public DefaultVOMSACParser(VOMSACLookupStrategy strategy) {

    this.acLookupStrategy = strategy;
  }

  public List<VOMSAttribute> parse(X509Certificate[] validatedChain) {

    if (validatedChain == null)
      throw new NullPointerException("Cannot parse a null certchain!");
    List<ACParsingContext> parsedACs = acLookupStrategy
      .lookupVOMSAttributeCertificates(validatedChain);
    return acNormalizationStrategy.normalizeAttributes(parsedACs);
  }
}
