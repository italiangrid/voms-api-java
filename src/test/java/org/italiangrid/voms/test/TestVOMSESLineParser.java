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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URISyntaxException;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.impl.VOMSESLineParser;
import org.junit.jupiter.api.Test;

public class TestVOMSESLineParser {

  @Test
  public void nullLineFailure() throws URISyntaxException {

    String line = null;
    try {

      VOMSESLineParser p = new VOMSESLineParser();
      p.parse(line);
      fail("No error raised.");

    } catch (VOMSError e) {
      assertNotNull(e.getMessage(), "Got a null error message");
      assertEquals("Cannot parse a null VOMSES line", e.getMessage());
    }
  }

  @Test
  public void emptyLineReturnsNull() throws URISyntaxException {

    String line = "";

    VOMSESLineParser p = new VOMSESLineParser();
    VOMSServerInfo i = p.parse(line);
    assertNull(i);

  }

  @Test
  public void emptyAlias() {

    String line = "\"\"";
    try {
      VOMSESLineParser p = new VOMSESLineParser();
      p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {
      assertNotNull(e.getMessage(), "Got a null error message");
      assertTrue(e.getMessage().contains("Invalid VOMSES line: empty 'vo alias' field."));
    }
  }

  @Test
  public void incompleteAlias() {

    String line = "\"incomplete-alias";
    try {
      VOMSESLineParser p = new VOMSESLineParser();
      p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {
      assertNotNull(e.getMessage(), "Got a null error message");
      assertTrue(e.getMessage().contains("Invalid VOMSES line: incomplete 'vo alias' field."));
    }
  }

  @Test
  public void incompleteHost() {

    String line = "\"alias\" \"voms.cnaf.infn.it";
    try {
      VOMSESLineParser p = new VOMSESLineParser();
      p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {
      assertNotNull("Got a null error message", e.getMessage());
      assertTrue(e.getMessage().contains("Invalid VOMSES line: incomplete 'voms host' field."));
    }
  }

  @Test
  public void onlyAlias() {

    String line = "\"ciccio\" ";
    try {
      VOMSESLineParser p = new VOMSESLineParser();
      p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {
      assertNotNull("Got a null error message", e.getMessage());
      assertTrue(e.getMessage().contains("Invalid VOMSES line: incomplete information"));
    }
  }

  @Test
  public void minimumInfoFailure() {

    String line = "\t\"a\"  \"voms.cern.ch\" \t \"15000\" \"DN=Illo\"";

    try {
      VOMSESLineParser p = new VOMSESLineParser();
      p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {

      assertNotNull("Got a null error message", e.getMessage());
      assertTrue(e.getMessage().contains("Invalid VOMSES line: incomplete information"));
    }
  }

  @Test
  public void minimumInfo() {

    String line = "\t\"a\"  \"voms.cern.ch\" \t \"15000\" \"DN=Illo\" \"alice\" ";

    VOMSESLineParser p = new VOMSESLineParser();
    VOMSServerInfo i = p.parse(line);

    assertEquals("a", i.getAlias());
    assertEquals("voms://voms.cern.ch:15000", i.getURL().toString());
    assertEquals("alice", i.getVoName());
    assertEquals("DN=Illo", i.getVOMSServerDN());
  }

  @Test
  public void whitespaceHandling() {

    String line = "\t\"a\"  \"voms.cern.ch\" \t \"15000\" \"DN=Illo\" \"alice\" \"24\"";

    VOMSESLineParser p = new VOMSESLineParser();
    VOMSServerInfo i = p.parse(line);

    assertEquals("a", i.getAlias());

    assertEquals("voms://voms.cern.ch:15000", i.getURL().toString());

    assertEquals("alice", i.getVoName());

    assertEquals("DN=Illo", i.getVOMSServerDN());
  }

  @Test
  public void tooManyFields() {

    String line =
        "\t\"a\"  \"voms.cern.ch\" \t \"15000\" \"DN=Illo\" \"alice\" \"24\" \"Too much\"";

    try {
      VOMSESLineParser p = new VOMSESLineParser();
      p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {
      assertNotNull("Got a null error message", e.getMessage());
      assertEquals("Invalid VOMSES line: too many fields!", e.getMessage());
    }

  }

  @Test
  public void invalidPort() {

    String line = "\t\"a\"  \"voms.cern.ch\" \t \"ciccio\" \"DN=Illo\" \"alice\"";

    try {
      VOMSESLineParser p = new VOMSESLineParser();
      p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {
      assertNotNull("Got a null error message", e.getMessage());
      assertTrue(e.getMessage().contains("Invalid VOMSES line: invalid port number."));
    }

  }

  @Test
  public void portOutOfRange1() {

    String line = "\t\"a\"  \"voms.cern.ch\" \t \"-1\" \"DN=Illo\" \"alice\"";

    try {
      VOMSESLineParser p = new VOMSESLineParser();
      p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {
      assertNotNull("Got a null error message", e.getMessage());
      assertTrue(e.getMessage().contains("Invalid VOMSES line: invalid port number: -1"));
    }
  }

  @Test
  public void portOutOfRange2() {

    String line = "\t\"a\"  \"voms.cern.ch\" \t \"65536\" \"DN=Illo\" \"alice\"";

    try {
      VOMSESLineParser p = new VOMSESLineParser();
      p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {
      assertNotNull("Got a null error message", e.getMessage());
      assertTrue(e.getMessage().contains("Invalid VOMSES line: invalid port number: 65536"));
    }
  }

  @Test
  public void tooMultiCall() {

    String line0 = "\"a\" \"voms.cern.ch\" \"15000\" \"DN=Illo\" \"alice\"";
    String line1 = "\"b\" \"voms.cern.ch\" \"15001\" \"DN=IllY\" \"bolice\"";

    VOMSESLineParser p = new VOMSESLineParser();
    VOMSServerInfo i0 = p.parse(line0);
    VOMSServerInfo i1 = p.parse(line1);

    assertEquals("a", i0.getAlias());
    assertEquals("voms://voms.cern.ch:15000", i0.getURL().toString());
    assertEquals("alice", i0.getVoName());

    assertEquals("b", i1.getAlias());
    assertEquals("voms://voms.cern.ch:15001", i1.getURL().toString());
    assertEquals("bolice", i1.getVoName());
  }
}
