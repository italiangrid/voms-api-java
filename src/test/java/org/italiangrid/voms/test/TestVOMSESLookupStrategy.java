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
package org.italiangrid.voms.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.italiangrid.voms.request.VOMSESLookupStrategy;
import org.italiangrid.voms.request.impl.BaseVOMSESLookupStrategy;
import org.junit.jupiter.api.Test;

public class TestVOMSESLookupStrategy {

  @Test
  public void testLookupVomsesInfo() {

    VOMSESLookupStrategy strategy = new BaseVOMSESLookupStrategy(Arrays.asList(
      "src/test/resources/vomses", "/non/existent/path"));

    List<File> paths = strategy.lookupVomsesInfo();

    assertEquals(1, paths.size());
    assertTrue(paths.contains(new File("src/test/resources/vomses")));

  }

}
