package org.italiangrid.voms.asn1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.X509Extension;

/**
 * A set of useful constants for ASN.1 parsing of VOMS attributes.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSConstants {

	/**
	 * The VOMS attribute certificate extension OID.
	 */
	public final ASN1ObjectIdentifier VOMS_EXTENSION_OID = new ASN1ObjectIdentifier("1.3.6.1.4.1.8005.100.100.5");
	
	/**
	 * The VOMS attributes OID.
	 */
	public final ASN1ObjectIdentifier VOMS_FQANS_OID = new ASN1ObjectIdentifier("1.3.6.1.4.1.8005.100.100.4");
	
	/**
	 * The VOMS Certs extension OID. 
	 */
	public final ASN1ObjectIdentifier VOMS_CERTS_OID = new ASN1ObjectIdentifier("1.3.6.1.4.1.8005.100.100.10");
	
	/**
	 * The VOMS Generic attributes extension OID.
	 */
	public final ASN1ObjectIdentifier VOMS_GENERIC_ATTRS_OID = new ASN1ObjectIdentifier("1.3.6.1.4.1.8005.100.100.11");
	
	public final Set<ASN1ObjectIdentifier> VOMS_HANDLED_EXTENSIONS = new HashSet<ASN1ObjectIdentifier>(Arrays.asList(
			VOMS_FQANS_OID, 
			VOMS_CERTS_OID,
			VOMS_GENERIC_ATTRS_OID,
			X509Extension.targetInformation,
			X509Extension.noRevAvail,
			X509Extension.authorityKeyIdentifier));
	
}
