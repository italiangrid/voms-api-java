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
package org.italiangrid.voms.request;

import java.io.File;
import java.io.Reader;
import java.util.List;

import org.italiangrid.voms.VOMSError;

/**
 * A VOMSES file parser.
 * 
 * @author Andrea Ceccanti
 * 
 */
public interface VOMSESParser {

	/**
	 * Parses the VOMS contact information from the {@link Reader} passed as
	 * argument.
	 * 
	 * @param vomsesReader
	 *            the {@link Reader} object where voms contact information can
	 *            be read from.
	 * @return a {@link VOMSServerInfo} object containing the VOMS server
	 *         contact information.
	 * @throws VOMSError
	 *             in case of parsing errors
	 */
	public List<VOMSServerInfo> parse(Reader vomsesReader);

	/**
	 * Parses the VOMS contact information from the {@link File} passed as
	 * argument.
	 * 
	 * @param f
	 *            the {@link File} object where voms contact information can be
	 *            read from.
	 * @return a {@link VOMSServerInfo} object containing the VOMS server
	 *         contact information.
	 * @throws VOMSError
	 *             in case of parsing errors
	 */
	public List<VOMSServerInfo> parse(File f);
}
