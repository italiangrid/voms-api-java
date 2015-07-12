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
   *          uncaught exception handler
   */
  public VOMSThread(Runnable target, String name,
    UncaughtExceptionHandler handler) {

    super(target, name);
    setUncaughtExceptionHandler(handler);
  }
}
