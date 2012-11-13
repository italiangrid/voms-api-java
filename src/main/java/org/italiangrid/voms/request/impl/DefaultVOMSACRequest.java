/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2012.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
