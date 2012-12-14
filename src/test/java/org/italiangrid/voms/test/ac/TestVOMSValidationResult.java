package org.italiangrid.voms.test.ac;

import static org.junit.Assert.*;

import org.italiangrid.voms.ac.VOMSValidationResult;
import org.junit.Test;

public class TestVOMSValidationResult {

	@Test
	public void testGettersAndSetters() {
		VOMSValidationResult r  = new VOMSValidationResult(null, false);
		
		assertFalse(r.isValid());
		assertNull(r.getAttributes());
		assertTrue(r.getValidationErrors().isEmpty());
		
		assertEquals("VOMSValidationResult [valid=false, validationErrors=[], attributes=null]", r.toString());
		
	}

}
