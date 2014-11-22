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

import java.util.Set;

/**
 * A store containing the contact information for locally trusted VOMS servers.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSServerInfoStore {

  /**
   * Returns a set of {@link VOMSServerInfo} object matching a vo name passed as
   * argument.
   * 
   * @param voName
   *          a VO name
   * @return a possibly empty set of {@link VOMSServerInfo} object matching the
   *         vo name passed as argument
   */
  public Set<VOMSServerInfo> getVOMSServerInfo(String voName);

  /**
   * Returns a set of all {@link VOMSServerInfo} objects in this
   * {@link VOMSServerInfoStore}.
   * 
   * @return a possibly empty set of all {@link VOMSServerInfo} objects in this
   *         {@link VOMSServerInfoStore}.
   */
  public Set<VOMSServerInfo> getVOMSServerInfo();

  /**
   * Adds a {@link VOMSServerInfo} to this {@link VOMSServerInfoStore}.
   * 
   * @param info
   *          the {@link VOMSServerInfo} object to add.
   */
  public void addVOMSServerInfo(VOMSServerInfo info);

}