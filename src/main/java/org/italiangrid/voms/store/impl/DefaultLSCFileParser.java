package org.italiangrid.voms.store.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.store.LSCFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation for the LSC file parser.
 * 
 * @author Andrea Ceccanti
 *
 */
public class DefaultLSCFileParser implements LSCFileParser {

	Logger log = LoggerFactory.getLogger(DefaultLSCFileParser.class);
	
	public static final String EMPTY_LINE_REGEX = "(?m)^\\s*?$";
	
	private void checkFileExistanceAndReadabilty(File f){
		
		if (!f.exists())
			throw new VOMSError("LSC file does not exist: "+f.getAbsolutePath());
		
		if (!f.canRead())
			throw new VOMSError("LSC file is not readable: "+f.getAbsolutePath());
		
	}
	
	
	
	public LSCFile parse(String vo, String hostname, String filename) {
		
		log.debug("Parsing LSC information from file {} for VO {} and hostname {}", 
				new Object[]{filename,vo, hostname});
		
		LSCFile lsc = null;
		
		try{ 
			
			File f = new File(filename);
			
			checkFileExistanceAndReadabilty(f);
			
			lsc = parse(vo, hostname, new FileInputStream(f));
			
			lsc.setFilename(filename);
					
		} catch (IOException e) {
			throw new VOMSError("LSC file parsing error: "+e.getMessage(), e);
		}
		
		return lsc;
	}

	public synchronized LSCFile parse(String vo, String hostname, InputStream is) {
		
		LSCFile lsc = new LSCFile();
		
		lsc.setHostname(hostname);
		lsc.setVo(vo);
		
		try {
			
			BufferedReader lscReader = new BufferedReader(new InputStreamReader(is));
			
			String line = null;
			List<String> certificateChainDescription = new ArrayList<String>();
			
			do{
				line = lscReader.readLine();
				log.debug("Parsing line: {}", line); 
				
				// This is EOF
				if (line == null)
					break;
				
				// Ignore comments
				if (line.startsWith("#"))
					continue;
				
				// Ignore ---NEXT CHAIN---
				if (line.startsWith("-"))
					continue;
				
				// Ignore empty lines
				if (line.matches(EMPTY_LINE_REGEX))
					continue;
				
				if (line.startsWith("/"))
					certificateChainDescription.add(line);
				
				
			}while (line != null);
				
			lscReader.close();
			
			lsc.setCertificateChainDescription(certificateChainDescription);
		
		} catch (IOException e) {
			throw new VOMSError("LSC file parsing error: "+e.getMessage(), e);
		}
		
		return lsc;
	}



	public LSCFile parse(String vo, String hostname, File file) {
		
		log.debug("Parsing LSC information from file {} for VO {} and hostname {}", 
				new Object[]{file.getAbsolutePath(),vo, hostname});
		
		LSCFile lsc = null;
		
		try{ 
			
			checkFileExistanceAndReadabilty(file);
			
			lsc = parse(vo, hostname, new FileInputStream(file));
			
			lsc.setFilename(file.getAbsolutePath());
					
		} catch (IOException e) {
			throw new VOMSError("LSC file parsing error: "+e.getMessage(), e);
		}
		
		return lsc;
		
	}

}
