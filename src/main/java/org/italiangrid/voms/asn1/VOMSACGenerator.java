/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2012.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.italiangrid.voms.asn1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509v2AttributeCertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
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
 * 
 * This AC generator provides the VOMS AC encoding starting from a set of
 * attributes.
 * 
 * @author Andrea Ceccanti
 * 
 */
public class VOMSACGenerator implements VOMSConstants {
	
	public static enum ACGenerationProperties {
		SKIP_AC_CERTS_EXTENSION,
		FAKE_SIGNATURE_BITS,
		INCLUDE_FAKE_CRITICAL_EXTENSION,
		INCLUDE_CRITICAL_NO_REV_AVAIL_EXTENSION,
		INCLUDE_CRITICAL_AKID_EXTENSION,
		INCLUDE_EMPTY_AC_CERTS_EXTENSION
	}

	public static final EnumSet<ACGenerationProperties> 
		defaultGenerationProperties = EnumSet.noneOf(ACGenerationProperties.class);

	static class RandomContentSigner implements ContentSigner {

		public static int SIG_LENGHT = 1024;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		AlgorithmIdentifier sigAlgId;

		public RandomContentSigner(String sigAlgName) {

			this.sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder()
				.find(sigAlgName);
		}

		public AlgorithmIdentifier getAlgorithmIdentifier() {

			return sigAlgId;
		}

		public OutputStream getOutputStream() {

			return bos;
		}

		public byte[] getSignature() {

			try {
				bos.close();
			} catch (IOException e) {

			}

			Random r = new Random();

			byte[] sigBytes = new byte[SIG_LENGHT];
			r.nextBytes(sigBytes);

			return sigBytes;
		}

	}

	public static final ASN1ObjectIdentifier FAKE_EXT_OID = new ASN1ObjectIdentifier(
		"1.3.6.1.4.1.8005.100.120.82");

	private X509Credential aaCredential;
	private ContentSigner signer;

	private ContentSigner getSigner(EnumSet<ACGenerationProperties> properties) {

		if (signer == null) {

			JcaContentSignerBuilder builder = new JcaContentSignerBuilder(
				aaCredential.getCertificate().getSigAlgName());

			builder.setProvider(BouncyCastleProvider.PROVIDER_NAME);
			try {

				if (properties.contains(ACGenerationProperties.FAKE_SIGNATURE_BITS))
					signer = new RandomContentSigner(aaCredential.getCertificate()
						.getSigAlgName());
				else
					signer = builder.build(aaCredential.getKey());

			} catch (OperatorCreationException e) {
				throw new VOMSError(e.getMessage(), e);
			}
		}
		return signer;
	}

	public VOMSACGenerator(X509Credential aaCredential) {
		this.aaCredential = aaCredential;
	}

	private String buildVOURI(String voName, String host, int port) {

		return String.format("%s://%s:%d", voName, host, port);
	}

	private ASN1Encodable buildACCertsExtensionContent(
		EnumSet<ACGenerationProperties> properties) {

		ASN1EncodableVector issuerCertsContainer = new ASN1EncodableVector();

		if (properties.contains(ACGenerationProperties.INCLUDE_EMPTY_AC_CERTS_EXTENSION))
			issuerCertsContainer.add(new DERSequence());
		else
			issuerCertsContainer.add(new DERSequence(
				getCertAsDEREncodable(aaCredential.getCertificate())));

		return new DERSequence(issuerCertsContainer);
	}

