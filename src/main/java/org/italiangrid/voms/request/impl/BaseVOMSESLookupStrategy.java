package org.italiangrid.voms.request.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.italiangrid.voms.request.VOMSESLookupStrategy;

/**
 * A base VOMSES lookup strategy which just run a existence check on a given list of paths.
 * 
 * @author Andrea Ceccanti
 *
 */
public class BaseVOMSESLookupStrategy implements VOMSESLookupStrategy {

	private String[] checkedPaths;
	
	public BaseVOMSESLookupStrategy(String[] checkedPaths) {
		this.checkedPaths = checkedPaths;
	}
	
	public List<File> lookupVomsesInfo() {
		
		List<File> vomsesPaths = new ArrayList<File>();
		
		for (String p: checkedPaths){
			File f = new File(p);
			if (f.exists())
				vomsesPaths.add(f);
		}
			
		return vomsesPaths;
	}

	public List<String> searchedPaths() {
		return Arrays.asList(checkedPaths);
	}

}
