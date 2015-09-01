/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2014.
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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.italiangrid.voms.request.VOMSACRequest;

/**
 * The default implementation for a {@link VOMSACRequest}.
 * 
 * @author Valerio Venturi
 * 
 */
public class DefaultVOMSACRequest implements VOMSACRequest {

  public static final int DEFAULT_LIFETIME = (int) TimeUnit.HOURS.toSeconds(12);

  private int lifetime;

  private List<String> requestedFQANs;

  private List<String> targets;

  private String voName;

  public int getLifetime() {

    return lifetime;
  }

  public List<String> getRequestedFQANs() {

    return requestedFQANs;
  }

  public List<String> getTargets() {

    return targets;
  }

  public String getVoName() {

    return voName;
  }

  private DefaultVOMSACRequest(Builder b) {

    this.lifetime = b.lifetime;
    this.voName = b.voName;
    this.targets = b.targets;
    this.requestedFQANs = b.requestedFQANs;
  }

  public static class Builder {

    private int lifetime = DEFAULT_LIFETIME;

    private List<String> requestedFQANs = Collections.emptyList();

    private List<String> targets = Collections.emptyList();

    private String voName;

    public Builder(String voName) {

      this.voName = voName;
    }

    public Builder lifetime(int l) {

      this.lifetime = l;
      return this;
    }

    public Builder fqans(List<String> fqans) {

      if (fqans != null)
        this.requestedFQANs = fqans;
      return this;
    }

    public Builder targets(List<String> targets) {

      if (targets != null)
        this.targets = targets;
      return this;
    }

    public DefaultVOMSACRequest build() {

      return new DefaultVOMSACRequest(this);

    }
  }

}
