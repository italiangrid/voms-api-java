package org.italiangrid.voms.ac;

import java.security.cert.X509Certificate;
import java.util.List;

import org.italiangrid.voms.VOMSAttribute;


@Deprecated
/**
 * Legacy VOMS validator interface
 * 
 */
public interface LegacyVOMSValidator {

	public void cleanup();
	
	public void setClientChain(X509Certificate[] cert);
	public void parse();
	public void validate();
	
	public String[] getAllFullyQualifiedAttributes();
	public List<VOMSAttribute> getVOMSAttributes();
	
	public List<String> getRoles(String groupPrefix);
	
}
