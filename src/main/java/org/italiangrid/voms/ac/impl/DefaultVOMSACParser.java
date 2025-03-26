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
 * This class is responsible for extracting and normalizing VOMS attributes
 * from a given X.509 certificate chain.
 *
 * <p>It utilizes a {@link VOMSACLookupStrategy} to locate attribute certificates
 * within the provided chain and applies a {@link VOMSAttributesNormalizationStrategy}
 * to normalize the extracted attributes.</p>
 *
 * <p>By default, it uses {@link LeafACLookupStrategy} for lookup and
 * {@link LeafVOMSExtensionNormalizationStrategy} for normalization.</p>
 *
 */
public class DefaultVOMSACParser implements VOMSACParser {

  private final VOMSACLookupStrategy acLookupStrategy;
  private final VOMSAttributesNormalizationStrategy acNormalizationStrategy = new LeafVOMSExtensionNormalizationStrategy();

  /**
   * Creates a new {@code DefaultVOMSACParser} with the default lookup strategy.
   * Uses {@link LeafACLookupStrategy} with a {@link NullListener} instance.
   */
  public DefaultVOMSACParser() {

    this(new LeafACLookupStrategy(NullListener.INSTANCE));
  }

  /**
   * Creates a new {@code DefaultVOMSACParser} with a specified lookup strategy.
   * Uses {@link LeafVOMSExtensionNormalizationStrategy} for attribute normalization.
   *
   * @param strategy the lookup strategy to use for locating attribute certificates
   * @throws NullPointerException if the provided strategy is {@code null}
   */
  public DefaultVOMSACParser(VOMSACLookupStrategy strategy) {

    this.acLookupStrategy = strategy;
  }

  /**
   * Parses and extracts VOMS attributes from a validated X.509 certificate chain.
   *
   * @param validatedChain the certificate chain to analyze
   * @return a list of extracted and normalized {@link VOMSAttribute} objects
   * @throws NullPointerException if the provided certificate chain is {@code null}
   */
  @Override
  public List<VOMSAttribute> parse(X509Certificate[] validatedChain) {

    if (validatedChain == null)
      throw new NullPointerException("Cannot parse a null certchain!");
    List<ACParsingContext> parsedACs = acLookupStrategy
      .lookupVOMSAttributeCertificates(validatedChain);
    return acNormalizationStrategy.normalizeAttributes(parsedACs);
  }
}
