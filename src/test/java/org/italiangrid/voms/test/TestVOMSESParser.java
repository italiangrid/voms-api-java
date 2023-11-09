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
package org.italiangrid.voms.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSESParser;
import org.italiangrid.voms.request.VOMSESParserFactory;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.junit.jupiter.api.Test;

public class TestVOMSESParser {

  @Test
  public void testNonExistingFileParser() {

    String nonExistentFile = "/this/file/doesnt/exist";
    VOMSESParser parser = VOMSESParserFactory.newVOMSESParser();

    try {
      @SuppressWarnings("unused")
      List<VOMSServerInfo> info = parser.parse(new File(nonExistentFile));
    } catch (VOMSError e) {
      assertEquals("VOMSES file does not exist: " + nonExistentFile, e.getMessage());
      return;
    }

    fail("Parsing of non existent VOMSES file succeeded.");
  }

  @Test
  public void testValidStringParsing() throws URISyntaxException {

    String validVomsesString =
        "\"alice\" \"lcg-voms.cern.ch\" \"15000\" \"/DC=ch/DC=cern/OU=computers/CN=lcg-voms.cern.ch\" \"alice\" \"24\"";
    VOMSESParser parser = VOMSESParserFactory.newVOMSESParser();

    List<VOMSServerInfo> info = parser.parse(new StringReader(validVomsesString));

    assertEquals(1, info.size());
    VOMSServerInfo aliceInfo = info.get(0);

    assertEquals("alice", aliceInfo.getAlias());
    assertEquals("alice", aliceInfo.getVoName());
    assertEquals(new URI("voms://lcg-voms.cern.ch:15000"), aliceInfo.getURL());
    assertEquals("/DC=ch/DC=cern/OU=computers/CN=lcg-voms.cern.ch", aliceInfo.getVOMSServerDN());
  }

  @Test
  public void testValidFileParsing() throws URISyntaxException {

    String vomsesFile = "src/test/resources/vomses/eumed";
    VOMSESParser parser = VOMSESParserFactory.newVOMSESParser();
    List<VOMSServerInfo> info = parser.parse(new File(vomsesFile));
    assertEquals(2, info.size());

    VOMSServerInfo pdVoms = info.get(0);
    assertEquals("eumed", pdVoms.getAlias());
    assertEquals("eumed", pdVoms.getVoName());
    assertEquals(new URI("voms://voms-02.pd.infn.it:15016"), pdVoms.getURL());
    assertEquals("/C=IT/O=INFN/OU=Host/L=Padova/CN=voms-02.pd.infn.it", pdVoms.getVOMSServerDN());

    VOMSServerInfo cnafVoms = info.get(1);
    assertEquals("eumed", cnafVoms.getAlias());
    assertEquals("eumed", cnafVoms.getVoName());
    assertEquals(new URI("voms://voms2.cnaf.infn.it:15016"), cnafVoms.getURL());
    assertEquals("/C=IT/O=INFN/OU=Host/L=CNAF/CN=voms2.cnaf.infn.it", cnafVoms.getVOMSServerDN());
  }

  @Test
  public void testValidDirectoryParsing() throws URISyntaxException {

    String vomsesDir = "src/test/resources/vomses";
    VOMSESParser parser = VOMSESParserFactory.newVOMSESParser();
    List<VOMSServerInfo> info = parser.parse(new File(vomsesDir));
    assertEquals(5, info.size());
  }

  @Test
  public void testSingleCharAliasParsing() throws URISyntaxException {

    String validVomsesString =
        "\"a\" \"lcg-voms.cern.ch\" \"15000\" \"/DC=ch/DC=cern/OU=computers/CN=lcg-voms.cern.ch\" \"alice\" \"24\"";
    VOMSESParser parser = VOMSESParserFactory.newVOMSESParser();

    List<VOMSServerInfo> info = parser.parse(new StringReader(validVomsesString));

    assertEquals(1, info.size());
    VOMSServerInfo aliceInfo = info.get(0);

    assertEquals("a", aliceInfo.getAlias());
    assertEquals("alice", aliceInfo.getVoName());
    assertEquals(new URI("voms://lcg-voms.cern.ch:15000"), aliceInfo.getURL());
    assertEquals("/DC=ch/DC=cern/OU=computers/CN=lcg-voms.cern.ch", aliceInfo.getVOMSServerDN());
  }
}
