package org.italiangrid.voms.request;

public interface VOMSServerInfoStoreListener {
	
	public void serverInfoLoaded(String vomsesPath, VOMSServerInfo info);

}
