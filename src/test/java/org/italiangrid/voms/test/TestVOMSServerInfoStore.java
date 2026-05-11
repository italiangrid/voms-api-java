// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Set;
import org.italiangrid.voms.request.VOMSESLookupStrategy;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.impl.BaseVOMSESLookupStrategy;
import org.italiangrid.voms.request.impl.DefaultVOMSServerInfoStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestVOMSServerInfoStore {

  @Test
  public void testExistingVOMSESParsingSuccess() {

    VOMSESLookupStrategy strategy =
        new BaseVOMSESLookupStrategy(Arrays.asList("src/test/resources/vomses"));

    DefaultVOMSServerInfoStore store =
        new DefaultVOMSServerInfoStore.Builder().lookupStrategy(strategy).build();

    assertEquals(3, store.getVOMSServerInfo("atlas").size());
    assertEquals(2, store.getVOMSServerInfo("eumed").size());
    Assertions.assertTrue(store.getVOMSServerInfo("non-existing-vo").isEmpty());

    assertEquals(5, store.getVOMSServerInfo().size());
  }

  @Test
  public void testVOMSESAliasLookup() {

    VOMSESLookupStrategy strategy =
        new BaseVOMSESLookupStrategy(Arrays.asList("src/test/resources/vomses-alias"));

    DefaultVOMSServerInfoStore store =
        new DefaultVOMSServerInfoStore.Builder().lookupStrategy(strategy).build();

    assertEquals(3, store.getVOMSServerInfo("atlas").size());
    assertEquals(2, store.getVOMSServerInfo("eumed").size());

    Assertions.assertTrue(store.getVOMSServerInfo("non-existing-vo").isEmpty());

    Set<VOMSServerInfo> infos = store.getVOMSServerInfo("my-atlas");

    Assertions.assertFalse(infos.isEmpty());

    Assertions.assertEquals(2, infos.size());
  }

  @Test
  public void testVOMSESSingleCharAliasLookup() {

    VOMSESLookupStrategy strategy =
        new BaseVOMSESLookupStrategy(Arrays.asList("src/test/resources/vomses-alias-singlechar"));

    DefaultVOMSServerInfoStore store =
        new DefaultVOMSServerInfoStore.Builder().lookupStrategy(strategy).build();

    assertEquals(1, store.getVOMSServerInfo("atlas").size());

    Assertions.assertTrue(store.getVOMSServerInfo("non-existing-vo").isEmpty());

    Set<VOMSServerInfo> infos = store.getVOMSServerInfo("a");

    Assertions.assertFalse(infos.isEmpty());

    Assertions.assertEquals(1, infos.size());
  }
}
