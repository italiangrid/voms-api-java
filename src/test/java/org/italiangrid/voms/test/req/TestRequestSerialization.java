// SPDX-FileCopyrightText: 2026 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test.req;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import org.italiangrid.voms.request.impl.DefaultVOMSACRequest;
import org.italiangrid.voms.request.impl.DefaultVOMSServerInfo;
import org.italiangrid.voms.request.impl.RESTServiceURLBuilder;
import org.italiangrid.voms.request.impl.VOMSRequestFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

class TestRequestSerialization {

  private final DefaultVOMSServerInfo endpoint = new DefaultVOMSServerInfo();
  private final VOMSRequestFactory factory = VOMSRequestFactory.instance();
  private String previousOrder;
  private String previousTargets;
  private long previousLifetime;

  @BeforeEach
  void setUp() {
    endpoint.setVoName("test.vo");
    endpoint.setURL(URI.create("voms://voms.example:15000"));
    previousOrder = factory.getOrderString();
    previousTargets = factory.getTargetString();
    previousLifetime = factory.getLifetime();
    factory.setOrderString(null);
    factory.setTargetString(null);
  }

  @AfterEach
  void restoreFactoryOptions() {
    factory.setOrderString(previousOrder);
    factory.setTargetString(previousTargets);
    factory.setLifetime(previousLifetime);
  }

  @Test
  void restDefaultsToEndpointVOAndDefaultLifetime() {
    DefaultVOMSACRequest request = new DefaultVOMSACRequest.Builder("alias").build();
    URL url = new RESTServiceURLBuilder().build(endpoint, request);
    assertEquals("https", url.getProtocol());
    assertEquals("voms.example", url.getHost());
    assertEquals(15000, url.getPort());
    assertEquals("/generate-ac", url.getPath());
    assertEquals("fqans=/test.vo&lifetime=43200", url.getQuery());
  }

  @Test
  void restPreservesFQANOrderAndRequestedLifetime() {
    DefaultVOMSACRequest request =
        new DefaultVOMSACRequest.Builder("test.vo")
            .fqans(Arrays.asList("/test.vo/Role=production", "/test.vo/group"))
            .lifetime(900)
            .build();
    assertEquals(
        "fqans=/test.vo/Role=production,/test.vo/group&lifetime=900",
        new RESTServiceURLBuilder().build(endpoint, request).getQuery());
  }

  @Test
  void nullOptionalListsLeaveRequestDefaultsIntact() {
    DefaultVOMSACRequest request =
        new DefaultVOMSACRequest.Builder("test.vo").fqans(null).targets(null).build();
    assertEquals("test.vo", request.getVoName());
    assertTrue(request.getRequestedFQANs().isEmpty());
    assertTrue(request.getTargets().isEmpty());
    assertEquals(43200, request.getLifetime());
  }

  @Test
  void legacyDefaultsToEndpointGroup() {
    Document xml =
        factory.buildRequest(new DefaultVOMSACRequest.Builder("alias").build(), endpoint);
    assertEquals("voms", xml.getDocumentElement().getTagName());
    assertEquals(1, xml.getElementsByTagName("command").getLength());
    assertEquals("G/test.vo", text(xml, "command", 0));
    assertEquals("43200", text(xml, "lifetime", 0));
    assertEquals(0, xml.getElementsByTagName("order").getLength());
    assertEquals(0, xml.getElementsByTagName("targets").getLength());
  }

  @Test
  void legacySerializesEachCommandKindInRequestOrder() {
    DefaultVOMSACRequest request =
        new DefaultVOMSACRequest.Builder("test.vo")
            .fqans(Arrays.asList("all", "/test.vo/group", "Role=admin", "/test.vo/Role=production"))
            .lifetime(900)
            .build();
    Document xml = factory.buildRequest(request, endpoint);
    assertEquals(4, xml.getElementsByTagName("command").getLength());
    assertEquals("A", text(xml, "command", 0));
    assertEquals("G/test.vo/group", text(xml, "command", 1));
    assertEquals("Radmin", text(xml, "command", 2));
    assertEquals("B/test.vo:production", text(xml, "command", 3));
    assertEquals("1", text(xml, "base64", 0));
    assertEquals("4", text(xml, "version", 0));
    assertEquals("900", text(xml, "lifetime", 0));
  }

  @Test
  void legacyIncludesConfiguredTargetsAndDoesNotReusePreviousLifetime() {
    factory.setTargetString("host-a.example,host-b.example");
    factory.setOrderString("");
    Document first =
        factory.buildRequest(
            new DefaultVOMSACRequest.Builder("test.vo").lifetime(900).build(), endpoint);
    assertEquals("host-a.example,host-b.example", text(first, "targets", 0));
    assertEquals("900", text(first, "lifetime", 0));
    factory.setTargetString("");
    Document second =
        factory.buildRequest(new DefaultVOMSACRequest.Builder("test.vo").build(), endpoint);
    assertEquals("43200", text(second, "lifetime", 0));
    assertEquals(0, second.getElementsByTagName("targets").getLength());
  }

  private String text(Document document, String name, int index) {
    return document.getElementsByTagName(name).item(index).getTextContent();
  }
}
