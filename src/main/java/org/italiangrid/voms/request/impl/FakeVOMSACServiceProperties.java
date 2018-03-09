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
