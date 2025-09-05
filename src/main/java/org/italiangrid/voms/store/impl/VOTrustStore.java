// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.store.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.italiangrid.voms.store.LSCInfo;
import org.italiangrid.voms.store.VOMSTrustStoreStatusListener;

public class VOTrustStore extends DefaultVOMSTrustStore {

  private final String voName;

  public VOTrustStore(List<String> localTrustDirs, String voName,
      VOMSTrustStoreStatusListener listener) {

    super(localTrustDirs, listener);
    this.voName = voName;
  }

  @Override
  protected void loadLSCFromDirectory(File directory) {

    directorySanityChecks(directory);

    synchronized (listenerLock) {
      listener.notifyLSCLookupEvent(directory.getAbsolutePath());
    }

    if (!directory.getName().equals(voName))
      return;

    File[] lscFiles = directory.listFiles(new FilenameFilter() {

      public boolean accept(File dir, String name) {

        return name.endsWith(LSC_FILENAME_SUFFIX);
      }
    });

    if (lscFiles.length == 0)
      return;

    DefaultLSCFileParser lscParser = new DefaultLSCFileParser();

    for (File lsc : lscFiles) {

      String lscFileName = lsc.getName();

      String hostname = lscFileName.substring(0, lscFileName.indexOf(LSC_FILENAME_SUFFIX));

      LSCInfo info = null;

      info = lscParser.parse(voName, hostname, lsc);

      Set<LSCInfo> localLscForVo = localLSCInfo.get(voName);

      if (localLscForVo == null) {
        localLscForVo = new HashSet<LSCInfo>();
        localLSCInfo.put(voName, localLscForVo);
      }

      localLscForVo.add(info);

      listener.notifyLSCLoadEvent(info, lsc);

    }

  }

}
