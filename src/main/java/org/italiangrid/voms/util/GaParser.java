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
package org.italiangrid.voms.util;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.italiangrid.voms.VOMSGenericAttribute;
import org.italiangrid.voms.ac.impl.VOMSGenericAttributeImpl;

  

public class GaParser {
  
  private static final String GA_TOKEN_REGEXP = "\\s*([a-zA-Z][a-zA-Z0-9_-]*)\\s*=\\s*(\\S+)\\s*";
  private static final Pattern GA_TOKEN_PATTERN = Pattern.compile(GA_TOKEN_REGEXP);
  
  private GaParser() {
    // empty constructor
  }
  
  public static List<VOMSGenericAttribute> parseGaString(String gaString) {
    if (isNull(gaString)) {
      throw new NullPointerException("Cannot parse a null gaString");
    }
    
    if (gaString.isEmpty()) {
      return emptyList();
    }
    
    List<VOMSGenericAttribute> result = new ArrayList<>();
    
    String[] gaTokens = gaString.split(",");
    
    for (String token: gaTokens) {
      
      Matcher m = GA_TOKEN_PATTERN.matcher(token);
      
      if (m.matches()) {
        String key = m.group(1);
        String value = m.group(2);
        VOMSGenericAttributeImpl ga = new VOMSGenericAttributeImpl();
        ga.setName(key);
        ga.setValue(value);
        result.add(ga);
      }      
    }
    
    return result;
    
  }

}
