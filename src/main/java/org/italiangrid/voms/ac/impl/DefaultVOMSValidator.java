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
package org.italiangrid.voms.ac.impl;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.ac.VOMSACLookupStrategy;
import org.italiangrid.voms.ac.VOMSACValidationStrategy;
import org.italiangrid.voms.ac.VOMSACValidator;
import org.italiangrid.voms.ac.VOMSValidationResult;
import org.italiangrid.voms.ac.ValidationResultListener;
import org.italiangrid.voms.asn1.VOMSACUtils;
import org.italiangrid.voms.store.UpdatingVOMSTrustStore;
import org.italiangrid.voms.store.VOMSTrustStore;
import org.italiangrid.voms.store.VOMSTrustStores;
import org.italiangrid.voms.util.CertificateValidatorBuilder;
import org.italiangrid.voms.util.NullListener;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;

/**
 * The default implementation of the VOMS validator.
 * 
 * @author andreaceccanti
 *
 */
public class DefaultVOMSValidator extends DefaultVOMSACParser implements
  VOMSACValidator {

  public static final String DEFAULT_TRUST_ANCHORS_DIR = "/etc/grid-security/certificates";

  private final VOMSACValidationStrategy validationStrategy;
  private final VOMSTrustStore trustStore;
  private ValidationResultListener validationResultListener;
  private final Object listenerLock = new Object();

  public static class Builder {

    private VOMSACValidationStrategy validationStrategy;
    private VOMSTrustStore trustStore;
    private ValidationResultListener validationResultListener;

    private X509CertChainValidatorExt certChainValidator;
    private VOMSACLookupStrategy acLookupStrategy;

    public Builder() {

    }

    public Builder validationStrategy(VOMSACValidationStrategy s) {

      this.validationStrategy = s;
      return this;
    }

    public Builder trustStore(VOMSTrustStore ts) {

      this.trustStore = ts;
      return this;
    }

    public Builder validationListener(ValidationResultListener l) {

      this.validationResultListener = l;
      return this;
    }

    public Builder certChainValidator(X509CertChainValidatorExt v) {

      this.certChainValidator = v;
      return this;
    }

    public Builder acLookupStrategy(VOMSACLookupStrategy ls) {

      this.acLookupStrategy = ls;
      return this;
    }

    private void sanityChecks() {

      if (validationStrategy == null) {
        if (trustStore == null)
          trustStore = VOMSTrustStores.newTrustStore();

        if (certChainValidator == null)
          certChainValidator = new CertificateValidatorBuilder()
            .trustAnchorsDir(DEFAULT_TRUST_ANCHORS_DIR).build();

        validationStrategy = new DefaultVOMSValidationStrategy(trustStore,
          certChainValidator);
      }

      if (validationResultListener == null)
        validationResultListener = NullListener.INSTANCE;

      if (acLookupStrategy == null)
        acLookupStrategy = new LeafACLookupStrategy();
    }

    public DefaultVOMSValidator build() {

      sanityChecks();
      return new DefaultVOMSValidator(this);
    }
  }

  private DefaultVOMSValidator(Builder b) {

    super(b.acLookupStrategy);

    this.validationStrategy = b.validationStrategy;
    this.trustStore = b.trustStore;
    this.validationResultListener = b.validationResultListener;
  }

  public List<VOMSValidationResult> validateWithResult(
    X509Certificate[] validatedChain) {

    return internalValidate(validatedChain);
  }

  protected List<VOMSValidationResult> internalValidate(
    X509Certificate[] validatedChain) {

    List<VOMSAttribute> parsedAttrs = parse(validatedChain);
    List<VOMSValidationResult> results = new ArrayList<VOMSValidationResult>();

    for (VOMSAttribute a : parsedAttrs) {

      VOMSValidationResult result = validationStrategy.validateAC(a,
        validatedChain);

      synchronized (listenerLock) {
        validationResultListener.notifyValidationResult(result);
      }

      results.add(result);

    }

    return results;
  }

  public List<VOMSAttribute> validate(X509Certificate[] validatedChain) {

    List<VOMSAttribute> validAttributes = new ArrayList<VOMSAttribute>();
    for (VOMSValidationResult result : internalValidate(validatedChain)) {
      if (result.isValid())
        validAttributes.add(result.getAttributes());
    }
    return validAttributes;
  }

  public void shutdown() {

    if (trustStore instanceof UpdatingVOMSTrustStore)
      ((UpdatingVOMSTrustStore) trustStore).cancel();
  }

  public List<AttributeCertificate> validateACs(List<AttributeCertificate> acs) {

    List<AttributeCertificate> validatedAcs = new ArrayList<AttributeCertificate>();

    for (AttributeCertificate ac : acs) {

      VOMSAttribute vomsAttrs = VOMSACUtils.deserializeVOMSAttributes(ac);

      VOMSValidationResult result = validationStrategy.validateAC(vomsAttrs);

      synchronized (listenerLock) {
        validationResultListener.notifyValidationResult(result);
      }

      if (result.isValid())
        validatedAcs.add(ac);
    }

    return validatedAcs;
  }

  public void setValidationResultListener(ValidationResultListener listener) {

    synchronized (listenerLock) {
      if (listener != null)
        this.validationResultListener = listener;
    }
  }
}
