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

import static org.italiangrid.voms.error.VOMSValidationErrorCode.aaCertFailsSignatureVerification;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.aaCertNotFound;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.acCertFailsSignatureVerification;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.acHolderDoesntMatchCertChain;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.acNotValidAtCurrentTime;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.canlError;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.emptyAcCertsExtension;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.invalidAaCert;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.invalidAcCert;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.localhostDoesntMatchAcTarget;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.lscDescriptionDoesntMatchAcCert;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.lscFileNotFound;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.other;
import static org.italiangrid.voms.error.VOMSValidationErrorMessage.newErrorMessage;

import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentVerifierProviderBuilder;
import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.ac.VOMSACValidationStrategy;
import org.italiangrid.voms.ac.VOMSValidationResult;
import org.italiangrid.voms.asn1.VOMSConstants;
import org.italiangrid.voms.error.VOMSValidationErrorMessage;
import org.italiangrid.voms.store.LSCInfo;
import org.italiangrid.voms.store.VOMSTrustStore;

import eu.emi.security.authn.x509.ValidationError;
import eu.emi.security.authn.x509.ValidationResult;
import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.impl.X500NameUtils;
import eu.emi.security.authn.x509.proxy.ProxyUtils;

/**
 * The Default VOMS validation strategy.
 * @author andreaceccanti
 *
 */
public class DefaultVOMSValidationStrategy implements VOMSACValidationStrategy{

	private final VOMSTrustStore store;
	private final X509CertChainValidatorExt certChainValidator;
	private final LocalHostnameResolver hostnameResolver;
	
	public DefaultVOMSValidationStrategy(VOMSTrustStore store, X509CertChainValidatorExt validator, LocalHostnameResolver resolver) {
		this.store = store;
		this.certChainValidator = validator;
		this.hostnameResolver = resolver;
		
	}
	
	public DefaultVOMSValidationStrategy(VOMSTrustStore store, X509CertChainValidatorExt validator) {
		this(store, validator,new DefaultLocalHostnameResolver());
	}
	
	private boolean checkACHolder(VOMSAttribute attributes, X509Certificate[] chain, List<VOMSValidationErrorMessage> validationErrors){
		
		X500Principal chainHolder = ProxyUtils.getOriginalUserDN(chain);
		
		boolean holderDoesMatch = chainHolder.equals(attributes.getHolder());
		
		if (!holderDoesMatch){
			
			String acHolderSubject = X500NameUtils.getReadableForm(attributes.getHolder());
			String certChainSubject =X500NameUtils.getReadableForm(chainHolder);
			
			validationErrors.add(VOMSValidationErrorMessage.newErrorMessage(acHolderDoesntMatchCertChain,
					acHolderSubject,
					certChainSubject));
		}
		
		return holderDoesMatch;
	}
	
	private boolean checkACValidity(VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors){
		Date now = new Date();
		
		boolean valid = attributes.validAt(now); 
		
		if (!valid){
			VOMSValidationErrorMessage m = VOMSValidationErrorMessage.newErrorMessage(acNotValidAtCurrentTime,
					attributes.getNotBefore(),
					attributes.getNotAfter(),
					now);
			
			validationErrors.add(m);
		}
		
		return valid;
	}
	
	
	private boolean checkLocalAACertSignature(VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors){
		
		X509Certificate localAACert = store.getAACertificateBySubject(attributes.getIssuer());
		if (localAACert == null){
			validationErrors.add(VOMSValidationErrorMessage.newErrorMessage(aaCertNotFound));
			return false;
		}
		
		if (!validateCertificate(localAACert, validationErrors)){
			validationErrors.add(VOMSValidationErrorMessage.newErrorMessage(invalidAaCert));
			return false;
		}
		
		boolean signatureValid = verifyACSignature(attributes, localAACert);
		
		if (!signatureValid){
			String readableSubject = X500NameUtils.getReadableForm(localAACert.getSubjectX500Principal());
			validationErrors.add(VOMSValidationErrorMessage.newErrorMessage(aaCertFailsSignatureVerification, readableSubject));
		}
		
		return signatureValid;
			
	}
	
	private boolean checkLSCSignature(VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors){
		
		LSCInfo lsc = store.getLSC(attributes.getVO(), attributes.getHost());
		X509Certificate[] aaCerts = attributes.getAACertificates();
		
		if (lsc == null){
			validationErrors.add(VOMSValidationErrorMessage.newErrorMessage(lscFileNotFound));
			return false;
		}
		
		if (aaCerts == null || aaCerts.length == 0){
			validationErrors.add(VOMSValidationErrorMessage.newErrorMessage(emptyAcCertsExtension));
			return false;
		}
		
		if (!lsc.matches(aaCerts)){
			validationErrors.add(VOMSValidationErrorMessage.newErrorMessage(lscDescriptionDoesntMatchAcCert));
			return false;
		}
		
		// LSC matches aa certs, verify certificates extracted from the AC
		if (!validateCertificateChain(aaCerts, validationErrors)){
			validationErrors.add(VOMSValidationErrorMessage.newErrorMessage(invalidAcCert));
			return false;
		}
		
		boolean signatureValid = verifyACSignature(attributes, aaCerts[0]);
		
		if (!signatureValid){
			String readableSubject = X500NameUtils.getReadableForm(aaCerts[0].getSubjectX500Principal());
			validationErrors.add(VOMSValidationErrorMessage.newErrorMessage(acCertFailsSignatureVerification, readableSubject));
		}
			
		return signatureValid; 
	}
	
