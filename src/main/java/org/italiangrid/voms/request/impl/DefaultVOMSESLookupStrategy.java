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

import java.util.Arrays;

/**
 * Default VOMSES information lookup strategy.
 * 
 * This implementation looks for vomses information in the following paths:
 * <ul>
 * <li>/etc/vomses
 * <li>${user.home}/.glite/vomses
 * <li>${user.home}/.voms/vomses
 * </ul>
 * 
 * @author Andrea Ceccanti
 * 
 */
public class DefaultVOMSESLookupStrategy extends BaseVOMSESLookupStrategy {

  public static final String DEFAULT_VOMSES_DIR = "/etc/vomses";

  public DefaultVOMSESLookupStrategy() {

    super(Arrays.asList(DEFAULT_VOMSES_DIR, System.getProperty("user.home")
      + "/.glite/vomses", System.getProperty("user.home") + "/.voms/vomses"));
  }
}
