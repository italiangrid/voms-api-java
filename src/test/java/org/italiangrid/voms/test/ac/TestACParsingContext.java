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
package org.italiangrid.voms.test.ac;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.ac.ACParsingContext;
import org.italiangrid.voms.test.utils.Fixture;
import org.junit.Test;

public class TestACParsingContext implements Fixture{

	@Test
	public void testGettersAndSetters() {
		X509Certificate[] emptyChain = new X509Certificate[5];
		
		ACParsingContext ctxt = new ACParsingContext(null, 0, emptyChain);
		assertNull(ctxt.getACs());
		assertEquals(0, ctxt.getCertChainPostion());
		assertArrayEquals(emptyChain, ctxt.getCertChain());
		
		X509Certificate[] nullChain = null;
		
		List<AttributeCertificate> emptyAttrs = new ArrayList<AttributeCertificate>();
		ctxt.setACs(emptyAttrs);
		ctxt.setCertChain(nullChain);
		ctxt.setCertChainPostion(2);
		
		assertNull(ctxt.getCertChain());
		assertEquals(emptyAttrs, ctxt.getACs());
		assertEquals(2, ctxt.getCertChainPostion());

	}

}
