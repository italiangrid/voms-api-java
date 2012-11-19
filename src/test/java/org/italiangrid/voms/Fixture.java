package org.italiangrid.voms;

public interface Fixture {
	
	static final String keyPassword = "pass";
	
	static final String aaCert = "src/test/resources/certs/test_host_cnaf_infn_it.cert.pem";
	static final String aaKey = "src/test/resources/certs/test_host_cnaf_infn_it.key.pem";
	
	static final String aaCert2 = "src/test/resources/certs/wilco_cnaf_infn_it.cert.pem";
	static final String aaKey2 = "src/test/resources/certs/wilco_cnaf_infn_it.key.pem";
	
	static final String expiredCert = "src/test/resources/certs/expired.cert.pem";
	static final String expiredKey = "src/test/resources/certs/expired.key.pem";
	
	static final String revokedCert = "src/test/resources/certs/revoked.cert.pem";
	static final String revokedKey = "src/test/resources/certs/revoked.key.pem";
	
	static final String holderCert ="src/test/resources/certs/test0.cert.pem";
	static final String holderKey ="src/test/resources/certs/test0.key.pem";
	
	static final String vomsdir = "src/test/resources/vomsdir";
	static final String trustAnchorsDir = "src/test/resources/trust-anchors";

}
