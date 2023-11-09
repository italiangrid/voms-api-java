/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.italiangrid.voms.test.ac;

import static org.italiangrid.voms.util.GaParser.parseGaString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.italiangrid.voms.VOMSGenericAttribute;
import org.junit.Test;

public class TestGaParser {

  @Test
  public void testEmptyString() {

    List<VOMSGenericAttribute> result = parseGaString("");
    assertTrue(result.isEmpty());

  }

  @Test
  public void testNullString() {

    assertThrows(NullPointerException.class, () -> {
      parseGaString(null);
    });
  }

  @Test
  public void testInvalidStrings() {

    assertTrue(parseGaString("dsa").isEmpty());
    assertTrue(parseGaString("=, a == d").isEmpty());
  }

  @Test
  public void testValidStrings() {
    List<VOMSGenericAttribute> gas = parseGaString("ciccio = paglia");

    assertEquals(1, gas.size());

    assertEquals("ciccio", gas.get(0).getName());
    assertEquals("paglia", gas.get(0).getValue());
    assertNull(gas.get(0).getContext());

    gas = parseGaString("  c= p   , pippo =franco,a8_d2=789");

    assertEquals(3, gas.size());

    assertEquals("c", gas.get(0).getName());
    assertEquals("p", gas.get(0).getValue());

    assertEquals("pippo", gas.get(1).getName());
    assertEquals("franco", gas.get(1).getValue());

    assertEquals("a8_d2", gas.get(2).getName());
    assertEquals("789", gas.get(2).getValue());
  }
}
