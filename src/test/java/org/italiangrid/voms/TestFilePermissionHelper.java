package org.italiangrid.voms;

import java.io.IOException;

import org.italiangrid.voms.credential.FilePermissionError;
import org.italiangrid.voms.util.FilePermissionHelper;
import org.junit.Test;

public class TestFilePermissionHelper{
	
	public static final String keyWithRightPerms = "src/test/resources/perm-test/test0.key.pem";
	public static final String p12WithRightPerms = "src/test/resources/perm-test/test0.p12";
	
	public static final String keyWithWrongPerms = "src/test/resources/perm-test/test1.key.pem";
	public static final String p12WithWrongPerms = "src/test/resources/perm-test/test1.p12";
	
	@Test
	public void testFilePermissions() throws IOException {

		FilePermissionHelper.checkPrivateKeyPermissions(keyWithRightPerms);
		FilePermissionHelper.checkPKCS12Permissions(p12WithRightPerms);
		
	}
	
	@Test(expected=FilePermissionError.class)
	public void testFilePermissionsFailureKey() throws IOException{
		FilePermissionHelper.checkPrivateKeyPermissions(keyWithWrongPerms);
	}
	
	@Test(expected=FilePermissionError.class)
	public void testFilePermissionsFailureP12() throws IOException{
		FilePermissionHelper.checkPKCS12Permissions(p12WithWrongPerms);
	}
	
}

