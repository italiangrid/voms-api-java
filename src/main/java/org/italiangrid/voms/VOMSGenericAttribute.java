// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms;

/**
 * A VOMS generic attribute is a name=value pair attribute augmented with a
 * context.
 * 
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSGenericAttribute {

  /**
   * This method returns the name of this generic attribute
   * 
   * @return the name of this generic attribute
   */
  public String getName();

  /**
   * This method returns the value of this generic attribute
   * 
   * @return the value of this generic attribute
   */
  public String getValue();

  /**
   * This method returns the context for this generic attribute
   * 
   * @return the context of this generic attribute
   */
  public String getContext();

}
