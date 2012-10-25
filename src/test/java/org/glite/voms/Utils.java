package org.glite.voms;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Utils implements TestFixture{

	public synchronized static void setCRL(String crlFileName) throws IOException{
		System.out.println("Setting CRL to "+crlFileName);
		File crlStartFile = new File(crlFileName); 
		
		long crlOrigChecksum = FileUtils.checksumCRC32(crlStartFile);
		
		for (String caHash: caHashes){
			
			File hashFile = new File(trustDir+"/"+caHash+".r0");
		
			FileUtils.deleteQuietly(hashFile);
			FileUtils.copyFile(crlStartFile, hashFile );
			long destChecksum = FileUtils.checksumCRC32(hashFile);
			
			if (destChecksum != crlOrigChecksum)
				throw new IllegalStateException("Checksum verification failed!");
		}
	}
	
}
