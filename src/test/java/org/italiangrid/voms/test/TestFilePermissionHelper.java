/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2014.
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
package org.italiangrid.voms.test;

import java.io.IOException;

import org.italiangrid.voms.credential.FilePermissionError;
import org.italiangrid.voms.util.FilePermissionHelper;
import org.italiangrid.voms.util.FilePermissionHelper.PosixFilePermission;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestFilePermissionHelper {

  public static final String keyWithRightPerms = "src/test/resources/perm-test/test0.key.pem";
  public static final String p12WithRightPerms = "src/test/resources/perm-test/test0.p12";

  public static final String keyWithWrongPerms = "src/test/resources/perm-test/test1.key.pem";
  public static final String p12WithWrongPerms = "src/test/resources/perm-test/test1.p12";

  public static final String keyWith600Perms = "src/test/resources/perm-test/test2.key.pem";

  @BeforeClass
  public static void setupPermissions() {

    FilePermissionHelper.setPrivateKeyPermissions(keyWithRightPerms);
    FilePermissionHelper.setPKCS12Permissions(p12WithRightPerms);

    FilePermissionHelper.setFilePermissions(keyWithWrongPerms,
      PosixFilePermission.ALL_PERMS);
    FilePermissionHelper.setFilePermissions(p12WithWrongPerms,
      PosixFilePermission.ALL_PERMS);

    FilePermissionHelper.setFilePermissions(keyWith600Perms,
      PosixFilePermission.USER_RW);

  }

  @Test
  public void testFilePermissions() throws IOException {

    FilePermissionHelper.checkPrivateKeyPermissions(keyWithRightPerms);
    FilePermissionHelper.checkPrivateKeyPermissions(keyWith600Perms);
    FilePermissionHelper.checkPKCS12Permissions(p12WithRightPerms);

  }

  @Test(expected = FilePermissionError.class)
  public void testFilePermissionsFailureKey() throws IOException {

    FilePermissionHelper.checkPrivateKeyPermissions(keyWithWrongPerms);
  }

  @Test(expected = FilePermissionError.class)
  public void testFilePermissionsFailureP12() throws IOException {

    FilePermissionHelper.checkPKCS12Permissions(p12WithWrongPerms);
  }

}
