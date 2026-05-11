// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test.ac;

import static org.italiangrid.voms.util.GaParser.parseGaString;

import java.util.List;
import org.italiangrid.voms.VOMSGenericAttribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestGaParser {

  @Test
  public void testEmptyString() {

    List<VOMSGenericAttribute> result = parseGaString("");
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void testNullString() {

    Assertions.assertThrows(NullPointerException.class, () -> parseGaString(null));
  }

  @Test
  public void testInvalidStrings() {

    Assertions.assertTrue(parseGaString("dsa").isEmpty());
    Assertions.assertTrue(parseGaString("=, a == d").isEmpty());
  }

  @Test
  public void testValidStrings() {

    List<VOMSGenericAttribute> gas = parseGaString("ciccio = paglia");

    Assertions.assertEquals(1, gas.size());

    Assertions.assertEquals("ciccio", gas.get(0).getName());
    Assertions.assertEquals("paglia", gas.get(0).getValue());
    Assertions.assertNull(gas.get(0).getContext());

    gas = parseGaString("  c= p   , pippo =franco,a8_d2=789");

    Assertions.assertEquals(3, gas.size());

    Assertions.assertEquals("c", gas.get(0).getName());
    Assertions.assertEquals("p", gas.get(0).getValue());

    Assertions.assertEquals("pippo", gas.get(1).getName());
    Assertions.assertEquals("franco", gas.get(1).getValue());

    Assertions.assertEquals("a8_d2", gas.get(2).getName());
    Assertions.assertEquals("789", gas.get(2).getValue());
  }
}
