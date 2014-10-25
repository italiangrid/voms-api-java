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
package org.italiangrid.voms.request;



/**
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSResponse {

	/**
	 * @return <code>true</code> if this {@link VOMSResponse} has errors, <code>false</code> otherwise
	 */
	public boolean hasErrors();

	/**
	 * @return <code>true</code> if this {@link VOMSResponse} has warnings, <code>false</code> otherwise
	 */
	public boolean hasWarnings();

	/**
	 * 
	 * Extracts the AC from the VOMS response.
	 * 
	 * @return an array of bytes containing the AC.
	 */
	public byte[] getAC();

	/**
	 * Extracts the version from the VOMS response.
	 * 
	 * @return an integer containing the AC.
	 */
	public abstract int getVersion();

	/**
	 * 
	 * Extracts the error messages from the VOMS response.
	 * 
	 * @return an array of {@link VOMSErrorMessage} objects.
	 */
	public VOMSErrorMessage[] errorMessages();

	/**
	 * Extracts the warning messags from the VOMS response.
	 * @return an array of {@link VOMSWarningMessage} objects.
	 */
	public VOMSWarningMessage[] warningMessages();
	
	/**
	 * 
	 * @return Returns the XML representation of the response as a string.
	 */
	public String getXMLAsString();

}