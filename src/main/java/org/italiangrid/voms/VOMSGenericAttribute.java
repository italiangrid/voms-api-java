package org.italiangrid.voms;

/**
 * A VOMS generic attribute is a name=value pair attribute augmented with a context.
 * 
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSGenericAttribute {

	/**
	 * Returns the name of this generic attribute
	 * @return
	 */
	public String getName();
	
	/**
	 * Returns the value of this generic attribute
	 * @return
	 */
	public String getValue();
	
	/**
	 * Returns the context of this generic attribute
	 * @return
	 */
	public String getContext();
	
}
