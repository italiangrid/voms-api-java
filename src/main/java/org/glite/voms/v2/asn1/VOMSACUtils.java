package org.glite.voms.v2.asn1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IetfAttrSyntax;
import org.bouncycastle.asn1.x509.X509CertificateStructure;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.glite.voms.v2.VOMSAttributes;
import org.glite.voms.v2.VOMSError;
import org.glite.voms.v2.VOMSGenericAttribute;
import org.glite.voms.v2.ac.VOMSConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A set of VOMS AC handling utilities.
 * 
 * @author andreaceccanti
 *
 */
public class VOMSACUtils implements VOMSConstants{

	public static final Logger log = LoggerFactory.getLogger(VOMSACUtils.class);
	
	public static final String POLICY_AUTHORITY_SEP ="://";
	/**
	 * Returns the VOMS extension, if present, in a given certificate
	 * @param cert the {@link X509Certificate} where the extension will be searched
	 * @return the DER-encoded octet string of the extension value or null if it is not present.
	 */
	public static byte[] getVOMSExtensionFromCertificate(X509Certificate cert){
		
		return cert.getExtensionValue(VOMSConstants.VOMS_EXTENSION_OID);
	}
	
	/**
	 * Deserializes the VOMS Attribute certificates in a given certificate extension
	 * 
	 * @param vomsExtension
	 * @return
	 * @throws IOException 
	 */
	public static List<AttributeCertificate> getACsFromVOMSExtension(byte[] vomsExtension) throws IOException{
		
		List<AttributeCertificate> acs = null;
		
		if (vomsExtension == null)
			return Collections.emptyList();
		
		acs = new ArrayList<AttributeCertificate>();
		
		// Convert extension to a DEROctetString
		ASN1InputStream asn1Stream = new ASN1InputStream(new ByteArrayInputStream(vomsExtension));
		byte[] payload = ((DEROctetString) asn1Stream.readObject()).getOctets();
		
		asn1Stream.close();

		asn1Stream = new ASN1InputStream(new ByteArrayInputStream(payload));
		
        // VOMS extension is SEQUENCE of SET of AttributeCertificate
        // now, SET is an ordered sequence, and an AC is a sequence as
        // well -- thus the three nested ASN.1 sequences below...
        ASN1Sequence baseSequence = (ASN1Sequence) asn1Stream.readObject();
        asn1Stream.close();

        @SuppressWarnings("unchecked")
		Enumeration<ASN1Sequence> setSequence = baseSequence.getObjects();
        
        while(setSequence.hasMoreElements()){
        	
        	ASN1Sequence acSequence = setSequence.nextElement();
        	
        	@SuppressWarnings("unchecked")
        	Enumeration<ASN1Sequence> theACs = acSequence.getObjects();
        	
        	while (theACs.hasMoreElements()){
        		
        		AttributeCertificate parsedAC = new AttributeCertificate(theACs.nextElement());
        		acs.add(parsedAC);
        	}
        }
        
        return acs;
	}
	
	
	/**
	 * Deserializes the VOMS Attribute certificates, if present, in a given certificate passed as argument
	 * 
	 * @param cert the {@link X509Certificate} where the ACs will be searched
	 * @return
	 * @throws IOException
	 */
	public static List<AttributeCertificate> getACsFromCertificate(X509Certificate cert) throws IOException{
		return getACsFromVOMSExtension(getVOMSExtensionFromCertificate(cert));
	}
	
	/**
	 * Deserializes the FQANs contained in a {@link IetfAttrSyntax} object
	 * 
	 * @param attr the {@link IetfAttrSyntax} attribute syntax object containing the VOMS extension
	 * @return a {@link List} of FQANs
	 */
	private static List<String> deserializeFQANs(IetfAttrSyntax attr){
		
		if (attr.getValueType() != IetfAttrSyntax.VALUE_OCTETS)
			throw new VOMSError("Non conformant VOMS Attribute certificate: unsupported attribute values encoding.");
		
		List<String> fqans = new ArrayList<String>();
		
		ASN1OctetString[] values = (ASN1OctetString[])attr.getValues();
					
		for (ASN1OctetString s: values)
			fqans.add(new String(s.getOctets()));
		
		return fqans;
	}
	
