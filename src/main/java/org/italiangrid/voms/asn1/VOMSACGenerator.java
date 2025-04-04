// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.asn1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v2AttributeCertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.VOMSGenericAttribute;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.proxy.CertificateExtension;

/**
 * A generator for VOMS Attribute Certificates (ACs).
 * <p>
 * This class provides methods for creating VOMS ACs with customizable properties, including
 * optional extensions and fake signature bits for testing purposes.
 * </p>
 * 
 * <p>
 * It uses BouncyCastle for cryptographic operations and supports various extensions required for
 * VOMS attribute certificates.
 * </p>
 * 
 */
public class VOMSACGenerator implements VOMSConstants {

  /**
   * Enumeration defining various properties that can influence the generation of VOMS Attribute
   * Certificates.
   */
  public enum ACGenerationProperties {

    // @formatting off

    /**
     * Skips the inclusion of the AC Certs extension in the generated Attribute Certificate.
     * <p>
     * This extension normally contains the issuer's certificate chain, which may be omitted
     * if the relying party already possesses it.
     * </p>
     */
    SKIP_AC_CERTS_EXTENSION,

    /**
     * Generates fake signature bits instead of signing the certificate with a real key.
     * <p>
     * This is primarily used for testing purposes, as the resulting AC will not be verifiable.
     * </p>
     */
    FAKE_SIGNATURE_BITS,

    /**
     * Includes a fake critical extension in the generated Attribute Certificate.
     * <p>
     * This extension is added for testing scenarios where certificate parsers need to handle
     * unknown critical extensions.
     * </p>
     */
    INCLUDE_FAKE_CRITICAL_EXTENSION,

    /**
     * Includes the "No Revocation Available" extension as a critical extension.
     * <p>
     * This extension indicates that no revocation information is available for the AC.
     * </p>
     */
    INCLUDE_CRITICAL_NO_REV_AVAIL_EXTENSION,

    /**
     * Includes the Authority Key Identifier (AKID) extension as a critical extension.
     * <p>
     * The AKID extension helps in linking the AC to its issuer, making it easier for
     * verification systems to locate the issuing certificate.
     * </p>
     */
    INCLUDE_CRITICAL_AKID_EXTENSION,

    /**
     * Includes an empty AC Certs extension in the generated Attribute Certificate.
     * <p>
     * This is useful for testing scenarios where the extension is expected but contains no
     * actual certificate information.
     * </p>
     */
    INCLUDE_EMPTY_AC_CERTS_EXTENSION;
    // @formatting on
  }

  /** Default generation properties (none enabled). */
  public static final EnumSet<ACGenerationProperties> defaultGenerationProperties =
      EnumSet.noneOf(ACGenerationProperties.class);

  /**
   * A ContentSigner implementation that generates random signature bits.
   * <p>
   * This is used for testing purposes to create attribute certificates with fake signatures.
   * </p>
   */
  static class RandomContentSigner implements ContentSigner {

    /** The length of the randomly generated signature. */
    public static final int SIG_LENGHT = 1024;

    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    AlgorithmIdentifier sigAlgId;

    /**
     * Constructs a RandomContentSigner with the given signature algorithm name.
     *
     * @param sigAlgName the name of the signature algorithm
     */
    public RandomContentSigner(String sigAlgName) {

      this.sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find(sigAlgName);
    }

    @Override
    public AlgorithmIdentifier getAlgorithmIdentifier() {

      return sigAlgId;
    }

    @Override
    public OutputStream getOutputStream() {

      return bos;
    }

    @Override
    public byte[] getSignature() {

      try {
        bos.close();
      } catch (IOException e) {
        // Ignore
      }

      Random r = new Random();

      byte[] sigBytes = new byte[SIG_LENGHT];
      r.nextBytes(sigBytes);

      return sigBytes;
    }

  }

