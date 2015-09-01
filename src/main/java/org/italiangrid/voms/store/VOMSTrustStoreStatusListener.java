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
package org.italiangrid.voms.store;

import java.io.File;
import java.security.cert.X509Certificate;

/**
 * 
 * This interface used to notify interested listeners in status changes of a
 * VOMS trust store.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSTrustStoreStatusListener {

  /**
   * Informs that certificates are being looked for in the directory passed as
   * argument
   * 
   * @param dir
   *          the directory where certificates are being looked for
   */
  public void notifyCertficateLookupEvent(String dir);

  /**
   * Informs that VOMS LSC file information is being looked for in the directory
   * passed as argument.
   * 
   * @param dir
   *          the directory where certificates are being looked for
   */
  public void notifyLSCLookupEvent(String dir);

  /**
   * Informs that a VOMS AA certificate has been loaded in the store
   * 
   * @param cert
   *          the VOMS AA certificate loaded
   * @param f
   *          the file from which the certificate has been loaded
   */
  public void notifyCertificateLoadEvent(X509Certificate cert, File f);

  /**
   * Informs that VOMS LSC information has been loaded in the store
   * 
   * @param lsc
   *          the loaded VOMS LSC information
   * @param f
   *          the file from which the LSC information has been loaded
   */
  public void notifyLSCLoadEvent(LSCInfo lsc, File f);

}
