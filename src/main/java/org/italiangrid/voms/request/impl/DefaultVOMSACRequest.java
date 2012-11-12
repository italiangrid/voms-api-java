package org.italiangrid.voms.request.impl;

import java.util.ArrayList;
import java.util.List;

import org.italiangrid.voms.request.VOMSACRequest;

/**
 * The default implementation for a {@link VOMSACRequest}.
 * 
 * @author Valerio Venturi
 *
 */
public class DefaultVOMSACRequest implements VOMSACRequest {

  private int lifetime;
  
  private List<String> requestedFQANs;
  
  private List<String> targets;

  private String voName;
  
  public int getLifetime() {

    return lifetime;
  }

  public void setLifetime(int lifetime) {

    this.lifetime = lifetime;
  }
  
  public List<String> getRequestedFQANs() {
    
    if(requestedFQANs == null)
      requestedFQANs = new ArrayList<String>();
    
    return requestedFQANs;
  }

  public void setRequestedFQANs(List<String> requestedFQANs) {

    this.requestedFQANs = requestedFQANs;
  }

  public void setTargets(List<String> targets) {

    this.targets = targets;
  }

  public List<String> getTargets() {
    
    if(targets == null)
      targets = new ArrayList<String>();
    
    return targets;
  }

  public void setVoName(String voName) {

    this.voName = voName;
  }

  public String getVoName() {
    
    return voName;
  }

}
