// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.request.impl;

import org.bouncycastle.util.encoders.Base64;
import org.italiangrid.voms.request.ACDecodingStrategy;

public class GoodACDecodingStrategy implements ACDecodingStrategy {

  public byte[] decode(String ac) {

    return Base64.decode(ac.trim().replaceAll("\n", ""));
  }

}
