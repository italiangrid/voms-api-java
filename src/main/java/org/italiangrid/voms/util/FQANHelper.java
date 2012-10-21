package org.italiangrid.voms.util;

import java.util.ArrayList;
import java.util.List;

import org.glite.voms.FQAN;

/**
 * FQAN handling utilities
 * 
 * @author Andrea Ceccanti
 *
 */
public class FQANHelper {

	public static List<FQAN> convert(List<String> fqanStrings){
		if (fqanStrings == null)
			return null;
		
		List<FQAN> fqanList = new ArrayList<FQAN>();
		for (String s: fqanStrings)
			fqanList.add(new FQAN(s));
		
		return fqanList;
	}
}