	private boolean checkSignature(VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors){
		
		boolean valid = checkLSCSignature(attributes, validationErrors);
		
		if (!valid)
			valid = checkLocalAACertSignature(attributes, validationErrors);
		
		return valid;

	}
	
	private boolean checkTargets(VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors){
		
		if (attributes.getTargets() == null || attributes.getTargets().size() == 0)
			return true;
		
		String localhostName;
		
		try {
			localhostName = hostnameResolver.resolveLocalHostname();
		
		} catch (UnknownHostException e) {
			validationErrors.add(newErrorMessage(other, "Error resolving localhost name: "+e.getMessage()));
			return false;
		}
		
		if (!attributes.getTargets().contains(localhostName)){
			validationErrors.add(newErrorMessage(localhostDoesntMatchAcTarget, localhostName, attributes.getTargets().toString()));
			return false;
		}
		
		return true;
	}
	
	private boolean checkNoRevAvailExtension(VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors){
		
		Extension noRevAvail = attributes.getVOMSAC().getExtension(X509Extension.noRevAvail);
		if (noRevAvail != null && noRevAvail.isCritical()){
			validationErrors.add(newErrorMessage(other, "NoRevAvail AC extension cannot be critical!"));
			return false;
		}
		return true;
	}
	
	private boolean checkAuthorityKeyIdentifierExtension(VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors){
		
		Extension authKeyId = attributes.getVOMSAC().getExtension(X509Extension.authorityKeyIdentifier);
		if (authKeyId != null && authKeyId.isCritical()){
			validationErrors.add(newErrorMessage(other, "AuthorityKeyIdentifier AC extension cannot be critical!"));
			return false;
		}
		return true;
	}
	
	private boolean checkUnhandledCriticalExtensions(VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors){
		
		@SuppressWarnings("unchecked")
		List<ASN1ObjectIdentifier> acExtensions = attributes.getVOMSAC().getExtensionOIDs();
		
		for (ASN1ObjectIdentifier extId: acExtensions){
			if (!VOMSConstants.VOMS_HANDLED_EXTENSIONS.contains(extId) && 
					attributes.getVOMSAC().getExtension(extId).isCritical()){
				validationErrors.add(newErrorMessage(other, "unknown critical extension found in VOMS AC: "+extId.getId()));
				return false;
			}
		}
		return true;
	}

	public VOMSValidationResult validateAC(VOMSAttribute attributes) {
		boolean valid = true;
		List<VOMSValidationErrorMessage> validationErrors = new ArrayList<VOMSValidationErrorMessage>();

		// Check temporal validity
		valid = checkACValidity(attributes, validationErrors);

		if (valid)
			// Verify signature on AC checking LSC file or local AA certificate
			valid = checkSignature(attributes, validationErrors);
		
		if (valid)
			// Check targets
			valid = checkTargets(attributes, validationErrors);
		
		// AC extension checking to be compliant with rfc 3281
		if (valid)
			valid = checkAuthorityKeyIdentifierExtension(attributes, validationErrors);
				
		if (valid)
			valid = checkNoRevAvailExtension(attributes, validationErrors);
				
		if (valid)
			valid = checkUnhandledCriticalExtensions(attributes, validationErrors);
		
		return new VOMSValidationResult(attributes,valid, validationErrors);
	}
	
	public synchronized VOMSValidationResult validateAC(VOMSAttribute attributes, X509Certificate[] chain) {
		
		boolean valid = true;
		List<VOMSValidationErrorMessage> validationErrors = new ArrayList<VOMSValidationErrorMessage>();
		
		// Check temporal validity
		valid = checkACValidity(attributes, validationErrors);
		
		if (valid)
			// Verify signature on AC checking LSC file or local AA certificate
			valid = checkSignature(attributes, validationErrors);
		
		if (valid)
			// Check AC holder
			valid = checkACHolder(attributes, chain, validationErrors);
		
		if (valid)
			// Check targets
			valid = checkTargets(attributes, validationErrors);
		
		
		// AC extension checking to be compliant with rfc 3281
		if (valid)
			valid = checkAuthorityKeyIdentifierExtension(attributes, validationErrors);
		
		if (valid)
			valid = checkNoRevAvailExtension(attributes, validationErrors);
		
		if (valid)
			valid = checkUnhandledCriticalExtensions(attributes, validationErrors);
		
		return new VOMSValidationResult(attributes, valid, validationErrors);
	}
	
	
	private boolean validateCertificate(X509Certificate c, List<VOMSValidationErrorMessage> validationErrors){
		
		return validateCertificateChain(new X509Certificate[]{c}, validationErrors);
	}
	
	private boolean validateCertificateChain(X509Certificate[] chain, List<VOMSValidationErrorMessage> validationErrors){
		
		ValidationResult result = certChainValidator.validate(chain);
		
		for (ValidationError e: result.getErrors())
			validationErrors.add(VOMSValidationErrorMessage.newErrorMessage(canlError, e.getMessage()));
		
		return result.isValid();
	}

	private boolean verifyACSignature(VOMSAttribute attributes, X509Certificate cert){
		try{
			
			X509CertificateHolder certHolder = new JcaX509CertificateHolder(cert);
			ContentVerifierProvider cvp = new BcRSAContentVerifierProviderBuilder(new DefaultDigestAlgorithmIdentifierFinder()).build(certHolder);
			return attributes.getVOMSAC().isSignatureValid(cvp);
			
		}catch (Exception e) {
			throw new VOMSError("Error verifying AC signature: "+e.getMessage(),e);
		}
	}

}
