// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.request.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSESParser;
import org.italiangrid.voms.request.VOMSServerInfo;

/**
 * Implementation of the {@link org.italiangrid.voms.request.VOMSESParser} interface.
 * This class is responsible for parsing VOMSES configuration files and extracting
 * {@link org.italiangrid.voms.request.VOMSServerInfo} instances from them.
 *
 * <p>It supports parsing from files, directories, and readers, and ensures that
 * the VOMSES files exist and are readable before processing.</p>
 *
 * <p>VOMSES files typically contain server connection information in a structured format.</p>
 */
public class LegacyVOMSESParserImpl implements VOMSESParser {

  /**
   * Line parser used to process individual VOMSES lines.
   */
  private final VOMSESLineParser lineParser = new VOMSESLineParser();

  /**
   * Performs basic sanity checks on the provided file.
   *
   * @param f the file to check
   * @throws VOMSError if the file does not exist or is not readable
   */
  protected void fileSanityChecks(File f) {

    if (!f.exists()) {
      throw new VOMSError("VOMSES file does not exist: " + f.getAbsolutePath());
    }
    if (!f.canRead()) {
      throw new VOMSError("VOMSES file is not readable: " + f.getAbsolutePath());
    }
  }

  /**
   * Parses a single line from a VOMSES file.
   *
   * @param vomsesLine the line to parse
   * @return a {@link VOMSServerInfo} instance representing the parsed line
   * @throws URISyntaxException if the URI in the line is malformed
   */
  protected VOMSServerInfo parseLine(String vomsesLine)
    throws URISyntaxException {

    return lineParser.parse(vomsesLine);
  }

  /**
   * Parses VOMSES configuration from a {@link Reader}.
   *
   * @param vomsesReader the reader containing VOMSES configuration
   * @return a list of {@link VOMSServerInfo} instances extracted from the input
   */
  public List<VOMSServerInfo> parse(Reader vomsesReader) {

    BufferedReader reader = new BufferedReader(vomsesReader);

    String line = null;
    List<VOMSServerInfo> result = new ArrayList<VOMSServerInfo>();

    try {

      while ((line = reader.readLine()) != null) {

        // Ignore comments
        if (line.startsWith("#"))
          continue;

        // skip empty lines
        if (line.matches("\\s*$"))
          continue;

        VOMSServerInfo parsedInfo = parseLine(line);

        if (parsedInfo != null)
          result.add(parsedInfo);

      }

    } catch (Exception e) {

      throw new VOMSError("Error parsing VOMSES information...", e);
    }
    return result;
  }

  /**
   * Parses all VOMSES files in a given directory.
   *
   * @param directory the directory containing VOMSES files
   * @return a list of {@link VOMSServerInfo} instances parsed from the directory
   */
  protected List<VOMSServerInfo> parseDirectory(File directory) {

    Set<VOMSServerInfo> joinedServerInfo = new HashSet<VOMSServerInfo>();

    File[] certFiles = directory.listFiles(new FileFilter() {

      public boolean accept(File pathname) {

        return pathname.isFile() && !pathname.getName().startsWith(".");
      }
    });

    for (File f : certFiles)
      joinedServerInfo.addAll(parse(f));

    return new ArrayList<VOMSServerInfo>(joinedServerInfo);
  }

  /**
   * Parses a VOMSES file or directory.
   *
   * @param f the file or directory to parse
   * @return a list of {@link VOMSServerInfo} instances parsed from the file/directory
   * @throws VOMSError if the file is not found or an error occurs during parsing
   */
  public List<VOMSServerInfo> parse(File f) {

    fileSanityChecks(f);

    if (f.isDirectory())
      return parseDirectory(f);

    try {

      BufferedReader r = new BufferedReader(new FileReader(f));
      return parse(r);

    } catch (FileNotFoundException e) {
      throw new VOMSError("VOMSES file not found: " + f.getAbsolutePath(), e);

    } catch (VOMSError e) {
      throw new VOMSError("Error parsing VOMSES file: " + f.getAbsolutePath(),
        e);
    }
  }

}
