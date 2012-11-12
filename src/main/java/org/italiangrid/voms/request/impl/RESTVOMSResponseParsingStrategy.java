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
 * Strategy for parsing a response coming from a RESTFul VOMS.
 * 
 * @author valerioventuri
 *
 */
public class RESTVOMSResponseParsingStrategy implements VOMSResponseParsingStrategy {
  
  private DocumentBuilder docBuilder;
  
  /**
   * 
   */
  public RESTVOMSResponseParsingStrategy() {
    
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setIgnoringComments(true);
    factory.setNamespaceAware(false);
    factory.setValidating(false);

    try {
    
      docBuilder = factory.newDocumentBuilder();
    
    } catch (ParserConfigurationException e) {
      
      throw new VOMSError(e.getMessage(), e);
    }
    
  }

  /**
   * Parse a response coming from a RESTFul VOMS service and builds a {@link VOMSResponse} object
   * representing the response.
   * 
   * @param inputStream the response coming from the service
   * @return a {@link VOMSResponse} object representing the response.
   * 
   */
  public VOMSResponse parse(InputStream inputStream) {
    
    try {

      Document document = docBuilder.parse(inputStream);
      
      return new RESTVOMSResponse(document);

    } catch (Exception e) {

      throw new VOMSError(e.getMessage());

    } 
  }

}
