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

import java.net.URL;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.glite.voms.contact.VOMSESFileParser;
import org.italiangrid.voms.request.VOMSServerInfo;

/**
 * 
 * This class represents information about a remote voms server as found in
 * vomses configuration files. See {@link VOMSESFileParser}.
 * 
 * @author Andrea Ceccanti
 * @author Vincenzo Ciaschini
 * 
 */
public class VOMSServerInfoImpl implements VOMSServerInfo{

	String hostName;
	int port;
	String hostDn;
	String voName;
	String globusVersion;
	String alias;

	/* (non-Javadoc)
	 * @see org.glite.voms.contact.VOMSServerInfoIF#getAlias()
	 */
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	/* (non-Javadoc)
	 * @see org.glite.voms.contact.VOMSServerInfoIF#getHostDn()
	 */
	public String getVOMSServerDN() {

		return hostDn;
	}

	public void setHostDn(String hostDn) {

		this.hostDn = hostDn;
	}

	/* (non-Javadoc)
	 * @see org.glite.voms.contact.VOMSServerInfoIF#getHostName()
	 */
	public String getHostName() {

		return hostName;
	}

	public void setHostName(String hostname) {

		this.hostName = hostname;
	}

	/* (non-Javadoc)
	 * @see org.glite.voms.contact.VOMSServerInfoIF#getPort()
	 */
	public int getPort() {

		return port;
	}

	public void setPort(int port) {

		this.port = port;
	}

	/* (non-Javadoc)
	 * @see org.glite.voms.contact.VOMSServerInfoIF#getVoName()
	 */
	public String getVoName() {

		return voName;
	}

	public void setVoName(String voName) {

		this.voName = voName;
	}

	/* (non-Javadoc)
	 * @see org.glite.voms.contact.VOMSServerInfoIF#getGlobusVersionAsInt()
	 */
	public int getGlobusVersionAsInt() {

		if (globusVersion == null)
			return -1;

		return (int) (Integer.parseInt(globusVersion) / 10);
	}

	/* (non-Javadoc)
	 * @see org.glite.voms.contact.VOMSServerInfoIF#getGlobusVersion()
	 */
	public String getGlobusVersion() {

		return globusVersion;
	}

	public void setGlobusVersion(String globusVersion) {

		this.globusVersion = globusVersion;
	}

	public boolean equals(Object obj) {

		if (this == obj)
			return true;

		if (!(obj instanceof VOMSServerInfoImpl))
			return false;

		VOMSServerInfoImpl other = (VOMSServerInfoImpl) obj;

		if (other.getHostName() != null) {
			if (this.hostName.equals(other.getHostName())) {
				return this.getPort() == other.getPort();
			}
		}

		return false;

	}

	public int hashCode() {
		int result = 14;

		result = 29 * result + hostName.hashCode();
		return 29 * result + port;
	}

	public static VOMSServerInfoImpl fromStringArray(String[] tokens) {
		VOMSServerInfoImpl info = new VOMSServerInfoImpl();

		info.setVoName(tokens[4]);
		info.setHostName(tokens[1]);
		info.setPort(Integer.parseInt(tokens[2]));
		info.setHostDn(tokens[3]);
		info.setAlias(tokens[0]);

		// Check if the globus version is there
		if (tokens.length == 6)
			info.setGlobusVersion(tokens[5]);

		return info;

	}

	/* (non-Javadoc)
	 * @see org.glite.voms.contact.VOMSServerInfoIF#compactString()
	 */
	public String asString() {

		return "[vo=" + voName + ",host=" + hostName + ",port=" + port
				+ ",hostDN=" + hostDn + ",globusVersion=" + globusVersion + "]";
	}

	public String toString() {

		return ToStringBuilder.reflectionToString(this);

	}

	public URL getURL() {
		// TODO Auto-generated method stub
		return null;
	}
}
