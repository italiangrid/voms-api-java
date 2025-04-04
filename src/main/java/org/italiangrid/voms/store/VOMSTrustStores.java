// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.store;

import java.util.List;

import org.italiangrid.voms.store.impl.DefaultUpdatingVOMSTrustStore;

/**
 * A factory for VOMS trust stores
 * 
 * @author cecco
 *
 */
public class VOMSTrustStores {

  /**
   * Creates a {@link VOMSTrustStore} configured with default settings.
   * 
   * @return a {@link VOMSTrustStore} configured with default settings.
   */
  public static VOMSTrustStore newTrustStore() {

    return new DefaultUpdatingVOMSTrustStore();
  }

  /**
   * Creates a {@link VOMSTrustStore} configured according to the parameters
   * passed as argument
   * 
   * @param localTrustDirs
   *          the directory where voms information will be searched
   * @param updateFrequency
   *          the trust store update frequency in milliseconds
   * @param statusListener
   *          a listener that is notified of events related to the created trust
   *          store
   * @return a {@link VOMSTrustStore} configured as requested.
   */
  public static VOMSTrustStore newTrustStore(List<String> localTrustDirs,
    long updateFrequency, VOMSTrustStoreStatusListener statusListener) {

    return new DefaultUpdatingVOMSTrustStore(localTrustDirs, updateFrequency,
      statusListener);
  }

  /**
   * Creates a {@link VOMSTrustStore} configured according to the parameters
   * passed as argument
   * 
   * @param localTrustDirs
   *          the directory where voms information will be searched
   * @return a {@link VOMSTrustStore} configured as requested.
   */
  public static VOMSTrustStore newTrustStore(List<String> localTrustDirs) {

    return new DefaultUpdatingVOMSTrustStore(localTrustDirs);
  }

  /**
   * Creates a {@link VOMSTrustStore} configured according to the parameters
   * passed as argument
   * 
   * @param updateFrequency
   *          the trust store update frequency in milliseconds
   * @return a {@link VOMSTrustStore} configured as requested.
   */
  public static VOMSTrustStore newTrustStore(long updateFrequency) {

    return new DefaultUpdatingVOMSTrustStore(updateFrequency);
  }

}
