// SPDX-FileCopyrightText: 2026 Istituto Nazionale di Fisica Nucleare
// SPDX-License-Identifier: Apache-2.0
package org.italiangrid.voms.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.InMemoryRepresentable;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/** Strict regression tests for bc-java #2547. Expected to fail on affected BC. */
class TestMalformedAsn1Regression {

  private static final String[] MALFORMED = {
    "238003000000", // Indefinite constructed BIT STRING
    // with empty content.
    "3080030209000000", // Indefinite SEQUENCE, BIT STRING with invalid pad bits.
    "300403020900" // Definite SEQUENCE containing invalid pad bits.
  };

  @Disabled
  @Test
  void malformedAsn1UsesCheckedExceptions() {
    for (String input : MALFORMED) {
      assertThrows(IOException.class, () -> ASN1Primitive.fromByteArray(Hex.decode(input)), input);
    }
  }

  @Disabled
  @Test
  void streamingMalformedAsn1UsesCheckedExceptions() {
    for (String input : MALFORMED) {
      assertThrows(
          IOException.class,
          () -> {
            ASN1StreamParser parser = new ASN1StreamParser(Hex.decode(input));
            ((InMemoryRepresentable) parser.readObject()).getLoadedObject();
          },
          input);
    }
  }

  @Disabled
  @Test
  void validInputStillParsesAfterRejectedInput() throws Exception {
    for (String input : MALFORMED) {
      assertThrows(IOException.class, () -> ASN1Primitive.fromByteArray(Hex.decode(input)), input);
      assertEquals(new ASN1Integer(1), ASN1Primitive.fromByteArray(Hex.decode("020101")));
    }
  }
}
