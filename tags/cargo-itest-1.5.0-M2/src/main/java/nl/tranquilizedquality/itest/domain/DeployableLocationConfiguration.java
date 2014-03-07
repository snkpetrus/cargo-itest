/*
 * Copyright 2009 Salomo Petrus
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package nl.tranquilizedquality.itest.domain;

import org.springframework.beans.factory.annotation.Required;

/**
 * This class represents the location where a deployable can be found and what
 * type of deployable it is. This can be either a WAR, EAR or a EJB. The context
 * name you can specify is only applicable for a deployable of type WAR.
 * 
 * @author Salomo Petrus (sape)
 * @since 1 apr 2009
 * 
 */
public class DeployableLocationConfiguration {
	/** The path where to find the deployable. */
	private String path;

	/** The type of deployable. Can be either WAR, EAR or EJB. */
	private String type;

	/** The context name of the application. NOTE: IS ONLY APPLICABLE FOR WAR. */
	private String contextName;

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	@Required
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	@Required
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the contextName
	 */
	public String getContextName() {
		return contextName;
	}

	/**
	 * @param contextName
	 *            the contextName to set
	 */
	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

}
