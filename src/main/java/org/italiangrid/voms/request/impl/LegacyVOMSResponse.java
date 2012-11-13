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
package org.italiangrid.voms.request.impl;

import org.italiangrid.voms.request.VOMSErrorMessage;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSWarningMessage;
import org.italiangrid.voms.util.VOMSBase64Decoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LegacyVOMSResponse implements VOMSResponse {

  private static int ERROR_OFFSET = 1000;
  
  protected Document xmlResponse;

  /**
   * Builds a VOMSResponse starting from a DOM an XML document (see
   * {@link Document}).
   * 
   * @param res
   */
  public LegacyVOMSResponse(Document res) {

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
    
    // errors imply that no AC were created
    return ((xmlResponse.getElementsByTagName("item").getLength() != 0) && 
        (xmlResponse.getElementsByTagName("ac").getLength() == 0));
  }

  /* (non-Javadoc)
   * @see org.glite.voms.contact.VOMSResponseIF#hasWarnings()
   */
  public boolean hasWarnings() {
    
    // warnings imply that ACs were created
    return ((xmlResponse.getElementsByTagName("item").getLength() != 0) && 
        (xmlResponse.getElementsByTagName("ac").getLength() != 0));
  }

  /* (non-Javadoc)
   * @see org.glite.voms.contact.VOMSResponseIF#getAC()
   */
  public byte[] getAC() {

    Element acElement = (Element) xmlResponse.getElementsByTagName("ac").item(0);

    byte[] ac = new GoodACDecodingStrategy().decode(acElement.getFirstChild().getNodeValue());
    
    if(ac== null) 
      ac = VOMSBase64Decoder.decode(acElement.getFirstChild().getNodeValue());
    
    return ac;
  }

  /* (non-Javadoc)
   * @see org.glite.voms.contact.VOMSResponseIF#errorMessages()
   */
  public VOMSErrorMessage[] errorMessages() {

    NodeList nodes = xmlResponse.getElementsByTagName("item");

    if (nodes.getLength() == 0)
      return null;

    VOMSErrorMessage[] result = new VOMSErrorMessage[nodes.getLength()];

    for (int i = 0; i < nodes.getLength(); i++) {

      Element itemElement = (Element) nodes.item(i);

      Element numberElement = (Element) itemElement.getElementsByTagName(
          "number").item(0);
      Element messageElement = (Element) itemElement
          .getElementsByTagName("message").item(0);

      int number = Integer.parseInt(numberElement.getFirstChild()
          .getNodeValue());

      if (number >= ERROR_OFFSET) {
        
        result[i] = new VOMSErrorMessage(number, messageElement.getFirstChild().getNodeValue());
      }
    }

    return result;
  }

  /* (non-Javadoc)
   * @see org.glite.voms.contact.VOMSResponseIF#warningMessages()
   */
  public VOMSWarningMessage[] warningMessages() {
   
    NodeList nodes = xmlResponse.getElementsByTagName("item");

    if (nodes.getLength() == 0)
      return null;

    VOMSWarningMessage[] result = new VOMSWarningMessage[nodes.getLength()];

    for (int i = 0; i < nodes.getLength(); i++) {

      Element itemElement = (Element) nodes.item(i);

      Element numberElement = (Element) itemElement.getElementsByTagName(
          "number").item(0);
      Element messageElement = (Element) itemElement
          .getElementsByTagName("message").item(0);

      int number = Integer.parseInt(numberElement.getFirstChild()
          .getNodeValue());

      if (number < ERROR_OFFSET) {
        result[i] = new VOMSWarningMessage(number, messageElement
            .getFirstChild().getNodeValue());
      }
    }

    return result;
  }

}
