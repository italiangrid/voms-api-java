package org.italiangrid.voms.request;

import org.italiangrid.voms.request.impl.LegacyVOMSESParserImpl;

/**
 * A factory class for {@link VOMSESParser}.
 * @author Andrea Ceccanti
 *
 */
public class VOMSESParserFactory {
	
	/**
	 * @return Returns a {@link VOMSESParser}.
	 */
	public static VOMSESParser newVOMSESParser(){
		
		return new LegacyVOMSESParserImpl();
	}
}
