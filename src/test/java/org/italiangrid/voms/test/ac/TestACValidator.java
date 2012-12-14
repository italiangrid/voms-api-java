package org.italiangrid.voms.test.ac;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.italiangrid.voms.VOMSValidators;
import org.italiangrid.voms.ac.VOMSACValidator;
import org.italiangrid.voms.ac.VOMSValidationResult;
import org.italiangrid.voms.error.VOMSValidationErrorCode;
import org.italiangrid.voms.error.VOMSValidationErrorMessage;
import org.italiangrid.voms.store.impl.DefaultVOMSTrustStore;
import org.italiangrid.voms.test.Fixture;
import org.italiangrid.voms.test.Utils;
import org.italiangrid.voms.util.CertificateValidatorBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.impl.PEMCredential;
import eu.emi.security.authn.x509.proxy.ProxyCertificate;

public class TestACValidator implements Fixture{

	
	static PEMCredential holder, holder2;
	static VOMSACValidator validator;
	
	@BeforeClass
	public static void setup() throws KeyStoreException, CertificateException, IOException{
	
		holder = Utils.getTestUserCredential();
		holder2 = Utils.getTest1UserCredential();
		validator = Utils.getVOMSValidator();
		
	}
	
	@Test
	public void testValidityCheckSuccess() throws Exception {
		
		ProxyCertificate proxy = Utils.getVOMSAA().createVOMSProxy(holder, defaultVOFqans);
		List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
		assertTrue(results.size() == 1);
		assertTrue(results.get(0).isValid());
		assertEquals(defaultVOFqans, results.get(0).getAttributes().getFQANs());
		
	}
	
	@Test
	public void testTimeValidityFailure() throws Exception {
		Date start = Utils.getDate(1975,12,1);
		Date end = Utils.getDate(1975,12,2);
		
		ProxyCertificate proxy = Utils.getVOMSAA()
				.setAcNotBefore(start)
				.setAcNotAfter(end)
				.createVOMSProxy(holder, defaultVOFqans);
		
		List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
		
		assertTrue(results.size() == 1);
		
		VOMSValidationResult result = results.get(0); 
		
		Assert.assertFalse(result.isValid());
		Assert.assertTrue(result.getValidationErrors().size() == 1);
		VOMSValidationErrorMessage m = result.getValidationErrors().get(0);
		Assert.assertEquals(VOMSValidationErrorCode.acNotValidAtCurrentTime, 
				m.getErrorCode());
	}
	
	@Test
	public void testHolderCheckFailure() throws Exception{
		
		ProxyCertificate proxy = Utils.getVOMSAA().createVOMSProxy(holder, holder2, defaultVOFqans, null, null);
		List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
		assertTrue(results.size() == 1);
		
		VOMSValidationResult result = results.get(0);
		Assert.assertFalse(result.isValid());
		Assert.assertTrue(result.getValidationErrors().size() == 1);
		VOMSValidationErrorMessage m = result.getValidationErrors().get(0);
		Assert.assertEquals(VOMSValidationErrorCode.acHolderDoesntMatchCertChain,m.getErrorCode());
	}
	
	@Test
	public void testSignatureCheckFailure() throws Exception{
		ProxyCertificate proxy = Utils.getVOMSAA().createVOMSProxy(holder, defaultVOFqans);
		VOMSACValidator validator = Utils.getVOMSValidator(vomsdir_fake_aa_cert);
		List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
		
		assertTrue(results.size() == 1);
		VOMSValidationResult result = results.get(0);
		Assert.assertFalse(result.isValid());
		Assert.assertTrue(result.getValidationErrors().size() == 2);
		
		Assert.assertEquals(VOMSValidationErrorCode.lscFileNotFound, 
				result.getValidationErrors().get(0).getErrorCode());
		Assert.assertEquals(VOMSValidationErrorCode.aaCertFailsSignatureVerification,
				result.getValidationErrors().get(1).getErrorCode());
		
	}
	
	@Test
	public void testExpiredAACredFailure() throws Exception{
		
		ProxyCertificate proxy = Utils
				.getVOMSAA()
				.setCredential(Utils.getExpiredCredential())
				.createVOMSProxy(holder, defaultVOFqans);
		
		X509CertChainValidatorExt certValidator = Utils.getCertificateValidator();
				
				
		VOMSACValidator validator = VOMSValidators.newValidator(new DefaultVOMSTrustStore(Arrays.asList(vomsdir_expired_aa_cert))
			,certValidator);
		
		List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
		
		assertTrue(results.size() == 1);
		VOMSValidationResult result = results.get(0);
		Assert.assertFalse(result.isValid());
		// System.out.println(result.getValidationErrors());
		
		Assert.assertEquals(4, result.getValidationErrors().size());
		
		Assert.assertEquals(VOMSValidationErrorCode.lscFileNotFound, 
				result.getValidationErrors().get(0).getErrorCode());
		
		// Certificate expired notification from CAnL
		Assert.assertEquals(VOMSValidationErrorCode.canlError, 
				result.getValidationErrors().get(1).getErrorCode());
		
		// This is probably a bug in CAnL: No valid CRL was found for the CA which issued the chain. But this happens only when validating the expired cert. 
		Assert.assertEquals(VOMSValidationErrorCode.canlError, 
				result.getValidationErrors().get(2).getErrorCode());
		
		Assert.assertEquals(VOMSValidationErrorCode.invalidAaCert,
				result.getValidationErrors().get(3).getErrorCode());
		
	}

}
