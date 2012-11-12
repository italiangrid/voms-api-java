package org.italiangrid.voms.credential.impl;

import org.italiangrid.voms.credential.ProxyNamingPolicy;

public class DefaultProxyPathBuilder implements ProxyNamingPolicy {

	public String buildProxyFileName(String tmpPath, int userId) {
		return String.format("%s/x509up_u%d", tmpPath, userId);
	}

}
