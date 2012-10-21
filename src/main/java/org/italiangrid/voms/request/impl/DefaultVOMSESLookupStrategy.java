package org.italiangrid.voms.request.impl;


/**
 * Default VOMSES information lookup strategy.
 * 
 * This implementation looks for vomses information in the following paths:
 * <ul>
 * 	<li>/etc/vomses
 * 	<li>${user.home}/.glite/vomses
 * </ul>
 * 
 * @author Andrea Ceccanti
 *
 */
public class DefaultVOMSESLookupStrategy extends BaseVOMSESLookupStrategy {

	public static final String DEFAULT_VOMSES_DIR = "/etc/vomses";
	
	
	public DefaultVOMSESLookupStrategy() {
		super(new String[]{DEFAULT_VOMSES_DIR, 
				System.getProperty("user.home")+"/.glite/vomses"});
	}

}
