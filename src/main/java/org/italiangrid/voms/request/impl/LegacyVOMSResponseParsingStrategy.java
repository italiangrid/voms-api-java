package org.italiangrid.voms.request.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSResponseParsingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Parsing strategy for legacy VOMS responses.
 * 
 * @author valerioventuri
 *
 */
public class LegacyVOMSResponseParsingStrategy implements VOMSResponseParsingStrategy {

  private static Logger log = LoggerFactory.getLogger(LegacyVOMSResponseParsingStrategy.class);

  protected DocumentBuilder documentBuilder;
  
  public LegacyVOMSResponseParsingStrategy() {
    
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setIgnoringComments(true);
    factory.setNamespaceAware(false);
    factory.setValidating(false);

    try {
    
      documentBuilder = factory.newDocumentBuilder();

    } catch (ParserConfigurationException e) {

      log.error("Error configuring DOM document builder.");
      
      if (log.isDebugEnabled()) {
        log.debug(e.getMessage(), e);
      }

      throw new VOMSError(e.getMessage(), e);
    }
  }
  
  public VOMSResponse parse(InputStream inputStream) {
    
    try {

      Document document = documentBuilder.parse(inputStream);
      
      return new LegacyVOMSResponse(document);

    } catch (SAXException e) {

      log.error("Error parsing voms server response:" + e.getMessage());

      if (log.isDebugEnabled())
        log.error(e.getMessage(), e);

      throw new VOMSError(e.getMessage());

    } catch (IOException e) {

      log.error("I/O error reading voms server response:" + e.getMessage());
      
      if (log.isDebugEnabled())
        log.error(e.getMessage(), e);

      throw new VOMSError(e.getMessage());
    }

  }


  
  
}
