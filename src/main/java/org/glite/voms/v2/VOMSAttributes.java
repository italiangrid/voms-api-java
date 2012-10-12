package org.glite.voms.v2;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.X509AttributeCertificateHolder;


/**
 * The VOMS attribute information.
 * 
 * @author andreaceccanti
 *
 */
public interface VOMSAttributes {
	
	/**
	 * @return The name of the VO this VOMS attributes are about
	 */
	public String getVO();
	
	/**
	 * @return The name of the host where the VOMS AA that signed these attributes lives
	 */
	public String getHost();
	
	/**
	 * @return The port on which the VOMS AA that signed these attributes listens for requests
	 */
	public int getPort();
	
	/**
	 * @return The subject of the holder of these VOMS attributes
	 */
	public X500Principal getHolder();
	
	/**
	 * @return The subject of the VOMS AA that signed these attributes
	 */
	public X500Principal getIssuer();
	
	/**
	 * @return The attributes' validity start time 
	 */
	public Date getNotBefore();
	
	/**
	 * @return The attributes' validity end time
	 */
	public Date getNotAfter();
	
	/**
	 * @return The {@link List} of VOMS fully qualified attribute names
	 */
	public List<String> getFQANs();
	
	/**
	 * @return The primary VOMS fully qualified attribute name
	 */
	public String getPrimaryFQAN();
	
	/**
	 * @return The signature of this VOMS attributes
	 */
	public byte[] getSignature();
	
	/**
	 * @return The VOMS generic attributes
	 */
	public List<VOMSGenericAttribute> getGenericAttributes();
	
	/**
	 * @return The targets for this VOMS attributes
	 * @return
	 */
	public List<String> getTargets();
	
	/**
	 * @return The VOMS AA certificate chain
	 */
	public X509Certificate[] getAACertificates();
	
	/**
	 * This method checks whether the attributes are valid in the
	 * current instant of time. No validation is performed on the attributes.
	 * @return <code>true</code> if valid, <code>false</code> otherwise
	 */
	public boolean isValid();
	
	/**
	 * This method checks whether the attributes are valid in a given time
	 * passed as argument. No validation is performed on the attributes.
	 *  
	 * @param time 
	 * @return <code>true</code> if valid, <code>false</code> otherwise
	 */
	public boolean validAt(Date time);
	
	/**
	 * Returns the underlying VOMS attribute certificate.
	 * 
	 * @return 
	 */
	public X509AttributeCertificateHolder getVOMSAC();
}
