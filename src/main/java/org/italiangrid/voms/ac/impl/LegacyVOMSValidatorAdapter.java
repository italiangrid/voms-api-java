package org.italiangrid.voms.ac.impl;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.ac.LegacyVOMSValidator;
import org.italiangrid.voms.ac.VOMSACValidator;

@SuppressWarnings("deprecation")
public class LegacyVOMSValidatorAdapter implements
		LegacyVOMSValidator {

	private final VOMSACValidator validator;
	
	private X509Certificate[] theChain;
	
	
	public LegacyVOMSValidatorAdapter(X509Certificate certChain) {
		this(new X509Certificate[]{certChain});
	}
	
	public LegacyVOMSValidatorAdapter(X509Certificate[] certChain) {
		
		validator = new DefaultVOMSValidator();
		theChain = certChain;
	}
	
	public void cleanup() {
		
		validator.shutdown(); 
	}

	public void setClientChain(X509Certificate[] certChain) {
		theChain = certChain;
	}

	public void parse() {
		// noop
		
	}

	public void validate() {
		// noop
		
	}

	public String[] getAllFullyQualifiedAttributes() {
		List<VOMSAttribute> attributes = validator.validate(theChain);
		List<String> allFQANs = new ArrayList<String>();
		
		for (VOMSAttribute a: attributes)
			allFQANs.addAll(a.getFQANs());
		
		return allFQANs.toArray(new String[allFQANs.size()]);
	}

	public List<VOMSAttribute> getVOMSAttributes() {
		List<VOMSAttribute> attributes = validator.validate(theChain);
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
