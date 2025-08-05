// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.request;

import org.italiangrid.voms.request.impl.LegacyVOMSESParserImpl;

/**
 * A factory class for {@link VOMSESParser}.
 * 
 * @author Andrea Ceccanti
 *
 */
public class VOMSESParserFactory {

  /**
   * @return Returns a {@link VOMSESParser}.
   */
  public static VOMSESParser newVOMSESParser() {

    return new LegacyVOMSESParserImpl();
  }
}
