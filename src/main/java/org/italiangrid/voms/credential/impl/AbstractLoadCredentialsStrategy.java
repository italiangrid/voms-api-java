package org.italiangrid.voms.credential.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.bouncycastle.openssl.PasswordFinder;
import org.italiangrid.voms.credential.LoadCredentialsEventListener;
import org.italiangrid.voms.credential.LoadCredentialsEventListener.LoadCredentialOutcome;
import org.italiangrid.voms.credential.LoadCredentialsStrategy;

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
public abstract class AbstractLoadCredentialsStrategy implements LoadCredentialsStrategy {

	/** 
	 * The {@link LoadCredentialsEventListener} that is notified of load credentials outcome.
	 */
	LoadCredentialsEventListener listener;
	
	/**
	 * Default constructor
	 */
	protected AbstractLoadCredentialsStrategy(){
		this.listener = new LoggingCredentialNotificationListener();
	}
	
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
		
		try {
			cred = new VOMSPEMCredential(privateKeyPath, certificatePath, pf);
			
			notifyLoadSuccess(privateKeyPath, certificatePath);
		
		} catch (Throwable t) {
			notifyLoadFailure(t, privateKeyPath, certificatePath);
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
		
		if (fileExistsAndIsReadable(pkcs12FilePath)){
			char[] keyPassword = pf.getPassword();
			try {
				
				cred = new KeystoreCredential(pkcs12FilePath, keyPassword, keyPassword, null, "PKCS12");
				notifyLoadSuccess(pkcs12FilePath);
			
			} catch (Throwable t) {
				
				notifyLoadFailure(t,pkcs12FilePath);
			}
		
		}else
			notifyLoadFailure(new IOException(pkcs12FilePath+" (cannot read file)"), pkcs12FilePath);
			
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
		
		try {
			
			cred = new PEMCredential(new FileInputStream(proxyPath), null);
			notifyLoadSuccess(proxyPath);
		
		} catch (Throwable t) {
			
			notifyLoadFailure(t, proxyPath);
		}
		
		return cred;	
	}
	
	/**
	 * Notifies a credential load failure to the registered {@link LoadCredentialsEventListener}.
	 * 
	 * @param error the {@link Throwable} associated to the load failure
	 * @param credentialPaths the files for which the credentials load failed
	 */
	private void notifyLoadFailure(Throwable error, String... credentialPaths){
		listener.loadCredentialNotification(LoadCredentialOutcome.FAILURE, error, credentialPaths);
	}

	/**
	 * Notifies a credential load success to the registered {@link LoadCredentialsEventListener}.
	 * @param credentialPaths the files for which the credentials load succeded
	 */
	private void notifyLoadSuccess(String...credentialPaths){
		listener.loadCredentialNotification(LoadCredentialOutcome.SUCCESS, null, credentialPaths);
	}
}
