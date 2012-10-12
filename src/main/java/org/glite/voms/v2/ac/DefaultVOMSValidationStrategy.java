package org.glite.voms.v2.ac;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentVerifierProviderBuilder;
import org.glite.voms.v2.VOMSAttributes;
import org.glite.voms.v2.VOMSError;
import org.glite.voms.v2.store.LSCInfo;
import org.glite.voms.v2.store.VOMSTrustStore;

import eu.emi.security.authn.x509.ValidationError;
import eu.emi.security.authn.x509.ValidationResult;
import eu.emi.security.authn.x509.helpers.pkipath.AbstractValidator;
import eu.emi.security.authn.x509.impl.X500NameUtils;
import eu.emi.security.authn.x509.proxy.ProxyUtils;

public class DefaultVOMSValidationStrategy implements VOMSACValidationStrategy{

	private final VOMSTrustStore store;
	private final AbstractValidator certChainValidator;
	
	
	public DefaultVOMSValidationStrategy(VOMSTrustStore store, AbstractValidator validator) {
		this.store = store;
		this.certChainValidator = validator;
		
	}
	
	public synchronized VOMSValidationResult validateAC(VOMSAttributes attributes, X509Certificate[] chain) {
		
		boolean valid = true;
		List<String> validationErrors = new ArrayList<String>();
		
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
	
	private boolean checkACHolder(VOMSAttributes attributes, X509Certificate[] chain, List<String> validationErrors){
		
		X500Principal chainHolder = ProxyUtils.getOriginalUserDN(chain);
		
		boolean holderDoesMatch = chainHolder.equals(attributes.getHolder());
		
		if (!holderDoesMatch){
			
			String acHolderSubject = X500NameUtils.getReadableForm(attributes.getHolder());
			String certChainSubject =X500NameUtils.getReadableForm(chainHolder);
			
			String msg = String.format("AC holder check failed: AC holder '%s' does not match certificate chain subject '%s'.", 
					acHolderSubject, 
					certChainSubject);
			
			validationErrors.add(msg);
		}
		
		return holderDoesMatch;
	}
	
	private boolean checkLSCSignature(VOMSAttributes attributes, List<String> validationErrors){
		
		LSCInfo lsc = store.getLSC(attributes.getVO(), attributes.getHost());
		X509Certificate[] aaCerts = attributes.getAACertificates();
		
		if (lsc == null){
			validationErrors.add("LSC validation failed: LSC file matching VOMS attributes not found in store.");
			return false;
		}
		
		if (aaCerts == null || aaCerts.length == 0){
			validationErrors.add("LSC validation failed: AC certs extension is empty");
			return false;
		}
		
		if (!lsc.matches(aaCerts)){
			validationErrors.add("LSC validation failed: LSC chain description does not match AA certificate chain embedded in the VOMS AC!");
			return false;
		}
		
		// LSC matches aa certs, verify certificates extracted from the AC
		if (!validateCertificateChain(aaCerts, validationErrors)){
			validationErrors.add("LSC validation failed: AA certificate chain embedded in the VOMS AC failed certificate validation!");
			return false;
		}
		
		boolean signatureValid = verifyACSignature(attributes, aaCerts[0]);
		
		if (!signatureValid){
			String readableSubject = X500NameUtils.getReadableForm(aaCerts[0].getSubjectX500Principal());
			String msg = String.format("LSC signature validation failed: matching AA cert '%s' fails signature verification.", readableSubject);
			validationErrors.add(msg);
		}
			
		return signatureValid; 
	}
	
	private boolean verifyACSignature(VOMSAttributes attributes, X509Certificate cert){
		try{
			
			X509CertificateHolder certHolder = new JcaX509CertificateHolder(cert);
			ContentVerifierProvider cvp = new BcRSAContentVerifierProviderBuilder(new DefaultDigestAlgorithmIdentifierFinder()).build(certHolder);
			return attributes.getVOMSAC().isSignatureValid(cvp);
			
		}catch (Exception e) {
			throw new VOMSError("Error verifying AC signature: "+e.getMessage(),e);
		}
	}
	
	private boolean checkLocalAACertSignature(VOMSAttributes attributes, List<String> validationErrors){
		
		X509Certificate localAACert = store.getAACertificateBySubject(attributes.getIssuer());
		if (localAACert == null){
			validationErrors.add("AC signature verification failure: no valid VOMS server credential found.");
			return false;
		}
		
		if (!validateCertificate(localAACert, validationErrors)){
			validationErrors.add("AC signature verification failure: local AA cert failed certificate validation!");
			return false;
		}
		
		boolean signatureValid = verifyACSignature(attributes, localAACert);
		if (!signatureValid){
			String readableSubject = X500NameUtils.getReadableForm(localAACert.getSubjectX500Principal());
			String msg = String.format("Signature validation failed: matching AA cert '%s' fails signature verification.", readableSubject);
			validationErrors.add(msg);
		}
		
		return signatureValid;
			
	}
	
	private boolean checkSignature(VOMSAttributes attributes, List<String> validationErrors){
		
		boolean valid = checkLSCSignature(attributes, validationErrors);
		
		if (!valid)
			valid = checkLocalAACertSignature(attributes, validationErrors);
		
		return valid;

	}

	private boolean validateCertificate(X509Certificate c, List<String> validationErrors){
		
		return validateCertificateChain(new X509Certificate[]{c}, validationErrors);
	}
	
	private boolean validateCertificateChain(X509Certificate[] chain, List<String> validationErrors){
		
		ValidationResult result = certChainValidator.validate(chain);
		
		for (ValidationError e: result.getErrors())
			validationErrors.add(e.getMessage());
		
		return result.isValid();
	}
	
	
	private boolean checkTargets(VOMSAttributes attributes, List<String> validationErrors){
		return true;
	}
	
	private boolean checkUnhandledExtensions(VOMSAttributes attributes, List<String> validationErrors){
		return true;
	}

}
