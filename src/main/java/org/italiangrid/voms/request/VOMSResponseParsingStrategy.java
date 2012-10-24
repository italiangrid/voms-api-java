package org.italiangrid.voms.request;

import java.io.InputStream;

/**
 * The strategy for parsing a response coming from a VOMS service.
 * 
 * @author valerioventuri
 *
 */
public interface VOMSResponseParsingStrategy {

  /**
   * Parse the response coming from a VOSM service and build a {@link VOMSResponse} object.
   * 
   * @param inputStream the response from the VOMS service.
   * @return the response object representing the response from the service.
   */
  public VOMSResponse parse(InputStream inputStream);
  
}
