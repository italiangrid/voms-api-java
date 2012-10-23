/*********************************************************************
 *
 * Authors: 
 *      Andrea Ceccanti - andrea.ceccanti@cnaf.infn.it 
 *          
 * Copyright (c) Members of the EGEE Collaboration. 2004-2010.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Parts of this code may be based upon or even include verbatim pieces,
 * originally written by other people, in which case the original header
 * follows.
 *
 *********************************************************************/
package org.italiangrid.voms.request.impl;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSESLookupStrategy;
import org.italiangrid.voms.request.VOMSESParser;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.VOMSServerInfoStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * A {@link DefaultVOMSServerInfoStore} organizes voms servers found in vomses configuration
 * files in map keyed by vo. This way is easy to know which servers acts as
 * replicas for the same vos. 
 * 
 * @author Andrea Ceccanti
 * 
 * 
 */
public class DefaultVOMSServerInfoStore implements VOMSServerInfoStore {

	public static final Logger log = LoggerFactory.getLogger(DefaultVOMSServerInfoStore.class);
	
	private VOMSESLookupStrategy lookupStrategy;
	
	protected Map<String, Set<VOMSServerInfo>> serverInfoStore = new TreeMap<String, Set<VOMSServerInfo>>();
	private VOMSESParser vomsesParser;
	
	public DefaultVOMSServerInfoStore() {
		this(new DefaultVOMSESLookupStrategy(), new LegacyVOMSESParserImpl());
	}
	
	public DefaultVOMSServerInfoStore(VOMSESLookupStrategy lookupStrategy){
		this(lookupStrategy, new LegacyVOMSESParserImpl());
	}
	
	public DefaultVOMSServerInfoStore(VOMSESLookupStrategy lookupStrategy, VOMSESParser parser) {
		this.lookupStrategy = lookupStrategy;
		this.vomsesParser = parser;
		initializeStore();
	}

	public void addVOMSServerInfo(VOMSServerInfo info) {
		
		if (serverInfoStore.containsKey(info.getVoName())){
			
			serverInfoStore.get(info.getVoName()).add(info);
			
		}else{
			
			Set<VOMSServerInfo> siCont = new HashSet<VOMSServerInfo>();
			siCont.add(info);
			serverInfoStore.put(info.getVoName(), siCont);
		}
	}

	public Set<VOMSServerInfo> getVOMSServerInfo() {
		Set<VOMSServerInfo> allEntries = new HashSet<VOMSServerInfo>();
		
		for (Map.Entry<String, Set<VOMSServerInfo>> entry: serverInfoStore.entrySet())
			allEntries.addAll(entry.getValue());
		
		return allEntries;
	}

	public Set<VOMSServerInfo> getVOMSServerInfo(String voAlias) {
		Set<VOMSServerInfo> result = serverInfoStore.get(voAlias);
		if (result == null)
			return Collections.emptySet();
		return result;
	}

	private void initializeStore() {
		
		List<File> vomsesPaths = lookupStrategy.lookupVomsesInfo();
		
		if (vomsesPaths.isEmpty())
			throw new VOMSError("No VOMSES path found in local system.");
		
		for (File f: vomsesPaths){
			log.debug("Looking for VOMSES information in {}", f.getAbsolutePath());
			List<VOMSServerInfo> vomsServerInfo  = vomsesParser.parse(f);
			for (VOMSServerInfo si: vomsServerInfo)
				addVOMSServerInfo(si);
		}
		
		if (serverInfoStore.isEmpty())
			throw new VOMSError("No VOMSES contact information found in local system.");
	}
}
