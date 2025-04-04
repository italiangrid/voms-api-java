// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

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
