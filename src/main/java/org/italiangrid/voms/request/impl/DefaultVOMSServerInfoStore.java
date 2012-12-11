/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2012.
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
package org.italiangrid.voms.request.impl;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.italiangrid.voms.request.VOMSESLookupStrategy;
import org.italiangrid.voms.request.VOMSESParser;
import org.italiangrid.voms.request.VOMSESParserFactory;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.VOMSServerInfoStore;
import org.italiangrid.voms.request.VOMSServerInfoStoreListener;
import org.italiangrid.voms.util.NullListener;

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
public class DefaultVOMSServerInfoStore implements VOMSServerInfoStore{

	private VOMSESLookupStrategy lookupStrategy;
	private VOMSServerInfoStoreListener listener;
	
	protected Map<String, Set<VOMSServerInfo>> serverInfoStore = new TreeMap<String, Set<VOMSServerInfo>>();
	private VOMSESParser vomsesParser;
	
	
	private DefaultVOMSServerInfoStore(Builder b){
		
		this.lookupStrategy = b.lookupStrategy;
		this.listener = b.listener;
		this.vomsesParser = b.vomsesParser;
		initializeStore();
		
	}

	public void addVOMSServerInfo(VOMSServerInfo info) {
		
		addVOMSServerInfo(info, null);
	}

	private void addVOMSServerInfo(VOMSServerInfo info, String path){
		
		if (serverInfoStore.containsKey(info.getVoName())){
			
			serverInfoStore.get(info.getVoName()).add(info);
			
		}else{
			
			Set<VOMSServerInfo> siCont = new HashSet<VOMSServerInfo>();
			siCont.add(info);
			serverInfoStore.put(info.getVoName(), siCont);
		}
		
		listener.notifyVOMSESInformationLoaded(path, info);
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
			listener.notifyNoValidVOMSESError(lookupStrategy.searchedPaths());
		
		for (File f: vomsesPaths){
			
			listener.notifyVOMSESlookup(f.getAbsolutePath());
			
			List<VOMSServerInfo> vomsServerInfo  = vomsesParser.parse(f);
			for (VOMSServerInfo si: vomsServerInfo){
				addVOMSServerInfo(si, f.getAbsolutePath());
			}
				
		}
	}
	/**
	 * Creates a {@link DefaultVOMSServerInfoStore}.
	 * The {@link DefaultVOMSServerInfoStore} parameters can be set with the appropriate methods. Example:
	 * 
	 * <pre>
	    {@code
	    VOMSServerInfoStore serverInfoStore = new DefaultVOMSServerInfoStore.Builder()
				.storeListener(storeListener)
				.vomsesPaths(vomsesLocations)
				.build();
		};
		</pre>
	 *  
	 */
	public static class Builder{
		
		/**
		 * A list of paths where vomses information will be looked for
		 */
		private List<String> vomsesPaths;
		/**
		 * The {@link VOMSESLookupStrategy} that will be used to lookup vomses information
		 */
		private VOMSESLookupStrategy lookupStrategy;
		/**
		 * The listener that will be notified of interesting store events
		 */
		private VOMSServerInfoStoreListener listener = NullListener.INSTANCE;
		
		/**
		 * The parser implementation used to parse VOMSES files
		 */
		private VOMSESParser vomsesParser =  VOMSESParserFactory.newVOMSESParser();
		
		public Builder() {
			
		}
		
		/**
		 * Sets the {@link VOMSESLookupStrategy} that will be used to lookup vomses information for the {@link DefaultVOMSServerInfoStore}
		 * that this builder is creating 
		 * @param strategy The strategy that will be used to lookup vomses information
		 * @return this {@link Builder} instance
		 */
		public Builder lookupStrategy(VOMSESLookupStrategy strategy){
			this.lookupStrategy = strategy;
			return this;
		}
		
		/**
		 * Sets the {@link VOMSServerInfoStoreListener} that will receive store-related notifications for the
		 * {@link DefaultVOMSServerInfoStore} that this builder is creating
		 * @param l the listener 
		 * @return this {@link Builder} instance
		 */
		public Builder storeListener(VOMSServerInfoStoreListener l){
			this.listener = l;
			return this;
		}
		
		/**
		 * Sets the {@link VOMSESParser} implementation that will be used to parse vomses files
		 * @param p the parser
		 * @return this {@link Builder} instance
		 */
		public Builder vomsesParser(VOMSESParser p){
			this.vomsesParser = p;
			return this;
		}
		
		/**
		 * Sets a list of paths where vomses files will be looked up by the {@link DefaultVOMSServerInfoStore}
		 * that this builder is creating
		 * @param paths a list of paths
		 * @return this {@link Builder} instance
		 */
		public Builder vomsesPaths(List<String> paths){
			this.vomsesPaths = paths;
			return this;
		}
		
		private void buildLookupStrategy(){
			
			if (lookupStrategy != null)
				return;
			
			if (vomsesPaths != null)
				lookupStrategy = new BaseVOMSESLookupStrategy(vomsesPaths);
			else
				lookupStrategy = new DefaultVOMSESLookupStrategy();
		}
		
		/**
		 * Builds the {@link DefaultVOMSServerInfoStore}
		 * @return a {@link DefaultVOMSServerInfoStore} configured as required by this builder
		 */
		public DefaultVOMSServerInfoStore build(){
			buildLookupStrategy();
			return new DefaultVOMSServerInfoStore(this);
			
		}
	}
}
