package org.italiangrid.voms.request;

/**
 * Decodes an encoded VOMS attribute certificate  
 * 
 * @author andreaceccanti
 *
 */
public interface ACDecodingStrategy {

	/**
	 * Decodes an encoded VOMS attribute certificate
	 * @param ac the encoded VOMS attribute certificate
	 * @return a byte array containing the VOMS attribute certificate
	 */
  public byte[] decode(String ac);
  
}
