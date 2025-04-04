// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.util;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.italiangrid.voms.VOMSError;
import org.w3c.dom.Document;

public class XMLUtils {

  public static String documentAsString(Document doc) {

    try {
      Transformer transformer = TransformerFactory.newInstance()
        .newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      StreamResult result = new StreamResult(new StringWriter());
      DOMSource source = new DOMSource(doc);
      transformer.transform(source, result);
      return result.getWriter().toString();

    } catch (Throwable e) {
      throw new VOMSError(e.getMessage(), e);
    }

  }
}
