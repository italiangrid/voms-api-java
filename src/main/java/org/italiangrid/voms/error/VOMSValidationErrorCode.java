package org.italiangrid.voms.error;

/**
 * VOMS validation error codes.
 * 
 * @author cecco
 *
 */
public enum VOMSValidationErrorCode {
	
	// Temporal validity
	acNotValidAtCurrentTime,
	
	// LSC signature checks
	lscFileNotFound,
	emptyAcCertsExtension,
	lscDescriptionDoesntMatchAcCert,
	invalidAcCert,
	acCertFailsSignatureVerification,
	
	// Local AA cert signature checks
	aaCertNotFound,
	invalidAaCert,
	aaCertFailsSignatureVerification,
	
	// Holder checks
	acHolderDoesntMatchCertChain,
	
	// Targets checks
	localhostDoesntMatchAcTarget,
	
	// CAnL errors
	canlError,
	
	// Other
	other;
}
