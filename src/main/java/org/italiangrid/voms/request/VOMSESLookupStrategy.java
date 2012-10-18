package org.italiangrid.voms.request;

import java.io.Reader;
import java.util.List;

/**
 * An strategy for building a list of {@link Reader} objects which will provide access
 * to the local trusted VOMS server contact information.
 * 
 * @author cecco
 *
 */
public interface VOMSESLookupStrategy {

	/**
	 * @return a {@link List} of {@link Reader} objects that can be 
	 * used to parse VOMSES information.
	 */
	public List<Reader> lookupVomsesInfo();
}
