package org.italiangrid.voms.asn1;

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
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.IetfAttrSyntax;
import org.bouncycastle.asn1.x509.Target;
import org.bouncycastle.asn1.x509.TargetInformation;
import org.bouncycastle.asn1.x509.Targets;
import org.bouncycastle.asn1.x509.X509CertificateStructure;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.VOMSGenericAttribute;
import org.italiangrid.voms.ac.impl.VOMSAttributesImpl;
import org.italiangrid.voms.ac.impl.VOMSGenericAttributeImpl;
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
		
		return cert.getExtensionValue(VOMSConstants.VOMS_EXTENSION_OID.getId());
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
	
	private static List<String> deserializeACTargets(X509AttributeCertificateHolder ac){
		List<String> targets = new ArrayList<String>();
		
		X509Extension targetExtension = ac.getExtension(X509Extension.targetInformation);
		
		if (targetExtension == null)
			return targets;
		
		TargetInformation ti = TargetInformation.getInstance((ASN1Sequence)targetExtension.getParsedValue());
		
		// Only one Targets according to RFC 3281
		Targets asn1TargetContainer = ti.getTargetsObjects()[0];
		
		// The deserialization has to be done by hand since it seems VOMS
		// does not correctly encode the ACTargets extension...
		ASN1Sequence targetSequence = (ASN1Sequence) asn1TargetContainer.getDERObject();
		Target[] asn1Targets = new Target[targetSequence.size()];
		
		int count = 0;
        
		for (@SuppressWarnings("rawtypes")
		Enumeration e = targetSequence.getObjects(); e.hasMoreElements();){
        	
        	// There's one sequence more than expected here that makes
        	// the bc constructor fail...
        	ASN1Sequence seq = (ASN1Sequence) e.nextElement();
            ASN1TaggedObject val = (ASN1TaggedObject) seq.getObjectAt(0);    
        	asn1Targets[count++] = Target.getInstance(val);
        }
		
        // Extract the actual string 
        for (Target t: asn1Targets){
				
			GeneralName targetURI = t.getTargetName();
				
			if (targetURI.getTagNo() != GeneralName.uniformResourceIdentifier)
				raiseACNonConformantError("wrong AC target extension encoding. Only URI targets are supported.");
				
			String targetString = ((DERIA5String)targetURI.getName()).getString(); 
			targets.add(targetString);
		}
		return targets;
	}
	
	private static void raiseACNonConformantError(String errorString){
		throw new VOMSError("Non conformant VOMS Attribute certificate: "+errorString);
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
			raiseACNonConformantError("unsupported policy authority encoding '"+policyAuthority+"'");
		
		return policyAuthority;
		
	}
	
	/**
	 * Deserializes the information in a list of VOMS attribute certificates.
	 * @param acs a {@link List} of VOMS acs
	 * @return a possibly empty list of {@link VOMSAttribute}
	 */
	public static List<VOMSAttribute> deserializeVOMSAttributes(List<AttributeCertificate> acs){
		
		if (acs == null || acs.size() == 0)
			return Collections.emptyList();
		
		List<VOMSAttribute> attributes = new ArrayList<VOMSAttribute>();
		for (AttributeCertificate a : acs){
			attributes.add(deserializeVOMSAttributes(a));
		}
		
		return attributes;
	}
	
	/**
	 * Deserializes the information in a VOMS attribute certificate.
	 * @param ac a VOMS {@link AttributeCertificate}
	 * @return a {@link VOMSAttribute} object which provides more convenient access to the VOMS authorization information
	 */
	public static VOMSAttribute deserializeVOMSAttributes(AttributeCertificate ac) {
		
		VOMSAttributesImpl attrs = new VOMSAttributesImpl();
		
		X509AttributeCertificateHolder acHolder = new X509AttributeCertificateHolder(ac);
		Attribute[] asn1Attrs = acHolder.getAttributes(VOMS_FQANS_OID);
		
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
			
			attrs.setNotBefore(acHolder.getNotBefore());
			attrs.setNotAfter(acHolder.getNotAfter());
			attrs.setSignature(acHolder.getSignature());
			attrs.setGenericAttributes(deserializeGAs(acHolder));
			attrs.setAACertificates(deserializeACCerts(acHolder));
			attrs.setTargets(deserializeACTargets(acHolder));
			
			attrs.setVOMSAC(acHolder);
			
			try{
				
				attrs.setIssuer(new X500Principal(acHolder.getIssuer().getNames()[0].getEncoded()));			
				attrs.setHolder(new X500Principal(acHolder.getHolder().getIssuer()[0].getEncoded()));
				attrs.setHolderSerialNumber(acHolder.getHolder().getSerialNumber());
			
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
		
		X509Extension gasExtension = ac.getExtension(VOMS_GENERIC_ATTRS_OID);
		
		if (gasExtension == null)
			return gas;
		
		// SEQUENCE of TagList - contains just one taglist element
		ASN1Sequence tagContainerSeq = (ASN1Sequence) gasExtension.getParsedValue();
		if (tagContainerSeq.size() != 1)
			raiseACNonConformantError("unsupported generic attributes container format.");
		
		// TagList -  this also should be a sigle element sequence
		ASN1Sequence tagListSeq = (ASN1Sequence) tagContainerSeq.getObjectAt(0);
		if (tagListSeq.size() != 1)
			raiseACNonConformantError("unsupported taglist format.");

		// Down one level
		tagListSeq = (ASN1Sequence) tagListSeq.getObjectAt(0);
		
		// TODO: check policyAuthority!!
		// GeneralNames policyAuthority = GeneralNames.getInstance(tagListSeq.getObjectAt(0));
		
		// tags SEQUENCE OF Tag 
		ASN1Sequence tags = (ASN1Sequence) tagListSeq.getObjectAt(1);
		
		@SuppressWarnings("unchecked")
		Enumeration<ASN1Sequence> e = tags.getObjects();
		while(e.hasMoreElements()){
			
			ASN1Sequence theActualTag = e.nextElement();
			
			if (theActualTag.size() != 3)
				raiseACNonConformantError("unsupported tag format.");
			
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
		
		X509Extension e = ac.getExtension(VOMS_CERTS_OID);
		
		if (e == null)
			return null;
		
		ASN1Sequence certSeq = (ASN1Sequence)e.getParsedValue();
		if (certSeq.size() != 1)
			raiseACNonConformantError("unsupported accerts format.");
		
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
