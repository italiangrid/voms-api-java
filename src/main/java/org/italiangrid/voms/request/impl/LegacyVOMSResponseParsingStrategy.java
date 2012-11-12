package org.italiangrid.voms.request.impl;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSResponseParsingStrategy;
import org.w3c.dom.Document;

/**
 * Parsing strategy for legacy VOMS responses.
 * 
 * @author valerioventuri
 *
 */
public class LegacyVOMSResponseParsingStrategy implements VOMSResponseParsingStrategy {

  protected DocumentBuilder documentBuilder;
  
  public LegacyVOMSResponseParsingStrategy() {
    
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setIgnoringComments(true);
    factory.setNamespaceAware(false);
    factory.setValidating(false);

    try {
    
      documentBuilder = factory.newDocumentBuilder();

    } catch (ParserConfigurationException e) {

      throw new VOMSError(e.getMessage(), e);
    }
  }
  
  public VOMSResponse parse(InputStream inputStream) {
    
    try {

      Document document = documentBuilder.parse(inputStream);
      
      return new LegacyVOMSResponse(document);

    } catch (Exception e) {

      throw new VOMSError(e.getMessage());

    }

  }  
}
