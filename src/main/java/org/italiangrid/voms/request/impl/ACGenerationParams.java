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

/**
 * This class represents the parameters required for generating an Attribute Certificate (AC).
 * It encapsulates various attributes such as Virtual Organization (VO), Fully Qualified Attribute Names (FQANs),
 * Generic Attributes (GAs), host details, validity periods, and a serial number.
 */
public class ACGenerationParams {

  /** The Virtual Organization name. */
  final String vo;
  /** The list of Fully Qualified Attribute Names. */
  final List<String> fqans;
  /** The list of Generic Attributes. */
  final List<VOMSGenericAttribute> gas;
  /** The host associated with the AC request. */
  final String host;
  /** The port associated with the AC request. */
  final int port;
  /** The start time of the validity period. */
  final Date notBefore;
  /** The end time of the validity period. */
  final Date notAfter;
  /** The serial number of the attribute certificate. */
  final BigInteger serialNo;

  /**
   * Private constructor to initialize an instance using the Builder pattern.
   *
   * @param builder the builder instance used to construct this object
   */
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

  /** @return the Virtual Organization name */
  public String getVo() {
    return vo;
  }

  /** @return the list of Fully Qualified Attribute Names */
  public List<String> getFqans() {
    return fqans;
  }

  /** @return the list of Generic Attributes */
  public List<VOMSGenericAttribute> getGas() {
    return gas;
  }

  /** @return the host associated with the AC request */
  public String getHost() {
    return host;
  }

  /** @return the port associated with the AC request */
  public int getPort() {
    return port;
  }

  /** @return the start time of the validity period */
  public Date getNotBefore() {
    return notBefore;
  }

  /** @return the end time of the validity period */
  public Date getNotAfter() {
    return notAfter;
  }

  /** @return the serial number of the attribute certificate */
  public BigInteger getSerialNo() {
    return serialNo;
  }

  /**
   * Builder class for constructing {@link ACGenerationParams} instances.
   */
  public static class Builder {
    String vo = "test";
    List<String> fqans;
    List<VOMSGenericAttribute> gas;
    String host = "voms.example";
    int port = 15000;
    Date notBefore;
    Date notAfter;
    BigInteger serialNo = BigInteger.valueOf(0L);

    /** Default constructor initializing lists. */
    public Builder() {
      fqans = new ArrayList<>();
      gas = new ArrayList<>();
    }

    /**
     * Sets the VO.
     * @param vo the Virtual Organization name
     * @return the Builder instance
     */
    public Builder vo(String vo) {
      this.vo = vo;
      return this;
    }

    /**
     * Adds a Fully Qualified Attribute Name.
     * @param fqan the FQAN to add
     * @return the Builder instance
     */
    public Builder fqan(String fqan) {
      fqans.add(fqan);
      return this;
    }

    /**
     * Adds a Generic Attribute.
     * @param name the attribute name
     * @param value the attribute value
     * @param context the attribute context
     * @return the Builder instance
     */
    public Builder ga(String name, String value, String context) {
      VOMSGenericAttributeImpl ga = new VOMSGenericAttributeImpl();
      ga.setName(name);
      ga.setValue(value);
      ga.setContext(context);
      gas.add(ga);
      return this;
    }

    /**
     * Sets the host.
     * @param host the host
     * @return the Builder instance
     */
    public Builder host(String host) {
      this.host = host;
      return this;
    }

    /**
     * Sets the port.
     * @param port the port number
     * @return the Builder instance
     */
    public Builder port(int port) {
      this.port = port;
      return this;
    }

    /**
     * Sets the not-before validity time.
     * @param notBefore the start of the validity period
     * @return the Builder instance
     */
    public Builder notBefore(Date notBefore) {
      this.notBefore = notBefore;
      return this;
    }

    /**
     * Sets the not-after validity time.
     * @param notAfter the end of the validity period
     * @return the Builder instance
     */
    public Builder notAfter(Date notAfter) {
      this.notAfter = notAfter;
      return this;
    }

    /**
     * Sets the serial number.
     * @param serialNo the serial number
     * @return the Builder instance
     */
    public Builder serialNo(long serialNo) {
      this.serialNo = BigInteger.valueOf(serialNo);
      return this;
    }

    /**
     * Builds the {@link ACGenerationParams} instance.
     * @return a new ACGenerationParams object
     */
    public ACGenerationParams build() {
      return new ACGenerationParams(this);
    }
  }

  /** @return a new Builder instance */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Constructs an {@link ACGenerationParams} instance from system properties.
   *
   * @return a configured ACGenerationParams instance.
   */
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