	/**
	 * Peforms some sanity checks on the format of the policy authority field found in a VOMS extension.
	 * The enforced format is: vo://host:port
	 * 
	 * @param attr the {@link IetfAttrSyntax} attribute syntax object containing the VOMS extension
	 * @return the validated policy authority as a {@link String}
	 */
	private static String policyAuthoritySanityChecks(IetfAttrSyntax attr){
		
		// The policy authority value is encoded as a DERIA5String
		String policyAuthority = ((DERIA5String) attr.getPolicyAuthority().getNames()[0].getName()).getString();
		
		// PolicyAuthority scheme: <vo name>://<hostname>:<port>
		int index = policyAuthority.indexOf(POLICY_AUTHORITY_SEP);
		
		if ((index < 0) || (index == policyAuthority.length()-1))
			throw new VOMSError("Non conformant VOMS Attribute certificate: unsupported policy authority encoding '"+policyAuthority+"'");
		
		return policyAuthority;
		
	}
	
	/**
	 * Deserializes the information in a VOMS attribute certificate.
	 * @param ac a VOMS {@link AttributeCertificate}
	 * @return a {@link VOMSAttributes} object which provides more convenient access to the VOMS authorization information
	 */
	public static VOMSAttributes deserializeVOMSAttributes(AttributeCertificate ac) {
		
		VOMSAttributesImpl attrs = new VOMSAttributesImpl();
		
		X509AttributeCertificateHolder holder = new X509AttributeCertificateHolder(ac);
		Attribute[] asn1Attrs = holder.getAttributes(new ASN1ObjectIdentifier(VOMS_ATTRS_OID));
		
		for (Attribute a: asn1Attrs){
			DERObject theVOMSDerObject = a.getAttributeValues()[0].getDERObject();
			IetfAttrSyntax attrSyntax = new IetfAttrSyntax(ASN1Sequence.getInstance(theVOMSDerObject));
			
			
			String policyAuthority = policyAuthoritySanityChecks(attrSyntax);
			
			
			// The policy authority string has the following format:
			// <vo name>://<hostname>:<port>
			
			
			attrs.setVO(policyAuthority.substring(0, policyAuthority.indexOf(POLICY_AUTHORITY_SEP)));
			attrs.setHost(policyAuthority.substring(policyAuthority.indexOf(POLICY_AUTHORITY_SEP)+3, 
					policyAuthority.lastIndexOf(":")));
			attrs.setPort(Integer.parseInt(policyAuthority.substring(policyAuthority.lastIndexOf(":")+1)));
			
			attrs.setFQANs(deserializeFQANs(attrSyntax));
			
			attrs.setNotBefore(holder.getNotBefore());
			attrs.setNotAfter(holder.getNotAfter());
			attrs.setSignature(holder.getSignature());
			attrs.setGenericAttributes(deserializeGAs(holder));
			attrs.setAACertificates(deserializeACCerts(holder));
			
			attrs.setVOMSAC(holder);
			
			try{
				
				attrs.setIssuer(new X500Principal(holder.getIssuer().getNames()[0].getEncoded()));			
				attrs.setHolder(new X500Principal(holder.getHolder().getIssuer()[0].getEncoded()));
			
			}catch (IOException e){
				throw new VOMSError("Error parsing attribute certificate issuer  or holder name: "+e.getMessage(),e);
			}
		}
		
		return attrs;
	}
	
