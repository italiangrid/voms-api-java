package org.italiangrid.voms.test.ac;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.italiangrid.voms.util.GaParser.parseGaString;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.italiangrid.voms.VOMSGenericAttribute;
import org.junit.Test;

public class TestGaParser {


  
  @Test
  public void testEmptyString() {
    
    List<VOMSGenericAttribute> result = parseGaString("");
    assertThat(result, empty());
    
  }
  
  @Test(expected=NullPointerException.class)
  public void testNullString() {
    
    parseGaString(null);
  }
  
  @Test
  public void testInvalidStrings() {
    
    assertThat(parseGaString("dsa"), empty());
    assertThat(parseGaString("=, a == d"), empty());
  }
  
  @Test
  public void testValidStrings() {
    List<VOMSGenericAttribute> gas = parseGaString("ciccio = paglia");
    
    assertThat(gas, hasSize(1));
    
    assertThat(gas.get(0).getName(), is("ciccio"));
    assertThat(gas.get(0).getValue(), is("paglia"));
    assertThat(gas.get(0).getContext(), is(nullValue()));
    
    gas =parseGaString("  c= p   , pippo =franco,a8_d2=789");
    
    assertThat(gas, hasSize(3));
    
    assertThat(gas.get(0).getName(), is("c"));
    assertThat(gas.get(0).getValue(), is("p"));
    
    assertThat(gas.get(1).getName(), is("pippo"));
    assertThat(gas.get(1).getValue(), is("franco"));
    
    assertThat(gas.get(2).getName(), is("a8_d2"));
    assertThat(gas.get(2).getValue(), is("789"));
  }
}
