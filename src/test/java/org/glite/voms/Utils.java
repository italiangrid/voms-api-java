package org.glite.voms;

import java.io.File;
import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class Utils implements TestFixture{

	public static final Logger logger = Logger.getLogger(Utils.class);
	
	public synchronized static void setCRL(String crlFileName) throws IOException, CRLException{
		logger.info("Setting CRL to "+crlFileName);
		File crlStartFile = new File(crlFileName); 
		
		long crlOrigChecksum = FileUtils.checksumCRC32(crlStartFile);
		
		for (String caHash: caHashes){
			
			File hashFile = new File(trustDir+"/"+caHash+".r0");
		
			FileUtils.deleteQuietly(hashFile);
			FileUtils.copyFile(crlStartFile, hashFile );
			long destChecksum = FileUtils.checksumCRC32(hashFile);
			
			if (destChecksum != crlOrigChecksum)
				throw new IllegalStateException("Checksum verification failed!");
			
			X509CRL crl = PKIUtils.loadCRL(hashFile);
			logger.info("Loaded :"+crl);
			
		}
	}
	
}
