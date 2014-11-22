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

import java.net.URI;

/**
 * This interface represents a VOMS server contact information, typically
 * provided in vomses files.
 * 
 * @see VOMSESLookupStrategy
 * @see VOMSESParser
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSServerInfo {

  /**
   * Returns the alias for this {@link VOMSServerInfo}.
   * 
   * @return the alias
   */
  public String getAlias();

  /**
   * Returns the VO name for this {@link VOMSServerInfo}.
   * 
   * @return the vo name
   */
  public String getVoName();

  /**
   * Returns the URL for this {@link VOMSServerInfo}.
   * 
   * @return the contact {@link URI}
   */
  public URI getURL();

  /**
   * Returns the certificate subject as listed in the VOMSES configuration for
   * this {@link VOMSServerInfo}
   * 
   * @return a string containing the certificate subject, enconded following the
   *         DN openssl slash-separated syntax
   */
  public String getVOMSServerDN();

}