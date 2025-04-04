// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

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
import org.italiangrid.voms.request.VOMSProtocolListener;
import org.italiangrid.voms.request.VOMSServerInfo;
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

  private VOMSProtocolListener listener;

  private LegacyRequestSender(VOMSProtocolListener listener) {

    transformerFactory = TransformerFactory.newInstance();
    this.listener = listener;
  }

  public static LegacyRequestSender instance(VOMSProtocolListener listener) {

    return new LegacyRequestSender(listener);
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
   * 
   * @param acRequest
   *          the AC request parameters. See {@link VOMSACRequest}.
   * @param endpoint
   *          the {@link VOMSServerInfo} endpoint to use for this 
   *          request
   * @param stream
   *          an output stream.
   */
  public void sendRequest(VOMSACRequest acRequest, VOMSServerInfo endpoint,
    OutputStream stream) {

    Document request = requestFactory.buildRequest(acRequest, endpoint);

    Transformer transformer;

    try {

      transformer = transformerFactory.newTransformer();

    } catch (TransformerConfigurationException e) {

      throw new VOMSError(e.getMessage(), e);
    }

    listener.notifyLegacyRequest(xmlDocAsString(request));

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
