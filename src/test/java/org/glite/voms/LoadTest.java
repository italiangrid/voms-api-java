package org.glite.voms;
import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.glite.voms.PKIStore;
import org.glite.voms.PKIUtils;
import org.glite.voms.PKIVerifier;
import org.glite.voms.VOMSValidator;
import org.glite.voms.ac.ACValidator;


public class LoadTest extends TestCase {

  public void test() throws CertificateException {
    
    throw new OutOfMemoryError();
    
//    X509Certificate[] proxyChain = PKIUtils.loadCertificates("src/test/resources/certs/test0.cert.pem");
//    
//    while(true){
//      
//      VOMSValidator validator = new VOMSValidator(proxyChain);
//      validator.validate();
//      
//      validator.getAllFullyQualifiedAttributes();
//    }
    
  }
  
//  public void anotherTest() throws CertificateException, CRLException, IOException {
//    
//    PKIStore caStore = new PKIStore("src/test/resources/trust-anchors", PKIStore.TYPE_CADIR, true);
//    PKIStore vomsTrustStore = new PKIStore("src/test/resources/vomsdir", PKIStore.TYPE_VOMSDIR, true);
//    
//    caStore.rescheduleRefresh((int)TimeUnit.SECONDS.toMillis(5));
//    
//    PKIVerifier verifier = new PKIVerifier(vomsTrustStore,caStore);
//    ACValidator acValidator = new ACValidator(verifier);
//    
//    X509Certificate[] proxyChain = PKIUtils.loadCertificates("src/test/resources/certs/test0.cert.pem");
//    
//    VOMSValidator validator = new VOMSValidator(proxyChain, acValidator);
//    
//    while(true){
//      
//      verifier.verify(proxyChain);
//      
//      validator.setClientChain(proxyChain);
//      validator.validate().getAllFullyQualifiedAttributes();
//      
//      try {
//        
//        Thread.currentThread().sleep(TimeUnit.SECONDS.toMillis(1));
//      
//      } catch (InterruptedException e) {
//        
//        break;
//      }
//      
//    }
//    
//  }
  
}
