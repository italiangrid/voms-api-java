package org.italiangrid.voms.request.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSServerInfo;
/**
 * An helper class which builds a URL suitable for an HTTPS VOMS REST request
 * starting from the VOMS URI as available in a {@link VOMSServerInfo} object.
 * 
 * @author Valerio Venturi
 *
 */
public class RESTServiceURLBuilder {

  public URL build(URI uri, VOMSACRequest request) {
    
    URL url = null;
    
    try {
    
      url = new URL("https", uri.getHost(), uri.getPort(), buildPath(request));
    
    } catch (MalformedURLException e) {
      
      throw new VOMSError("Malformed URI: " + e.getMessage());
    }

    return url;
  }
  
  private String buildPath(VOMSACRequest request) {
    
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append("/generate-ac?fqans=");

    if (request.getRequestedFQANs().isEmpty()) {

      if (request.getVoName() == null) {
        
        throw new VOMSError("No vo name specified for AC retrieval.");
      }
      
      String voName = request.getVoName();

      if (!voName.startsWith("/"))
        voName = "/" + voName;

      stringBuilder.append(voName);
    
    } else {
      
      List<String> FQANs = request.getRequestedFQANs();
      
      Iterator<String> i = FQANs.iterator();
      
      boolean first = true;

      while (i.hasNext()) {
        
        if (!first)
          stringBuilder.append(",");
        
        stringBuilder.append((String) i.next());
        
        first = false;
      }
    }

    String targetString = targetListAsCommaSeparatedList(request.getTargets());
    
    if (targetString != null && targetString.trim().length() != 0) {
      
      stringBuilder.append("&targets=");
      stringBuilder.append(targetString);
    }

    stringBuilder.append("&lifetime=");
    stringBuilder.append(request.getLifetime());
    
    return stringBuilder.toString();
  }
  
  private String targetListAsCommaSeparatedList(List<String> targets) {
    
    StringBuilder targetStringBuilder = new StringBuilder();

    for(String target : targets) {
      
      targetStringBuilder.append(target);
      targetStringBuilder.append(',');
    }
    
    return targetStringBuilder.toString();
  }
}
