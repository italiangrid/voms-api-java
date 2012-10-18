package org.italiangrid.voms.request;

import java.io.Reader;
import java.util.List;

/**
 * An strategy for building a set of paths where VOMSES files
 * live on the local filesystem.
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
