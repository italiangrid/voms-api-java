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
package org.italiangrid.voms.test;


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
