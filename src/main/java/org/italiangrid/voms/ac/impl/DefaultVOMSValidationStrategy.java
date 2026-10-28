// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.ac.impl;

import static org.italiangrid.voms.error.VOMSValidationErrorCode.aaCertFailsSignatureVerification;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.aaCertNotFound;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.acCertFailsSignatureVerification;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.acHolderDoesntMatchCertChain;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.acNotValidAtCurrentTime;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.canlError;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.emptyAcCertsExtension;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.fqanDoesntMatchVo;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.invalidAaCert;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.invalidAcCert;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.localhostDoesntMatchAcTarget;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.lscDescriptionDoesntMatchAcCert;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.lscFileNotFound;
import static org.italiangrid.voms.error.VOMSValidationErrorCode.other;
import static org.italiangrid.voms.error.VOMSValidationErrorMessage.newErrorMessage;

import eu.emi.security.authn.x509.ValidationError;
import eu.emi.security.authn.x509.ValidationResult;
import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.impl.X500NameUtils;
import eu.emi.security.authn.x509.proxy.ProxyUtils;
import java.net.UnknownHostException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentVerifierProviderBuilder;
import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.ac.VOMSACValidationStrategy;
import org.italiangrid.voms.ac.VOMSValidationResult;
import org.italiangrid.voms.asn1.VOMSConstants;
import org.italiangrid.voms.error.VOMSValidationErrorMessage;
import org.italiangrid.voms.store.LSCInfo;
import org.italiangrid.voms.store.VOMSTrustStore;

/**
 * The Default VOMS validation strategy.
 *
 * @author andreaceccanti
 */
public class DefaultVOMSValidationStrategy implements VOMSACValidationStrategy {

  private final VOMSTrustStore store;
  private final X509CertChainValidatorExt certChainValidator;
  private final LocalHostnameResolver hostnameResolver;

  public DefaultVOMSValidationStrategy(
      VOMSTrustStore store, X509CertChainValidatorExt validator, LocalHostnameResolver resolver) {

    this.store = store;
    this.certChainValidator = validator;
    this.hostnameResolver = resolver;
  }

  public DefaultVOMSValidationStrategy(VOMSTrustStore store, X509CertChainValidatorExt validator) {

    this(store, validator, new DefaultLocalHostnameResolver());
  }

  private boolean checkACHolder(
      VOMSAttribute attributes,
      X509Certificate[] chain,
      List<VOMSValidationErrorMessage> validationErrors) {

    X500Principal chainHolder = ProxyUtils.getOriginalUserDN(chain);

    if (chainHolder.equals(attributes.getHolder())) {
      return true;
    }

    String acHolderSubject = X500NameUtils.getReadableForm(attributes.getHolder());
    String certChainSubject = X500NameUtils.getReadableForm(chainHolder);
    validationErrors.add(
        VOMSValidationErrorMessage.newErrorMessage(
            acHolderDoesntMatchCertChain, acHolderSubject, certChainSubject));
    return false;
  }

  private boolean checkACValidity(
      VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors) {

    Date now = new Date();

    if (attributes.validAt(now)) {
      return true;
    }

    VOMSValidationErrorMessage m =
        VOMSValidationErrorMessage.newErrorMessage(
            acNotValidAtCurrentTime, attributes.getNotBefore(), attributes.getNotAfter(), now);

    validationErrors.add(m);
    return false;
  }

  private boolean checkLocalAACertSignature(
      VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors) {

    X509Certificate localAACert = store.getAACertificateBySubject(attributes.getIssuer());
    if (localAACert == null) {
      validationErrors.add(VOMSValidationErrorMessage.newErrorMessage(aaCertNotFound));
      return false;
    }

    if (!validateCertificate(localAACert, validationErrors)) {
      validationErrors.add(VOMSValidationErrorMessage.newErrorMessage(invalidAaCert));
      return false;
    }

    if (!checkAuthorityKeyIdentifier(localAACert, attributes, validationErrors)) {
      return false;
    }

    if (verifyACSignature(attributes, localAACert)) {
      return true;
    }

    String readableSubject = X500NameUtils.getReadableForm(localAACert.getSubjectX500Principal());
    validationErrors.add(
        VOMSValidationErrorMessage.newErrorMessage(
            aaCertFailsSignatureVerification, readableSubject));
    return false;
  }

