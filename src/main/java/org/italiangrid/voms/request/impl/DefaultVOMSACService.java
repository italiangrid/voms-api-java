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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSACService;
import org.italiangrid.voms.request.VOMSESLookupStrategy;
import org.italiangrid.voms.request.VOMSProtocol;
import org.italiangrid.voms.request.VOMSProtocolError;
import org.italiangrid.voms.request.VOMSProtocolListener;
import org.italiangrid.voms.request.VOMSRequestListener;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.VOMSServerInfoStore;
import org.italiangrid.voms.request.VOMSServerInfoStoreListener;
import org.italiangrid.voms.util.NullListener;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.X509Credential;

/**
 * The default implementation of the {@link VOMSACService}.
 * 
 * 
 * @author Valerio Venturi
 * @author Andrea Ceccanti
 * 
 */
public class DefaultVOMSACService implements VOMSACService {

  /**
   * The listener that will be informed about request events
   */
  protected final VOMSRequestListener requestListener;

  /**
   * The listener that will be informed about low-level protocol details
   */
  protected final VOMSProtocolListener protocolListener;

  /**
   * The validator used for the SSL handshake
   */
  protected final X509CertChainValidatorExt validator;

  /**
   * The store used to keep VOMS server contact information.
   */
  protected final VOMSServerInfoStore serverInfoStore;

  /**
   * The http protocol implementation
   */
  protected final VOMSProtocol httpProtocol;

  /**
   * The voms legacy protocol implementation
   */
  protected final VOMSProtocol legacyProtocol;

  /**
   * Whether VOMS legacy protocol should be used as a fallback when REST protocol fails
   */
  protected final boolean legacyProtocolEnabled;

  /**
   * Constructor which builds a {@link DefaultVOMSACService} from a {@link Builder}
   * 
   * @param builder the builder object that provides the settings for this {@link VOMSACService}
   */
  protected DefaultVOMSACService(Builder builder) {

    this.validator = builder.validator;
    this.requestListener = builder.requestListener;
    this.protocolListener = builder.protocolListener;
    this.serverInfoStore = builder.serverInfoStore;
    this.httpProtocol = builder.httpProtocol;
    this.legacyProtocol = builder.legacyProtocol;
    this.legacyProtocolEnabled = builder.legacyProtocolEnabled;
  }

  /**
   * Extracts an AC from a VOMS response
   * 
   * @param request the request
   * @param response the received response
   * @return a possibly <code>null</code> {@link AttributeCertificate} object
   */
  protected AttributeCertificate getACFromResponse(VOMSACRequest request, VOMSResponse response) {

    byte[] acBytes = response.getAC();

    if (acBytes == null)
      return null;

    ASN1InputStream asn1InputStream = new ASN1InputStream(acBytes);

    AttributeCertificate attributeCertificate = null;

    try {

      attributeCertificate = AttributeCertificate.getInstance(asn1InputStream.readObject());

      asn1InputStream.close();
      return attributeCertificate;

    } catch (Throwable e) {

      requestListener.notifyVOMSRequestFailure(request, null,
          new VOMSError("Error unmarshalling VOMS AC. Cause: " + e.getMessage(), e));

      return null;
    }
  }

  private VOMSResponse doRequest(VOMSProtocol protocol, VOMSServerInfo endpoint,
      X509Credential cred, VOMSACRequest req) {

    VOMSResponse response = null;

    try {

      response = protocol.doRequest(endpoint, cred, req);

    } catch (VOMSProtocolError e) {
      requestListener.notifyVOMSRequestFailure(req, endpoint, e);
    }

    return response;

  }

  /**
   * Handles errors included in the VOMS response
   * 
   * @param request the request
   * @param si the VOMS server endpoint information
   * @param response the received {@link VOMSResponse}
   */
  protected void handleErrorsInResponse(VOMSACRequest request, VOMSServerInfo si,
      VOMSResponse response) {

    if (response.hasErrors())
      requestListener.notifyErrorsInVOMSReponse(request, si, response.errorMessages());

  }

  /**
   * Handles warnings included in the VOMS response
   * 
   * @param request the request
   * @param si the VOMS server endpoint information
   * @param response the received {@link VOMSResponse}
   */
  protected void handleWarningsInResponse(VOMSACRequest request, VOMSServerInfo si,
      VOMSResponse response) {

    if (response.hasWarnings())
      requestListener.notifyWarningsInVOMSResponse(request, si, response.warningMessages());
  }

