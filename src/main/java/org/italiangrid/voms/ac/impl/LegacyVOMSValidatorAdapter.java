package org.italiangrid.voms.ac.impl;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.ac.LegacyVOMSValidator;

public class LegacyVOMSValidatorAdapter implements
		LegacyVOMSValidator {

	private final DefaultVOMSValidator validator;
	
	public LegacyVOMSValidatorAdapter(X509Certificate cert) {
		this(new X509Certificate[]{cert});
	}
	
	public LegacyVOMSValidatorAdapter(X509Certificate[] certChain) {
		
		validator = new DefaultVOMSValidator();
		validator.setCertificateChain(certChain);
	}
	
	public void cleanup() {
		
		validator.shutdown(); 
	}

	public void setClientChain(X509Certificate[] cert) {
		validator.setCertificateChain(cert);
	}

	public void parse() {
		// noop
		
	}

	public void validate() {
		// noop
		
	}

	public String[] getAllFullyQualifiedAttributes() {
		List<VOMSAttribute> attributes = validator.validate();
		List<String> allFQANs = new ArrayList<String>();
		
		for (VOMSAttribute a: attributes)
			allFQANs.addAll(a.getFQANs());
		
		return allFQANs.toArray(new String[allFQANs.size()]);
	}

	public List<VOMSAttribute> getVOMSAttributes() {
		List<VOMSAttribute> attributes = validator.validate();
		return attributes;
	}

	public List<String> getRoles(String groupPrefix) {
		
		String[] allFQANs = getAllFullyQualifiedAttributes();
		List<String> roles = new ArrayList<String>();
		
		String roleMatcherString = "^.*/Role=([\\w.-]+)$";
		
		Pattern roleMatcher = Pattern.compile(roleMatcherString);
		
		for (String fqan: allFQANs){
			if (fqan.startsWith(groupPrefix)){
				Matcher m = roleMatcher.matcher(fqan); 
				if (m.matches())
					roles.add(m.group(1));
			}
		}
		return roles;
	}
}
