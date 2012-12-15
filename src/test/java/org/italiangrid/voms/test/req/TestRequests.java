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
package org.italiangrid.voms.test.req;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.ac.VOMSACValidator;
import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSACService;
import org.italiangrid.voms.request.VOMSProtocol;
import org.italiangrid.voms.request.VOMSProtocolError;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.impl.DefaultVOMSACRequest;
import org.italiangrid.voms.test.utils.EchoVOMSProtocol;
import org.italiangrid.voms.test.utils.Fixture;
import org.italiangrid.voms.test.utils.Utils;
import org.junit.Test;
import org.mockito.Mockito;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.impl.PEMCredential;

public class TestRequests implements Fixture{

	@Test
	public void testEchoRequest() throws Exception {
		
		VOMSACService acService = Utils.buildACService(new EchoVOMSProtocol(Utils.getAACredential()));
		
		VOMSACRequest req = new DefaultVOMSACRequest.Builder("test.vo").build();
		
		PEMCredential holder = Utils.getTestUserCredential();
		
		AttributeCertificate ac = acService.getVOMSAttributeCertificate(holder, req);
		
		VOMSACValidator validator = Utils.getVOMSValidator();
		List<AttributeCertificate> acs = validator.validateACs(Arrays.asList(ac));
		
		Assert.assertFalse(acs.isEmpty());
		
		
	}
	
	@Test
	public void testFailureIfVOIsNotKnown() throws Exception {
		
		VOMSACService acService = Utils.buildACService(new EchoVOMSProtocol(Utils.getAACredential()));
		
		VOMSACRequest req = new DefaultVOMSACRequest.Builder("test.unknown.vo").build();
		
		PEMCredential holder = Utils.getTestUserCredential();
		
		try{
			AttributeCertificate ac = acService.getVOMSAttributeCertificate(holder, req);
			
		}catch (VOMSError e) {
			Assert.assertEquals("VOMS server for VO test.unknown.vo is not known! Check your vomses configuration.", e.getMessage());
			return;
		}
		
		Assert.fail("No exceptions raised for unknown VO");
	}
	
	@Test
	public void testNullACBytesHandling() throws Exception{
		
		VOMSProtocol nullBytesProtocol = new VOMSProtocol() {
			
			public VOMSResponse doRequest(VOMSServerInfo endpoint, X509Credential credential,
					VOMSACRequest request) {
				
				VOMSResponse r = Mockito.mock(VOMSResponse.class);
				
				return r;
			}
		};
		
		VOMSACService acService = Utils.buildACService(nullBytesProtocol);
		
		VOMSACRequest req = new DefaultVOMSACRequest.Builder("test.vo").build();
		
		AttributeCertificate ac = acService.getVOMSAttributeCertificate(Utils.getTestUserCredential()
				, req);	
		
		Assert.assertNull(ac);
	}
	
	@Test
	public void testRandomACBytesHandling() throws Exception{
		
		VOMSProtocol nullBytesProtocol = new VOMSProtocol() {
			
			public VOMSResponse doRequest(VOMSServerInfo endpoint, X509Credential credential,
					VOMSACRequest request) {
				
				Random r = new Random();
				byte[] acBytes = new byte[2048];
				
				r.nextBytes(acBytes);
				
				VOMSResponse response = Mockito.mock(VOMSResponse.class);
				Mockito.when(response.getAC()).thenReturn(acBytes);
				
				return response;
			}
		};
		
		VOMSACService acService = Utils.buildACService(nullBytesProtocol);
		
		VOMSACRequest req = new DefaultVOMSACRequest.Builder("test.vo").build();
		
		AttributeCertificate ac = acService.getVOMSAttributeCertificate(Utils.getTestUserCredential()
				, req);	
		
		Assert.assertNull(ac);
	}
	
	@Test
	public void testProtocolFallback() throws Exception{
		VOMSProtocol exceptionProtocol = Mockito.mock(VOMSProtocol.class);
		
		Mockito.when(exceptionProtocol.doRequest(Mockito.any(VOMSServerInfo.class), 
				Mockito.any(X509Credential.class), 
				Mockito.any(VOMSACRequest.class)))
				.thenReturn(null);
				
		
		
		VOMSProtocol fallBackProtocol = Mockito.mock(VOMSProtocol.class);
		
		VOMSACService acService = Utils.buildACService(exceptionProtocol, fallBackProtocol);
		VOMSACRequest req = new DefaultVOMSACRequest.Builder("test.vo").build();
		
		AttributeCertificate ac = acService.getVOMSAttributeCertificate(Utils.getTestUserCredential()
				,req);
		
		Mockito.verify(fallBackProtocol, Mockito.atLeastOnce()).doRequest(
				Mockito.any(VOMSServerInfo.class), 
				Mockito.any(X509Credential.class), 
				Mockito.any(VOMSACRequest.class));
		
		Assert.assertNull(ac);
	}
	
	@Test
	public void testProtocolFallback2() throws Exception{
		
		VOMSProtocol exceptionProtocol = Mockito.mock(VOMSProtocol.class);
		
		Mockito.when(exceptionProtocol.doRequest(Mockito.any(VOMSServerInfo.class), 
				Mockito.any(X509Credential.class), 
				Mockito.any(VOMSACRequest.class)))
				.thenThrow(new VOMSProtocolError("protocol error", null, null, null, null));
		
		VOMSProtocol fallBackProtocol = Mockito.mock(VOMSProtocol.class);
		
		VOMSACService acService = Utils.buildACService(exceptionProtocol, fallBackProtocol);
		VOMSACRequest req = new DefaultVOMSACRequest.Builder("test.vo").build();
		
		AttributeCertificate ac = acService.getVOMSAttributeCertificate(Utils.getTestUserCredential()
				,req);
		
		Mockito.verify(fallBackProtocol, Mockito.atLeastOnce()).doRequest(
				Mockito.any(VOMSServerInfo.class), 
				Mockito.any(X509Credential.class), 
				Mockito.any(VOMSACRequest.class));
		
		Assert.assertNull(ac);
	}
	
	
}
