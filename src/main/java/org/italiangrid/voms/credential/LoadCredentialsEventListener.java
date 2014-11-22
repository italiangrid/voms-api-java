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
package org.italiangrid.voms.credential;

/**
 * A {@link LoadCredentialsEventListener} is notified of the outcome of load
 * credentials operations.
 * 
 * @author andreaceccanti
 *
 */
public interface LoadCredentialsEventListener {

  /**
   * Informs that credentials are been looked for in the locations passed as
   * argument.
   * 
   * @param locations
   */
  public void notifyCredentialLookup(String... locations);

  /**
   * Informs that credentials have been succesfully loaded from the credentials
   * passed as argument.
   * 
   * @param locations
   */
  public void notifyLoadCredentialSuccess(String... locations);

  /**
   * Informs that credentials could not be loaded form the locations passed as
   * argument.
   * 
   * @param error
   *          the {@link Throwable} that caused the credential load operation to
   *          fail
   * @param locations
   *          the locations where the credentials where loaded from
   */
  public void notifyLoadCredentialFailure(Throwable error, String... locations);
}
