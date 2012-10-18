package org.italiangrid.voms.request.impl;

import java.security.cert.X509Certificate;
import java.util.List;

import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSProxyInit;

import eu.emi.security.authn.x509.impl.PEMCredential;
/**
 * The default {@link VOMSProxyInit} functionality implementation.
 * 
 * @author andreaceccanti
 *
 */
public class VOMSProxyInitImpl implements VOMSProxyInit {

	public VOMSProxyInitImpl() {
		
	}

	public PEMCredential getVOMSProxy(X509Certificate[] parentChain,
			VOMSACRequest request) {
		
		return null;
	}

	public PEMCredential getVOMSProxy(X509Certificate[] parentChain,
			List<VOMSACRequest> requests) {
		
		return null;
	}

	

}
