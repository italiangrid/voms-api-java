package org.italiangrid.voms.request.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSESParser;
import org.italiangrid.voms.request.VOMSServerInfo;

public class LegacyVOMSESParserImpl implements VOMSESParser {
	
	private static final String splitSyntax = "\\x22[^\\x22]\\x22";
	
	protected void fileSanityChecks(File f){
		if (!f.exists())
			throw new VOMSError("VOMSES file does not exist: "+f.getAbsolutePath());
		if (!f.canRead())
			throw new VOMSError("VOMSES file is not readable: "+f.getAbsolutePath());
		if (f.isDirectory())
			throw new VOMSError("VOMSES file is a directory: "+f.getAbsolutePath());
	}
	
	private String[] splitLine(String line) {

		String tokens[] = line.split(splitSyntax);

		for (int i = 0; i < tokens.length; i++)
			tokens[i] = fixQuotes(tokens[i]);

		return tokens;
	}
	
	private String fixQuotes(String s) {

		if (s.startsWith("\""))
			s = s.substring(1);
		if (s.endsWith("\""))
			s = s.substring(0, s.length() - 1);

		return s;

	}
	
	protected VOMSServerInfo parseLine(String vomsesLine) throws URISyntaxException {
		
		String[] tokens = splitLine(vomsesLine.trim());
		if (tokens.length < 5 || tokens.length > 6)
			throw new VOMSError("Unsupported VOMSES line format: "+vomsesLine);
		
		DefaultVOMSServerInfo info = new DefaultVOMSServerInfo();
		
		// Format:
		// "<alias>" "<service host>" "<service port>" "<service certificate subject in openssl format>" "<vo name>" "<globus version>"
		info.setAlias(tokens[0]);
		info.setVoName(tokens[4]);
		
		String url = String.format("voms://%s:%s", tokens[1], tokens[2]);
		
		info.setURL(new URI(url));
		
		info.setVOMSServerDN(tokens[3]);
		
		return info;
	}
	
	
	public List<VOMSServerInfo> parse(Reader vomsesReader) {

		BufferedReader reader = new BufferedReader(vomsesReader);

		String line = null;
		List<VOMSServerInfo> result = new ArrayList<VOMSServerInfo>();
		
		try {
			
			while ((line = reader.readLine()) != null) {

				// Ignore comments
				if (line.startsWith("#"))
					continue;

				// skip empty lines
				if (line.matches("\\s*$"))
					continue;
				
				VOMSServerInfo parsedInfo = parseLine(line);
				
				if (parsedInfo != null)
					result.add(parsedInfo);
				
			}
			
		} catch (Exception e) {
			
			throw new VOMSError("Error parsing VOMSES information...",e);
		} 
		return result;
	}

	
	public List<VOMSServerInfo> parse(File f) {
		fileSanityChecks(f);
		
		try {
			
			BufferedReader r = new BufferedReader(new FileReader(f));
			return parse(r);
		
		} catch (FileNotFoundException e) {
			throw new VOMSError("VOMSES file not found: "+f.getAbsolutePath(),e);
		
		} catch (VOMSError e) {
			throw new VOMSError("Error parsing VOMSES file: "+f.getAbsolutePath(),e);
		}
		
	}

}
