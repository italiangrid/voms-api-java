package org.italiangrid.voms.request;

import org.italiangrid.voms.VOMSError;

import eu.emi.security.authn.x509.X509Credential;

/**
 * Exception used when errors are raised during the interaction
 * with a (possibly) remote VOMS server.
 *  
 * @author andreaceccanti
 *
 */
public class VOMSProtocolError extends VOMSError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The request that is related with this error
	 */
	private VOMSACRequest request;
	
	/**
	 * The credential related with this error
	 */
	private X509Credential credential;
	
	/**
	 * The VOMS server endpoint information related with this error
	 */
	private VOMSServerInfo serverInfo;
	
	public VOMSProtocolError(String message, VOMSServerInfo serv, VOMSACRequest req, X509Credential cred, Throwable c) {
		super(message, c);
		this.request = req;
		this.credential = cred;
		this.serverInfo = serv;
		
	}

	/**
	 * @return the request that is related with this error
	 */
	public VOMSACRequest getRequest() {
		return request;
	}

	/**
	 * @return the client credential related with this error 
	 */
	public X509Credential getCredential() {
		return credential;
	}

	/**
	 * @return the VOMS serverInfo related with this error
	 */
	public VOMSServerInfo getServerInfo() {
		return serverInfo;
	}	
	
}
