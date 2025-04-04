// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.request.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSServerInfo;

/**
 * A parser for VOMSES lines.
 * 
 * The VOMSES line format is as follows:
 * 
 * <pre>
 * "alias" "hostname" "port" "server DN" "vo_name"
 * </pre>
 * 
 * This parser eats up whitespace and characters outside of quotes and tolerates
 * an additional quoted field ("globus_version") that was for some time needed.
 * 
 * 
 * @author andreaceccanti
 * 
 */
public class VOMSESLineParser {

  private interface ParserState {

    void parse(char c);
  };

  private final ParserState outsideQuotes = new ParserState() {

    public void parse(char c) {

      if (c == '"')
        VOMSESLineParser.this.tokenStart();
    }
  };

  private final ParserState insideQuotes = new ParserState() {

    public void parse(char c) {

      if (c == '"') {
        VOMSESLineParser.this.tokenEnd();
      } else {
        VOMSESLineParser.this.tokenChar(c);
      }
    }
  };

  static final String VOMSES_FIELD_NAMES[] = { "vo alias", "voms host",
    "voms port", "voms server DN", "vo name", "globus version" };

  static final int VO_ALIAS = 0, VOMS_HOST = 1, VOMS_PORT = 2,
    VOMS_SERVER_DN = 3, VO_NAME = 4, GLOBUS_VERSION = 5;

  static final int MIN_VOMSES_FIELD_COUNT = 4;

  private String[] tokens = new String[VOMSES_FIELD_NAMES.length];

  private StringBuilder currentToken;
  private int tokenCount;
  private boolean tokenComplete;

  private ParserState currentState;

  private void lineSanityChecks(String line) {

    if (line == null)
      throw new VOMSError("Cannot parse a null VOMSES line");
  }

  private void init() {

    tokenCount = -1;
    currentToken = null;
    tokenComplete = false;
    currentState = outsideQuotes;
    for (int i = 0; i < tokens.length; i++)
      tokens[i] = null;
  }

  public VOMSServerInfo parse(String line) {

    init();

    lineSanityChecks(line);

    if (line.isEmpty())
      return null;

    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      currentState.parse(c);
    }

    if (!tokenComplete) {
      String msg = String.format(
        "Invalid VOMSES line: incomplete '%s' field. [line: %s]",
        VOMSES_FIELD_NAMES[tokenCount], line);

      throw new VOMSError(msg);
    }
    if (tokenCount < MIN_VOMSES_FIELD_COUNT) {
      String msg = String.format(
        "Invalid VOMSES line: incomplete information. [line: %s]", line);
      throw new VOMSError(msg);
    }

    DefaultVOMSServerInfo si = new DefaultVOMSServerInfo();
    si.setAlias(tokens[VO_ALIAS]);

    String url = String.format("voms://%s:%s", tokens[VOMS_HOST],
      tokens[VOMS_PORT]);

    validateTokens(line);

    try {

      si.setURL(new URI(url));
      si.setVOMSServerDN(tokens[VOMS_SERVER_DN]);
      si.setVoName(tokens[VO_NAME]);
      return si;

    } catch (URISyntaxException e) {
      String msg = String.format(
        "Invalid VOMSES line: cannot build URL for voms " + "service: %s",
        e.getMessage());

      throw new VOMSError(msg);
    }
  }

  private void validateTokens(String line) {

    // Validate port number
    try {

      int portNo = Integer.parseInt(tokens[VOMS_PORT]);
      if (portNo <= 0 || portNo > 65535) {
        String msg = String.format(
          "Invalid VOMSES line: invalid port number: %d. [line: %s]", portNo,
          line);
        throw new VOMSError(msg);
      }
    } catch (NumberFormatException e) {
      String msg = String.format("Invalid VOMSES line: invalid port number. "
        + "[line: %s]. Error: %s", line, e.getMessage());

      throw new VOMSError(msg, e);
    }
  }

  public void tokenStart() {

    if (++tokenCount == VOMSES_FIELD_NAMES.length)
      throw new VOMSError("Invalid VOMSES line: too many fields!");

    currentToken = new StringBuilder();
    currentState = insideQuotes;
    tokenComplete = false;
  }

  public void tokenEnd() {

    if (currentToken.length() != 0) {

      tokens[tokenCount] = currentToken.toString();
      currentState = outsideQuotes;
      tokenComplete = true;

    } else {

      String msg = String.format("Invalid VOMSES line: empty '%s' field.",
        VOMSES_FIELD_NAMES[tokenCount]);

      throw new VOMSError(msg);
    }

  }

  public void tokenChar(char c) {

    currentToken.append(c);
  }

}
