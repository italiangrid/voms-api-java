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

public class LegacyVOMSESParserImpl implements VOMSESParser {

  private final VOMSESLineParser lineParser = new VOMSESLineParser();

  protected void fileSanityChecks(File f) {

    if (!f.exists())
      throw new VOMSError("VOMSES file does not exist: " + f.getAbsolutePath());
    if (!f.canRead())
      throw new VOMSError("VOMSES file is not readable: " + f.getAbsolutePath());
  }

  protected VOMSServerInfo parseLine(String vomsesLine)
    throws URISyntaxException {

    return lineParser.parse(vomsesLine);
  }

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