  private boolean checkLSCSignature(
      VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors) {

    LSCInfo lsc = store.getLSC(attributes.getVO(), attributes.getHost());
    X509Certificate[] aaCerts = attributes.getAACertificates();

    if (lsc == null) {
      validationErrors.add(VOMSValidationErrorMessage.newErrorMessage(lscFileNotFound));
      return false;
    }

    if (aaCerts == null || aaCerts.length == 0) {
      validationErrors.add(VOMSValidationErrorMessage.newErrorMessage(emptyAcCertsExtension));
      return false;
    }

    if (!lsc.matches(aaCerts)) {
      validationErrors.add(
          VOMSValidationErrorMessage.newErrorMessage(lscDescriptionDoesntMatchAcCert));
      return false;
    }

    // LSC matches aa certs, verify certificates extracted from the AC
    if (!validateCertificateChain(aaCerts, validationErrors)) {
      validationErrors.add(VOMSValidationErrorMessage.newErrorMessage(invalidAcCert));
      return false;
    }

    if (!checkAuthorityKeyIdentifier(aaCerts[0], attributes, validationErrors)) {
      return false;
    }

    boolean signatureValid = verifyACSignature(attributes, aaCerts[0]);

    if (!signatureValid) {
      String readableSubject = X500NameUtils.getReadableForm(aaCerts[0].getSubjectX500Principal());
      validationErrors.add(
          VOMSValidationErrorMessage.newErrorMessage(
              acCertFailsSignatureVerification, readableSubject));
    }

    return signatureValid;
  }

  private boolean checkSignature(
      VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors) {

    return checkLSCSignature(attributes, validationErrors)
        || checkLocalAACertSignature(attributes, validationErrors);
  }

  private boolean checkTargets(
      VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors) {

    if (attributes.getTargets() == null || attributes.getTargets().size() == 0) return true;

    String localhostName;

    try {
      localhostName = hostnameResolver.resolveLocalHostname();

    } catch (UnknownHostException e) {
      validationErrors.add(
          newErrorMessage(other, "Error resolving localhost name: " + e.getMessage()));
      return false;
    }

    if (!attributes.getTargets().contains(localhostName)) {
      validationErrors.add(
          newErrorMessage(
              localhostDoesntMatchAcTarget, localhostName, attributes.getTargets().toString()));
      return false;
    }

    return true;
  }

  private boolean checkNoRevAvailExtension(
      VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors) {

    Extension noRevAvail = attributes.getVOMSAC().getExtension(Extension.noRevAvail);

    if (noRevAvail != null && noRevAvail.isCritical()) {
      validationErrors.add(newErrorMessage(other, "NoRevAvail AC extension cannot be critical!"));
      return false;
    }
    return true;
  }

  private boolean checkAuthorityKeyIdentifier(
      X509Certificate aaCert,
      VOMSAttribute attributes,
      List<VOMSValidationErrorMessage> validationErrors) {

    AuthorityKeyIdentifier akid =
        AuthorityKeyIdentifier.fromExtensions(attributes.getVOMSAC().getExtensions());

    try {

      X509CertificateHolder aaCertHolder = new JcaX509CertificateHolder(aaCert);

      SubjectKeyIdentifier skid = SubjectKeyIdentifier.fromExtensions(aaCertHolder.getExtensions());
      boolean authKeyIdMatches =
          Arrays.equals(skid.getKeyIdentifier(), akid.getKeyIdentifierOctets());

      if (!authKeyIdMatches) {
        validationErrors.add(
            newErrorMessage(
                other,
                "AuthorityKeyIdentifier in the AC  does not match AA certificate subject key identifier!"));
        return false;
      }

      return true;

    } catch (CertificateEncodingException e) {
      validationErrors.add(
          newErrorMessage(
              other, String.format("VOMS AA certificate parse error: %s", e.getMessage())));
      return false;
    }
  }

  private boolean checkAuthorityKeyIdentifierExtension(
      VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors) {

    Extension authKeyId = attributes.getVOMSAC().getExtension(Extension.authorityKeyIdentifier);

    if (authKeyId != null && authKeyId.isCritical()) {
      validationErrors.add(
          newErrorMessage(other, "AuthorityKeyIdentifier AC extension cannot be critical!"));
      return false;
    }

    // authKeyIdentifier value is checked in AC signature verification
    return true;
  }

