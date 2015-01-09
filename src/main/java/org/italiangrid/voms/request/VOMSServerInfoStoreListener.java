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

import java.util.List;

/**
 * This interface is used to notify about events related to the load operations
 * of VOMSES server endpoint information.
 * 
 * @author andreaceccanti
 *
 */
public interface VOMSServerInfoStoreListener {

  /**
   * Informs that no valid VOMS information was found on the system.
   * 
   * @param searchedPaths
   *          the list of searched paths
   */
  public void notifyNoValidVOMSESError(List<String> searchedPaths);

  /**
   * Informs that VOMSES is being search at the path passed as argument
   * 
   * @param vomsesPath
   *          the path where VOMSES information are being looked for
   */
  public void notifyVOMSESlookup(String vomsesPath);

  /**
   * Informs that VOMSES information was succesfully loaded from a given path
   * 
   * @param vomsesPath
   *          the path where VOMSES information was loaded from
   * @param info
   *          the {@link VOMSServerInfo} voms endpoint information
   */
  public void notifyVOMSESInformationLoaded(String vomsesPath,
    VOMSServerInfo info);

}
