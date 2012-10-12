package org.glite.voms.v2.ac;

import java.security.cert.X509Certificate;

import org.glite.voms.v2.VOMSAttributes;
import org.glite.voms.v2.store.UpdatingVOMSTrustStore;
import org.glite.voms.v2.store.VOMSTrustStore;

import eu.emi.security.authn.x509.helpers.pkipath.AbstractValidator;
import eu.emi.security.authn.x509.impl.OpensslCertChainValidator;

public class DefaultVOMSValidator extends DefaultVOMSACParser implements
		VOMSACValidator {

	public static final String DEFAULT_TRUST_ANCHORS_DIR = "/etc/grid-security/certificates";
	
	private final VOMSACValidationStrategy validationStrategy;
	private final ValidationResultListener validationResultHandler;
 
	public DefaultVOMSValidator() {
		this (new UpdatingVOMSTrustStore(), 
				new OpensslCertChainValidator(DEFAULT_TRUST_ANCHORS_DIR),
				new LoggingValidationResultListener());
	}
	
	public DefaultVOMSValidator(VOMSTrustStore store, 
			AbstractValidator validator){
		this(store, validator, new LoggingValidationResultListener());
	}
	
	public DefaultVOMSValidator(VOMSTrustStore store, 
			AbstractValidator validator,
			ValidationResultListener resultHandler){
		
		validationStrategy = new DefaultVOMSValidationStrategy(store, validator);
		this.validationResultHandler = resultHandler;
	}
	
	public synchronized VOMSAttributes validate() {
		
		VOMSAttributes attrs = parse();
		VOMSValidationResult result = validationStrategy.validateAC(attrs, getCertificateChain());
		
		validationResultHandler.notifyValidationResult(result, attrs);
		
		if (result.isValid())
			return attrs;
		else
			return null;
	}

	public synchronized VOMSAttributes validate(X509Certificate[] validatedChain) {
		setCertificateChain(validatedChain);
		return validate();
	}
	
}
