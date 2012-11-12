package org.italiangrid.voms.ac.impl;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.ac.VOMSACValidationStrategy;
import org.italiangrid.voms.ac.VOMSACValidator;
import org.italiangrid.voms.ac.VOMSValidationResult;
import org.italiangrid.voms.ac.ValidationResultListener;
import org.italiangrid.voms.asn1.VOMSACUtils;
import org.italiangrid.voms.store.UpdatingVOMSTrustStore;
import org.italiangrid.voms.store.VOMSTrustStore;
import org.italiangrid.voms.store.VOMSTrustStores;
import org.italiangrid.voms.util.CertificateValidatorBuilder;
import org.italiangrid.voms.util.LoggingListener;

import eu.emi.security.authn.x509.helpers.pkipath.AbstractValidator;

public class DefaultVOMSValidator extends DefaultVOMSACParser implements
		VOMSACValidator {

	public static final String DEFAULT_TRUST_ANCHORS_DIR = "/etc/grid-security/certificates";
	
	private final VOMSACValidationStrategy validationStrategy;
	private final ValidationResultListener validationResultHandler;
	private final VOMSTrustStore trustStore;
 
	public DefaultVOMSValidator(ValidationResultListener resultHandler){
		this(VOMSTrustStores.newTrustStore(), 
				CertificateValidatorBuilder.buildCertificateValidator(DEFAULT_TRUST_ANCHORS_DIR),
				resultHandler);
	}
	
	public DefaultVOMSValidator() {
		this (VOMSTrustStores.newTrustStore(), 
				CertificateValidatorBuilder.buildCertificateValidator(DEFAULT_TRUST_ANCHORS_DIR),
				new LoggingListener());
	}
	
	public DefaultVOMSValidator(VOMSTrustStore store, 
			AbstractValidator validator){
		this(store, validator, new LoggingListener());
	}
	
	public DefaultVOMSValidator(VOMSTrustStore store, 
			AbstractValidator validator,
			ValidationResultListener resultHandler){
		trustStore = store;
		validationStrategy = new DefaultVOMSValidationStrategy(trustStore, validator);
		this.validationResultHandler = resultHandler;
	}
	
	public synchronized List<VOMSAttribute> validate() {
		
		List<VOMSAttribute> parsedAttrs = parse();
		List<VOMSAttribute> validatedAttrs = new ArrayList<VOMSAttribute>();
		
		for (VOMSAttribute a: parsedAttrs){
			
			VOMSValidationResult result = validationStrategy.validateAC(a, getCertChain());
			validationResultHandler.notifyValidationResult(result, a);
			
			if (result.isValid())
				validatedAttrs.add(a);
		}
		
		return validatedAttrs;
	}

	public synchronized List<VOMSAttribute> validate(X509Certificate[] validatedChain) {
		setCertChain(validatedChain);
		return validate();
	}

	public synchronized void shutdown() {
		// Shut down eventual truststore refresh thread.
		if (trustStore instanceof UpdatingVOMSTrustStore)
			((UpdatingVOMSTrustStore)trustStore).cancel();
	}

	public List<AttributeCertificate> validateACs(List<AttributeCertificate> acs) {
		
		List<AttributeCertificate> validatedAcs = new ArrayList<AttributeCertificate>();
		
		for (AttributeCertificate ac : acs){

			VOMSAttribute vomsAttrs = VOMSACUtils.deserializeVOMSAttributes(ac);
		
			VOMSValidationResult result = validationStrategy.validateAC(vomsAttrs);
			validationResultHandler.notifyValidationResult(result, vomsAttrs);
			
			if (result.isValid())
				validatedAcs.add(ac);
		}
		
		return validatedAcs;
	}
	
}
