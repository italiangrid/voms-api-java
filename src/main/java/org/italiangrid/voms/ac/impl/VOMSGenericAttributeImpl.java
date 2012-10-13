package org.italiangrid.voms.ac.impl;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VOMSGenericAttributeImpl other = (VOMSGenericAttributeImpl) obj;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
