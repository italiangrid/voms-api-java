// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.store;

/**
 * Represents an object which can be periodically updated according to an update
 * frequency.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface Updateable {

  /**
   * @return the frequency (in milliseconds) currently set for this
   *         {@link Updateable} object.
   */
  public long getUpdateFrequency();

  /**
   * Updates the object.
   */
  public void update();

  /**
   * Cancels the future updates of this {@link Updateable} object.
   */
  public void cancel();

}
