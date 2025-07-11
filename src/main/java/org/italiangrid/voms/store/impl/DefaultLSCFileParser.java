// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.store.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.store.LSCFileParser;

/**
 * The default implementation for the LSC file parser.
 * 
 * @author Andrea Ceccanti
 *
 */
public class DefaultLSCFileParser implements LSCFileParser {

  public static final String MALFORMED_LSC_FILE_ERROR_TEMPLATE = "LSC file parsing error: Malformed LSC file (vo=%s, host=%s): %s";

  private void checkFileExistanceAndReadabilty(File f) {

    if (!f.exists()) {
      throw new VOMSError("LSC file does not exist: " + f.getAbsolutePath());
    }
    if (!f.canRead()) {
      throw new VOMSError("LSC file is not readable: " + f.getAbsolutePath());
    }
  }

  public synchronized LSCFile parse(String vo, String hostname, InputStream is) {

    final String NEXT_CHAIN = "------NEXT CHAIN------";

    LSCFile lsc = new LSCFile();

    lsc.setHostname(hostname);
    lsc.setVo(vo);

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      List<String> certificateChainDescription = new ArrayList<String>();
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().startsWith("#") || line.trim().isEmpty()) {
          continue;
        }
        if (line.equalsIgnoreCase(NEXT_CHAIN)) {
          /* This parser doesn't support multi-chain LSC files */
          break;
        }
        if (line.startsWith("/")) {
          certificateChainDescription.add(line);
        }
      }
      validateChain(certificateChainDescription, vo, hostname);
      lsc.setCertificateChainDescription(certificateChainDescription);
    } catch (IOException e) {
      throw new VOMSError("LSC file parsing error: " + e.getMessage(), e);
    }
    if (lsc.getCertificateChainDescription().isEmpty()) {
      String errorMessage =
          String.format(MALFORMED_LSC_FILE_ERROR_TEMPLATE, vo, hostname, "No chains found.");
      throw new VOMSError(errorMessage);
    }
    return lsc;
  }

  private void validateChain(List<String> certificateChainDescription, String vo, String hostname) {
    if (certificateChainDescription.size() % 2 != 0) {
      String errorMessage = String.format(MALFORMED_LSC_FILE_ERROR_TEMPLATE, vo, hostname,
          "Odd number of distinguished name entries.");
      throw new VOMSError(errorMessage);
    }
    if (certificateChainDescription.size() == 0) {
      String errorMessage = String.format(MALFORMED_LSC_FILE_ERROR_TEMPLATE, vo, hostname,
          "No distinguished name entries found.");

      throw new VOMSError(errorMessage);
    }
  }

  public LSCFile parse(String vo, String hostname, File file) {

    LSCFile lsc = null;

    try {

      checkFileExistanceAndReadabilty(file);

      lsc = parse(vo, hostname, new FileInputStream(file));

      lsc.setFilename(file.getAbsolutePath());

    } catch (IOException e) {
      throw new VOMSError("LSC file parsing error: " + e.getMessage(), e);
    }

    return lsc;
  }
}
