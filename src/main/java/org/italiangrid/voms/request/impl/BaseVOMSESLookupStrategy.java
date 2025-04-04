// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.request.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.italiangrid.voms.request.VOMSESLookupStrategy;

/**
 * A base VOMSES lookup strategy which just run a existence check on a given
 * list of paths.
 * 
 * @author Andrea Ceccanti
 * 
 */
public class BaseVOMSESLookupStrategy implements VOMSESLookupStrategy {

  private final List<String> checkedPaths;

  public BaseVOMSESLookupStrategy() {

    checkedPaths = new ArrayList<String>();
  }

  public BaseVOMSESLookupStrategy(List<String> checkedPaths) {

    if (checkedPaths == null)
      throw new NullPointerException("Please provide a non-null list of paths.");

    this.checkedPaths = checkedPaths;
  }

  public List<File> lookupVomsesInfo() {

    List<File> vomsesPaths = new ArrayList<File>();

    for (String p : checkedPaths) {
      File f = new File(p);
      if (f.exists())
        vomsesPaths.add(f);
    }

    return vomsesPaths;
  }

  public List<String> searchedPaths() {

    return checkedPaths;
  }

  public void addPath(String vomsesPath) {

    checkedPaths.add(vomsesPath);
  }
}
