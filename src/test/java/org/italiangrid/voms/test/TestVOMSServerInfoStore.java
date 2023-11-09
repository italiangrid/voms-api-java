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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.italiangrid.voms.request.VOMSESLookupStrategy;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.impl.BaseVOMSESLookupStrategy;
import org.italiangrid.voms.request.impl.DefaultVOMSServerInfoStore;
import org.junit.jupiter.api.Test;

public class TestVOMSServerInfoStore {

  @Test
  public void testExistingVOMSESParsingSuccess() {

    VOMSESLookupStrategy strategy = new BaseVOMSESLookupStrategy(
      Arrays.asList("src/test/resources/vomses"));

    DefaultVOMSServerInfoStore store = new DefaultVOMSServerInfoStore.Builder()
      .lookupStrategy(strategy).build();

    assertEquals(3, store.getVOMSServerInfo("atlas").size());
    assertEquals(2, store.getVOMSServerInfo("eumed").size());
    assertTrue(store.getVOMSServerInfo("non-existing-vo").isEmpty());

    assertEquals(5, store.getVOMSServerInfo().size());
  }

  @Test
  public void testVOMSESAliasLookup() {

    VOMSESLookupStrategy strategy = new BaseVOMSESLookupStrategy(
      Arrays.asList("src/test/resources/vomses-alias"));

    DefaultVOMSServerInfoStore store = new DefaultVOMSServerInfoStore.Builder()
      .lookupStrategy(strategy).build();

    assertEquals(3, store.getVOMSServerInfo("atlas").size());
    assertEquals(2, store.getVOMSServerInfo("eumed").size());

    assertTrue(store.getVOMSServerInfo("non-existing-vo").isEmpty());

    Set<VOMSServerInfo> infos = store.getVOMSServerInfo("my-atlas");

    assertFalse(infos.isEmpty());

    assertEquals(2, infos.size());

  }

  @Test
  public void testVOMSESSingleCharAliasLookup() {

    VOMSESLookupStrategy strategy = new BaseVOMSESLookupStrategy(
      Arrays.asList("src/test/resources/vomses-alias-singlechar"));

    DefaultVOMSServerInfoStore store = new DefaultVOMSServerInfoStore.Builder()
      .lookupStrategy(strategy).build();

    assertEquals(1, store.getVOMSServerInfo("atlas").size());

    assertTrue(store.getVOMSServerInfo("non-existing-vo").isEmpty());

    Set<VOMSServerInfo> infos = store.getVOMSServerInfo("a");

    assertFalse(infos.isEmpty());

    assertEquals(1, infos.size());

  }
}
