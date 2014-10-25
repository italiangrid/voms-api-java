/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2014.
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

/**
 * A {@link ProxyNamingPolicy} defines the naming policy for a VOMS proxy.
 * @author andreaceccanti
 *
 */
public interface ProxyNamingPolicy {

	/**
	 * Builds the file name of a VOMS proxy
	 * 
	 * @param tmpPath the path of the temporary directory of the system
	 * @param userId the effective user id the user for which the proxy is created 
	 * @return a {@link String} representing the proxy file name
	 */
	public String buildProxyFileName(String tmpPath, int userId);
	
}
