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
package org.italiangrid.voms.request.impl;

import org.italiangrid.voms.request.ACDecodingStrategy;
import org.italiangrid.voms.request.VOMSErrorMessage;
import org.italiangrid.voms.request.VOMSWarningMessage;
import org.italiangrid.voms.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * This class is used to parse and represent VOMS server responses 
 * coming from a RESTful VOMS service.
 * 
 * @author Andrea Ceccanti
 * @author Vincenzo Ciaschini
 * @author Valerio Venturi
 * 
 */
public class RESTVOMSResponse implements org.italiangrid.voms.request.VOMSResponse {

	private static int ERROR_OFFSET = 1000;
	
	protected Document xmlResponse;

	public RESTVOMSResponse(Document res) {
	  xmlResponse = res;
  }
	
  /* (non-Javadoc)
   * @see org.glite.voms.contact.VOMSResponseIF#getVersion()
   */
  public int getVersion() {
    
    Element versionElement = (Element) xmlResponse.getElementsByTagName("version").item(0);
    
    if (versionElement == null) {
      
      return 0;
    }
    
    return Integer.parseInt(versionElement.getFirstChild().getNodeValue());
  }
	
	/* (non-Javadoc)
	 * @see org.glite.voms.contact.VOMSResponseIF#hasErrors()
	 */
	public boolean hasErrors() {
		
		return (xmlResponse.getElementsByTagName("error").getLength() != 0);
	}

	/* (non-Javadoc)
	 * @see org.glite.voms.contact.VOMSResponseIF#hasWarnings()
	 */
	public boolean hasWarnings() {
		
		return (xmlResponse.getElementsByTagName("warning").getLength() != 0);
	}

	/* (non-Javadoc)
	 * @see org.glite.voms.contact.VOMSResponseIF#getAC()
	 */
	public byte[] getAC() {

		Element acElement = (Element) xmlResponse.getElementsByTagName("ac").item(0);

		if (acElement == null || !acElement.hasChildNodes())
			return null;
		
		String acString = acElement.getFirstChild().getNodeValue();
		
		ACDecodingStrategy acDecodingStrategy = new GoodACDecodingStrategy();
		
		byte[] decodedAc = acDecodingStrategy.decode(acString);
		
		return decodedAc;
	}

	public VOMSErrorMessage[] errorMessages() {
		
	  NodeList nodes = xmlResponse.getElementsByTagName("error");

		if (nodes.getLength() == 0)
			return null;

		VOMSErrorMessage[] result = new VOMSErrorMessage[nodes.getLength()];

		for (int i = 0; i < nodes.getLength(); i++) {
			
		  Element itemElement = (Element) nodes.item(i);
		  Element codeElement = (Element) itemElement.getElementsByTagName("code").item(0);
			Element messageElement = (Element) itemElement.getElementsByTagName("message").item(0);
			String strcode = codeElement.getFirstChild().getNodeValue();
			
			int code;

			if (strcode.equals("NoSuchUser"))
				code = 1001;
			else if (strcode.equals("BadRequest"))
				code = 1005;
			else if (strcode.equals("SuspendedUser"))
				code = 1004;
			else
				// InternalError
				code = 1006;

			result[i] = new VOMSErrorMessage(code, messageElement.getFirstChild().getNodeValue());
		}
		
		return result;
	}

	public VOMSWarningMessage[] warningMessages() {
		
	  NodeList nodes = xmlResponse.getElementsByTagName("warning");

		if (nodes.getLength() == 0)
			return null;

		VOMSWarningMessage[] result = new VOMSWarningMessage[nodes.getLength()];

		for (int i = 0; i < nodes.getLength(); i++) {

			Element itemElement = (Element) nodes.item(i);

			// Element messageElement = (Element) itemElement.getElementsByTagName("message").item(0);

			String message = itemElement.getFirstChild().getNodeValue();
			
			int number;

			if (message.contains("validity"))
				number = 2;
			else if (message.contains("selected"))
				number = 1;
			else if (message.contains("contains attributes"))
				number = 3;
			else
				number = 4;
			
			if (number < ERROR_OFFSET) {
				result[i] = new VOMSWarningMessage(number, message);
			}
		}

		return result;
	}

	public String getXMLAsString() {
		return XMLUtils.documentAsString(xmlResponse);
	}

}
