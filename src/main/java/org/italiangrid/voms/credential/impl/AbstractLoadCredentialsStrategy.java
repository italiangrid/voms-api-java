package org.italiangrid.voms.credential.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.bouncycastle.openssl.PasswordFinder;
import org.italiangrid.voms.credential.LoadCredentialsEventListener;
import org.italiangrid.voms.credential.LoadCredentialsStrategy;
import org.italiangrid.voms.credential.VOMSEnvironmentVariables;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.impl.KeystoreCredential;
import eu.emi.security.authn.x509.impl.PEMCredential;
/**
 * Base class for load credentials strategy providing utility classes for loading
 * credentials from the filesystem and notifying the results of load operations
 * to interested listeners (via {@link LoadCredentialsEventListener}).
 *  
 * @author andreaceccanti
 *
 */
public abstract class AbstractLoadCredentialsStrategy implements LoadCredentialsStrategy, VOMSEnvironmentVariables {

	/** 
	 * The {@link LoadCredentialsEventListener} that is notified of load credentials outcome.
	 */
	LoadCredentialsEventListener listener;
	
	/**
	 * Constructor that let client pass in a {@link LoadCredentialsEventListener}.
	 * @param listener the listener that is notified of load credential events from this {@link AbstractLoadCredentialsStrategy}.
	 */
	protected AbstractLoadCredentialsStrategy(LoadCredentialsEventListener listener){
		this.listener = listener;
	}
	
	/**
	 * Convenience method to check if a file exists and is readable
	 * @param filename the file to be checked
	 * @return <code>true</code> if the file exists and is readable, <code>false</code> otherwise
	 */
	protected boolean fileExistsAndIsReadable(String filename) {
		File f = new File(filename);
		return f.exists() && f.isFile() && f.canRead();
	}
	
	/**
	 * Loads a  PEM X.509 credential and notifies the registered {@link LoadCredentialsEventListener} of
	 * the load operation outcome.
	 * 
	 * @param privateKeyPath the path to the private key
	 * @param certificatePath the path to the certificate
	 * @param pf a {@link PasswordFinder} used to resolve the private key password when needed
	 * @return the loaded {@link X509Credential}, or <code>null</code> if the credential couldn't be loaded 
	 */
	protected X509Credential loadPEMCredential(String privateKeyPath, String certificatePath, PasswordFinder pf){
		
		VOMSPEMCredential cred = null;
		
		listener.notifyCredentialLookup(privateKeyPath, certificatePath);
		
		try {
			cred = new VOMSPEMCredential(privateKeyPath, certificatePath, pf);
			
			listener.notifyLoadCredentialSuccess(privateKeyPath, certificatePath);
			
		
		} catch (Throwable t) {
			
			listener.notifyLoadCredentialFailure(t, privateKeyPath, certificatePath);
		}
	
		return cred;
		
	}
	
	/**
	 * Loads a PCKS12 X.509 credential and notifies the registered {@link LoadCredentialsEventListener} of
	 * the load operation outcome.
	 * 
	 * @param pkcs12FilePath the path to the pkcs12 credential
	 * @param pf a {@link PasswordFinder} used to resolve the private key password 
	 * @return the loaded {@link X509Credential}, or <code>null</code> if the credential couldn't be loaded 
	 */
	protected X509Credential loadPKCS12Credential(String pkcs12FilePath, PasswordFinder pf){
		KeystoreCredential cred = null;
		
		listener.notifyCredentialLookup(pkcs12FilePath);
		
		if (fileExistsAndIsReadable(pkcs12FilePath)){
			char[] keyPassword = pf.getPassword();
			try {
				
				cred = new KeystoreCredential(pkcs12FilePath, keyPassword, keyPassword, null, "PKCS12");
				listener.notifyLoadCredentialSuccess(pkcs12FilePath);
			
			} catch (Throwable t) {
				
				listener.notifyLoadCredentialFailure(t, pkcs12FilePath);
			}
		
		}else
			listener.notifyLoadCredentialFailure(new IOException(pkcs12FilePath+" (cannot read file)"), pkcs12FilePath);
			
		return cred;
	}
	
	/**
	 * Loads an X.509 proxy credential and notifies the registered {@link LoadCredentialsEventListener} of
	 * the load operation outcome.
	 * 
	 * @param proxyPath the path to the proxy credential
	 * @return the loaded {@link X509Credential}, or <code>null</code> if the credential couldn't be loaded 
	 */
	protected X509Credential loadProxyCredential(String proxyPath){
		PEMCredential cred = null;
		
		listener.notifyCredentialLookup(proxyPath);
		
		try {
			
			cred = new PEMCredential(new FileInputStream(proxyPath), null);
			listener.notifyLoadCredentialSuccess(proxyPath);
		
		} catch (Throwable t) {
			
			listener.notifyLoadCredentialFailure(t, proxyPath);
		}
		
		return cred;	
	}
}
