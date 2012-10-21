package org.italiangrid.voms.request;

import java.io.File;
import java.io.Reader;
import java.util.List;

import org.italiangrid.voms.VOMSError;

/**
 * A VOMSES file parser.
 * 
 * @author Andrea Ceccanti
 * 
 */
public interface VOMSESParser {

	/**
	 * Parses the VOMS contact information from the {@link Reader} passed as
	 * argument.
	 * 
	 * @param vomsesReader
	 *            the {@link Reader} object where voms contact information can
	 *            be read from.
	 * @return a {@link VOMSServerInfo} object containing the VOMS server
	 *         contact information.
	 * @throws VOMSError
	 *             in case of parsing errors
	 */
	public List<VOMSServerInfo> parse(Reader vomsesReader);

	/**
	 * Parses the VOMS contact information from the {@link File} passed as
	 * argument.
	 * 
	 * @param f
	 *            the {@link File} object where voms contact information can be
	 *            read from.
	 * @return a {@link VOMSServerInfo} object containing the VOMS server
	 *         contact information.
	 * @throws VOMSError
	 *             in case of parsing errors
	 */
	public List<VOMSServerInfo> parse(File f);
}
