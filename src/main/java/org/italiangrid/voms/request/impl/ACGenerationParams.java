/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.italiangrid.voms.request.impl;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.util.Objects.isNull;
import static org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties.FQANS;
import static org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties.GAS;
import static org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties.HOST;
import static org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties.NOT_AFTER;
import static org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties.NOT_BEFORE;
import static org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties.PORT;
import static org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties.SERIAL;
import static org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties.VO;
import static org.italiangrid.voms.util.GaParser.parseGaString;
import static org.italiangrid.voms.util.TimeUtils.parseDate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.italiangrid.voms.VOMSGenericAttribute;
import org.italiangrid.voms.ac.impl.VOMSGenericAttributeImpl;

public class ACGenerationParams {

  final String vo;
  final List<String> fqans;
  final List<VOMSGenericAttribute> gas;
  final String host;
  final int port;
  final Date notBefore;
  final Date notAfter;
  final BigInteger serialNo;

  private ACGenerationParams(Builder builder) {
    this.vo = builder.vo;
    this.fqans = builder.fqans;
    this.gas = builder.gas;
    this.host = builder.host;
    this.port = builder.port;
    this.notBefore = builder.notBefore;
    this.notAfter = builder.notAfter;
    this.serialNo = builder.serialNo;
  }

  public String getVo() {
    return vo;
  }


  public List<String> getFqans() {
    return fqans;
  }



  public List<VOMSGenericAttribute> getGas() {
    return gas;
  }



  public String getHost() {
    return host;
  }



  public int getPort() {
    return port;
  }


  public Date getNotBefore() {
    return notBefore;
  }

  public Date getNotAfter() {
    return notAfter;
  }

  public BigInteger getSerialNo() {
    return serialNo;
  }

  public static class Builder {
    String vo = "test";
    List<String> fqans;
    List<VOMSGenericAttribute> gas;
    String host = "voms.example";
    int port = 15000;
    Date notBefore;
    Date notAfter;
    BigInteger serialNo = BigInteger.valueOf(0L);

    public Builder() {
      fqans = new ArrayList<String>();
      gas = new ArrayList<VOMSGenericAttribute>();
    }

    public Builder vo(String vo) {
      this.vo = vo;
      return this;
    }

    public Builder fqan(String fqan) {
      fqans.add(fqan);
      return this;
    }

    public Builder ga(String name, String value, String context) {
      VOMSGenericAttributeImpl ga = new VOMSGenericAttributeImpl();

      ga.setName(name);
      ga.setValue(value);
      ga.setContext(context);

      gas.add(ga);

      return this;
    }

    public Builder host(String host) {
      this.host = host;
      return this;
    }

    public Builder port(int port) {
      this.port = port;
      return this;
    }

    public Builder notBefore(Date notBefore) {
      this.notBefore = notBefore;
      return this;
    }

    public Builder notAfter(Date notAfter) {
      this.notAfter = notAfter;
      return this;
    }

    public Builder serialNo(long serialNo) {
      this.serialNo = BigInteger.valueOf(serialNo);
      return this;
    }

    public ACGenerationParams build() {
      return new ACGenerationParams(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public static ACGenerationParams fromSystemProperties() {
    ACGenerationParams.Builder builder = ACGenerationParams.builder();
    builder.vo(VO.getSystemPropertyValue());
    String fqansString = FQANS.getSystemPropertyValue();

    for (String f : fqansString.split(",")) {
      String trimmedFqan = f.trim();
      if (!trimmedFqan.isEmpty()) {
        builder.fqan(trimmedFqan);
      }
    }
    builder.host(HOST.getSystemPropertyValue());
    builder.port(parseInt(PORT.getSystemPropertyValue()));
    builder.serialNo(parseLong(SERIAL.getSystemPropertyValue()));

    String notBefore = NOT_BEFORE.getSystemPropertyValue();
    if (!isNull(notBefore)) {
      builder.notBefore(parseDate(notBefore));
    }

    String notAfter = NOT_AFTER.getSystemPropertyValue();
    if (!isNull(notAfter)) {
      builder.notAfter(parseDate(notAfter));
    }

    String gaString = GAS.getSystemPropertyValue();

    if (!isNull(gaString)) {
      parseGaString(gaString).forEach(a -> builder.ga(a.getName(), a.getValue(), builder.vo));
    }

    return builder.build();
  }
}
