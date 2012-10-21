package org.italiangrid.voms.request;

import java.security.cert.X509Certificate;
import java.util.List;

import org.italiangrid.voms.VOMSError;

import eu.emi.security.authn.x509.impl.PEMCredential;

/**
 * An interface describing the process of requesting attributes from a VOMS server and generating
 * a proxy embedding the received attributes.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSProxyInit {
	
	/**
	 *  
	 *  Requests attributes from a VOMS server and generates a proxy embedding the received 
	 *  attributes. 
	 * 
	 * @param parentChain the certificate chain which will be the parent of the created proxy.
	 * @param request the {@link VOMSACRequest} describing for which VO the VOMS proxy will be created.
	 * 
	 * @return a possibly null {@link PEMCredential} holding the generated VOMS proxy
	 * @throws VOMSError if something goes terribly wrong
	 */
	public PEMCredential getVOMSProxy(X509Certificate[] parentChain, VOMSACRequest request);
	
	/**
	 * Requests attributes from a list of VOMS servers and generates a proxy embedding the received 
	 * attributes.
	 *  
	 * @param parentChain the certificate chain which will be the parent of the created proxy.
	 * @param requests a list of {@link VOMSACRequest} describing for which VOs the VOMS proxy will be created.
	 * @return a possibly null {@link PEMCredential} holding the generated VOMS proxy
	 * @throws VOMSError if something goes terribly wrong
	 */
	public PEMCredential getVOMSProxy(X509Certificate[] parentChain, List<VOMSACRequest> requests);

}
