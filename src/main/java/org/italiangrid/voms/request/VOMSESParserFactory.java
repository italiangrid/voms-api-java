package org.italiangrid.voms.request;

import org.italiangrid.voms.request.impl.LegacyVOMSESParserImpl;

public class VOMSESParserFactory {
	
	public static VOMSESParser newVOMSESParser(){
		
		return new LegacyVOMSESParserImpl();
	}

}
