package org.italiangrid.voms.asn1;

import org.italiangrid.voms.VOMSGenericAttribute;

/**
 * 
 * @author andreaceccanti
 *
 */
public class VOMSGenericAttributeImpl implements VOMSGenericAttribute{

	private String name;
	private String value;
	private String context;

	public VOMSGenericAttributeImpl() {
		
	}
	
	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getContext() {
		return context;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setContext(String context) {
		this.context = context;
	}

	@Override
	public String toString() {
		return "VOMSGenericAttribute [name=" + name + ", value=" + value
				+ ", context=" + context + "]";
	}
	
	
}
