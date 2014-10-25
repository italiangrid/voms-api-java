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

import org.italiangrid.voms.VOMSError;

/**
 * This error is raised when there is an attempt to load a credential
 * which has the wrong file permissions  
 * 
 */
public class FilePermissionError extends VOMSError {

	public FilePermissionError(String message) {
		super(message);
	}

	
	public FilePermissionError(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
