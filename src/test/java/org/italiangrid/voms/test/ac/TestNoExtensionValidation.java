// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test.ac;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateParsingException;
import java.util.List;

import org.junit.Assert;

import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.ac.VOMSACValidator;
import org.italiangrid.voms.test.utils.Fixture;
import org.italiangrid.voms.test.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.emi.security.authn.x509.impl.PEMCredential;
import eu.emi.security.authn.x509.proxy.ProxyCertificate;
import eu.emi.security.authn.x509.proxy.ProxyCertificateOptions;
import eu.emi.security.authn.x509.proxy.ProxyGenerator;
import eu.emi.security.authn.x509.proxy.ProxyType;
import java.io.IOException;

public class TestNoExtensionValidation implements Fixture{

	PEMCredential cred;

	@Before
	public void setUp() throws Exception {
		cred = new PEMCredential(holderKey, holderCert, keyPassword.toCharArray());
	}

	@After
	public void tearDown() throws Exception {
		cred = null;
	}

	@Test
	public void testNoExtensionValidation() throws InvalidKeyException, CertificateParsingException,
                SignatureException, NoSuchAlgorithmException, IOException {

                ProxyCertificateOptions options = new ProxyCertificateOptions(cred.getCertificateChain());
		options.setType(ProxyType.LEGACY);

		ProxyCertificate proxy = ProxyGenerator.generate(options, cred.getKey());

		VOMSACValidator validator = Utils.getVOMSValidator();
		List<VOMSAttribute> attrs = validator.validate(proxy.getCertificateChain());

		Assert.assertNotNull(attrs);
		Assert.assertTrue(attrs.isEmpty());

	}
}
