// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test;

import java.io.File;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSESParser;
import org.italiangrid.voms.request.VOMSESParserFactory;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.junit.jupiter.api.Assertions;
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
      Assertions.assertEquals("VOMSES file does not exist: " + nonExistentFile, e.getMessage());
      return;
    }

    Assertions.fail("Parsing of non existent VOMSES file succeeded.");
  }

  @Test
  public void testValidStringParsing() throws URISyntaxException {

    String validVomsesString =
        "\"alice\" \"lcg-voms.cern.ch\" \"15000\" \"/DC=ch/DC=cern/OU=computers/CN=lcg-voms.cern.ch\" \"alice\" \"24\"";
    VOMSESParser parser = VOMSESParserFactory.newVOMSESParser();

    List<VOMSServerInfo> info = parser.parse(new StringReader(validVomsesString));

    Assertions.assertEquals(1, info.size());
    VOMSServerInfo aliceInfo = info.get(0);

    Assertions.assertEquals("alice", aliceInfo.getAlias());
    Assertions.assertEquals("alice", aliceInfo.getVoName());
    Assertions.assertEquals(new URI("voms://lcg-voms.cern.ch:15000"), aliceInfo.getURL());
    Assertions.assertEquals(
        "/DC=ch/DC=cern/OU=computers/CN=lcg-voms.cern.ch", aliceInfo.getVOMSServerDN());
  }

  @Test
  public void testValidFileParsing() throws URISyntaxException {

    String vomsesFile = "src/test/resources/vomses/eumed";
    VOMSESParser parser = VOMSESParserFactory.newVOMSESParser();
    List<VOMSServerInfo> info = parser.parse(new File(vomsesFile));
    Assertions.assertEquals(2, info.size());

    VOMSServerInfo pdVoms = info.get(0);
    Assertions.assertEquals("eumed", pdVoms.getAlias());
    Assertions.assertEquals("eumed", pdVoms.getVoName());
    Assertions.assertEquals(new URI("voms://voms-02.pd.infn.it:15016"), pdVoms.getURL());
    Assertions.assertEquals(
        "/C=IT/O=INFN/OU=Host/L=Padova/CN=voms-02.pd.infn.it", pdVoms.getVOMSServerDN());

    VOMSServerInfo cnafVoms = info.get(1);
    Assertions.assertEquals("eumed", cnafVoms.getAlias());
    Assertions.assertEquals("eumed", cnafVoms.getVoName());
    Assertions.assertEquals(new URI("voms://voms2.cnaf.infn.it:15016"), cnafVoms.getURL());
    Assertions.assertEquals(
        "/C=IT/O=INFN/OU=Host/L=CNAF/CN=voms2.cnaf.infn.it", cnafVoms.getVOMSServerDN());
  }

  @Test
  public void testValidDirectoryParsing() throws URISyntaxException {

    String vomsesDir = "src/test/resources/vomses";
    VOMSESParser parser = VOMSESParserFactory.newVOMSESParser();
    List<VOMSServerInfo> info = parser.parse(new File(vomsesDir));
    Assertions.assertEquals(5, info.size());
  }

  @Test
  public void testSingleCharAliasParsing() throws URISyntaxException {

    String validVomsesString =
        "\"a\" \"lcg-voms.cern.ch\" \"15000\" \"/DC=ch/DC=cern/OU=computers/CN=lcg-voms.cern.ch\" \"alice\" \"24\"";
    VOMSESParser parser = VOMSESParserFactory.newVOMSESParser();

    List<VOMSServerInfo> info = parser.parse(new StringReader(validVomsesString));

    Assertions.assertEquals(1, info.size());
    VOMSServerInfo aliceInfo = info.get(0);

    Assertions.assertEquals("a", aliceInfo.getAlias());
    Assertions.assertEquals("alice", aliceInfo.getVoName());
    Assertions.assertEquals(new URI("voms://lcg-voms.cern.ch:15000"), aliceInfo.getURL());
    Assertions.assertEquals(
        "/DC=ch/DC=cern/OU=computers/CN=lcg-voms.cern.ch", aliceInfo.getVOMSServerDN());
  }
}
