package org.italiangrid.voms.request;

public interface ACDecodingStrategy {

  public byte[] decode(String ac);
  
}
