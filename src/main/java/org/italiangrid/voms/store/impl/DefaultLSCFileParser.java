/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2012.
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

/**
 * The default implementation for the LSC file parser.
 * 
 * @author Andrea Ceccanti
 *
 */
public class DefaultLSCFileParser implements LSCFileParser {
	
	public static final String EMPTY_LINE_REGEX = "(?m)^\\s*?$";
	
	private void checkFileExistanceAndReadabilty(File f){
		
		if (!f.exists())
			throw new VOMSError("LSC file does not exist: "+f.getAbsolutePath());
		
		if (!f.canRead())
			throw new VOMSError("LSC file is not readable: "+f.getAbsolutePath());
		
	}
	
	
	
	public LSCFile parse(String vo, String hostname, String filename) {
		
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
			
			if (certificateChainDescription.size() % 2 != 0){
			  throw new VOMSError("LSC file parsing error: "
			    + "Malformed LSC file. It should contain an even number of "
			    + "distinguished name entries expressed in OpenSSL slash-separated "
			    + "format.");
			}
			
			if (certificateChainDescription.size() == 0){
			  throw new VOMSError("LSC file parsing error: "
			    + "Malformed LSC file. No distinguished name entries found.");
			}
			
			lsc.setCertificateChainDescription(certificateChainDescription);
		
		} catch (IOException e) {
			throw new VOMSError("LSC file parsing error: "+e.getMessage(), e);
		}
		
		return lsc;
	}



	public LSCFile parse(String vo, String hostname, File file) {
		
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
