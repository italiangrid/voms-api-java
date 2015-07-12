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

import java.util.List;

import org.italiangrid.voms.VOMSAttribute;

/**
 * A strategy to select the set of relevant and appliable VOMS attributes from a
 * set of parsed VOMS attribute certificates.
 * 
 * This strategy is responsible of creating the {@link VOMSAttribute} objects
 * which represents the authorizative VOMS authorization information.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSAttributesNormalizationStrategy {

  /**
   * Returns the normalized view of VOMS Authorization information starting from
   * a list of VOMS Attribute certificates.
   * 
   * @param acs a list of VOMS Attribute certificates
   * @return a possibly empty list {@link VOMSAttribute} object
   */
  public List<VOMSAttribute> normalizeAttributes(List<ACParsingContext> acs);
}
