// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.credential.impl;

import org.italiangrid.voms.credential.ProxyNamingPolicy;

public class DefaultProxyPathBuilder implements ProxyNamingPolicy {

  public String buildProxyFileName(String tmpPath, int userId) {

    return String.format("%s/x509up_u%d", tmpPath, userId);
  }

}
