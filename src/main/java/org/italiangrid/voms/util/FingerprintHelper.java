/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2014.
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
package org.italiangrid.voms.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

/**
 * A utility class for computing fingerprints of X.509 certificates.
 * <p>
 * This class provides methods to generate a cryptographic fingerprint (hash) of an X.509
 * certificate using a specified digest algorithm.
 * </p>
 *
 * <p>
 * The default digest algorithm used is SHA-1.
 * </p>
 *
 */
public class FingerprintHelper {

  /** The default message digest algorithm used for computing fingerprints. */
  public static final String DEFAULT_DIGEST_ALGORITHM = "SHA-1";

  /**
   * Converts a byte array to a hexadecimal string representation.
   *
   * @param bytes the byte array to convert
   * @return a string containing the hexadecimal representation of the byte array
   */
  private static String hexify(byte[] bytes) {

    char[] hexDigits =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    StringBuffer buf = new StringBuffer(bytes.length * 2);

    for (int i = 0; i < bytes.length; ++i) {
      buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
      buf.append(hexDigits[bytes[i] & 0x0f]);
    }

    return buf.toString();
  }

  /**
   * Computes the fingerprint of an X.509 certificate using the default digest algorithm.
   *
   * @param cert the X.509 certificate for which to compute the fingerprint
   * @return the fingerprint of the certificate as a hexadecimal string
   * @throws NoSuchAlgorithmException if the specified digest algorithm is not available
   * @throws CertificateEncodingException if encoding the certificate fails
   */
  public static String getFingerprint(X509Certificate cert)
      throws NoSuchAlgorithmException, CertificateEncodingException {

    MessageDigest md = MessageDigest.getInstance(DEFAULT_DIGEST_ALGORITHM);
    byte[] der = cert.getEncoded();

    md.update(der);
    byte[] digest = md.digest();

    return hexify(digest);
  }

}
