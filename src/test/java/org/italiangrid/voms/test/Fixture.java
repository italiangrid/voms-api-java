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

import java.util.Arrays;
import java.util.List;

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
	
	static final String holderCert2 ="src/test/resources/certs/test1.cert.pem";
	static final String holderKey2 ="src/test/resources/certs/test1.key.pem";
	
	static final String vomsdir = "src/test/resources/vomsdir";
	static final String vomsdir_fake_aa_cert = "src/test/resources/vomsdir-fake-aa-cert";
	static final String vomsdir_expired_aa_cert = "src/test/resources/vomsdir-expired-aa-cert";
	
	static final String trustAnchorsDir = "src/test/resources/trust-anchors";

	static final String defaultVO = "test.vo";
	static final String defaultVOHost = "test-host.cnaf.infn.it";
	static final int defaultVOPort = 15000;
	
	static final List<String> defaultVOFqans = Arrays.asList("/test.vo");
	
}
