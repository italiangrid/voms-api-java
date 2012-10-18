package org.italiangrid.voms.request;

import java.io.Reader;

import org.italiangrid.voms.VOMSError;


/**
 * A VOMSES file parser. 
 * 
 * @author cecco
 *
 */
public interface VOMSESParser {

	/**
	 * Parses the VOMS contact information from the {@link Reader} passed as argument.
	 * 
	 * @param vomsesReader the {@link Reader} object where voms contact information can be read from.
	 * @return a {@link VOMSServerInfo} object containing the VOMS server contact information.
	 * @throws VOMSError in case of parsing errors
	 */
	public VOMSServerInfo parse(Reader vomsesReader);
}
