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

import java.util.Collections;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.ac.ACParsingContext;
import org.italiangrid.voms.ac.VOMSAttributesNormalizationStrategy;
import org.italiangrid.voms.asn1.VOMSACUtils;
import org.italiangrid.voms.asn1.VOMSConstants;

/**
 * 
 * This strategy extracts the VOMS attributes from the top VOMS extension found
 * in the parsing context passed as argument.
 * 
 * @author Andrea Ceccanti
 *
 */
public class LeafVOMSExtensionNormalizationStrategy implements
  VOMSAttributesNormalizationStrategy, VOMSConstants {

  public List<VOMSAttribute> normalizeAttributes(List<ACParsingContext> acs) {

    if (acs == null || acs.isEmpty())
      return Collections.emptyList();

    List<AttributeCertificate> attrs = acs.get(0).getACs();

    return VOMSACUtils.deserializeVOMSAttributes(attrs);

  }

}
