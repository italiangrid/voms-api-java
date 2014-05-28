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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;

import org.hamcrest.CoreMatchers;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.impl.VOMSESLineParser;
import org.junit.Test;

public class TestVOMSESLineParser {

  @Test
  public void nullLineFailure() throws URISyntaxException {

    String line = null;
    try {

      VOMSESLineParser p = new VOMSESLineParser();
      p.parse(line);
      fail("No error raised.");

    } catch (VOMSError e) {
      assertNotNull("Got a null error message", e.getMessage());
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
      VOMSServerInfo i = p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {
      assertNotNull("Got a null error message", e.getMessage());
      assertThat(e.getMessage(),
        CoreMatchers
          .containsString("Invalid VOMSES line: empty 'vo alias' field."));
    }
  }

  @Test
  public void incompleteAlias() {

    String line = "\"incomplete-alias";
    try {
      VOMSESLineParser p = new VOMSESLineParser();
      VOMSServerInfo i = p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {
      assertNotNull("Got a null error message", e.getMessage());
      assertThat(e.getMessage(),
        CoreMatchers
          .containsString("Invalid VOMSES line: incomplete 'vo alias' field."));
    }
  }

  @Test
  public void incompleteHost() {

    String line = "\"alias\" \"voms.cnaf.infn.it";
    try {
      VOMSESLineParser p = new VOMSESLineParser();
      VOMSServerInfo i = p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {
      assertNotNull("Got a null error message", e.getMessage());
      assertThat(e.getMessage(),
        CoreMatchers
          .containsString("Invalid VOMSES line: incomplete 'voms host' field."));
    }
  }

  @Test
  public void onlyAlias() {

    String line = "\"ciccio\" ";
    try {
      VOMSESLineParser p = new VOMSESLineParser();
      VOMSServerInfo i = p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {
      assertNotNull("Got a null error message", e.getMessage());
      assertThat(e.getMessage(),
        CoreMatchers
          .containsString("Invalid VOMSES line: incomplete information"));
    }
  }

  @Test
  public void minimumInfoFailure() {

    String line = "\t\"a\"  \"voms.cern.ch\" \t \"15000\" \"DN=Illo\"";

    try {
      VOMSESLineParser p = new VOMSESLineParser();
      VOMSServerInfo i = p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {

      assertNotNull("Got a null error message", e.getMessage());
      assertThat(e.getMessage(),
        CoreMatchers
          .containsString("Invalid VOMSES line: incomplete information"));
    }
  }

  @Test
  public void minimumInfo() {

    String line = "\t\"a\"  \"voms.cern.ch\" \t \"15000\" \"DN=Illo\" \"alice\" ";

    VOMSESLineParser p = new VOMSESLineParser();
    VOMSServerInfo i = p.parse(line);

    assertThat(i.getAlias(), CoreMatchers.equalTo("a"));

    assertThat(i.getURL().toString(),
      CoreMatchers.equalTo("voms://voms.cern.ch:15000"));

    assertThat(i.getVoName(), CoreMatchers.equalTo("alice"));

    assertThat(i.getVOMSServerDN(), CoreMatchers.equalTo("DN=Illo"));
  }

  @Test
  public void whitespaceHandling() {

    String line = "\t\"a\"  \"voms.cern.ch\" \t \"15000\" \"DN=Illo\" \"alice\" \"24\"";

    VOMSESLineParser p = new VOMSESLineParser();
    VOMSServerInfo i = p.parse(line);

    assertThat(i.getAlias(), CoreMatchers.equalTo("a"));

    assertThat(i.getURL().toString(),
      CoreMatchers.equalTo("voms://voms.cern.ch:15000"));

    assertThat(i.getVoName(), CoreMatchers.equalTo("alice"));

    assertThat(i.getVOMSServerDN(), CoreMatchers.equalTo("DN=Illo"));
  }

  @Test
  public void tooManyFields() {

    String line = "\t\"a\"  \"voms.cern.ch\" \t \"15000\" \"DN=Illo\" \"alice\" \"24\" \"Too much\"";

    try {
      VOMSESLineParser p = new VOMSESLineParser();
      VOMSServerInfo i = p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {
      assertNotNull("Got a null error message", e.getMessage());
      assertThat(e.getMessage(),
        CoreMatchers.containsString("Invalid VOMSES line: too many fields!"));
    }

  }

  @Test
  public void invalidPort() {

    String line = "\t\"a\"  \"voms.cern.ch\" \t \"ciccio\" \"DN=Illo\" \"alice\"";

    try {
      VOMSESLineParser p = new VOMSESLineParser();
      VOMSServerInfo i = p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {
      assertNotNull("Got a null error message", e.getMessage());
      assertThat(e.getMessage(),
        CoreMatchers
          .containsString("Invalid VOMSES line: invalid port number."));
    }

  }

  @Test
  public void portOutOfRange1() {

    String line = "\t\"a\"  \"voms.cern.ch\" \t \"-1\" \"DN=Illo\" \"alice\"";

    try {
      VOMSESLineParser p = new VOMSESLineParser();
      VOMSServerInfo i = p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {
      assertNotNull("Got a null error message", e.getMessage());
      assertThat(e.getMessage(),
        CoreMatchers
          .containsString("Invalid VOMSES line: invalid port number: -1"));
    }
  }

  @Test
  public void portOutOfRange2() {

    String line = "\t\"a\"  \"voms.cern.ch\" \t \"65536\" \"DN=Illo\" \"alice\"";

    try {
      VOMSESLineParser p = new VOMSESLineParser();
      VOMSServerInfo i = p.parse(line);
      fail("No error raised.");
    } catch (VOMSError e) {
      assertNotNull("Got a null error message", e.getMessage());
      assertThat(e.getMessage(),
        CoreMatchers
          .containsString("Invalid VOMSES line: invalid port number: 65536"));
    }
  }

  @Test
  public void tooMultiCall() {

    String line0 = "\"a\" \"voms.cern.ch\" \"15000\" \"DN=Illo\" \"alice\"";
    String line1 = "\"b\" \"voms.cern.ch\" \"15001\" \"DN=IllY\" \"bolice\"";

    VOMSESLineParser p = new VOMSESLineParser();
    VOMSServerInfo i0 = p.parse(line0);
    VOMSServerInfo i1 = p.parse(line1);

    assertThat(i0.getAlias(), CoreMatchers.equalTo("a"));
    assertThat(i0.getURL().toString(),
      CoreMatchers.equalTo("voms://voms.cern.ch:15000"));
    assertThat(i0.getVoName(), CoreMatchers.equalTo("alice"));

    assertThat(i1.getAlias(), CoreMatchers.equalTo("b"));
    assertThat(i1.getURL().toString(),
      CoreMatchers.equalTo("voms://voms.cern.ch:15001"));
    assertThat(i1.getVoName(), CoreMatchers.equalTo("bolice"));
  }
}
