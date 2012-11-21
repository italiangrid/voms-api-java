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
package org.italiangrid.voms.credential;

public interface VOMSEnvironmentVariables {

	public static final String X509_CERT_DIR = "X509_CERT_DIR";
	public static final String X509_USER_PROXY = "X509_USER_PROXY";
	public static final String X509_USER_CERT = "X509_USER_CERT";
	public static final String X509_USER_KEY = "X509_USER_KEY";
	public static final String PKCS12_USER_CERT = "PKCS12_USER_CERT";
	public static final String VOMS_USER_ID = "VOMS_UID";

}