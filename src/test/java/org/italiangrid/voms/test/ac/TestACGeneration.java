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
package org.italiangrid.voms.test.ac;

import static org.italiangrid.voms.error.VOMSValidationErrorCode.aaCertNotFound;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.canlError;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.invalidAcCert;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.lscDescriptionDoesntMatchAcCert;
import static org.italiangrid.voms.error.VOMSValidationErrorMessage.newErrorMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.operator.OperatorCreationException;
import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.VOMSGenericAttribute;
import org.italiangrid.voms.VOMSValidators;
import org.italiangrid.voms.ac.VOMSACValidator;
import org.italiangrid.voms.ac.VOMSValidationResult;
import org.italiangrid.voms.ac.ValidationResultListener;
import org.italiangrid.voms.ac.impl.VOMSGenericAttributeImpl;
import org.italiangrid.voms.asn1.VOMSACGenerator;
import org.italiangrid.voms.asn1.VOMSACUtils;
import org.italiangrid.voms.error.VOMSValidationErrorMessage;
import org.italiangrid.voms.store.VOMSTrustStore;
import org.italiangrid.voms.store.impl.DefaultVOMSTrustStore;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.emi.security.authn.x509.impl.OpensslCertChainValidator;
import eu.emi.security.authn.x509.impl.PEMCredential;
import eu.emi.security.authn.x509.proxy.ProxyCertificate;
import eu.emi.security.authn.x509.proxy.ProxyCertificateOptions;
import eu.emi.security.authn.x509.proxy.ProxyGenerator;

public class TestACGeneration {

  static final String keyPassword = "pass";

  static final String aaCert = "src/test/resources/certs/test_host_cnaf_infn_it.cert.pem";
  static final String aaKey = "src/test/resources/certs/test_host_cnaf_infn_it.key.pem";

  static final String aaCert2 = "src/test/resources/certs/wilco_cnaf_infn_it.cert.pem";
  static final String aaKey2 = "src/test/resources/certs/wilco_cnaf_infn_it.key.pem";

  static final String expiredCert = "src/test/resources/certs/expired.cert.pem";
  static final String expiredKey = "src/test/resources/certs/expired.key.pem";

  static final String revokedCert = "src/test/resources/certs/revoked.cert.pem";
  static final String revokedKey = "src/test/resources/certs/revoked.key.pem";

  static final String holderCert = "src/test/resources/certs/test0.cert.pem";
  static final String holderKey = "src/test/resources/certs/test0.key.pem";

  static final String defaultVO = "test.vo";
  static final String defaultHost = "test-host.cnaf.infn.it";
  static final int port = 15000;

  static final String vomsdir = "src/test/resources/vomsdir";
  static final String trustAnchorsDir = "src/test/resources/trust-anchors";

  static final List<String> defaultFQANs = Arrays.asList("/test.vo",
    "/test.vo/G1", "/test.vo/G2");

  final List<VOMSGenericAttribute> defaultGAs = Arrays.asList(
    buildGA("test", "value", defaultVO), buildGA("test2", "value", defaultVO));

  static PEMCredential aaCredential = null;
  static PEMCredential aaCredential2 = null;
  static PEMCredential expiredCredential = null;
  static PEMCredential revokedCredential = null;
  static PEMCredential holderCredential = null;

  static VOMSTrustStore trustStore;
  static OpensslCertChainValidator certValidator = null;

  static VOMSValidationErrorMessage expiredCertErrorMessage;
  static VOMSValidationErrorMessage expiredCertCRLErrorMessage;
  static VOMSValidationErrorMessage revokedCertErrorMessage;

  static VOMSACGenerator defaultGenerator;

