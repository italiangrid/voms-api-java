package org.italiangrid.voms.request;

import java.io.File;
import java.util.List;

/**
 * An strategy for building a list of {@link File} objects which will provide access
 * to the local trusted VOMS server contact information.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSESLookupStrategy {

	/**
	 * @return a {@link List} of {@link File} objects that can be 
	 * used to parse VOMSES information.
	 */
	public List<File> lookupVomsesInfo();
}
