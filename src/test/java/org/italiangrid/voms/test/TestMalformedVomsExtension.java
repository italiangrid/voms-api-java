// SPDX-FileCopyrightText: 2026 Istituto Nazionale di Fisica Nucleare
// SPDX-License-Identifier: Apache-2.0
package org.italiangrid.voms.test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.encoders.Hex;
import org.italiangrid.voms.asn1.VOMSACUtils;
import org.junit.jupiter.api.Test;

class TestMalformedVomsExtension {

  @Test
  void malformedOuterEncodingIsRejectedAsIOException() {
    for (String hex : new String[] {"238003000000", "3080030209000000"}) {
      assertThrows(
          IOException.class, () -> VOMSACUtils.getACsFromVOMSExtension(Hex.decode(hex)), hex);
    }
  }

  @Test
  void malformedExtensionPayloadIsRejectedAsIOException() throws Exception {
    for (String hex : new String[] {"238003000000", "3080030209000000"}) {
      byte[] extension = new DEROctetString(Hex.decode(hex)).getEncoded();
      assertThrows(IOException.class, () -> VOMSACUtils.getACsFromVOMSExtension(extension), hex);
      // A rejected input must not poison subsequent parsing.
      assertTrue(
          VOMSACUtils.getACsFromVOMSExtension(new DEROctetString(Hex.decode("3000")).getEncoded())
              .isEmpty());
    }
  }

  @Test
  void absentExtensionStillReturnsEmptyList() throws Exception {
    assertTrue(VOMSACUtils.getACsFromVOMSExtension(null).isEmpty());
  }
}
