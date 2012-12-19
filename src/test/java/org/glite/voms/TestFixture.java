package org.glite.voms;

public interface TestFixture {

	public static final String trustDir = "src/test/resources/trust-anchors";
	public static final String noCRLsTrustDir = "src/test/resources/no-crls-trust-anchors";

	public static final String vomsDir = "src/test/resources/vomsdir";
	public static final String testCert = "src/test/resources/certs/quasi_revoked.cert.pem";
	public static final String revokedCert = "src/test/resources/certs/revoked.cert.pem";
	public static final String validCert = "src/test/resources/certs/test0.cert.pem";
	public static final String dnWithParenthesisCert = "src/test/resources/certs/dn_with_parenthesis.cert.pem";
	public static final String dnWithParenthesisKey = "src/test/resources/certs/dn_with_parenthesis.key.pem";

	public static final String defaultCRL = "src/test/resources/crls/default-crl.pem";
	public static final String testCertRevokedCRL = "src/test/resources/crls/cert-17-revoked-crl.pem";
	public static final String expiredCRL = "src/test/resources/crls/expired-crl.pem";

	public static final String[] caHashes = { "d82942ab", "10b10516" };
}
