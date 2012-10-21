package org.italiangrid.voms.request;

import java.util.List;

/**
 * A request for a VOMS Attribute certificate.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSACRequest {

	/**
	 * @return the lifetime for this {@link VOMSACRequest}.
	 */
	public int getLifetime();

	/**
	 * @return the list of the requested FQANs specified in this
	 *         {@link VOMSACRequest} object.
	 */
	public List<String> getRequestedFQANs();

	/**
	 * @return the list of targets (i.e., host where the requested ACs will be
	 *         valid) for this {@link VOMSACRequest} object.
	 */
	public List<String> getTargets();

	/**
	 * @return the name of the VO this {@link VOMSACRequest} object is about.
	 */
	public String getVoName();

}