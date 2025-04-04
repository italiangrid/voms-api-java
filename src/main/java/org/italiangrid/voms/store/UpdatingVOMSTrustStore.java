// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.store;

/**
 * A VOMS trust store that can be periodically refreshed.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface UpdatingVOMSTrustStore extends VOMSTrustStore, Updateable {

}