  /** Fake extension OID used in testing. */
  public static final ASN1ObjectIdentifier FAKE_EXT_OID =
      new ASN1ObjectIdentifier("1.3.6.1.4.1.8005.100.120.82");

  private X509Credential aaCredential;
  private ContentSigner signer;

  /**
   * Retrieves the appropriate ContentSigner based on the provided properties.
   *
   * @param properties the properties influencing AC generation
   * @return a ContentSigner instance
   * @throws VOMSError if an error occurs during signer creation
   */
  private ContentSigner getSigner(EnumSet<ACGenerationProperties> properties) {

    if (signer == null) {

      JcaContentSignerBuilder builder =
          new JcaContentSignerBuilder(aaCredential.getCertificate().getSigAlgName());

      builder.setProvider(BouncyCastleProvider.PROVIDER_NAME);
      try {

        if (properties.contains(ACGenerationProperties.FAKE_SIGNATURE_BITS)) {
          signer = new RandomContentSigner(aaCredential.getCertificate().getSigAlgName());
        } else {
          signer = builder.build(aaCredential.getKey());
        }

      } catch (OperatorCreationException e) {
        throw new VOMSError(e.getMessage(), e);
      }
    }
    return signer;
  }

  /**
   * Constructs a VOMSACGenerator with the given credential.
   *
   * @param aaCredential the attribute authority credential
   */
  public VOMSACGenerator(X509Credential aaCredential) {

    this.aaCredential = aaCredential;
  }

  /**
   * Builds a VOMS URI.
   *
   * @param voName the VO name
   * @param host the host name
   * @param port the port number
   * @return a formatted VOMS URI
   */
  private String buildVOURI(String voName, String host, int port) {

    return String.format("%s://%s:%d", voName, host, port);
  }

  private ASN1Encodable buildACCertsExtensionContent(EnumSet<ACGenerationProperties> properties) {

    ASN1EncodableVector issuerCertsContainer = new ASN1EncodableVector();

    if (properties.contains(ACGenerationProperties.INCLUDE_EMPTY_AC_CERTS_EXTENSION))
      issuerCertsContainer.add(new DERSequence());
    else
      issuerCertsContainer
        .add(new DERSequence(getCertAsDEREncodable(aaCredential.getCertificate())));

    return new DERSequence(issuerCertsContainer);
  }

  private AuthorityKeyIdentifier buildAuthorityKeyIdentifier()
      throws CertificateEncodingException, NoSuchAlgorithmException {

    X509CertificateHolder holder = new JcaX509CertificateHolder(aaCredential.getCertificate());
    JcaX509ExtensionUtils utils = new JcaX509ExtensionUtils();

    return utils.createAuthorityKeyIdentifier(holder.getSubjectPublicKeyInfo());

  }

  private ASN1Encodable buildFQANsAttributeContent(List<String> fqans,
      GeneralName policyAuthorityInfo) {

    ASN1EncodableVector container = new ASN1EncodableVector();
    ASN1EncodableVector encodedFQANs = new ASN1EncodableVector();

    // Policy authority info
    DERTaggedObject pai = new DERTaggedObject(0, policyAuthorityInfo);
    container.add(pai);

    for (String s : fqans)
      encodedFQANs.add(new DEROctetString(s.getBytes()));

    container.add(new DERSequence(encodedFQANs));

    return new DERSequence(container);
  }

  private ASN1Encodable buildGAExtensionContent(EnumSet<ACGenerationProperties> properties,
      List<VOMSGenericAttribute> gas, GeneralName policyAuthorityInfo) {

    ASN1EncodableVector tagContainer = new ASN1EncodableVector();
    ASN1EncodableVector tagSequences = new ASN1EncodableVector();

    for (VOMSGenericAttribute a : gas) {
      tagSequences.add(buildTagSequence(a));
    }

    tagContainer.add(new GeneralNames(policyAuthorityInfo));
    tagContainer.add(new DERSequence(tagSequences));

    DERSequence finalSequence;

    // We wrap this three times as VOMS core does, even if I think this
    // is a bug
    finalSequence = new DERSequence(new DERSequence(new DERSequence(tagContainer)));

    return finalSequence;
  }

