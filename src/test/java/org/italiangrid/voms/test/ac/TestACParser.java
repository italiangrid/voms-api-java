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

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;

import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.ac.impl.DefaultVOMSACParser;
import org.italiangrid.voms.test.utils.Fixture;
import org.italiangrid.voms.test.utils.Utils;
import org.italiangrid.voms.test.utils.VOMSAA;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.emi.security.authn.x509.impl.PEMCredential;
import eu.emi.security.authn.x509.proxy.ProxyCertificate;

public class TestACParser implements Fixture{

	static VOMSAA aa;
	static PEMCredential holder;
	
	@BeforeClass
	public static void setup() throws KeyStoreException, CertificateException, IOException{
		aa = Utils.getVOMSAA();
		
	}
	
	@Test
	public void test() throws Exception{
		PEMCredential holder = Utils.getTestUserCredential();
		ProxyCertificate proxy = aa.createVOMSProxy(holder, defaultVOFqans);
		
		DefaultVOMSACParser parser = new DefaultVOMSACParser();
		List<VOMSAttribute> attrs = parser.parse(proxy.getCertificateChain());
		Assert.assertFalse(attrs.isEmpty());
		Assert.assertEquals(1,attrs.size());
		Assert.assertEquals(defaultVOFqans, attrs.get(0).getFQANs());
	}

	@Test(expected=NullPointerException.class)
	public void testParseNullChainFailure(){
		DefaultVOMSACParser parser = new DefaultVOMSACParser();
		parser.parse(null);
	}
	
	@Test
	public void testEmptyFqansParsing() throws Exception{
		PEMCredential holder = Utils.getTestUserCredential();
		List<String> fqans = Collections.emptyList();
		ProxyCertificate proxy = aa.createVOMSProxy(holder, fqans);
		
		DefaultVOMSACParser parser = new DefaultVOMSACParser();
		
		try{
			parser.parse(proxy.getCertificateChain());
		}catch (VOMSError e) {
			Assert.assertEquals("Non conformant VOMS Attribute certificate: unsupported attribute values encoding.", e.getMessage());
			return;
		}
		
		Assert.fail("No exception raised when parsing invalid VOMS AC!");
	}
}
