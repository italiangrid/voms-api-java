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

import java.util.List;

/**
 * A request for a VOMS Attribute certificate.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSACRequest {

	/**
	 * @return the lifetime for this {@link VOMSACRequest}.
	 */
	public int getLifetime();

	/**
	 * @return the list of the requested FQANs specified in this
	 *         {@link VOMSACRequest} object.
	 */
	public List<String> getRequestedFQANs();

	/**
	 * @return the list of targets (i.e., host where the requested ACs will be
	 *         valid) for this {@link VOMSACRequest} object.
	 */
	public List<String> getTargets();

	/**
	 * @return the name of the VO this {@link VOMSACRequest} object is about.
	 */
	public String getVoName();

}