package org.italiangrid.voms.store;

import java.io.File;
import java.io.InputStream;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.store.impl.LSCFile;

/**
 * This interface defines a parser for VOMS LSC files.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface LSCFileParser {

	/**
	 * Parses an LSC file.
	 * 
	 * @param vo the name of the VO this LSC file is about
	 * @param hostname the name of host this LSC file is about
	 * @param file the LSC file
	 * @return an {@link LSCFile} object
	 * @throws VOMSError in case of parsing errors 
	 */
	public LSCFile parse(String vo, 
			String hostname,			
			File file);
	
	
	/**
	 * Parses an LSC file from a generic input stream.
	 * 
	 * @param vo the name of the VO this LSC file is about
	 * @param hostname the name of host this LSC file is about
	 * @param is an {@link InputStream} that contains the LSC information
	 * 
	 * @return an {@link LSCFile} object
	 * @throws VOMSError in case of parsing errors
	 */
	public LSCFile parse(String vo,
			String hostname,
			InputStream is);
	
	
	
}
