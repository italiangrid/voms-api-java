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
package org.italiangrid.voms.store;

import java.util.List;

import org.italiangrid.voms.store.impl.DefaultUpdatingVOMSTrustStore;

/**
 * A factory for VOMS trust stores
 * @author cecco
 *
 */
public class VOMSTrustStores {

	/**
	 * Creates a  {@link VOMSTrustStore} configured with default settings.
	 * 
	 * @return a {@link VOMSTrustStore} configured with default settings. 
	 */
	public static VOMSTrustStore newTrustStore(){
		
		return new DefaultUpdatingVOMSTrustStore();
	}
	
	
	
	
	/**
	 * Creates a {@link VOMSTrustStore} configured according to the parameters
	 * passed as argument
	 * 
	 * @param localTrustDirs the directory where voms information will be searched
	 * @param updateFrequency the trust store update frequency in milliseconds
	 * @param statusListener a listener that is notified of events related to the created trust store
	 * @return a {@link VOMSTrustStore} configured as requested.
	 */
	public static VOMSTrustStore newTrustStore(
			List<String> localTrustDirs, long updateFrequency, VOMSTrustStoreStatusListener statusListener) {
		
		return new DefaultUpdatingVOMSTrustStore(localTrustDirs,
				updateFrequency, statusListener);
	}

	/**
	 * Creates a {@link VOMSTrustStore} configured according to the parameters
	 * passed as argument
	 * 
	 * @param localTrustDirs the directory where voms information will be searched
	 * @return a {@link VOMSTrustStore} configured as requested.
	 */
	public static VOMSTrustStore newTrustStore(List<String> localTrustDirs){
		return new DefaultUpdatingVOMSTrustStore(localTrustDirs);
	}
	
	/**
	 * Creates a {@link VOMSTrustStore} configured according to the parameters
	 * passed as argument
	 * 
	 * @param updateFrequency the trust store update frequency in milliseconds
	 * @return  a {@link VOMSTrustStore} configured as requested.
	 */
	public static VOMSTrustStore newTrustStore(
			long updateFrequency) {
		return new DefaultUpdatingVOMSTrustStore(updateFrequency);
	}
	
}
