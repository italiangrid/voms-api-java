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
