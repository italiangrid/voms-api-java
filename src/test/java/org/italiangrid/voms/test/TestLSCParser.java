// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.store.impl.DefaultLSCFileParser;
import org.italiangrid.voms.store.impl.LSCFile;
import org.junit.Test;

public class TestLSCParser {

  @Test
  public void testParse() {

    DefaultLSCFileParser parser = new DefaultLSCFileParser();

    String lscContent = "# First line is a comment \n"
      + "--- second line should skipped \n" + "/C=it/O=org/CN=commonName\n"
      + "     \t\n" + "/C=it/O=org/CN=CA\n";

    LSCFile f = parser.parse("vo", "host",
      new ByteArrayInputStream(lscContent.getBytes()));

    assertNull(f.getFilename());

    assertEquals("vo", f.getVOName());

    assertEquals("host", f.getHostname());

    assertNotNull(f.getCertificateChainDescription());

    assertEquals(2, f.getCertificateChainDescription().size());

    assertEquals("/C=it/O=org/CN=commonName", f.getCertificateChainDescription().get(0));
    assertEquals("/C=it/O=org/CN=CA", f.getCertificateChainDescription().get(1));

  }

  @Test
  public void testOddLSCFileParseError() {

    String singleEntryLSCFile = "# This is a comment \n"
      + "/C=it/O=org/CN=commonName\n";

    String errorMessage = "LSC file parsing error: "
      + "Malformed LSC file (vo=vo, host=host): "
      + "Odd number of distinguished name entries.";

    DefaultLSCFileParser parser = new DefaultLSCFileParser();

    try {

      @SuppressWarnings("unused")
      LSCFile f = parser.parse("vo", "host", new ByteArrayInputStream(
        singleEntryLSCFile.getBytes()));

    } catch (VOMSError e) {

      assertEquals(errorMessage, e.getMessage());
      return;

    }

    fail("No error caught for malformed, single line LSC file parsing.");

  }

  @Test
  public void testEmptyLSCFileParseError() {

    DefaultLSCFileParser parser = new DefaultLSCFileParser();

    String emptyLSCContent = "# This is a comment";

    String errorMessage = "LSC file parsing error: "
      + "Malformed LSC file (vo=vo, host=host): "
      + "No distinguished name entries found.";

    try {

      @SuppressWarnings("unused")
      LSCFile f = parser.parse("vo", "host", new ByteArrayInputStream(
        emptyLSCContent.getBytes()));

    } catch (VOMSError e) {
      assertEquals(errorMessage, e.getMessage());
      return;
    }

    fail("No error caught for malformed, empty LSC file parsing.");
  }

  @Test
  public void testSlashInsideCommonNameIsIgnored() {

    DefaultLSCFileParser parser = new DefaultLSCFileParser();

    String malformedLSCContent = "/C=it/O=org/CN=commonName\n" + "/C=it/O=org/CN=common/Name";

    LSCFile f =
        parser.parse("vo", "host", new ByteArrayInputStream(malformedLSCContent.getBytes()));

    assertEquals(2, f.getCertificateChainDescription().size());
  }

  @Test
  public void testUnsupportedMultichainLSCFileParseSuccess() {

    DefaultLSCFileParser parser = new DefaultLSCFileParser();

    String multichainLSCContent = "/C=IT/O=IGI/CN=test-host.cnaf.infn.it\n"
        + "/C=IT/O=IGI/CN=Test CA\n" + "------NEXT CHAIN------\n"
        + "/C=IT/O=IGI/CN=test-host2.cnaf.infn.it\n" + "/C=IT/O=IGI/CN=Test CA";

    try {

      LSCFile f =
          parser.parse("vo", "host", new ByteArrayInputStream(multichainLSCContent.getBytes()));
      assertEquals(2, f.getCertificateChainDescription().size());
      assertEquals("/C=IT/O=IGI/CN=test-host.cnaf.infn.it", f.getCertificateChainDescription().get(0));
      assertEquals("/C=IT/O=IGI/CN=Test CA", f.getCertificateChainDescription().get(1));

    } catch (VOMSError e) {
      fail("No error expected for malformed, empty LSC file parsing.");
      return;
    }
  }


  @Test
  public void testNonExistingFileParse() {

    DefaultLSCFileParser parser = new DefaultLSCFileParser();

    String nonExistentFile = "/this/file/doesnt/exist";

    try {

      @SuppressWarnings("unused")
      LSCFile f = parser.parse("vo", "host", new File(nonExistentFile));

    } catch (VOMSError e) {

      assertEquals("LSC file does not exist: " + nonExistentFile,
        e.getMessage());

      return;

    }

    fail("VOMS error not thrown for non existing LSC file parsing attempt.");

  }
}
