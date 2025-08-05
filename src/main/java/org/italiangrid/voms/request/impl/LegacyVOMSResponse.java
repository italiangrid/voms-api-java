// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.request.impl;

import java.util.ArrayList;
import java.util.List;

import org.italiangrid.voms.request.VOMSErrorMessage;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSWarningMessage;
import org.italiangrid.voms.util.VOMSBase64Decoder;
import org.italiangrid.voms.util.XMLUtils;
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
   *          the XML document for the response
   */
  public LegacyVOMSResponse(Document res) {

    xmlResponse = res;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.glite.voms.contact.VOMSResponseIF#getVersion()
   */
  public int getVersion() {

    Element versionElement = (Element) xmlResponse.getElementsByTagName(
      "version").item(0);

    if (versionElement == null) {

      return 0;
    }

    return Integer.parseInt(versionElement.getFirstChild().getNodeValue());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.glite.voms.contact.VOMSResponseIF#hasErrors()
   */
  public boolean hasErrors() {

    return errorMessages() != null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.glite.voms.contact.VOMSResponseIF#hasWarnings()
   */
  public boolean hasWarnings() {

    return warningMessages() != null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.glite.voms.contact.VOMSResponseIF#getAC()
   */
  public byte[] getAC() {

    Element acElement = (Element) xmlResponse.getElementsByTagName("ac")
      .item(0);

    byte[] ac = VOMSBase64Decoder.decode(acElement.getFirstChild()
      .getNodeValue());

    if (ac == null)
      ac = new GoodACDecodingStrategy().decode(acElement.getFirstChild()
        .getNodeValue());

    return ac;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.glite.voms.contact.VOMSResponseIF#errorMessages()
   */
  public VOMSErrorMessage[] errorMessages() {

    NodeList nodes = xmlResponse.getElementsByTagName("item");

    if (nodes.getLength() == 0)
      return null;

    List<VOMSErrorMessage> errorList = new ArrayList<VOMSErrorMessage>();

    for (int i = 0; i < nodes.getLength(); i++) {

      Element itemElement = (Element) nodes.item(i);

      Element numberElement = (Element) itemElement.getElementsByTagName(
        "number").item(0);
      Element messageElement = (Element) itemElement.getElementsByTagName(
        "message").item(0);

      int number = Integer.parseInt(numberElement.getFirstChild()
        .getNodeValue());
      if (number >= ERROR_OFFSET)
        errorList.add(new VOMSErrorMessage(number, messageElement
          .getFirstChild().getNodeValue()));

    }

    if (errorList.isEmpty())
      return null;

    return errorList.toArray(new VOMSErrorMessage[errorList.size()]);
  }

  public VOMSWarningMessage[] warningMessages() {

    NodeList nodes = xmlResponse.getElementsByTagName("item");

    if (nodes.getLength() == 0)
      return null;

    List<VOMSWarningMessage> warningList = new ArrayList<VOMSWarningMessage>();

    for (int i = 0; i < nodes.getLength(); i++) {

      Element itemElement = (Element) nodes.item(i);

      Element numberElement = (Element) itemElement.getElementsByTagName(
        "number").item(0);
      Element messageElement = (Element) itemElement.getElementsByTagName(
        "message").item(0);

      int number = Integer.parseInt(numberElement.getFirstChild()
        .getNodeValue());

      if (number < ERROR_OFFSET)
        warningList.add(new VOMSWarningMessage(number, messageElement
          .getFirstChild().getNodeValue()));

    }

    if (warningList.isEmpty())
      return null;

    return warningList.toArray(new VOMSWarningMessage[warningList.size()]);
  }

  public String getXMLAsString() {

    return XMLUtils.documentAsString(xmlResponse);

  }

}
