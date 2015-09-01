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
package org.italiangrid.voms.request;

import java.io.InputStream;

/**
 * The strategy for parsing a response coming from a VOMS service.
 * 
 * @author valerioventuri
 *
 */
public interface VOMSResponseParsingStrategy {

  /**
   * Parse the response coming from a VOMS service and build a
   * {@link VOMSResponse} object.
   * 
   * @param inputStream
   *          the response from the VOMS service.
   * @return the response object representing the response from the service.
   */
  public VOMSResponse parse(InputStream inputStream);

}
