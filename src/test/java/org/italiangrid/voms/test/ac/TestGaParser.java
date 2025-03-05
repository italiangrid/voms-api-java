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
package org.italiangrid.voms.test.ac;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.italiangrid.voms.util.GaParser.parseGaString;

import java.util.List;

import org.italiangrid.voms.VOMSGenericAttribute;
import org.junit.Test;

public class TestGaParser {


  
  @Test
  public void testEmptyString() {
    
    List<VOMSGenericAttribute> result = parseGaString("");
    assertThat(result.isEmpty(), equalTo(true));
    
  }
  
  @Test(expected=NullPointerException.class)
  public void testNullString() {
    
    parseGaString(null);
  }
  
  @Test
  public void testInvalidStrings() {
    
    assertThat(parseGaString("dsa").isEmpty(), equalTo(true));
    assertThat(parseGaString("=, a == d").isEmpty(), equalTo(true));
  }
  
  @Test
  public void testValidStrings() {
    List<VOMSGenericAttribute> gas = parseGaString("ciccio = paglia");
    
    assertThat(gas.size(), equalTo(1));
    
    assertThat(gas.get(0).getName(), equalTo("ciccio"));
    assertThat(gas.get(0).getValue(), equalTo("paglia"));
    assertThat(gas.get(0).getContext(), nullValue());
    
    gas =parseGaString("  c= p   , pippo =franco,a8_d2=789");
    
    assertThat(gas.size(), equalTo(3));
    
    assertThat(gas.get(0).getName(), equalTo("c"));
    assertThat(gas.get(0).getValue(), equalTo("p"));
    
    assertThat(gas.get(1).getName(), equalTo("pippo"));
    assertThat(gas.get(1).getValue(), equalTo("franco"));
    
    assertThat(gas.get(2).getName(), equalTo("a8_d2"));
    assertThat(gas.get(2).getValue(), equalTo("789"));
  }
}
