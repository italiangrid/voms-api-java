package org.glite.voms;

import java.util.HashMap;
import java.util.Map;

/**
 * A Singleton PKIStore cache to avoid PKIStore unbounded growt in memory when the API
 * is not used sensibly by the clients.
 * 
 *  Stores for trust anchors and voms information are keyed by directory. Only
 *  one store per directory is cached.
 */
public enum PKIStoreCache {

	INSTANCE;
	
	private Map<String, PKIStore> caStoreCache;
	private Map<String, PKIStore> vomsStoreCache;
	
	private PKIStoreCache() {
		caStoreCache = new HashMap<String, PKIStore>(1);
		vomsStoreCache = new HashMap<String, PKIStore>(1);
	}
	
	public synchronized PKIStore getCAStore(String dir){
		return caStoreCache.get(dir);
	}
	
	public synchronized PKIStore getVOMSStore(String dir){
		return vomsStoreCache.get(dir);
	}
	
	public synchronized PKIStore addCAStore(String dir, PKIStore s){
		return caStoreCache.put(dir, s);
	}
	
	public synchronized PKIStore addVOMSStore(String dir, PKIStore s){
		return vomsStoreCache.put(dir, s);
	}
	
}


