/*********************************************************************
 *
 * Authors: 
 *      Andrea Ceccanti - andrea.ceccanti@cnaf.infn.it 
 *          
 * Copyright (c) Members of the EGEE Collaboration. 2004-2010.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Parts of this code may be based upon or even include verbatim pieces,
 * originally written by other people, in which case the original header
 * follows.
 *
 *********************************************************************/
package org.italiangrid.voms.request.impl;

import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSACRequest;
import org.w3c.dom.Document;

/**
 * This class manages the client-side communication protocol with the VOMS
 * server.
 * 
 * @author Andrea Ceccanti
 * 
 */
public class LegacyRequestSender {

	private VOMSRequestFactory requestFactory = VOMSRequestFactory.instance();
	private TransformerFactory transformerFactory;

	private LegacyRequestSender() {
	  
		transformerFactory = TransformerFactory.newInstance();
	}

	public static LegacyRequestSender instance() {
		
	  return new LegacyRequestSender();
	}

	protected String xmlDocAsString(Document doc) {

		Transformer transformer;

		try {

			transformer = transformerFactory.newTransformer();

		} catch (TransformerConfigurationException e) {

			throw new VOMSError(e.getMessage(), e);

		}
		
		StringWriter writer = new StringWriter();

		DOMSource source = new DOMSource(doc);
		
		StreamResult res = new StreamResult(writer);

		try {

			transformer.transform(source, res);

		} catch (TransformerException e) {
			
			throw new VOMSError(e.getMessage(), e);

		}
		
		writer.flush();

		return writer.toString();
	}

	/**
	 * 
	 * This method is used to send a request to a VOMS server.
	 * 
	 * @param acRequest
	 *            the request options. See {@link VOMSRequestOptions}.
	 * @param stream
	 *            an output stream.
	 */
	public void sendRequest(VOMSACRequest acRequest, OutputStream stream) {

		Document request = requestFactory.buildRequest(acRequest);

		Transformer transformer;

		try {

			transformer = transformerFactory.newTransformer();

		} catch (TransformerConfigurationException e) {

			throw new VOMSError(e.getMessage(), e);
		}

		DOMSource source = new DOMSource(request);
		
		StreamResult res = new StreamResult(stream);

		try {

			transformer.transform(source, res);
			stream.flush();

		} catch (Exception e) {

			throw new VOMSError(e.getMessage(), e);

		}
	}
}
