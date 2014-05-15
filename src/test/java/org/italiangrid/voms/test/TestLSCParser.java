/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2012.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.italiangrid.voms.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;

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

    assertEquals("vo", f.getVo());

    assertEquals("host", f.getHostname());

    assertNotNull(f.getCertificateChainDescription());

    assertEquals(2, f.getCertificateChainDescription().size());

    assertEquals("/C=it/O=org/CN=commonName", f
      .getCertificateChainDescription().get(0));
    assertEquals("/C=it/O=org/CN=CA", f.getCertificateChainDescription().get(1));

  }

  @Test
  public void testOddLSCFileParseError() {

    String singleEntryLSCFile = "# This is a comment \n"
      + "/C=it/O=org/CN=commonName\n";

    String errorMessage = "LSC file parsing error: "
      + "Malformed LSC file. It should contain an even number of "
      + "distinguished name entries expressed in OpenSSL slash-separated"
      + "format.";

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
      + "Malformed LSC file. No distinguished name entries found.";

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
  public void testNonExistingFileParse() {

    DefaultLSCFileParser parser = new DefaultLSCFileParser();

    String nonExistentFile = "/this/file/doesnt/exist";

    try {

      @SuppressWarnings("unused")
      LSCFile f = parser.parse("vo", "host", nonExistentFile);

    } catch (VOMSError e) {

      assertEquals("LSC file does not exist: " + nonExistentFile,
        e.getMessage());

      return;

    }

    fail("VOMS error not thrown for non existing LSC file parsing attempt.");

  }
}
