// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test.cred;

import static org.junit.jupiter.api.Assertions.assertEquals;

import eu.emi.security.authn.x509.proxy.ProxyCertificate;
import eu.emi.security.authn.x509.proxy.ProxyCertificateOptions;
import eu.emi.security.authn.x509.proxy.ProxyGenerator;
import eu.emi.security.authn.x509.proxy.ProxyType;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.time.Instant;
import java.util.Date;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/** Regression coverage for italiangrid/voms-clients#53. */
public class TestECDSAProxyDelegation {

  @BeforeAll
  static void installProvider() {
    if (Security.getProvider("BC") == null) {
      Security.addProvider(new BouncyCastleProvider());
    }
  }

  @Test
  void ecCredentialCanIssueRsaProxy() throws Exception {
    Fixture fixture = new Fixture("EC");
    ProxyCertificate proxy = delegate(fixture.chain, fixture.user.getPrivate());
    assertEquals("RSA", proxy.getPrivateKey().getAlgorithm());
    assertLeaf(proxy, fixture.chain, "RSA", "1.2.840.10045.4.3.2");
  }

  @Test
  @Disabled("Blocked by CANL RSA/ECDSA signature selection bug; see eu-emi/canl-java#129")
  void rsaProxySignedWithEcdsaCanDelegateAgain() throws Exception {
    Fixture fixture = new Fixture("EC");
    ProxyCertificate first = delegate(fixture.chain, fixture.user.getPrivate());
    assertLeaf(first, fixture.chain, "RSA", "1.2.840.10045.4.3.2");

    // The first proxy's key is RSA, even though its issuer signed it with ECDSA.
    ProxyCertificate second = delegate(first.getCertificateChain(), first.getPrivateKey());
    assertLeaf(second, first.getCertificateChain(), "RSA", "1.2.840.113549.1.1.11");
  }

  @Test
  @Disabled("Blocked by CANL RSA/ECDSA signature selection bug; see eu-emi/canl-java#129")
  void rsaEndEntitySignedByEcCaCanIssueProxy() throws Exception {
    Fixture fixture = new Fixture("RSA");
    assertEquals("RSA", fixture.chain[0].getPublicKey().getAlgorithm());
    assertEquals("1.2.840.10045.4.3.2", fixture.chain[0].getSigAlgOID());
    ProxyCertificate proxy = delegate(fixture.chain, fixture.user.getPrivate());
    assertLeaf(proxy, fixture.chain, "RSA", "1.2.840.113549.1.1.11");
  }

  private static ProxyCertificate delegate(X509Certificate[] chain, PrivateKey key)
      throws Exception {
    ProxyCertificateOptions options = new ProxyCertificateOptions(chain);
    options.setType(ProxyType.RFC3820);
    options.setKeyLength(2048);
    options.setLifetime(3600);
    return ProxyGenerator.generate(options, key);
  }

  private static void assertLeaf(
      ProxyCertificate proxy, X509Certificate[] parent, String keyAlgorithm, String signatureOid)
      throws Exception {
    X509Certificate[] chain = proxy.getCertificateChain();
    assertEquals(parent.length + 1, chain.length);
    assertEquals(keyAlgorithm, chain[0].getPublicKey().getAlgorithm());
    assertEquals(signatureOid, chain[0].getSigAlgOID());
    assertEquals(parent[0].getSubjectX500Principal(), chain[0].getIssuerX500Principal());
    chain[0].checkValidity();
    chain[0].verify(parent[0].getPublicKey());
    for (int i = 0; i < parent.length; i++) {
      assertEquals(parent[i], chain[i + 1]);
    }
  }

  private static KeyPair keyPair(String algorithm) throws Exception {
    KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm, "BC");
    if ("EC".equals(algorithm)) {
      generator.initialize(new ECGenParameterSpec("secp256r1"));
    } else {
      generator.initialize(2048);
    }
    return generator.generateKeyPair();
  }

  private static X509Certificate certificate(
      X500Name subject, KeyPair subjectKey, X500Name issuer, KeyPair issuerKey, boolean ca)
      throws Exception {
    Instant now = Instant.now();
    JcaX509v3CertificateBuilder builder =
        new JcaX509v3CertificateBuilder(
            issuer,
            ca ? BigInteger.ONE : BigInteger.valueOf(2),
            Date.from(now.minusSeconds(300)),
            Date.from(now.plusSeconds(86400)),
            subject,
            subjectKey.getPublic());
    builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(ca));
    builder.addExtension(
        Extension.keyUsage,
        true,
        new KeyUsage(ca ? KeyUsage.keyCertSign | KeyUsage.cRLSign : KeyUsage.digitalSignature));
    X509Certificate certificate =
        new JcaX509CertificateConverter()
            .setProvider("BC")
            .getCertificate(
                builder.build(
                    new JcaContentSignerBuilder("SHA256withECDSA")
                        .setProvider("BC")
                        .build(issuerKey.getPrivate())));
    certificate.verify(issuerKey.getPublic());
    return certificate;
  }

  private static class Fixture {
    final KeyPair user;
    final X509Certificate[] chain;

    Fixture(String userAlgorithm) throws Exception {
      KeyPair ca = keyPair("EC");
      user = keyPair(userAlgorithm);
      X500Name caName = new X500Name("CN=ECDSA test CA");
      X509Certificate caCertificate = certificate(caName, ca, caName, ca, true);
      X509Certificate userCertificate =
          certificate(new X500Name("CN=Delegation test user"), user, caName, ca, false);
      chain = new X509Certificate[] {userCertificate, caCertificate};
    }
  }
}