  public AttributeCertificate getVOMSAttributeCertificate(X509Credential credential,
      VOMSACRequest request) {

    List<VOMSServerInfo> vomsServerInfos = getVOMSServerInfos(request);

    if (vomsServerInfos.isEmpty())
      throw new VOMSError("VOMS server for VO " + request.getVoName() + " "
          + "is not known! Check your vomses configuration.");

    VOMSResponse response = null;

    AttributeCertificate vomsAC = null;

    for (VOMSServerInfo vomsServerInfo : vomsServerInfos) {

      requestListener.notifyVOMSRequestStart(request, vomsServerInfo);

      // Try HTTP request first
      response = doRequest(httpProtocol, vomsServerInfo, credential, request);

      // If failed, try legacy request
      if (response == null && legacyProtocolEnabled) {
        response = doRequest(legacyProtocol, vomsServerInfo, credential, request);
      }

      if (response == null) {

        if (legacyProtocolEnabled) {
          requestListener.notifyVOMSRequestFailure(request, vomsServerInfo,
              new VOMSError("REST and legacy VOMS endpoints failed."));
        }

        continue;
      }

      // Notify that the server was contacted successfully
      requestListener.notifyVOMSRequestSuccess(request, vomsServerInfo);

      // Notify errors
      handleErrorsInResponse(request, vomsServerInfo, response);

      // Notify warnings
      handleWarningsInResponse(request, vomsServerInfo, response);

      vomsAC = getACFromResponse(request, response);

      // Exit the loop only when succesfully get an AC
      // out of the VOMS server
      if (!response.hasErrors() && vomsAC != null) {
        return vomsAC;
      }

    }

    // if we reach this point we had failures in contacting
    // all known voms server for the VO
    requestListener.notifyVOMSRequestFailure(request, null, null);
    return null;

  }

  /**
   * Get VOMS server endpoint information that matches with the {@link VOMSACRequest} passed as
   * argument.
   * 
   * This method returns a random shuffle of the {@link VOMSServerInfo} objects that match the input
   * request.
   * 
   * @param request the request
   * @return a possibly empty {@link List} of {@link VOMSServerInfo} objects
   */
  protected List<VOMSServerInfo> getVOMSServerInfos(VOMSACRequest request) {

    List<VOMSServerInfo> vomsServerInfos =
        new ArrayList<VOMSServerInfo>(serverInfoStore.getVOMSServerInfo(request.getVoName()));

    if (!vomsServerInfos.isEmpty()) {
      Collections.shuffle(vomsServerInfos);
    }
    return vomsServerInfos;
  }

  /**
   * Creates a {@link DefaultVOMSACService} object. The {@link DefaultVOMSACService} parameters can
   * be set with the appropriate methods. Example:
   * 
   * <pre>
   * 
   * 
   * 
   * {
   *   &#064;code
   *   VOMSACService acService =
   *       new DefaultVOMSACService.Builder(certChainValidator).requestListener(requestListener)
   *         .serverInfoStoreListener(serverInfoStoreListener)
   *         .protocolListener(protocolListener)
   *         .build();
   * }
   * </pre>
   * 
   * 
   */
  public static class Builder {

    /**
     * The listener that will be informed about request events
     */
    private VOMSRequestListener requestListener = NullListener.INSTANCE;

    /**
     * The listener that will be informed about low-level protocol details
     */
    private VOMSProtocolListener protocolListener = NullListener.INSTANCE;

    /**
     * The listener that will be informed about server info store events
     */
    private VOMSServerInfoStoreListener storeListener = NullListener.INSTANCE;

    /**
     * The validator used for the SSL handshake
     */
    private X509CertChainValidatorExt validator;

    /**
     * The store used to keep VOMS server contact information.
     */
    private VOMSServerInfoStore serverInfoStore;

    /**
     * The provided strategy to lookup vomses information.
     */
    private VOMSESLookupStrategy vomsesLookupStrategy;

    /**
     * A list of paths where vomses information will be looked for, used to create the server info
     * store.
     */
    private List<String> vomsesLocations;

    /**
     * The connect timeout value
     */
    private int connectTimeout = AbstractVOMSProtocol.DEFAULT_CONNECT_TIMEOUT;

    /**
     * The read timeout used
     */
    private int readTimeout = AbstractVOMSProtocol.DEFAULT_READ_TIMEOUT;

    /**
     * Whether the client should skip hostname checking
     */
    private boolean skipHostnameChecks = true;

    /**
     * The http protocol implementation
     */
    protected VOMSProtocol httpProtocol;

    /**
     * The voms legacy protocol implementation
     */
    protected VOMSProtocol legacyProtocol;

    /**
     * Whether the client should attempt legacy protocol requests
     */
    private boolean legacyProtocolEnabled = false;

    /**
     * Creates a Builder for a {@link DefaultVOMSACService}.
     * 
     * @param certChainValidator the validator to use to setup the SSL connection and validate the
     *        certificates
     */
    public Builder(X509CertChainValidatorExt certChainValidator) {

      if (certChainValidator == null)
        throw new NullPointerException("Please provide a non-null certificate chain validator");

      this.validator = certChainValidator;
    }

    /**
     * Sets the request listener for the {@link DefaultVOMSACService} that this builder is creating
     * 
     * @param l the request listener that will receive notifications about request events
     * @return this {@link Builder} instance
     */
    public Builder requestListener(VOMSRequestListener l) {

      this.requestListener = l;
      return this;
    }

