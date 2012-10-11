package org.glite.voms.v2;

import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

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
	
	
}
