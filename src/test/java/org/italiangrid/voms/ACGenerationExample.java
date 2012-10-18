package org.italiangrid.voms;


public class ACGenerationExample {
	String aaCert = "src/test/resources/certs/test_host_cnaf_infn_it.cert.pem";
	String aaKey = "src/test/resources/certs/test_host_cnaf_infn_it.key.pem";
	String holderCert ="src/test/resources/certs/test0.cert.pem";
	
	String vo = "test.vo";
	String host = "localhost";
	
	
	public ACGenerationExample() {
		
		
	}
	
	public static void main(String[] args) {
		new ACGenerationExample();
	}

}