	/**
	 * Deserializes the VOMS generic attributes 
	 * @param ac the VOMS {@link X509AttributeCertificateHolder} 
	 * @return the {@link List} of {@link VOMSGenericAttribute} contained in the ac 
	 */
	private static List<VOMSGenericAttribute> deserializeGAs(X509AttributeCertificateHolder ac){
		
		List<VOMSGenericAttribute> gas = new ArrayList<VOMSGenericAttribute>();
		
		X509Extension gasExtension = ac.getExtension(new ASN1ObjectIdentifier(VOMS_GENERIC_ATTRS_OID));
		
		if (gasExtension == null)
			return gas;
		
		// SEQUENCE of TagList - contains just one taglist element
		ASN1Sequence tagContainerSeq = (ASN1Sequence) gasExtension.getParsedValue();
		if (tagContainerSeq.size() != 1)
			throw new VOMSError("Non conformant VOMS Attribute certificate: unsupported tag container format.");
		
		// TagList -  this also should be a sigle element sequence
		ASN1Sequence tagListSeq = (ASN1Sequence) tagContainerSeq.getObjectAt(0);
		if (tagListSeq.size() != 1)
			throw new VOMSError("Non conformant VOMS Attribute certificate: unsupported taglist format.");

		// Down one level
		tagListSeq = (ASN1Sequence) tagListSeq.getObjectAt(0);
		
		// TODO: check policyAuthority!!
		GeneralNames policyAuthority = GeneralNames.getInstance(tagListSeq.getObjectAt(0));
		
		// tags SEQUENCE OF Tag 
		ASN1Sequence tags = (ASN1Sequence) tagListSeq.getObjectAt(1);
		
		@SuppressWarnings("unchecked")
		Enumeration<ASN1Sequence> e = tags.getObjects();
		while(e.hasMoreElements()){
			
			ASN1Sequence theActualTag = e.nextElement();
			
			if (theActualTag.size() != 3)
				throw new VOMSError("Non conformant VOMS Attribute certificate: unsupported tag format.");
			
			VOMSGenericAttributeImpl attribute = new VOMSGenericAttributeImpl();
			
			attribute.setName(new String(DEROctetString.getInstance(theActualTag.getObjectAt(0)).getOctets()));
			attribute.setValue(new String(DEROctetString.getInstance(theActualTag.getObjectAt(1)).getOctets()));
			attribute.setContext(new String(DEROctetString.getInstance(theActualTag.getObjectAt(2)).getOctets()));
			
			gas.add(attribute);
		}
		
		return gas;
	}
	
	/**
	 * Deserializes the VOMS ACCerts extension
	 * @param ac the VOMS {@link X509AttributeCertificateHolder}
	 * @return the parsed array of {@link X509Certificate}
	 */
	private static X509Certificate[] deserializeACCerts(X509AttributeCertificateHolder ac){
		List<X509Certificate> certs = new ArrayList<X509Certificate>();
		
		X509Extension e = ac.getExtension(new ASN1ObjectIdentifier(VOMS_CERTS_OID));
		
		if (e == null)
			return null;
		
		ASN1Sequence certSeq = (ASN1Sequence)e.getParsedValue();
		if (certSeq.size() != 1)
			throw new VOMSError("Non conformant VOMS Attribute certificate: unsupported accerts format.");
		
		// Down one level
		certSeq = (ASN1Sequence)certSeq.getObjectAt(0);
		
		@SuppressWarnings("unchecked")
		Enumeration<DERSequence> encodedCerts = certSeq.getObjects();
		
		CertificateFactory cf = null;
		
		try {
			cf = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
		} catch (Exception ex) {
			throw new VOMSError("Certificate factory creation error: "+ex.getMessage(),ex);
		}
		
		while (encodedCerts.hasMoreElements()){
			
			DERSequence s  = encodedCerts.nextElement();
			X509CertificateObject certObj = null;
			byte[] certData = null;
			X509Certificate theCert = null;
			
			try {
				
				certObj = new X509CertificateObject(X509CertificateStructure.getInstance(ASN1Sequence.getInstance(s)));
				certData = certObj.getEncoded();
				theCert = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(certData));
				
			} catch (CertificateParsingException ex) {
				throw new VOMSError("Certificate parsing error: "+ex.getMessage(), ex);
			} catch (CertificateEncodingException ex) {
				throw new VOMSError("Certificate encoding error: "+ex.getMessage(), ex);
			} catch (CertificateException ex) {
				throw new VOMSError("Error generating certificate from parsed data: "+ex.getMessage(), ex);
			}
			
			certs.add(theCert);
		}
		
		return certs.toArray(new X509Certificate[certs.size()]);
	}
	
	
}
