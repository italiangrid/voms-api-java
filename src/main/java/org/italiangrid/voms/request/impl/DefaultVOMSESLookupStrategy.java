// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.request.impl;

import java.util.Arrays;

/**
 * Default VOMSES information lookup strategy.
 * 
 * This implementation looks for vomses information in the following paths:
 * <ul>
 * <li>/etc/vomses
 * <li>${user.home}/.glite/vomses
 * <li>${user.home}/.voms/vomses
 * </ul>
 * 
 * @author Andrea Ceccanti
 * 
 */
public class DefaultVOMSESLookupStrategy extends BaseVOMSESLookupStrategy {

  public static final String DEFAULT_VOMSES_DIR = "/etc/vomses";

  public DefaultVOMSESLookupStrategy() {

    super(Arrays.asList(DEFAULT_VOMSES_DIR, System.getProperty("user.home")
      + "/.glite/vomses", System.getProperty("user.home") + "/.voms/vomses"));
  }
}