  @BeforeClass
  static public void classTestSetup() throws KeyStoreException,
    CertificateException, FileNotFoundException, IOException {

    aaCredential = new PEMCredential(new FileInputStream(aaKey),
      new FileInputStream(aaCert), (char[]) null);

    aaCredential2 = new PEMCredential(new FileInputStream(aaKey2),
      new FileInputStream(aaCert2), (char[]) null);

    expiredCredential = new PEMCredential(new FileInputStream(expiredKey),
      new FileInputStream(expiredCert), keyPassword.toCharArray());

    revokedCredential = new PEMCredential(new FileInputStream(revokedKey),
      new FileInputStream(revokedCert), keyPassword.toCharArray());

    holderCredential = new PEMCredential(new FileInputStream(holderKey),
      new FileInputStream(holderCert), keyPassword.toCharArray());

    trustStore = new DefaultVOMSTrustStore(Arrays.asList(vomsdir));
    certValidator = new OpensslCertChainValidator(trustAnchorsDir);

    expiredCertErrorMessage = newErrorMessage(canlError,
      "Certificate has expired on: Fri Dec 02 01:00:00 CET 2011");
    expiredCertCRLErrorMessage = newErrorMessage(
      canlError,
      "CRL for an expired certificate was not resolved Cause: No CRLs found for issuer \"CN=Test CA, O=IGI, C=IT\"");
    revokedCertErrorMessage = newErrorMessage(
      canlError,
      "Certificate was revoked at: Wed Sep 26 17:25:24 CEST 2012, the reason reported is: unspecified");

    defaultGenerator = new VOMSACGenerator(aaCredential);
  }

  @AfterClass
  static public void classTestShutdown() {

    certValidator.dispose();
  }

  private AttributeCertificate createAC(PEMCredential aaCredential,
    List<String> fqans, List<VOMSGenericAttribute> gas, String vo, String host) {

    VOMSACGenerator gen = new VOMSACGenerator(aaCredential);

    Calendar cal = Calendar.getInstance();

    Date now = cal.getTime();
    cal.add(Calendar.HOUR, 12);
    Date expiration = cal.getTime();

    X509AttributeCertificateHolder ac = gen.generateVOMSAttributeCertificate(
      fqans, gas, null, holderCredential.getCertificate(), BigInteger.ONE, now,
      expiration, vo, host, port);

    return ac.toASN1Structure();
  }

  private VOMSGenericAttribute buildGA(String name, String value, String context) {

    VOMSGenericAttributeImpl ga = new VOMSGenericAttributeImpl();

    ga.setName(name);
    ga.setValue(value);
    ga.setContext(context);

    return ga;
  }

  @Test
  public void testGeneratedACParsing() throws KeyStoreException,
    CertificateException, FileNotFoundException, IOException,
    OperatorCreationException {

    AttributeCertificate ac = createAC(aaCredential, defaultFQANs, defaultGAs,
      defaultVO, defaultHost);
    VOMSAttribute attrs = VOMSACUtils.deserializeVOMSAttributes(ac);

    // Check holder
    assertEquals(holderCredential.getCertificate().getSubjectX500Principal(),
      attrs.getHolder());

    // Check holder serial number
    assertEquals(holderCredential.getCertificate().getSerialNumber(),
      attrs.getHolderSerialNumber());

    // Check issuer
    assertEquals(aaCredential.getCertificate().getSubjectX500Principal(),
      attrs.getIssuer());

    // Check policyAuthority
    assertEquals(defaultVO, attrs.getVO());
    assertEquals(defaultHost, attrs.getHost());
    assertEquals(port, attrs.getPort());

    // Check FQANs ordered equality
    for (int i = 0; i < defaultFQANs.size(); i++)
      assertEquals(defaultFQANs.get(i), attrs.getFQANs().get(i));

    // Check GAs ordered equality
    for (int i = 0; i < defaultGAs.size(); i++)
      assertEquals(defaultGAs.get(i), attrs.getGenericAttributes().get(i));

    // Check targets
    assertTrue(attrs.getTargets().isEmpty());

  }

  @Test
  public void testACValidation() {

    ValidationResultChecker c = new ValidationResultChecker(true);

    VOMSACValidator validator = VOMSValidators.newValidator(trustStore,
      certValidator, c);

    AttributeCertificate ac = createAC(aaCredential, defaultFQANs, defaultGAs,
      defaultVO, defaultHost);
    List<AttributeCertificate> validatedAttrs = validator.validateACs(Arrays
      .asList(ac));

    assertEquals(validatedAttrs.size(), 1);

  }

  @Test
  public void testLSCValidationFailure() {

    ValidationResultChecker c = new ValidationResultChecker(false,
      newErrorMessage(lscDescriptionDoesntMatchAcCert),
      newErrorMessage(aaCertNotFound));

    VOMSACValidator validator = VOMSValidators.newValidator(trustStore,
      certValidator, c);
    AttributeCertificate ac = createAC(aaCredential2,
      Arrays.asList("/test.vo.1"), defaultGAs, "test.vo.1",
      "wilco.cnaf.infn.it");
    List<AttributeCertificate> validatedAttrs = validator.validateACs(Arrays
      .asList(ac));
    assertEquals(validatedAttrs.size(), 0);
  }

