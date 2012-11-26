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
package org.italiangrid.voms.ac.impl;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.ac.VOMSACLookupStrategy;
import org.italiangrid.voms.ac.VOMSACValidationStrategy;
import org.italiangrid.voms.ac.VOMSACValidator;
import org.italiangrid.voms.ac.VOMSValidationResult;
import org.italiangrid.voms.ac.ValidationResultListener;
import org.italiangrid.voms.asn1.VOMSACUtils;
import org.italiangrid.voms.store.UpdatingVOMSTrustStore;
import org.italiangrid.voms.store.VOMSTrustStore;
import org.italiangrid.voms.store.VOMSTrustStores;
import org.italiangrid.voms.util.CertificateValidatorBuilder;
import org.italiangrid.voms.util.NullListener;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;

/**
 * The default implementation of the VOMS validator.
 * 
 * @author andreaceccanti
 *
 */
public class DefaultVOMSValidator extends DefaultVOMSACParser implements
		VOMSACValidator {

	public static final String DEFAULT_TRUST_ANCHORS_DIR = "/etc/grid-security/certificates";
	
	private final VOMSACValidationStrategy validationStrategy;
	private ValidationResultListener validationResultListener;
	private final VOMSTrustStore trustStore;
 
	public DefaultVOMSValidator(ValidationResultListener resultHandler){
		this(VOMSTrustStores.newTrustStore(), 
				CertificateValidatorBuilder.buildCertificateValidator(DEFAULT_TRUST_ANCHORS_DIR),
				resultHandler);
	}
	
	public DefaultVOMSValidator() {
		this (VOMSTrustStores.newTrustStore(), 
				CertificateValidatorBuilder.buildCertificateValidator(DEFAULT_TRUST_ANCHORS_DIR),
				new NullListener());
	}
	
	public DefaultVOMSValidator(VOMSTrustStore store, 
			X509CertChainValidatorExt validator){
		this(store, validator, new NullListener());
	}
	
	public DefaultVOMSValidator(VOMSTrustStore store, 
			X509CertChainValidatorExt validator,
			ValidationResultListener resultHandler){
		trustStore = store;
		validationStrategy = new DefaultVOMSValidationStrategy(trustStore, validator);
		this.validationResultListener = resultHandler;
	}
	
	public DefaultVOMSValidator(VOMSTrustStore store, 
			X509CertChainValidatorExt validator,
			ValidationResultListener resultHandler,
			VOMSACLookupStrategy strategy){
		super(strategy);
		trustStore = store;
		validationStrategy = new DefaultVOMSValidationStrategy(trustStore, validator);
		this.validationResultListener = resultHandler;
	}
	
	public synchronized List<VOMSValidationResult> validateWithResult(X509Certificate[] validatedChain){
		setCertChain(validatedChain);
		return validate();
	}
	protected synchronized List<VOMSValidationResult> validate() {
		
		List<VOMSAttribute> parsedAttrs = parse();
		List<VOMSValidationResult> results = new ArrayList<VOMSValidationResult>();
		
		
		for (VOMSAttribute a: parsedAttrs){
			
			VOMSValidationResult result = validationStrategy.validateAC(a, getCertChain());
			validationResultListener.notifyValidationResult(result);
			results.add(result);
			
		}
		
		return results;
	}

	public synchronized List<VOMSAttribute> validate(X509Certificate[] validatedChain) {
		setCertChain(validatedChain);
		List<VOMSAttribute> validAttributes = new ArrayList<VOMSAttribute>();
		for (VOMSValidationResult result: validate()){
			if (result.isValid())
				validAttributes.add(result.getAttributes());
		}
		return validAttributes;
	}

	public synchronized void shutdown() {
		// Shut down eventual truststore refresh thread.
		if (trustStore instanceof UpdatingVOMSTrustStore)
			((UpdatingVOMSTrustStore)trustStore).cancel();
	}

	public synchronized List<AttributeCertificate> validateACs(List<AttributeCertificate> acs) {
		
		List<AttributeCertificate> validatedAcs = new ArrayList<AttributeCertificate>();
		
		for (AttributeCertificate ac : acs){

			VOMSAttribute vomsAttrs = VOMSACUtils.deserializeVOMSAttributes(ac);
		
			VOMSValidationResult result = validationStrategy.validateAC(vomsAttrs);
			validationResultListener.notifyValidationResult(result);
			
			if (result.isValid())
				validatedAcs.add(ac);
		}
		
		return validatedAcs;
	}

	public synchronized void setValidationResultListener(ValidationResultListener listener) {
		if (listener != null)
			this.validationResultListener= listener; 
		
	}
	
}