    /**
     * Sets the {@link VOMSServerInfoStoreListener} for the {@link DefaultVOMSACService} that this
     * builder is creating
     * 
     * @param sl the store listener that will receive notifications about store events
     * @return this {@link Builder} instance
     */
    public Builder serverInfoStoreListener(VOMSServerInfoStoreListener sl) {

      this.storeListener = sl;
      return this;
    }

    /**
     * Sets the {@link VOMSServerInfoStore} for the {@link DefaultVOMSACService} that this builder
     * is creating
     * 
     * @param sis a {@link VOMSServerInfoStore} object
     * @return this {@link Builder} instance
     */
    public Builder serverInfoStore(VOMSServerInfoStore sis) {

      this.serverInfoStore = sis;
      return this;
    }

    /**
     * Sets the {@link VOMSProtocolListener} for the {@link DefaultVOMSACService} that this builder
     * is creating
     * 
     * @param pl the {@link VOMSProtocolListener} that will receive notifications about protocol
     *        events
     * @return this {@link Builder} instance
     */
    public Builder protocolListener(VOMSProtocolListener pl) {

      this.protocolListener = pl;
      return this;
    }

    /**
     * Sets the connect timeout (in millisecods) for the {@link DefaultVOMSACService} that this
     * builder is creating
     * 
     * @param timeout the timeout value in milliseconds
     * @return this {@link Builder} instance
     */
    public Builder connectTimeout(int timeout) {

      this.connectTimeout = timeout;
      return this;
    }

    /**
     * Sets the read timeout (in milliseconds) for the {@link DefaultVOMSACService} that this
     * builder is creating
     * 
     * @param timeout the timeout value in milliseconds
     * @return this {@link Builder} instance
     */
    public Builder readTimeout(int timeout) {

      this.readTimeout = timeout;
      return this;
    }

    /**
     * Sets a flag to skip VOMS hostname checking. Allows for creative VOMS server side certificate
     * configuration.
     * 
     * @param s <code>true</code> to skip the checks, <code>false</code> otherwise
     * 
     * @return this {@link Builder} instance
     */
    public Builder skipHostnameChecks(boolean s) {

      this.skipHostnameChecks = s;
      return this;
    }

    /**
     * Sets the vomses lookup strategy for the {@link DefaultVOMSACService} that this builder is
     * creating
     * 
     * @param strategy the {@link VOMSESLookupStrategy} object
     * @return this {@link Builder} instance
     */
    public Builder vomsesLookupStrategy(VOMSESLookupStrategy strategy) {

      this.vomsesLookupStrategy = strategy;
      return this;
    }

    /**
     * Sets a list of locations that will be used to build a {@link VOMSESLookupStrategy} for the
     * {@link DefaultVOMSACService} that this builder is creating
     * 
     * @param vomsesLocations a list of paths where vomses information will be looked for
     * @return this {@link Builder} instance
     */
    public Builder vomsesLocations(List<String> vomsesLocations) {

      this.vomsesLocations = vomsesLocations;
      return this;
    }

    /**
     * Sets the http protocol implementation
     * 
     * @param httpProtocol the http protocol implementatino
     * @return this {@link Builder} instance
     */
    public Builder httpProtocol(VOMSProtocol httpProtocol) {

      this.httpProtocol = httpProtocol;
      return this;
    }

    /**
     * Sets the legacy protocol implementation
     * 
     * @param legacyProtocol the legacy protocol implementation
     * 
     * @return the {@link Builder}
     */
    public Builder legacyProtocol(VOMSProtocol legacyProtocol) {

      this.legacyProtocol = legacyProtocol;
      return this;
    }

    /**
     * Enables/disables the fallback the VOMS legacy protocol.
     * 
     * @param lpe <code>true</code> to enable the legacy protocol, <code>false</code> otherwise
     * 
     * @return this {@link Builder} instance
     */
    public Builder legacyProtocolEnabled(boolean lpe) {

      this.legacyProtocolEnabled = lpe;
      return this;
    }

    /**
     * Builds the server info store
     */
    protected void buildServerInfoStore() {

      if (serverInfoStore != null)
        return;

      serverInfoStore =
          new DefaultVOMSServerInfoStore.Builder().lookupStrategy(vomsesLookupStrategy)
            .storeListener(storeListener)
            .vomsesPaths(vomsesLocations)
            .build();
    }

    /**
     * Builds default protocols if needed
     */
    protected void buildProtocols() {

      if (httpProtocol == null) {

        RESTProtocol p = new RESTProtocol(validator, protocolListener, connectTimeout, readTimeout);

        p.setSkipHostnameChecks(skipHostnameChecks);

        httpProtocol = p;

      }

      if (legacyProtocol == null) {

        LegacyProtocol p =
            new LegacyProtocol(validator, protocolListener, connectTimeout, readTimeout);

        p.setSkipHostnameChecks(skipHostnameChecks);

        legacyProtocol = p;

      }
    }

    /**
     * Builds the {@link DefaultVOMSACService}
     * 
     * @return a {@link DefaultVOMSACService} configured as required by this builder
     */
    public DefaultVOMSACService build() {

      buildServerInfoStore();
      buildProtocols();
      return new DefaultVOMSACService(this);
    }
  }
}
