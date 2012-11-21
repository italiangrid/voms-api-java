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
package org.italiangrid.voms;

/**
 * A VOMS generic attribute is a name=value pair attribute augmented with a context.
 * 
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSGenericAttribute {

	/**
	 * @return the name of this generic attribute
	 */
	public String getName();
	
	/**
	 * @return the value of this generic attribute
	 */
	public String getValue();
	
	/**
	 * @return the context of this generic attribute
	 */
	public String getContext();
	
}
