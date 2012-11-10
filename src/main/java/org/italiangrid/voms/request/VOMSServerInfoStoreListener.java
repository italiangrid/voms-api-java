package org.italiangrid.voms.request;

import java.util.List;

public interface VOMSServerInfoStoreListener {
	
	public void noValidVomsesNotification(List<String> searchedPaths);
	public void lookupNotification(String vomsesPath);
	public void serverInfoLoaded(String vomsesPath, VOMSServerInfo info);

}
