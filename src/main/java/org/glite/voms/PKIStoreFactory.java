/*********************************************************************
 *
 * Authors: Vincenzo Ciaschini - Vincenzo.Ciaschini@cnaf.infn.it
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


package org.glite.voms;

import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;

public class PKIStoreFactory {
	
	private static PKIStore getCAStore(String dir, boolean aggressive, boolean timer) throws CertificateException, CRLException, IOException{
		String storeDir = dir == null ? PKIStore.DEFAULT_CADIR : dir;
		
		PKIStore s = PKIStoreCache.INSTANCE.getCAStore(storeDir);
		
		if (s == null)
			PKIStoreCache.INSTANCE.addCAStore(storeDir, new PKIStore(dir, PKIStore.TYPE_CADIR, aggressive, timer));
		
		return PKIStoreCache.INSTANCE.getCAStore(storeDir);
	}
	
	private static PKIStore getVOMSStore(String dir, boolean aggressive, boolean timer) throws CertificateException, CRLException, IOException{
		String storeDir = dir == null ? PKIStore.DEFAULT_VOMSDIR : dir;
		PKIStore s = PKIStoreCache.INSTANCE.getVOMSStore(storeDir);
		if (s == null)
			PKIStoreCache.INSTANCE.addVOMSStore(storeDir, new PKIStore(storeDir, PKIStore.TYPE_VOMSDIR, aggressive, timer));
		
		return PKIStoreCache.INSTANCE.getVOMSStore(storeDir);
	}
	
    public synchronized static PKIStore getStore(String dir, int type, boolean aggressive, boolean timer) throws IOException, CertificateException, CRLException {
    	
    	if (type == PKIStore.TYPE_CADIR)
    		return getCAStore(dir, aggressive, timer);
    	else if (type == PKIStore.TYPE_VOMSDIR)
    		return getVOMSStore(dir, aggressive, timer);
    	else
    		throw new IllegalArgumentException("Unsupported store type: "+type);       
    }

    public synchronized static PKIStore getStore(String dir, int type, boolean aggressive) throws IOException, CertificateException, CRLException {
        return PKIStoreFactory.getStore(dir, type, aggressive, true);
    }

    public synchronized static PKIStore getStore(String dir, int type) throws IOException, CertificateException, CRLException {
        return PKIStoreFactory.getStore(dir, type, true, true);
    }

    public synchronized static PKIStore getStore(int type) throws IOException, CertificateException, CRLException {
        return PKIStoreFactory.getStore(null, type, true, true);
    }

    public synchronized static PKIStore getStore() {
        return new PKIStore();
    }
}
