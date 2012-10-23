package org.italiangrid.voms;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.italiangrid.voms.request.VOMSESLookupStrategy;
import org.italiangrid.voms.request.impl.BaseVOMSESLookupStrategy;
import org.italiangrid.voms.request.impl.DefaultVOMSServerInfoStore;
import org.junit.Test;

public class TestVOMSServerInfoStore {

	@Test
	public void testExistingVOMSESParsingSuccess() {
		
		VOMSESLookupStrategy strategy = new BaseVOMSESLookupStrategy(new String[]{"src/test/resources/vomses"});
		
		DefaultVOMSServerInfoStore store = new DefaultVOMSServerInfoStore(strategy);
		
		assertEquals(3, store.getVOMSServerInfo("atlas").size());
		assertEquals(2, store.getVOMSServerInfo("eumed").size());
		Assert.assertTrue(store.getVOMSServerInfo("non-existing-vo").isEmpty());
		
		assertEquals(5, store.getVOMSServerInfo().size());
	}
	
	@Test
	public void testEmptyVOMSESParsingFailure() {
		
		VOMSESLookupStrategy strategy = new BaseVOMSESLookupStrategy(new String[]{"src/test/resources/empty-vomses"});
		
		try{
			DefaultVOMSServerInfoStore store = new DefaultVOMSServerInfoStore(strategy);
		}catch(VOMSError e){
			
			assertEquals("No VOMSES contact information found in local system.", e.getMessage());
			return;
		}
		
		Assert.fail("Parsing empty vomses file didn't raise any error!");
		
	}
	
	

}
