package org.italiangrid.voms.request;

import java.io.Reader;


/**
 * 
 * @author cecco
 *
 */
public interface VOMSESParser {

	public VOMSServerInfo parse(Reader vomsesReader);
}
