// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.store.impl;

/**
 * An helper class to create a named VOMS thread. This class just sets the name
 * for the thread and set an {@link java.lang.Thread.UncaughtExceptionHandler}
 * which logs the caught exception.
 * 
 * @author Andrea Ceccanti
 *
 */
public class VOMSThread extends Thread {

  /**
   * Default constructor.
   * 
   * @param target
   *          the object whose <code>run</code> method is called.
   * @param name
   *          the name of the new thread.
   * @param handler
   *          the {@link java.lang.Thread.UncaughtExceptionHandler} 
   *          used for this thread
   */
  public VOMSThread(Runnable target, String name,
    UncaughtExceptionHandler handler) {

    super(target, name);
    setUncaughtExceptionHandler(handler);
  }
}
