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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.italiangrid.voms.ac.VOMSValidationResult;
import org.junit.jupiter.api.Test;

public class TestVOMSValidationResult {

  @Test
  public void testGettersAndSetters() {

    VOMSValidationResult r = new VOMSValidationResult(null, false);

    assertFalse(r.isValid());
    assertNull(r.getAttributes());
    assertTrue(r.getValidationErrors().isEmpty());

    assertEquals("VOMSValidationResult [valid=false, validationErrors=[], attributes=null]",
        r.toString());

  }

}
