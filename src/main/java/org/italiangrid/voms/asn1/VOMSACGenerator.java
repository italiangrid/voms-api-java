/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2012.
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
package org.italiangrid.voms.asn1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
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
import org.bouncycastle.cert.CertIOException;
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
 * This AC generator provides the VOMS AC encoding starting from a set of attributes.
 *  
 * @author Andrea Ceccanti
 *
 */
public class VOMSACGenerator implements VOMSConstants{
	
	static class RandomContentSigner implements ContentSigner{

		public static int SIG_LENGHT = 1024;
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		AlgorithmIdentifier sigAlgId;
		
		public RandomContentSigner(String sigAlgName) {
			this.sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find(sigAlgName);
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
	
	public static final ASN1ObjectIdentifier FAKE_EXT_OID = new ASN1ObjectIdentifier("1.3.6.1.4.1.8005.100.120.82");
	
	private X509Credential aaCredential;
	private String host;
	private int port;
	private String voName;
	
	private String voURI;
	
	private boolean vomsCompatibility = true;
	
	private boolean includeEmptyACCertsExtension = false;
	
	private boolean skipACCertsExtension = false;
	
	private boolean useFakeSignatureBits = false;
	
	private boolean includeFakeCriticalExtensions = false;
	
	private boolean includeCriticalNoRevAvail = false;
	
	private boolean includeCriticalAKID = false;
	
	private ContentSigner signer;
	
	private ContentSigner getSigner(){
		if (signer == null){
			
			JcaContentSignerBuilder builder = new JcaContentSignerBuilder(aaCredential.getCertificate().getSigAlgName());
			builder.setProvider(BouncyCastleProvider.PROVIDER_NAME);	
			try {
				if (useFakeSignatureBits)
					signer = new RandomContentSigner(aaCredential.getCertificate().getSigAlgName());
				else
					signer = builder.build(aaCredential.getKey());
				
			} catch (OperatorCreationException e) {
				throw new VOMSError(e.getMessage(),e);
			}
		}
		return signer;
	}
	
	
	public VOMSACGenerator(X509Credential aaCredential, String voName, String host, int port) {
		this.aaCredential = aaCredential;
		this.voName = voName;
		this.host = host;
		this.port = port;
		voURI = String.format("%s://%s:%d",this.voName,this.host,this.port);
	}
	
	private ASN1Encodable buildACCertsExtensionContent(){
		
		ASN1EncodableVector issuerCertsContainer = new ASN1EncodableVector();
		
		if (includeEmptyACCertsExtension)
			issuerCertsContainer.add(new DERSequence());
		else
			issuerCertsContainer.add( new DERSequence(getCertAsDEREncodable( aaCredential.getCertificate() )));
		
		return new DERSequence(issuerCertsContainer);
	}
	
	
	private AuthorityKeyIdentifier buildAuthorityKeyIdentifier(){
		
		byte[] authKeyId = aaCredential.getCertificate().getExtensionValue(X509Extension.authorityKeyIdentifier.toString());
		
		if (authKeyId != null){
			return new AuthorityKeyIdentifier(authKeyId);
		}
		
		return null;
	}
	
	private ASN1Encodable buildFQANsAttributeContent(List<String> fqans){
		
		ASN1EncodableVector container = new ASN1EncodableVector();
		ASN1EncodableVector encodedFQANs = new ASN1EncodableVector();
		
		// Policy authority info
		DERTaggedObject pai = new DERTaggedObject(0, buildPolicyAuthorityInfo());
		container.add(pai);
		
		for (String s: fqans)
			encodedFQANs.add(new DEROctetString(s.getBytes()));
		
		container.add(new DERSequence(encodedFQANs));
		
		return new DERSequence(container);
	}
	
	
	private ASN1Encodable buildGAExtensionContent(List<VOMSGenericAttribute> gas){
		
		ASN1EncodableVector tagContainer = new ASN1EncodableVector();
        ASN1EncodableVector tagSequences = new ASN1EncodableVector();
        
        for (VOMSGenericAttribute a: gas)
        	tagSequences.add(buildTagSequence(a));
     
        tagContainer.add( new GeneralNames(buildPolicyAuthorityInfo()) );
        tagContainer.add( new DERSequence( tagSequences ) );
        
        DERSequence finalSequence;
        
        // I think VOMS has a bug that wraps this three times instead of two
        if (vomsCompatibility)
        	finalSequence = new DERSequence(new DERSequence(new DERSequence(tagContainer))); 
        else
        	finalSequence = new DERSequence(new DERSequence(tagContainer));
        
		return finalSequence;
	}
	
	private AttributeCertificateHolder buildHolder(X509Certificate holderCert) throws CertificateEncodingException{
		
		JcaX509CertificateHolder holderWrappedCert = new JcaX509CertificateHolder(holderCert);
		AttributeCertificateHolder acHolder = new AttributeCertificateHolder(holderWrappedCert.getSubject(),
				holderCert.getSerialNumber());
		
		return acHolder;
	}


	private AttributeCertificateIssuer buildIssuer() throws CertificateEncodingException{
		
		JcaX509CertificateHolder issuer = new JcaX509CertificateHolder(aaCredential.getCertificate());
		return new AttributeCertificateIssuer(issuer.getSubject());
	}
	private GeneralName buildPolicyAuthorityInfo() {
		return new GeneralName(GeneralName.uniformResourceIdentifier, voURI);
	}
	
	private DERSequence buildTagSequence( VOMSGenericAttribute ga ) {

        ASN1EncodableVector tagSequence = new ASN1EncodableVector();

        tagSequence.add( getDEROctetString( ga.getName() ) );
        tagSequence.add( getDEROctetString( ga.getValue() ) );
        tagSequence.add( getDEROctetString( ga.getContext() ) );

        return new DERSequence( tagSequence );

    }
	
	private ASN1Encodable buildTargetsExtensionContent(List<String> targets){
		
		ASN1EncodableVector targetSeq= new ASN1EncodableVector();
		
		for (String s: targets){
			
			DERTaggedObject encodedTarget = new DERTaggedObject(0,new GeneralName(GeneralName.uniformResourceIdentifier, s));
			
			if (vomsCompatibility)
				targetSeq.add(new DERSequence(encodedTarget));
			else
				targetSeq.add(encodedTarget);	
		}
		
		DERSequence targetExtensionContent = new DERSequence( new DERSequence(targetSeq));
		return targetExtensionContent;
	}
	
	public synchronized X509AttributeCertificateHolder generateVOMSAttributeCertificate(
			List<String> fqans, 
			List<VOMSGenericAttribute> gas,
			List<String> targets,
			X509Certificate holderCert,
			BigInteger serialNumber,
			Date notBefore,
			Date notAfter){
		
		AttributeCertificateHolder holder = null; 
		AttributeCertificateIssuer issuer = null; 
		
		try{
		
			holder = buildHolder(holderCert);
			issuer = buildIssuer();
			
		}catch(CertificateEncodingException e){
			throw new VOMSError(e.getMessage(), e);
		}
		
		X509v2AttributeCertificateBuilder builder = new X509v2AttributeCertificateBuilder(holder, issuer, serialNumber, notBefore, notAfter);
		builder.addAttribute(VOMS_FQANS_OID, buildFQANsAttributeContent(fqans));

        try {
            if (gas != null && !gas.isEmpty())
                builder.addExtension(VOMS_GENERIC_ATTRS_OID, false, buildGAExtensionContent(gas));

            if (targets != null && !targets.isEmpty())
                builder.addExtension(X509Extension.targetInformation , true, buildTargetsExtensionContent(targets));

            if (!skipACCertsExtension)
                builder.addExtension(VOMS_CERTS_OID, false, buildACCertsExtensionContent());

            if (includeFakeCriticalExtensions)
                builder.addExtension(FAKE_EXT_OID, true, new DERSequence());

            if (includeCriticalNoRevAvail)
                builder.addExtension(X509Extension.noRevAvail, true, new DERNull());

            if (includeCriticalAKID){
                AuthorityKeyIdentifier akid = buildAuthorityKeyIdentifier();
                builder.addExtension(X509Extension.authorityKeyIdentifier, true, akid != null ? akid : new DERNull());
            }
        } catch (CertIOException e) {
            throw new VOMSError(e.getMessage(), e);
        }

        return builder.build(getSigner());
	}
	
	public synchronized CertificateExtension generateVOMSExtension(List<X509AttributeCertificateHolder> acs){
		ASN1EncodableVector vomsACs = new ASN1EncodableVector();
		
		for (X509AttributeCertificateHolder ac: acs)
			vomsACs.add(ac.toASN1Structure());
		
		DERSequence acSeq = new DERSequence(vomsACs);
		
		CertificateExtension ext = new CertificateExtension(VOMS_EXTENSION_OID.getId(), acSeq.toASN1Object(), false);
		
		return ext;
	}
	
	private DEREncodable getCertAsDEREncodable(X509Certificate cert){
        
        try {
            byte[] certBytes = cert.getEncoded();
            
            ByteArrayInputStream bais = new ByteArrayInputStream(certBytes);
            ASN1InputStream is = new ASN1InputStream(bais);
            DERObject derCert = is.readObject();
            is.close();
            return derCert;
        
        } catch ( CertificateEncodingException e ) {
            throw new VOMSError("Error encoding X509 certificate: "+ e.getMessage(),e);
        } catch ( IOException e ) {
            throw new VOMSError("Error encoding X509 certificate: "+ e.getMessage(),e);
        }
        
    }


	private DEROctetString getDEROctetString(String s){
		return new DEROctetString(s.getBytes());
	}


	/**
	 * @param includeEmptyACCertsExtension the includeEmptyACCertsExtension to set
	 */
	public synchronized void setIncludeEmptyACCertsExtension(
			boolean includeEmptyACCertsExtension) {
		this.includeEmptyACCertsExtension = includeEmptyACCertsExtension;
	}
	
	/**
	 * @param skipACCertsExtension the skipACCertsExtension to set
	 */
	public synchronized void setSkipACCertsExtension(boolean skipACCertsExtension) {
		this.skipACCertsExtension = skipACCertsExtension;
	}

	/**
	 * @param useFakeSignatureBits the useFakeSignatureBits to set
	 */
	public synchronized void setUseFakeSignatureBits(boolean useFakeSignatureBits) {
		this.useFakeSignatureBits = useFakeSignatureBits;
	}

	/**
	 * @param includeFakeCriticalExtensions the includeFakeCriticalExtensions to set
	 */
	public synchronized void setIncludeFakeCriticalExtensions(
			boolean includeFakeCriticalExtensions) {
		this.includeFakeCriticalExtensions = includeFakeCriticalExtensions;
	}


	/**
	 * @param includeCriticalNoRevAvail the includeCriticalNoRevAvail to set
	 */
	public synchronized void setIncludeCriticalNoRevAvail(
			boolean includeCriticalNoRevAvail) {
		this.includeCriticalNoRevAvail = includeCriticalNoRevAvail;
	}

	/**
	 * @param includeCriticalAKID the includeCriticalAKID to set
	 */
	public synchronized void setIncludeCriticalAKID(boolean includeCriticalAKID) {
		this.includeCriticalAKID = includeCriticalAKID;
	}
}