  private AttributeCertificateHolder buildHolder(X509Certificate holderCert)
      throws CertificateEncodingException {

    JcaX509CertificateHolder holderWrappedCert = new JcaX509CertificateHolder(holderCert);

    return new AttributeCertificateHolder(holderWrappedCert.getSubject(),
        holderCert.getSerialNumber());
  }

  private AttributeCertificateIssuer buildIssuer() throws CertificateEncodingException {

    JcaX509CertificateHolder issuer = new JcaX509CertificateHolder(aaCredential.getCertificate());
    return new AttributeCertificateIssuer(issuer.getSubject());
  }

  private GeneralName buildPolicyAuthorityInfo(String voName, String host, int port) {

    return new GeneralName(GeneralName.uniformResourceIdentifier, buildVOURI(voName, host, port));
  }

  private DERSequence buildTagSequence(VOMSGenericAttribute ga) {

    ASN1EncodableVector tagSequence = new ASN1EncodableVector();

    tagSequence.add(getDEROctetString(ga.getName()));
    tagSequence.add(getDEROctetString(ga.getValue()));
    tagSequence.add(getDEROctetString(ga.getContext()));

    return new DERSequence(tagSequence);

  }

  private ASN1Encodable buildTargetsExtensionContent(EnumSet<ACGenerationProperties> properties,
      List<String> targets) {

    ASN1EncodableVector targetSeq = new ASN1EncodableVector();

    for (String s : targets) {

      DERTaggedObject encodedTarget =
          new DERTaggedObject(0, new GeneralName(GeneralName.uniformResourceIdentifier, s));

      // We wrap the target in another sequence as the old VOMS does
      targetSeq.add(new DERSequence(encodedTarget));
    }

    DERSequence targetExtensionContent = new DERSequence(new DERSequence(targetSeq));
    return targetExtensionContent;
  }

  /**
   * Generates a VOMS attribute certificate with the given properties.
   *
   * @param fqans the list of Fully Qualified Attribute Names (FQANs)
   * @param gas the list of generic attributes
   * @param targets the list of target restrictions
   * @param holderCert the X.509 certificate of the holder
   * @param serialNumber the serial number of the AC
   * @param notBefore the start of the AC validity period
   * @param notAfter the end of the AC validity period
   * @param voName the VO name
   * @param host the VOMS server hostname
   * @param port the VOMS server port
   * @return the generated X.509 attribute certificate
   * @throws VOMSError if certificate generation fails
   */
  public X509AttributeCertificateHolder generateVOMSAttributeCertificate(List<String> fqans,
      List<VOMSGenericAttribute> gas, List<String> targets, X509Certificate holderCert,
      BigInteger serialNumber, Date notBefore, Date notAfter, String voName, String host,
      int port) throws VOMSError {

    return generateVOMSAttributeCertificate(defaultGenerationProperties, fqans, gas, targets,
        holderCert, serialNumber, notBefore, notAfter, voName, host, port);
  }

