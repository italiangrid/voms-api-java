package org.glite.voms.v2.asn1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.IetfAttrSyntax;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.glite.voms.v2.VOMSAttributes;
import org.glite.voms.v2.VOMSError;
import org.glite.voms.v2.ac.VOMSAttributesImpl;
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
			attrs.setHost(policyAuthority.substring(policyAuthority.indexOf(POLICY_AUTHORITY_SEP)+1, 
					policyAuthority.indexOf(":")+1));
			attrs.setPort(Integer.parseInt(policyAuthority.substring(policyAuthority.lastIndexOf(":")+1)));
			
			attrs.setFQANs(deserializeFQANs(attrSyntax));
			
			attrs.setNotBefore(holder.getNotBefore());
			attrs.setNotAfter(holder.getNotAfter());
			attrs.setSignature(holder.getSignature());
			
			try{
				
				attrs.setIssuer(new X500Principal(holder.getIssuer().getNames()[0].getEncoded()));			
				attrs.setHolder(new X500Principal(holder.getHolder().getIssuer()[0].getEncoded()));
			
			}catch (IOException e){
				throw new VOMSError("Error parsing attribute certificate issuer  or holder name: "+e.getMessage(),e);
			}
		}
		
		return attrs;
	}
	
	
}
