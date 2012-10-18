package org.italiangrid.voms.request.impl;

import java.net.URI;

import org.italiangrid.voms.request.VOMSServerInfo;

public class DefaultVOMSServerInfo implements VOMSServerInfo {

	String alias;
	String voName;
	URI URL;
	String vomsServerDN;

	public DefaultVOMSServerInfo() {

	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getVoName() {
		return voName;
	}

	public void setVoName(String voName) {
		this.voName = voName;
	}

	public String getVOMSServerDN() {

		return vomsServerDN;
	}

	public void setVOMSServerDN(String vomsServerDN) {
		this.vomsServerDN = vomsServerDN;
	}

	public URI getURL() {
		return URL;
	}

	public void setURL(URI uRL) {
		URL = uRL;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((URL == null) ? 0 : URL.hashCode());
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		result = prime * result + ((voName == null) ? 0 : voName.hashCode());
		result = prime * result + ((vomsServerDN == null) ? 0 : vomsServerDN.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultVOMSServerInfo other = (DefaultVOMSServerInfo) obj;
		if (URL == null) {
			if (other.URL != null)
				return false;
		} else if (!URL.equals(other.URL))
			return false;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		if (voName == null) {
			if (other.voName != null)
				return false;
		} else if (!voName.equals(other.voName))
			return false;
		if (vomsServerDN == null) {
			if (other.vomsServerDN != null)
				return false;
		} else if (!vomsServerDN.equals(other.vomsServerDN))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VOMSServerInfo [alias=" + alias + ", voName=" + voName + ", URL=" + URL
				+ ", vomsServerDN=" + vomsServerDN + "]";
	}
}
