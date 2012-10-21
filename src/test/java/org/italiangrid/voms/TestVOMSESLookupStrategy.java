package org.italiangrid.voms;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.italiangrid.voms.request.VOMSESLookupStrategy;
import org.italiangrid.voms.request.impl.BaseVOMSESLookupStrategy;
import org.junit.Test;

public class TestVOMSESLookupStrategy {

	@Test
	public void testLookupVomsesInfo() {
		
		VOMSESLookupStrategy strategy =  new BaseVOMSESLookupStrategy(new String[]{"src/test/resources/vomses", 
				"/non/existent/path"});
		
		List<File> paths = strategy.lookupVomsesInfo();
		
		Assert.assertEquals(1, paths.size());
		Assert.assertTrue(paths.contains(new File("src/test/resources/vomses")));
		
	}

}
