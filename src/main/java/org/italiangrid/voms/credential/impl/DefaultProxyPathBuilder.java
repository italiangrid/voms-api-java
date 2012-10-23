package org.italiangrid.voms.credential.impl;

import org.italiangrid.voms.credential.ProxyPathBuilder;

public class DefaultProxyPathBuilder implements ProxyPathBuilder {

	public String buildProxyFilePath(String tmpPath, int userId) {
		return String.format("%s/x509up_u%d", tmpPath, userId);
	}

}
