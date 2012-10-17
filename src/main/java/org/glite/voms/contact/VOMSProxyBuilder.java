/*********************************************************************
 *
 * Authors:
 *
 *      Vincenzo Ciaschini - vincenzo.ciaschini@cnaf.infn.it
 *      Andrea Ceccanti - andrea.ceccanti@cnaf.infn.it
 *
 * Uses some code originally developed by:
 *      Gidon Moont - g.moont@imperial.ac.uk
 *      Joni Hahkala - joni.hahkala@cern.ch
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004-2010.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Parts of this code may be based upon or even include verbatim pieces,
 * originally written by other people, in which case the original header
 * follows.
 *
 *********************************************************************/
/*
 This file is licensed under the terms of the Globus Toolkit Public
 License, found at http://www.globus.org/toolkit/download/license.html.
 */

package org.glite.voms.contact;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateParsingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.proxy.ProxyCertificate;
import eu.emi.security.authn.x509.proxy.ProxyCertificateOptions;
import eu.emi.security.authn.x509.proxy.ProxyGenerator;
import eu.emi.security.authn.x509.proxy.ProxyType;

class ExtensionData {
	String oid;
	DERObject obj;
	boolean critical;

	public static ExtensionData creator(String oid, boolean critical, DERObject obj) {
		ExtensionData ed = new ExtensionData();
		ed.obj = obj;
		ed.oid = oid;
		ed.critical = critical;

		return ed;
	}

	public static ExtensionData creator(String oid, DERObject obj) {
		ExtensionData ed = new ExtensionData();
		ed.obj = obj;
		ed.oid = oid;
		ed.critical = false;

		return ed;
	}

	public String getOID() {
		return oid;
	}

	public DERObject getObj() {
		return obj;
	}

	public boolean getCritical() {
		return critical;
	}
}

/**
 * 
 * This class implements VOMS X509 proxy certificates creation.
 * 
 * @author Andrea Ceccanti
 * 
 */
public class VOMSProxyBuilder {

	private static final Logger log = LoggerFactory.getLogger(VOMSProxyBuilder.class);

	public static final int GT2_PROXY = 2;
	public static final int GT3_PROXY = 3;
	public static final int GT4_PROXY = 4;

	public static final int DEFAULT_PROXY_TYPE = GT2_PROXY;

	public static final int DEFAULT_DELEGATION_TYPE = VOMSProxyConstants.DELEGATION_FULL;

	public static final int DEFAULT_PROXY_LIFETIME = 86400;

	private static final String PROXY_CERT_INFO_V3_OID = "1.3.6.1.4.1.3536.1.222";
	private static final String PROXY_CERT_INFO_V4_OID = "1.3.6.1.5.5.7.1.14";

	/**
	 * 
	 * This methods builds an {@link AttributeCertificate} (AC) object starting
	 * from an array of bytes.
	 * 
	 * @param acBytes
	 *          the byte array containing the attribute certificate.
	 * @return the {@link AttributeCertificate} object
	 * @throws VOMSException
	 *           in case of parsing errors.
	 */
	public static AttributeCertificate buildAC(byte[] acBytes) {

		ByteArrayInputStream bai = new ByteArrayInputStream(acBytes);
		return AttributeCertificate.getInstance(bai);


	}

	/**
	 * 
	 * This method is used to create a VOMS proxy starting from the
	 * {@link UserCredentials} passed as arguments and including a list of
	 * {@link AttributeCertificate} objects that will be included in the proxy.
	 * 
	 * @param cred
	 *          the {@link UserCredentials} from which the proxy must be created.
	 * @param ACs
	 *          the list of {@link AttributeCertificate} objects.
	 * @param lifetime
	 *          the lifetime in seconds of the generated proxy.
	 * @param gtVersion
	 *          the version of globus to which the proxy conforms
	 * @return a {@link UserCredentials} object that represents the proxy.
	 * @throws VOMSException
	 *           if something goes wrong.
	 * 
	 * @author Vincenzo Ciaschini
	 * @author Andrea Ceccanti
	 * 
	 * 
	 */
	public static UserCredentials buildProxy(UserCredentials cred, List ACs, int lifetime, int gtVersion, int delegType,
			String policyType) {
		return buildProxy(cred, ACs, lifetime, gtVersion, delegType, policyType, 1024);
	}

	public static UserCredentials buildProxy(UserCredentials cred, List ACs, int lifetime, int gtVersion, int delegType,
			String policyType, int bits) {


		ProxyCertificateOptions proxyOptions = new ProxyCertificateOptions(cred.getUserChain());


		if (!ACs.isEmpty()) {
			ArrayList<AttributeCertificate> list = new ArrayList<AttributeCertificate>();

			Iterator<AttributeCertificate> i = ACs.iterator();

			while (i.hasNext())
				list.add(i.next());

			AttributeCertificate[] acs = list.toArray(new AttributeCertificate[list.size()]);

			proxyOptions.setAttributeCertificates(acs);
		}

		proxyOptions.setKeyLength(bits);
		proxyOptions.setLifetime(lifetime);

		switch (gtVersion) {
		case GT2_PROXY:
			proxyOptions.setType(ProxyType.LEGACY);
			break;
		case GT3_PROXY:
			proxyOptions.setType(ProxyType.DRAFT_RFC);
			break;
		case GT4_PROXY:
			proxyOptions.setType(ProxyType.RFC3820);
			break;
		default:
			throw new IllegalArgumentException("Unsopported proxy type: " + gtVersion);
		}

		switch (delegType) {
		case VOMSProxyConstants.DELEGATION_LIMITED:
			proxyOptions.setLimited(true);
			break;
		}


		ProxyCertificate proxy = null;
		try {
			proxy = ProxyGenerator.generate(proxyOptions, cred.getUserKey());
		} catch (InvalidKeyException e) {
			log.error(e.getMessage(), e);
		} catch (CertificateParsingException e) {
			log.error(e.getMessage(), e);
		} catch (SignatureException e) {
			log.error(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
		}


		return UserCredentials.instance(proxy.getPrivateKey(), proxy.getCertificateChain());

	}

	public static UserCredentials buildProxy(UserCredentials cred, int lifetime, int proxy_type) {
		return buildProxy(cred, lifetime, proxy_type, 1024);
	}

	public static UserCredentials buildProxy(UserCredentials cred, int lifetime, int proxy_type, int bits) {
		return buildProxy(cred, new ArrayList(), lifetime, GT2_PROXY, proxy_type, "", bits);
	}


	/**
	 * This method is write a globus proxy to an output stream.
	 * 
	 * @param cred
	 * @param os
	 */
	public static void saveProxy(UserCredentials cred, OutputStream os) {

		try {

			cred.save(os);
		} catch (IOException e) {
			log.error("Error saving generated proxy: " + e.getMessage());

			if (log.isDebugEnabled())
				log.error(e.getMessage(), e);
			throw new VOMSException("Error saving generated proxy: " + e.getMessage(), e);
		}

	}

	/**
	 * This method saves a globus proxy to a file.
	 * 
	 * @param cred
	 * @param filename
	 * @throws FileNotFoundException
	 */
	public static void saveProxy(UserCredentials cred, String filename) throws FileNotFoundException {

		saveProxy(cred, new FileOutputStream(filename));
	}

}
