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
package org.italiangrid.voms.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.credential.FilePermissionError;

/**
 * An helper class that does simple unix file permissions checks (until we 
 * get proper support for this stuff in Java 7)
 * 
 * @author Andrea Ceccanti
 * 
 */
public class FilePermissionHelper {
	
	public static enum PosixFilePermission{
		
		USER_RO("400", "-r--------"),
		USER_RW("600", "-rw-------"),
		ALL_PERMS("777", "-rwxrwxrwx");
		
		private PosixFilePermission(String chmodForm, String statForm){
			this.chmodForm = chmodForm;
			this.statForm = statForm;
		}
		
		private String statForm;
		private String chmodForm;
		
		public String statForm(){
			return statForm;
		}
		
		public String chmodForm(){
			return chmodForm;
		}
	}
	
	/**
	 * The command used to retrieve file permissions for a given file
	 */
	public static final String LS_CMD_TEMPLATE = "ls -al %s";

	/**
	 * The command used to set file permissions on a given file
	 */
	public static final String CHMOD_CMD_TEMPLATE = "chmod %s %s";


	/**
	 * Checks whether a proxy file has the right permissions
	 * 
	 * @param proxyFile
	 *            the file to be checked
	 * @return code>true</code> if the permissions are correct,
	 *         <code>false</code> otherwise
	 * @throws IOException if an error occurs checking file attributes
	 * @throws FilePermissionError if permissions are not as expected
	 */
	public static void checkProxyPermissions(String proxyFile) throws IOException {
		matchesFilePermissions(proxyFile, PosixFilePermission.USER_RW);
	}

	/**
	 * Checks whether a private key file has the 'right' permissions
	 * 
	 * @param privateKeyFile
	 *            the file to be checked
	 * @throws IOException if an error occurs checking file attributes
	 * @throws FilePermissionError
	 *             if the permissions are not correct
	 */
	public static void checkPrivateKeyPermissions(String privateKeyFile) throws IOException {
		matchesFilePermissions(privateKeyFile, PosixFilePermission.USER_RO);
	}

	/**
	 * Chekcs whether a pkcs12 file has the 'right' permissions
	 * 
	 * @param pkcs12File
	 *            the file to be checked
	 * @throws IOException if an error occurs checking file attributes
	 * @throws FilePermissionError
	 *             if the permissions are not correct
	 */
	public static void checkPKCS12Permissions(String pkcs12File) throws IOException {
		matchesFilePermissions(pkcs12File, PosixFilePermission.USER_RW);
	}

	/**
	 * Checks that a given file has the appropriate unix permissions. This naive
	 * implementation just fetches the output of ls -al on a given file and
	 * matches the resulting string with the permissionString passed as
	 * argument.
	 * 
	 * So the permissionString must be something like:
	 * 
	 * <pre>
	 * -rw-------
	 * </pre>
	 * 
	 * @param filename
	 *            the filename to be checked
	 * @param permissionString
	 *            the permission string that must be matched
	 * @throws IOException
	 *             if an error occurs checking file attributes
	 * @throws FilePermissionError
	 *             if file permissions are not as requested
	 */
	public static void matchesFilePermissions(String filename, PosixFilePermission p)
			throws IOException {

		filenameSanityChecks(filename);

		if (p == null)
			throw new NullPointerException("null permission passed as argument");

		File f = new File(filename);

		// Don't get fooled by symlinks...
		String canonicalPath = f.getCanonicalPath();
		
		String filePerms = getFilePermissions(canonicalPath);

		if (!filePerms.startsWith(p.statForm))
			throw new FilePermissionError("Wrong file permissions on file " + filename+". Required permissions are: "+p.chmodForm());

	}

	private static void filenameSanityChecks(String filename) {
		if (filename == null)
			throw new NullPointerException("null filename passed as argument");

		File f = new File(filename);
		if (!f.exists())
			throw new VOMSError("File not found: " + filename);
	}

	private static String getFilePermissions(String filename) {
		String cmd = String.format(LS_CMD_TEMPLATE, filename);

		String permString;

		ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));

		try {
			Process p = pb.start();
			int exitStatus = p.waitFor();

			if (exitStatus != 0)
				throw new VOMSError("Cannot list properties for file '" + filename
						+ "': error invoking the '" + cmd + "' os command!");

			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));

			permString = r.readLine();
			if (permString == null)
				throw new VOMSError("Cannot list properties for file '" + filename
						+ "': the output of '" + cmd + "' is empty!");

			return permString;

		} catch (IOException e) {
			throw new VOMSError("Cannot list properties for file '" + filename + "': "
					+ e.getMessage(), e);

		} catch (InterruptedException e) {
			return null;
		}
	}

	public static void setProxyPermissions(String filename){
		filenameSanityChecks(filename);
		setFilePermissions(filename, PosixFilePermission.USER_RW);
	}
	
	
	public static void setPKCS12Permissions(String filename){
		filenameSanityChecks(filename);
		setFilePermissions(filename, PosixFilePermission.USER_RW);
	}
	
	public static void setPrivateKeyPermissions(String filename) {
		filenameSanityChecks(filename);
		setFilePermissions(filename, PosixFilePermission.USER_RO);
	}

	public static void setFilePermissions(String filename, PosixFilePermission perm) {
		String cmd = String.format(CHMOD_CMD_TEMPLATE, perm.chmodForm(), filename);

		ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
		try {
			Process p = pb.start();
			int exitStatus = p.waitFor();

			if (exitStatus != 0)
				throw new VOMSError("Cannot change permissions on file '" + filename
						+ "': error invoking the '" + cmd + "' os command!");
		} catch (IOException e) {
			throw new VOMSError("Cannot list properties for file '" + filename + "': "
					+ e.getMessage(), e);

		} catch (InterruptedException e) {
			throw new VOMSError("Interrupted while running os command!", e);
		}
	}

}
