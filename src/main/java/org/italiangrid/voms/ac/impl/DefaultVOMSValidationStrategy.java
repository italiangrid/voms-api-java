package org.italiangrid.voms.ac.impl;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentVerifierProviderBuilder;
import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.ac.VOMSACValidationStrategy;
import org.italiangrid.voms.ac.VOMSValidationResult;
import org.italiangrid.voms.error.VOMSValidationErrorMessage;
import org.italiangrid.voms.store.LSCInfo;
import org.italiangrid.voms.store.VOMSTrustStore;

import eu.emi.security.authn.x509.ValidationError;
import eu.emi.security.authn.x509.ValidationResult;
import eu.emi.security.authn.x509.helpers.pkipath.AbstractValidator;
import eu.emi.security.authn.x509.impl.X500NameUtils;
import eu.emi.security.authn.x509.proxy.ProxyUtils;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.*;
public class DefaultVOMSValidationStrategy implements VOMSACValidationStrategy{

	private final VOMSTrustStore store;
	private final AbstractValidator certChainValidator;
	
	
	public DefaultVOMSValidationStrategy(VOMSTrustStore store, AbstractValidator validator) {
		this.store = store;
		this.certChainValidator = validator;
		
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
		return true;
	}
	
	private boolean checkUnhandledExtensions(VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors){
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
		
		if (valid)
			// Check unhandled extensions
			valid = checkUnhandledExtensions(attributes, validationErrors);
		
		return new VOMSValidationResult(valid, validationErrors);
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
		
		if (valid)
			// Check unhandled extensions
			valid = checkUnhandledExtensions(attributes, validationErrors);
		
		return new VOMSValidationResult(valid, validationErrors);
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
