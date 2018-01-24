/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.italiangrid.voms.request.impl;

import static org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties.AA_CERT;
import static org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties.AA_KEY;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.asn1.VOMSACGenerator;
import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSACService;
import org.italiangrid.voms.request.VOMSRequestListener;
import org.italiangrid.voms.request.VOMSServerInfo;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.impl.PEMCredential;

public class FakeVOMSACService implements VOMSACService {

  final VOMSACGenerator acGenerator;
  final VOMSRequestListener listener;

  ACGenerationParams acParams;

  long acSerialNumber = 0;

  private FakeVOMSACService(X509Credential aaCredential, ACGenerationParams acParams,
      VOMSRequestListener listener) {
    this.acGenerator = new VOMSACGenerator(aaCredential);
    this.listener = listener;
    this.acParams = acParams;
  }

  private VOMSServerInfo buildFakeServerInfo(String vo, String host, int port)
      throws URISyntaxException {
    DefaultVOMSServerInfo info = new DefaultVOMSServerInfo();
    info.setAlias("Fake local VOMS server");
    info.setVoName(vo);

    String vomsUri = String.format("voms://%s:%d", host, port);
    info.setURL(new URI(vomsUri));

    return info;
  }

  protected X509AttributeCertificateHolder buildAC(X509Credential credential,
      VOMSACRequest request) {


    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();

    List<String> fqans = request.getRequestedFQANs();
    if (Objects.isNull(fqans) || fqans.isEmpty()) {
      fqans = acParams.getFqans();
    }

    String vo = request.getVoName();
    if (Objects.isNull(vo)) {
      vo = acParams.getVo();
    }

    BigInteger serialNo = acParams.getSerialNo();
    if (Objects.isNull(serialNo)) {
      serialNo = BigInteger.valueOf(acSerialNumber++);
    }

    Date notBefore = acParams.getNotBefore();
    if (Objects.isNull(notBefore)) {
      notBefore = now;
    }


    Date notAfter = acParams.getNotAfter();
    if (Objects.isNull(notAfter)) {
      cal.add(Calendar.SECOND, request.getLifetime());
      notAfter = cal.getTime();
    }

    try {

      VOMSServerInfo fakeEndpoint = buildFakeServerInfo(vo, acParams.getHost(), acParams.getPort());

      listener.notifyVOMSRequestStart(request, fakeEndpoint);
      X509AttributeCertificateHolder ac = acGenerator.generateVOMSAttributeCertificate(fqans,
          acParams.getGas(), request.getTargets(), credential.getCertificate(), serialNo, notBefore,
          notAfter, vo, acParams.getHost(), acParams.getPort());

      listener.notifyVOMSRequestSuccess(request, fakeEndpoint);

      return ac;
    } catch (URISyntaxException e) {
      throw new VOMSError(e.getMessage(), e);
    }

  }

  @Override
  public AttributeCertificate getVOMSAttributeCertificate(X509Credential credential,
      VOMSACRequest request) {

    X509AttributeCertificateHolder acHolder = buildAC(credential, request);
    return acHolder.toASN1Structure();


  }

  public ACGenerationParams getAcParams() {
    return acParams;
  }

  public void setAcParams(ACGenerationParams acParams) {
    this.acParams = acParams;
  }

  public long getAcSerialNumber() {
    return acSerialNumber;
  }

  public void setAcSerialNumber(long acSerialNumber) {
    this.acSerialNumber = acSerialNumber;
  }

  public static FakeVOMSACService newInstance(X509Credential aaCredential,
      ACGenerationParams params, VOMSRequestListener listener) {
    return new FakeVOMSACService(aaCredential, params, listener);
  }

  public static FakeVOMSACService newInstanceFromProperties(VOMSRequestListener listener) {
    String aaCert = AA_CERT.getSystemPropertyValue();
    String aaKey = AA_KEY.getSystemPropertyValue();

    if (Objects.isNull(aaCert)) {
      throw new VOMSError(
          String.format("%s is not set", FakeVOMSACServiceProperties.AA_CERT.name()));
    }

    if (Objects.isNull(aaKey)) {
      throw new VOMSError(
          String.format("%s is not set", FakeVOMSACServiceProperties.AA_KEY.name()));
    }

    try {
      PEMCredential aaCredential = new PEMCredential(aaKey, aaCert, null);
      ACGenerationParams acParams = ACGenerationParams.fromSystemProperties();
      return new FakeVOMSACService(aaCredential, acParams, listener);
    } catch (Exception e) {

      String errorMsg =
          String.format("Error loading VOMS fake AC AA credential from '%s' and '%s': %s", aaKey,
              aaCert, e.getMessage());

      throw new VOMSError(errorMsg, e);
    }
  }
}
