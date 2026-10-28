// SPDX-FileCopyrightText: 2026 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test.req;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSResponseParsingStrategy;
import org.italiangrid.voms.request.impl.LegacyVOMSResponseParsingStrategy;
import org.italiangrid.voms.request.impl.RESTVOMSResponseParsingStrategy;
import org.junit.jupiter.api.Test;

class TestVOMSResponses {

  private VOMSResponse parse(VOMSResponseParsingStrategy parser, String body) {
    return parser.parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
  }

  private VOMSResponse rest(String body) {
    return parse(new RESTVOMSResponseParsingStrategy(), "<voms>" + body + "</voms>");
  }

  private VOMSResponse legacy(String body) {
    return parse(new LegacyVOMSResponseParsingStrategy(), "<voms>" + body + "</voms>");
  }

  private String item(int code, String message) {
    return "<item><number>" + code + "</number><message>" + message + "</message></item>";
  }

  @Test
  void emptyResponsesHaveNoDiagnosticsAndDefaultVersion() {
    for (VOMSResponse response : new VOMSResponse[] {rest(""), legacy("")}) {
      assertEquals(0, response.getVersion());
      assertFalse(response.hasErrors());
      assertFalse(response.hasWarnings());
      assertNull(response.errorMessages());
      assertNull(response.warningMessages());
    }
  }

  @Test
  void restAcceptsMissingOrEmptyAC() {
    assertNull(rest("").getAC());
    assertNull(rest("<ac/>").getAC());
  }

  @Test
  void restDecodesBase64AndRoundTripsXML() {
    VOMSResponse response = rest("<version>4</version><ac>\nAAEC\n/f7/\n</ac>");
    byte[] expected = {0, 1, 2, (byte) 253, (byte) 254, (byte) 255};
    assertEquals(4, response.getVersion());
    assertArrayEquals(expected, response.getAC());
    VOMSResponse reparsed = parse(new RESTVOMSResponseParsingStrategy(), response.getXMLAsString());
    assertEquals(4, reparsed.getVersion());
    assertArrayEquals(expected, reparsed.getAC());
  }

  @Test
  void restMapsAllErrorCodesAndPreservesMessageOrder() {
    String[] codes = {"NoSuchUser", "BadRequest", "SuspendedUser", "InternalError", "UnknownError"};
    int[] expected = {1001, 1005, 1004, 1006, 1006};
    StringBuilder xml = new StringBuilder();
    for (String code : codes) {
      xml.append("<error><code>")
          .append(code)
          .append("</code><message>")
          .append(code)
          .append(" &amp; detail</message></error>");
    }
    VOMSResponse response = rest(xml.toString());
    assertTrue(response.hasErrors());
    assertFalse(response.hasWarnings());
    assertEquals(codes.length, response.errorMessages().length);
    for (int i = 0; i < codes.length; i++) {
      assertEquals(expected[i], response.errorMessages()[i].getCode());
      assertEquals(codes[i] + " & detail", response.errorMessages()[i].getMessage());
    }
  }

  @Test
  void restClassifiesWarningsAndPreservesMessages() {
    String[] messages = {
      "validity reduced", "selected group missing", "contains attributes", "other warning"
    };
    int[] codes = {2, 1, 3, 4};
    StringBuilder xml = new StringBuilder();
    for (String message : messages) {
      xml.append("<warning>").append(message).append("</warning>");
    }
    VOMSResponse response = rest(xml.toString());
    assertTrue(response.hasWarnings());
    assertFalse(response.hasErrors());
    assertEquals(messages.length, response.warningMessages().length);
    for (int i = 0; i < messages.length; i++) {
      assertEquals(codes[i], response.warningMessages()[i].getCode());
      assertEquals(messages[i], response.warningMessages()[i].getMessage());
    }
  }

  @Test
  void legacySeparatesWarningsFromErrorsAtTheBoundary() {
    VOMSResponse response =
        legacy(item(999, "warning") + item(1000, "error") + item(1005, "bad request"));
    assertTrue(response.hasErrors());
    assertTrue(response.hasWarnings());
    assertEquals(1, response.warningMessages().length);
    assertEquals(999, response.warningMessages()[0].getCode());
    assertEquals("warning", response.warningMessages()[0].getMessage());
    assertEquals(2, response.errorMessages().length);
    assertEquals(1000, response.errorMessages()[0].getCode());
    assertEquals("error", response.errorMessages()[0].getMessage());
    assertEquals(1005, response.errorMessages()[1].getCode());
    assertEquals("bad request", response.errorMessages()[1].getMessage());
  }

  @Test
  void legacyReportsOnlyTheDiagnosticCategoryPresent() {
    VOMSResponse warning = legacy(item(1, "warning"));
    assertTrue(warning.hasWarnings());
    assertFalse(warning.hasErrors());
    assertNull(warning.errorMessages());
    VOMSResponse error = legacy(item(1001, "error"));
    assertTrue(error.hasErrors());
    assertFalse(error.hasWarnings());
    assertNull(error.warningMessages());
  }

  @Test
  void legacyDecodesItsAlphabetAndRoundTripsXML() {
    VOMSResponse response = legacy("<version>3</version><ac>aaec</ac>");
    assertEquals(3, response.getVersion());
    assertArrayEquals(new byte[] {0, 1, 2}, response.getAC());
    VOMSResponse reparsed =
        parse(new LegacyVOMSResponseParsingStrategy(), response.getXMLAsString());
    assertEquals(3, reparsed.getVersion());
    assertArrayEquals(response.getAC(), reparsed.getAC());
  }

  @Test
  void legacyAlsoAcceptsNewlineDelimitedStandardBase64() {
    assertArrayEquals(new byte[] {0, 1, 2}, legacy("<ac>\nAAEC\n</ac>").getAC());
  }

  @Test
  void parsersRejectMalformedXMLAndCanParseTheNextResponse() {
    for (VOMSResponseParsingStrategy parser :
        new VOMSResponseParsingStrategy[] {
          new RESTVOMSResponseParsingStrategy(), new LegacyVOMSResponseParsingStrategy()
        }) {
      assertThrows(VOMSError.class, () -> parse(parser, "<voms>"));
      assertEquals(4, parse(parser, "<voms><version>4</version></voms>").getVersion());
    }
  }
}