  @Test
  public void testExpiredAACertValidationFailure()
    throws OperatorCreationException {

    ValidationResultChecker c = new ValidationResultChecker(false,
      expiredCertErrorMessage, expiredCertCRLErrorMessage,
      newErrorMessage(invalidAcCert),
      newErrorMessage(aaCertNotFound));

    VOMSACValidator validator = VOMSValidators.newValidator(trustStore,
      certValidator, c);

    AttributeCertificate ac = createAC(expiredCredential,
      Arrays.asList("/test.vo.1"), defaultGAs, defaultVO,
      "test-expired.cnaf.infn.it");

    List<AttributeCertificate> validatedAttrs = validator.validateACs(Arrays
      .asList(ac));
    assertEquals(validatedAttrs.size(), 0);
  }

  @Test
  public void testRevokedAACertValidationFailure() {

    ValidationResultChecker c = new ValidationResultChecker(false,
      revokedCertErrorMessage, newErrorMessage(invalidAcCert),
      newErrorMessage(aaCertNotFound));

    VOMSACValidator validator = VOMSValidators.newValidator(trustStore,
      certValidator, c);
    AttributeCertificate ac = createAC(revokedCredential,
      Arrays.asList("/test.vo.1"), defaultGAs, defaultVO,
      "test-revoked.cnaf.infn.it");
    List<AttributeCertificate> validatedAttrs = validator.validateACs(Arrays
      .asList(ac));
    assertEquals(validatedAttrs.size(), 0);
  }

  @Test
  public void testSuccesfullACExtractionFromProxy() {

    ValidationResultChecker c = new ValidationResultChecker(true);

    VOMSACValidator validator = VOMSValidators.newValidator(trustStore,
      certValidator, c);

    AttributeCertificate ac = createAC(aaCredential, defaultFQANs, defaultGAs,
      defaultVO, defaultHost);

    X509Certificate[] chain;

    try {
      chain = createVOMSProxy(holderCredential,
        new AttributeCertificate[] { ac });
    } catch (Exception e) {
      throw new VOMSError("Error generating VOMS proxy:" + e.getMessage(), e);
    }

    List<VOMSAttribute> attrs = validator.validate(chain);
    assertEquals(1, attrs.size());
  }

  private X509Certificate[] createVOMSProxy(PEMCredential holder,
    AttributeCertificate[] acs) throws InvalidKeyException,
    CertificateParsingException, SignatureException, NoSuchAlgorithmException,
    IOException {

    ProxyCertificateOptions proxyOptions = new ProxyCertificateOptions(
      holder.getCertificateChain());

    proxyOptions.setAttributeCertificates(acs);
    ProxyCertificate proxy = ProxyGenerator.generate(proxyOptions,
      holder.getKey());

    return proxy.getCertificateChain();
  }
}

class ValidationResultChecker implements ValidationResultListener {

  final List<VOMSValidationErrorMessage> expectedErrorMessages;
  boolean expectedValidationResult;

  public ValidationResultChecker(boolean valid,
    VOMSValidationErrorMessage... expectedMessages) {

    expectedValidationResult = valid;
    expectedErrorMessages = Arrays.asList(expectedMessages);
    
  }

  private String errorMessage(String message, VOMSValidationResult result){
    return String.format("%s. VOMSValidationResult: <%s>", message, result);
  }
  
  public void notifyValidationResult(VOMSValidationResult result) {

    assertEquals(errorMessage("ValidationResult validity check failed.", result),
      expectedValidationResult, result.isValid());

    assertEquals(errorMessage("ValidationResult error message size check "
      + "failed.", result),
      expectedErrorMessages.size(), result.getValidationErrors().size());
    
    List<VOMSValidationErrorMessage> errorMessages = 
      new ArrayList<VOMSValidationErrorMessage>(result.getValidationErrors());
    
    for (VOMSValidationErrorMessage expectedMessage : expectedErrorMessages) {

      String failureMessage = errorMessage(String.format(
        "<%s> was not found in error messages. Error messages: <%s>",
        expectedMessage, result.getValidationErrors()), result);

      assertTrue(failureMessage,
        result.getValidationErrors().contains(expectedMessage));
    }
    
    if (errorMessages.size() > 0){
      errorMessages.removeAll(expectedErrorMessages);
    
      assertTrue(errorMessage("ValidationResult check failed. "
        + "Got more error messages than expected.",result),
        errorMessages.isEmpty());
    }
      
  }
}