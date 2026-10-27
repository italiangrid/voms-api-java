// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.emi.security.authn.x509.NamespaceCheckingMode;
import eu.emi.security.authn.x509.ValidationResult;
import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.helpers.trust.OpensslTruststoreHelper;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CertificatePolicies;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.italiangrid.voms.util.CertificateValidatorBuilder;
import org.italiangrid.voms.util.CertificateValidatorBuilder.OpensslHashFunction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Regression test for CANL's PKIXPolicyNode compatibility with Bouncy Castle 1.86. */
class TestPkixPolicyNodeCompatibility {

  private static final String TEST_POLICY = "2.16.840.1.101.3.2.1.48.1";
  private static final AtomicLong SERIAL_NUMBER = new AtomicLong(System.currentTimeMillis());

  @TempDir Path trustAnchorsDir;

  @Test
  void validatesChainContainingCertificatePolicies() throws Exception {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
      Security.addProvider(new BouncyCastleProvider());
    }

    KeyPair rootKey = newRsaKeyPair();
    KeyPair intermediateKey = newRsaKeyPair();
    KeyPair endEntityKey = newRsaKeyPair();

    X500Name rootName = new X500Name("CN=Policy Node Test Root");
    X500Name intermediateName = new X500Name("CN=Policy Node Test CA");
    X500Name endEntityName = new X500Name("CN=Policy Node Test User");

    X509Certificate root =
        issueCertificate(rootName, rootKey.getPublic(), rootName, rootKey, true, null);
    X509Certificate intermediate =
        issueCertificate(
            intermediateName, intermediateKey.getPublic(), rootName, rootKey, true, TEST_POLICY);
    X509Certificate endEntity =
        issueCertificate(
            endEntityName,
            endEntityKey.getPublic(),
            intermediateName,
            intermediateKey,
            false,
            TEST_POLICY);

    writeTrustAnchor(root);

    X509CertChainValidatorExt validator =
        new CertificateValidatorBuilder()
            .trustAnchorsDir(trustAnchorsDir.toString())
            .opensslHashFunction(OpensslHashFunction.SHA1)
            .namespaceChecks(NamespaceCheckingMode.IGNORE)
            .build();

    try {
      ValidationResult result = validator.validate(new X509Certificate[] {endEntity, intermediate});
      assertTrue(result.isValid(), result.toString());
    } finally {
      validator.dispose();
    }
  }

  private static KeyPair newRsaKeyPair() throws Exception {
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(2048);
    return generator.generateKeyPair();
  }

  private static X509Certificate issueCertificate(
      X500Name subject,
      PublicKey subjectKey,
      X500Name issuer,
      KeyPair issuerKey,
      boolean ca,
      String certificatePolicy)
      throws Exception {
    long now = System.currentTimeMillis();
    JcaX509v3CertificateBuilder builder =
        new JcaX509v3CertificateBuilder(
            issuer,
            BigInteger.valueOf(SERIAL_NUMBER.incrementAndGet()),
            new Date(now - 60_000),
            new Date(now + 86_400_000),
            subject,
            subjectKey);

    builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(ca));
    builder.addExtension(
        Extension.keyUsage,
        true,
        new KeyUsage(ca ? KeyUsage.keyCertSign | KeyUsage.cRLSign : KeyUsage.digitalSignature));

    if (certificatePolicy != null) {
      builder.addExtension(
          Extension.certificatePolicies,
          false,
          new CertificatePolicies(
              new PolicyInformation(new ASN1ObjectIdentifier(certificatePolicy))));
    }

    return new JcaX509CertificateConverter()
        .setProvider(BouncyCastleProvider.PROVIDER_NAME)
        .getCertificate(
            builder.build(
                new JcaContentSignerBuilder("SHA256withRSA")
                    .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                    .build(issuerKey.getPrivate())));
  }

  private void writeTrustAnchor(X509Certificate root) throws Exception {
    String hash = OpensslTruststoreHelper.getOpenSSLCAHash(root.getSubjectX500Principal(), true);
    Path trustAnchorFile = trustAnchorsDir.resolve(hash + ".0");

    try (Writer writer = Files.newBufferedWriter(trustAnchorFile, StandardCharsets.US_ASCII);
        JcaPEMWriter pemWriter = new JcaPEMWriter(writer)) {
      pemWriter.writeObject(root);
    }
  }
}
