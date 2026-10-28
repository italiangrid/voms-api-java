// SPDX-FileCopyrightText: 2026 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test;

import static org.junit.jupiter.api.Assertions.*;

import org.italiangrid.voms.util.VOMSBase64Decoder;
import org.junit.jupiter.api.Test;

class TestVOMSBase64Decoder {

  @Test
  void decodesEmptyInputAndPartialGroups() {
    assertArrayEquals(new byte[0], VOMSBase64Decoder.decode(""));
    assertArrayEquals(new byte[] {0}, VOMSBase64Decoder.decode("aa"));
    assertArrayEquals(new byte[] {0, 1}, VOMSBase64Decoder.decode("aae"));
    assertArrayEquals(new byte[] {0, 1, 2}, VOMSBase64Decoder.decode("aaec"));
    assertArrayEquals(new byte[] {0, 1, 2, 0}, VOMSBase64Decoder.decode("aaecaa"));
  }

  @Test
  void decodesLegacyAlphabetIncludingPunctuation() {
    assertArrayEquals(
        new byte[] {(byte) 251, (byte) 255, (byte) 255}, VOMSBase64Decoder.decode("[]]]"));
    assertArrayEquals(new byte[] {105, (byte) 183, (byte) 29}, VOMSBase64Decoder.decode("ABCD"));
  }

  @Test
  void newlineSelectsStandardBase64AndAllowsWhitespace() {
    assertArrayEquals(
        new byte[] {(byte) 251, (byte) 255, (byte) 255}, VOMSBase64Decoder.decode("\n+///\n"));
    assertArrayEquals(new byte[] {0, 1}, VOMSBase64Decoder.decode(" \nAAE=\n "));
  }

  @Test
  void rejectsNonASCIICharactersInEachPosition() {
    for (String value : new String[] {"\u0080aaa", "a\u0080aa", "aa\u0080a", "aaa\u0080"}) {
      assertThrows(IllegalArgumentException.class, () -> VOMSBase64Decoder.decode(value));
    }
  }
}
