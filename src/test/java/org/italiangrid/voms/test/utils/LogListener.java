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
package org.italiangrid.voms.test.utils;

import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSErrorMessage;
import org.italiangrid.voms.request.VOMSRequestListener;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.VOMSWarningMessage;

public enum LogListener implements VOMSRequestListener{

	INSTANCE;

	public void notifyVOMSRequestStart(VOMSACRequest request, VOMSServerInfo si) {
		System.out.format("Contacting %s for VO %s.\n", si.getURL(), si.getVoName());
		
	}

	public void notifyVOMSRequestSuccess(VOMSACRequest request, VOMSServerInfo endpoint) {
		System.out.format("Request for VO %s succeded.\n", endpoint.getVoName());
		
	}

	public void notifyVOMSRequestFailure(VOMSACRequest request, VOMSServerInfo endpoint,
			Throwable error) {
		System.out.format("Request for VO %s failed: %s.\n", request.getVoName(), error);
		
	}

	public void notifyErrorsInVOMSReponse(VOMSACRequest request, VOMSServerInfo si,
			VOMSErrorMessage[] errors) {
		
		System.out.format("Errors in voms response for VO %s.\n", si.getVoName());
		for (VOMSErrorMessage e: errors)
			System.out.println(e.getMessage());
	}

	public void notifyWarningsInVOMSResponse(VOMSACRequest request, VOMSServerInfo si,
			VOMSWarningMessage[] warnings) {
		System.out.format("Warnings in voms response for VO %s.\n", si.getVoName());
		
		for (VOMSWarningMessage m: warnings)
			System.out.println(m.getMessage());
		
	};
	
	
}
