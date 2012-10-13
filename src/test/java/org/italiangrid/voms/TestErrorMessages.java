package org.italiangrid.voms;

public enum TestErrorMessages {

	LSC_FAILURE_WRONG_CHAIN_DESCRIPTION("LSC validation failed: LSC chain description does not match AA certificate chain embedded in the VOMS AC!"),
	LSC_FAILURE_INVALID_AA_CERT("LSC validation failed: AA certificate chain embedded in the VOMS AC failed certificate validation!"),
	
	AC_SIGNATURE_VERIFICATION_FAILURE("AC signature verification failure: no valid VOMS server credential found."),
	
	CERTIFICATE_EXPIRED("Certificate has expired on: Fri Dec 02 01:00:00 CET 2011"),
	CERTIFICATE_REVOKED("Certificate was revoked at: Wed Sep 26 17:25:24 CEST 2012, the reason reported is: unspecified");
	
	
	private TestErrorMessages(final String text) {
		this.message = text;
	}
	
	private String message;

	@Override
	public String toString() {
		return message;
	}
	
}
