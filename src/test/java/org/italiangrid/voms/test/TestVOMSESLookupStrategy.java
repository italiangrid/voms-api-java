// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.italiangrid.voms.request.VOMSESLookupStrategy;
import org.italiangrid.voms.request.impl.BaseVOMSESLookupStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestVOMSESLookupStrategy {

  @Test
  public void testLookupVomsesInfo() {

    VOMSESLookupStrategy strategy =
        new BaseVOMSESLookupStrategy(
            Arrays.asList("src/test/resources/vomses", "/non/existent/path"));

    List<File> paths = strategy.lookupVomsesInfo();

    Assertions.assertEquals(1, paths.size());
    Assertions.assertTrue(paths.contains(new File("src/test/resources/vomses")));
  }
}