  private boolean checkUnhandledCriticalExtensions(
      VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors) {

    @SuppressWarnings("unchecked")
    List<ASN1ObjectIdentifier> acExtensions = attributes.getVOMSAC().getExtensionOIDs();

    for (ASN1ObjectIdentifier extId : acExtensions) {
      if (!VOMSConstants.VOMS_HANDLED_EXTENSIONS.contains(extId)
          && attributes.getVOMSAC().getExtension(extId).isCritical()) {
        validationErrors.add(
            newErrorMessage(
                other, "unknown critical extension found in VOMS AC: " + extId.getId()));
        return false;
      }
    }
    return true;
  }

  public VOMSValidationResult validateAC(VOMSAttribute attributes) {

    List<VOMSValidationErrorMessage> validationErrors = new ArrayList<VOMSValidationErrorMessage>();

    if (checkACValidity(attributes, validationErrors)
        && checkSignature(attributes, validationErrors)
        && checkTargets(attributes, validationErrors)
        && checkAuthorityKeyIdentifierExtension(attributes, validationErrors)
        && checkNoRevAvailExtension(attributes, validationErrors)
        && checkUnhandledCriticalExtensions(attributes, validationErrors)
        && checkFQANs(attributes, validationErrors)) {

      return VOMSValidationResult.success(attributes, validationErrors);
    }
    return VOMSValidationResult.failure(attributes, validationErrors);
  }

  public VOMSValidationResult validateAC(VOMSAttribute attributes, X509Certificate[] chain) {

    List<VOMSValidationErrorMessage> validationErrors = new ArrayList<VOMSValidationErrorMessage>();

    if (checkACValidity(attributes, validationErrors)
        && checkSignature(attributes, validationErrors)
        && checkACHolder(attributes, chain, validationErrors)
        && checkTargets(attributes, validationErrors)
        && checkAuthorityKeyIdentifierExtension(attributes, validationErrors)
        && checkNoRevAvailExtension(attributes, validationErrors)
        && checkUnhandledCriticalExtensions(attributes, validationErrors)
        && checkFQANs(attributes, validationErrors)) {

      return VOMSValidationResult.success(attributes, validationErrors);
    }
    return VOMSValidationResult.failure(attributes, validationErrors);
  }

  private boolean validateCertificate(
      X509Certificate c, List<VOMSValidationErrorMessage> validationErrors) {

    return validateCertificateChain(new X509Certificate[] {c}, validationErrors);
  }

  private boolean validateCertificateChain(
      X509Certificate[] chain, List<VOMSValidationErrorMessage> validationErrors) {

    ValidationResult result = certChainValidator.validate(chain);

    for (ValidationError e : result.getErrors())
      validationErrors.add(VOMSValidationErrorMessage.newErrorMessage(canlError, e.getMessage()));

    return result.isValid();
  }

  private boolean verifyACSignature(VOMSAttribute attributes, X509Certificate cert) {

    try {

      X509CertificateHolder certHolder = new JcaX509CertificateHolder(cert);
      ContentVerifierProvider cvp =
          new BcRSAContentVerifierProviderBuilder(new DefaultDigestAlgorithmIdentifierFinder())
              .build(certHolder);
      return attributes.getVOMSAC().isSignatureValid(cvp);

    } catch (Exception e) {
      throw new VOMSError("Error verifying AC signature: " + e.getMessage(), e);
    }
  }

  /**
   * Checks that every FQAN has the AC's VO name as its root group.
   *
   * @return false if any FQAN belongs to a different VO
   */
  private boolean checkFQANs(
      VOMSAttribute attributes, List<VOMSValidationErrorMessage> validationErrors) {

    String vo = attributes.getVO();

    if (vo == null || vo.isEmpty()) {
      validationErrors.add(newErrorMessage(other, "Missing VO name in AC"));
      return false;
    }

    List<String> fqans = attributes.getFQANs();

    if (fqans == null) {
      validationErrors.add(newErrorMessage(other, "Missing FQAN list in AC"));
      return false;
    }

    String root = "/" + vo;

    for (String fqan : fqans) {
      if (fqan == null || !(fqan.equals(root) || fqan.startsWith(root + "/"))) {
        validationErrors.add(newErrorMessage(fqanDoesntMatchVo, fqan, vo));
        return false;
      }
    }

    return true;
  }
}