	private AuthorityKeyIdentifier buildAuthorityKeyIdentifier() {

		byte[] authKeyId = aaCredential.getCertificate().getExtensionValue(
			X509Extension.authorityKeyIdentifier.toString());

		if (authKeyId != null) {
			return new AuthorityKeyIdentifier(authKeyId);
		}

		return null;
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

	private ASN1Encodable buildGAExtensionContent(
		EnumSet<ACGenerationProperties> properties, List<VOMSGenericAttribute> gas,
		GeneralName policyAuthorityInfo) {

		ASN1EncodableVector tagContainer = new ASN1EncodableVector();
		ASN1EncodableVector tagSequences = new ASN1EncodableVector();

		for (VOMSGenericAttribute a : gas)
			tagSequences.add(buildTagSequence(a));

		tagContainer.add(new GeneralNames(policyAuthorityInfo));
		tagContainer.add(new DERSequence(tagSequences));

		DERSequence finalSequence;

		// We wrap this three times as VOMS core does, even if I think this 
		// is a bug 
		finalSequence = new DERSequence(
			new DERSequence(new DERSequence(tagContainer)));

		return finalSequence;
	}

	private AttributeCertificateHolder buildHolder(X509Certificate holderCert)
		throws CertificateEncodingException {

		JcaX509CertificateHolder holderWrappedCert = new JcaX509CertificateHolder(
			holderCert);
		AttributeCertificateHolder acHolder = new AttributeCertificateHolder(
			holderWrappedCert.getSubject(), holderCert.getSerialNumber());

		return acHolder;
	}

	private AttributeCertificateIssuer buildIssuer()
		throws CertificateEncodingException {

		JcaX509CertificateHolder issuer = new JcaX509CertificateHolder(
			aaCredential.getCertificate());
		return new AttributeCertificateIssuer(issuer.getSubject());
	}

	private GeneralName buildPolicyAuthorityInfo(String voName, String host,
		int port) {

		return new GeneralName(GeneralName.uniformResourceIdentifier, buildVOURI(
			voName, host, port));
	}

	private DERSequence buildTagSequence(VOMSGenericAttribute ga) {

		ASN1EncodableVector tagSequence = new ASN1EncodableVector();

		tagSequence.add(getDEROctetString(ga.getName()));
		tagSequence.add(getDEROctetString(ga.getValue()));
		tagSequence.add(getDEROctetString(ga.getContext()));

		return new DERSequence(tagSequence);

	}

	private ASN1Encodable buildTargetsExtensionContent(
		EnumSet<ACGenerationProperties> properties, List<String> targets) {

		ASN1EncodableVector targetSeq = new ASN1EncodableVector();

		for (String s : targets) {

			DERTaggedObject encodedTarget = new DERTaggedObject(0, new GeneralName(
				GeneralName.uniformResourceIdentifier, s));

			// We wrap the target in another sequence as the old VOMS does	
			targetSeq.add(new DERSequence(encodedTarget));
		}

		DERSequence targetExtensionContent = new DERSequence(new DERSequence(
			targetSeq));
		return targetExtensionContent;
	}

	
	public X509AttributeCertificateHolder generateVOMSAttributeCertificate(
		List<String> fqans,
		List<VOMSGenericAttribute> gas, List<String> targets,
		X509Certificate holderCert, BigInteger serialNumber, Date notBefore,
		Date notAfter, String voName, String host, int port){
		
		return generateVOMSAttributeCertificate(defaultGenerationProperties, 
			fqans, 
			gas, 
			targets, 
			holderCert, 
			serialNumber, 
			notBefore, 
			notAfter, 
			voName, 
			host, 
			port);
	}
	
	
	public X509AttributeCertificateHolder generateVOMSAttributeCertificate(
		EnumSet<ACGenerationProperties> generationProperties, List<String> fqans,
		List<VOMSGenericAttribute> gas, List<String> targets,
		X509Certificate holderCert, BigInteger serialNumber, Date notBefore,
		Date notAfter, String voName, String host, int port) {

		AttributeCertificateHolder holder = null;
		AttributeCertificateIssuer issuer = null;

		try {

			holder = buildHolder(holderCert);
			issuer = buildIssuer();

		} catch (CertificateEncodingException e) {
			throw new VOMSError(e.getMessage(), e);
		}

		X509v2AttributeCertificateBuilder builder = new X509v2AttributeCertificateBuilder(
			holder, issuer, serialNumber, notBefore, notAfter);

		GeneralName policyAuthorityInfo = buildPolicyAuthorityInfo(voName, host,
			port);

		builder.addAttribute(VOMS_FQANS_OID,
			buildFQANsAttributeContent(fqans, policyAuthorityInfo));

		if (gas != null && !gas.isEmpty())
			builder
				.addExtension(
					VOMS_GENERIC_ATTRS_OID,
					false,
					buildGAExtensionContent(generationProperties, gas,
						policyAuthorityInfo));

		if (targets != null && !targets.isEmpty())
			builder.addExtension(X509Extension.targetInformation, true,
				buildTargetsExtensionContent(generationProperties, targets));

		if (!generationProperties
			.contains(ACGenerationProperties.SKIP_AC_CERTS_EXTENSION))
			builder.addExtension(VOMS_CERTS_OID, false,
				buildACCertsExtensionContent(generationProperties));

		if (generationProperties
			.contains(ACGenerationProperties.INCLUDE_FAKE_CRITICAL_EXTENSION))
			builder.addExtension(FAKE_EXT_OID, true, new DERSequence());

		if (generationProperties
			.contains(ACGenerationProperties.INCLUDE_CRITICAL_NO_REV_AVAIL_EXTENSION))
			builder.addExtension(X509Extension.noRevAvail, true, new DERNull());

		if (generationProperties
			.contains(ACGenerationProperties.INCLUDE_CRITICAL_AKID_EXTENSION)) {
			AuthorityKeyIdentifier akid = buildAuthorityKeyIdentifier();
			builder.addExtension(X509Extension.authorityKeyIdentifier, true,
				akid != null ? akid : new DERNull());
		}

		return builder.build(getSigner(generationProperties));

	}

	public CertificateExtension generateVOMSExtension(
		List<X509AttributeCertificateHolder> acs) {

		ASN1EncodableVector vomsACs = new ASN1EncodableVector();

		for (X509AttributeCertificateHolder ac : acs)
			vomsACs.add(ac.toASN1Structure());

		DERSequence acSeq = new DERSequence(vomsACs);

		CertificateExtension ext = new CertificateExtension(
			VOMS_EXTENSION_OID.getId(), acSeq.toASN1Object(), false);

		return ext;
	}

	private DEREncodable getCertAsDEREncodable(X509Certificate cert) {

		try {
			byte[] certBytes = cert.getEncoded();

			ByteArrayInputStream bais = new ByteArrayInputStream(certBytes);
			ASN1InputStream is = new ASN1InputStream(bais);
			DERObject derCert = is.readObject();
			is.close();
			return derCert;

		} catch (CertificateEncodingException e) {
			throw new VOMSError("Error encoding X509 certificate: " + e.getMessage(),
				e);
		} catch (IOException e) {
			throw new VOMSError("Error encoding X509 certificate: " + e.getMessage(),
				e);
		}

	}

	private DEROctetString getDEROctetString(String s) {

		return new DEROctetString(s.getBytes());
	}
}