// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.request.impl;

public enum FakeVOMSACServiceProperties {
  
  FAKE("voms.fake"),
  AA_CERT("voms.fake.aaCert", "/etc/grid-security/hostcert.pem"),
  AA_KEY("voms.fake.aaKey", "/etc/grid-security/hostkey.pem"),
  VO("voms.fake.vo", "test"),
  FQANS("voms.fake.fqans", "/test"),
  GAS("voms.fake.gas"),
  HOST("voms.fake.host", "voms.example"),
  PORT("voms.fake.port", "15000"),
  NOT_BEFORE("voms.fake.notBefore"),
  NOT_AFTER("voms.fake.notAfter"),
  SERIAL("voms.fake.serial", "0");
  
  private String propertyName;
  private String defaultValue;
  
  private FakeVOMSACServiceProperties(String propName, String defaultValue) {
    this.propertyName = propName;
    this.defaultValue = defaultValue;
  }
  
  private FakeVOMSACServiceProperties(String propName) {
    this(propName, null);
  }
  
  public String getPropertyName() {
    return propertyName;
  }
  
  public String getSystemPropertyValue() {
    return System.getProperty(getPropertyName(), defaultValue);
  }

}
