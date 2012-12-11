/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2012.
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
package org.italiangrid.voms;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import junit.framework.Assert;

import org.italiangrid.voms.request.VOMSESLookupStrategy;
import org.italiangrid.voms.request.impl.BaseVOMSESLookupStrategy;
import org.italiangrid.voms.request.impl.DefaultVOMSServerInfoStore;
import org.junit.Test;

public class TestVOMSServerInfoStore {

	@Test
	public void testExistingVOMSESParsingSuccess() {
		
		VOMSESLookupStrategy strategy = new BaseVOMSESLookupStrategy(Arrays.asList("src/test/resources/vomses"));
		
		DefaultVOMSServerInfoStore store = new DefaultVOMSServerInfoStore.Builder()
			.lookupStrategy(strategy)
			.build();
		
		assertEquals(3, store.getVOMSServerInfo("atlas").size());
		assertEquals(2, store.getVOMSServerInfo("eumed").size());
		Assert.assertTrue(store.getVOMSServerInfo("non-existing-vo").isEmpty());
		
		assertEquals(5, store.getVOMSServerInfo().size());
	}
}
