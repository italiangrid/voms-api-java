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

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSResponseParsingStrategy;
import org.w3c.dom.Document;

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
