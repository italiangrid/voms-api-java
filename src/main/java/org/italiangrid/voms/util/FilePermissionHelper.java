// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EnumSet;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.credential.FilePermissionError;

/**
 * A helper class for performing basic Unix file permission checks.
 *
 * <p>
 * This class is intended to provide simple permission validation and modification for specific
 * files, such as private keys and proxy certificates. It relies on executing system commands to
 * fetch and update file permissions.
 * </p>
 *
 * <p>
 * Note: This implementation is a workaround until proper support for POSIX file permissions is
 * available in Java.
 * </p>
 *
 */
public class FilePermissionHelper {

  /**
   * Enumeration representing POSIX file permissions.
   */
  public static enum PosixFilePermission {

    // @formatter:off
    /** Read-only permission for the user (chmod 400, stat -r--------). */
    USER_RO   ("400", "-r--------"),
    /** Read and write permission for the user (chmod 600, stat -rw-------). */
    USER_RW   ("600", "-rw-------"),
    /** Full permissions for the user (chmod 700, stat -rwx------). */
    USER_ALL  ("700", "-rwx------"),
    /** Full permissions for all users (chmod 777, stat -rwxrwxrwx). */
    ALL_PERMS ("777", "-rwxrwxrwx");
    // @formatter:off

    private String chmodForm;
    private String statForm;

    /**
     * Constructor for PosixFilePermission enum.
     *
     * @param chmodForm the chmod-style representation of the permission
     * @param statForm the stat-style representation of the permission
     */
    private PosixFilePermission(String chmodForm, String statForm) {

      this.chmodForm = chmodForm;
      this.statForm = statForm;
    }

    /**
     * Gets the stat-style (symbolic) representation of the permission.
     *
     * @return the stat-formatted permission string (e.g., "-rw-------").
     */
    public String statForm() {

      return statForm;
    }

    /**
     * Gets the chmod-style (numeric) representation of the permission.
     *
     * @return the chmod-formatted permission string (e.g., "600").
     */
    public String chmodForm() {

      return chmodForm;
    }
  }

  /**
   * Required file permissions for the private key file
   */
  public static final EnumSet<PosixFilePermission> PRIVATE_KEY_PERMS =
      EnumSet.of(PosixFilePermission.USER_RO, PosixFilePermission.USER_RW);

  /**
   * String representation of private key required permissions.
   */
  public static final String PRIVATE_KEY_PERMS_STR =
      PosixFilePermission.USER_RO.chmodForm() + ", " + PosixFilePermission.USER_RW.chmodForm();

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
   * @param proxyFile the file to be checked
   *
   * @throws IOException if an error occurs checking file attributes
   * @throws FilePermissionError if permissions are not as expected
   */
  public static void checkProxyPermissions(String proxyFile) throws IOException {

    matchesFilePermissions(proxyFile, PosixFilePermission.USER_RW);
  }

  /**
   * Checks whether a private key file has the 'right' permissions
   *
   * @param privateKeyFile the file to be checked
   * @throws IOException if an error occurs checking file attributes
   * @throws FilePermissionError if the permissions are not correct
   */
  public static void checkPrivateKeyPermissions(String privateKeyFile) throws IOException {

    for (PosixFilePermission p : PRIVATE_KEY_PERMS) {
      try {
        matchesFilePermissions(privateKeyFile, p);
        return;
      } catch (FilePermissionError e) {
      }
    }

    final String errorMessage =
        String.format("Wrong file permissions on file %s. Required permissions are: %s ",
            privateKeyFile, PRIVATE_KEY_PERMS_STR);

    throw new FilePermissionError(errorMessage);
  }

  /**
   * Checks whether a pkcs12 file has the 'right' permissions
   *
   * @param pkcs12File the file to be checked
   * @throws IOException if an error occurs checking file attributes
   * @throws FilePermissionError if the permissions are not correct
   */
  public static void checkPKCS12Permissions(String pkcs12File) throws IOException {

    matchesFilePermissions(pkcs12File, PosixFilePermission.USER_RW);
  }

  /**
   * Checks that a given file has the appropriate unix permissions. This naive implementation just
   * fetches the output of ls -al on a given file and matches the resulting string with the
   * permissionString passed as argument.
   *
   * So the permissionString must be something like:
   *
   * <pre>
   * -rw-------
   * </pre>
   *
   * @param filename the filename to be checked
   * @param expectedPerm the permission string that must be matched
   * @throws IOException if an error occurs checking file attributes
   * @throws FilePermissionError if file permissions are not as requested
   */
  public static void matchesFilePermissions(String filename, PosixFilePermission expectedPerm)
      throws IOException {

    filenameSanityChecks(filename);

    if (expectedPerm == null) {
      throw new NullPointerException("Expected permission cannot be null.");
    }

    String filePerms = getFilePermissions(new File(filename).getCanonicalPath());

    if (!filePerms.startsWith(expectedPerm.statForm())) {
      throw new FilePermissionError(
          String.format("Wrong file permissions on file %s. Expected: %s",
              filename, expectedPerm.chmodForm()));
    }
  }

  private static void filenameSanityChecks(String filename) {

    if (filename == null) {
      throw new NullPointerException("Filename cannot be null.");
    }

    File file = new File(filename);
    if (!file.exists()) {
      throw new VOMSError("File not found: " + filename);
    }
  }

  private static String getFilePermissions(String filename) {

    String cmd = String.format(LS_CMD_TEMPLATE, filename);
    ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));

    try {
      Process p = pb.start();
      int exitStatus = p.waitFor();

      if (exitStatus != 0) {
        throw new VOMSError("Failed to retrieve file properties: " + filename);
      }

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
        String permString = reader.readLine();
        if (permString == null) {
          throw new VOMSError("No output received from command: " + cmd);
        }
        return permString;
      }
    } catch (IOException e) {
      throw new VOMSError("Error retrieving file permissions for " + filename, e);
    } catch (InterruptedException e) {
      return null;
    }
  }

  /**
   * Sets the default POSIX permissions on a proxy identified by filename.
   *
   * @param filename the file to modify
   */
  public static void setProxyPermissions(String filename) {

    setFilePermissions(filename, PosixFilePermission.USER_RW);
  }

  /**
   * Sets the default POSIX permissions on a p12 identified by filename.
   *
   * @param filename the file to modify
   */
  public static void setPKCS12Permissions(String filename) {

    setFilePermissions(filename, PosixFilePermission.USER_RW);
  }

  /**
   * Sets the default POSIX permissions on a private key identified by filename.
   *
   * @param filename the file to modify
   */
  public static void setPrivateKeyPermissions(String filename) {

    setFilePermissions(filename, PosixFilePermission.USER_RO);
  }

  /**
   * Sets the specified POSIX permissions on a file.
   *
   * @param filename the file to modify
   * @param perm the permissions to apply
   */
  public static void setFilePermissions(String filename, PosixFilePermission perm) {

    filenameSanityChecks(filename);

    String cmd = String.format(CHMOD_CMD_TEMPLATE, perm.chmodForm(), filename);
    ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));

    try {
      Process process = pb.start();
      int exitStatus = process.waitFor();

      if (exitStatus != 0) {
        throw new VOMSError("Failed to change file permissions: " + filename);
      }
    } catch (IOException | InterruptedException e) {
      throw new VOMSError("Error setting file permissions for " + filename, e);
    }
  }

}
