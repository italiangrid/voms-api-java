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

import org.italiangrid.voms.VOMSAttribute;

/**
 * This interface is used to notify interested listeners of VOMS attribute
 * certificate validation outcome.
 * 
 * @author andreaceccanti
 *
 */
public interface ValidationResultListener {

  /**
   * Informs of the result of the validation of a set of {@link VOMSAttribute}
   * 
   * @param result
   *          the validation result
   * 
   */
  public void notifyValidationResult(VOMSValidationResult result);
}