  /**
   * Generates a VOMS attribute certificate with the specified properties.
   *
   * @param generationProperties the properties influencing AC generation
   * @param fqans the list of Fully Qualified Attribute Names (FQANs)
   * @param gas the list of generic attributes
   * @param targets the list of target restrictions
   * @param holderCert the X.509 certificate of the holder
   * @param serialNumber the serial number of the AC
   * @param notBefore the start of the AC validity period
   * @param notAfter the end of the AC validity period
   * @param voName the VO name
   * @param host the VOMS server hostname
   * @param port the VOMS server port
   * @return the generated X.509 attribute certificate
   * @throws VOMSError if certificate generation fails
   */
  public X509AttributeCertificateHolder generateVOMSAttributeCertificate(
      EnumSet<ACGenerationProperties> generationProperties, List<String> fqans,
      List<VOMSGenericAttribute> gas, List<String> targets, X509Certificate holderCert,
      BigInteger serialNumber, Date notBefore, Date notAfter, String voName, String host,
      int port) throws VOMSError {

    AttributeCertificateHolder holder = null;
    AttributeCertificateIssuer issuer = null;

    try {

      holder = buildHolder(holderCert);
      issuer = buildIssuer();

      X509v2AttributeCertificateBuilder builder =
          new X509v2AttributeCertificateBuilder(holder, issuer, serialNumber, notBefore, notAfter);

      GeneralName policyAuthorityInfo = buildPolicyAuthorityInfo(voName, host, port);

      builder.addAttribute(VOMS_FQANS_OID, buildFQANsAttributeContent(fqans, policyAuthorityInfo));

      if (gas != null && !gas.isEmpty()) {
        builder.addExtension(VOMS_GENERIC_ATTRS_OID, false,
            buildGAExtensionContent(generationProperties, gas, policyAuthorityInfo));
      }

      if (targets != null && !targets.isEmpty()) {
        builder.addExtension(Extension.targetInformation, true,
            buildTargetsExtensionContent(generationProperties, targets));
      }

      if (!generationProperties.contains(ACGenerationProperties.SKIP_AC_CERTS_EXTENSION)) {
        builder.addExtension(VOMS_CERTS_OID, false,
            buildACCertsExtensionContent(generationProperties));
      }

      if (generationProperties.contains(ACGenerationProperties.INCLUDE_FAKE_CRITICAL_EXTENSION)) {
        builder.addExtension(FAKE_EXT_OID, true, new DERSequence());
      }

      boolean noRevAvailIsCritical = false;
      boolean akidIsCritical = false;

      if (generationProperties
        .contains(ACGenerationProperties.INCLUDE_CRITICAL_NO_REV_AVAIL_EXTENSION)) {
        noRevAvailIsCritical = true;
      }

      if (generationProperties.contains(ACGenerationProperties.INCLUDE_CRITICAL_AKID_EXTENSION)) {
        akidIsCritical = true;
      }

      builder.addExtension(Extension.noRevAvail, noRevAvailIsCritical, DERNull.INSTANCE);

      AuthorityKeyIdentifier akid = buildAuthorityKeyIdentifier();

      builder.addExtension(Extension.authorityKeyIdentifier, akidIsCritical,
          akid != null ? akid : DERNull.INSTANCE);

      return builder.build(getSigner(generationProperties));
    } catch (CertificateEncodingException | CertIOException | NoSuchAlgorithmException e) {
      throw new VOMSError(e.getMessage(), e);
    }

  }

  /**
   * Generates a VOMS certificate extension.
   *
   * @param acs the list of X.509 attribute certificates
   * @return the generated certificate extension
   */
  public CertificateExtension generateVOMSExtension(List<X509AttributeCertificateHolder> acs) {

    ASN1EncodableVector vomsACs = new ASN1EncodableVector();

    for (X509AttributeCertificateHolder ac : acs) {
      vomsACs.add(ac.toASN1Structure());
    }

    DERSequence acSeq = new DERSequence(vomsACs);

    return new CertificateExtension(VOMS_EXTENSION_OID.getId(), acSeq.toASN1Primitive(), false);

  }

  private ASN1Encodable getCertAsDEREncodable(X509Certificate cert) {

    try {
      byte[] certBytes = cert.getEncoded();

      ByteArrayInputStream bais = new ByteArrayInputStream(certBytes);
      ASN1InputStream is = new ASN1InputStream(bais);
      ASN1Object derCert = is.readObject();
      is.close();
      return derCert;

    } catch (CertificateEncodingException | IOException e) {
      throw new VOMSError("Error encoding X509 certificate: " + e.getMessage(), e);
    }

  }

  private DEROctetString getDEROctetString(String s) {
    return new DEROctetString(s.getBytes());
  }
}
