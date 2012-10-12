package org.italiangrid.voms;

import java.io.ByteArrayInputStream;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.store.impl.DefaultLSCFileParser;
import org.italiangrid.voms.store.impl.LSCFile;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestLSCParser {

	@Test
	public void testParse() {
		DefaultLSCFileParser parser = new DefaultLSCFileParser();
		
		String lscContent = "# First line is a comment \n"+
				"--- second line should skipped \n" +
				"/C=it/O=org/CN=commonName\n"+
				"     \t\n"+
				"/C=it/O=org/CN=CA\n";
		
		
		LSCFile f = parser.parse("vo", "host", new ByteArrayInputStream(lscContent.getBytes()));
		
		assertNull(f.getFilename());
		
		assertEquals("vo",f.getVo());
		
		assertEquals("host", f.getHostname());
		
		assertNotNull(f.getCertificateChainDescription());
		
		assertEquals(2, f.getCertificateChainDescription().size());
		
		assertEquals("/C=it/O=org/CN=commonName", f.getCertificateChainDescription().get(0));
		assertEquals("/C=it/O=org/CN=CA", f.getCertificateChainDescription().get(1));
		 
	}

	@Test
	public void testNonExistingFileParse(){
		
		DefaultLSCFileParser parser = new DefaultLSCFileParser();
		
		String nonExistentFile = "/this/file/doesnt/exist";
		
		try{
			
			LSCFile f = parser.parse("vo", "host", nonExistentFile);
		
		}catch(VOMSError e){
			
			assertEquals("LSC file does not exist: "+ nonExistentFile, e.getMessage());
			
			return;
			
		}
		
		fail("VOMS error not thrown for non existing LSC file parsing attempt.");
		
	}
}
