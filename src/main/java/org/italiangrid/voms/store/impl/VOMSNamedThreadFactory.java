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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple thread factory to create named VOMS background threads.
 * 
 * @author Andrea Ceccanti
 *
 */
public class VOMSNamedThreadFactory implements ThreadFactory {

  private static final AtomicInteger created = new AtomicInteger();
  private static final String poolBaseName = "voms-thread";

  private UncaughtExceptionHandler handler;

  public VOMSNamedThreadFactory(UncaughtExceptionHandler h) {

    this.handler = h;
  }

  public VOMSNamedThreadFactory() {

  }

  public Thread newThread(Runnable r) {

    return new VOMSThread(r, poolBaseName + "-" + created.incrementAndGet(),
      handler);
  }
}
