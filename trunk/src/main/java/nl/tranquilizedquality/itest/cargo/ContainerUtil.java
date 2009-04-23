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

package nl.tranquilizedquality.itest.cargo;

/**
 * Inteface for a container utility. It can configure, start and stop a specific
 * container.
 * 
 * @author Salomo Petrus (sape)
 * 
 */
public interface ContainerUtil {

    /**
     * Starts up the installedLocalContainer and its deployed applications
     * 
     * @throws Exception Is thrown when something went wrong during start up.
     */
    void start() throws Exception;

    /**
     * Stops the installedLocalContainer.
     */
    void stop();

    /**
     * Adds a location of a deployable with the according type.
     * 
     * @param location The location where to get the deployable.
     * @param type The type of deployable. This can be one of the following
     *        three:
     *        <ol>
     *        <li>WAR</li>
     *        <li>EAR</li>
     *        <li>EJB</li>
     *        </ol>
     */
    void addDeployableLocation(String location, String type);

    /**
     * Retrieves the port where the container is running on.
     * 
     * @return Returns an integer value representing the port the container is
     *         running on.
     */
    Integer getContainerPort();

    /**
     * Retrieves the directory where shared libraries can be copied to.
     * 
     * @return Returns a String representation of the full path to the
     *         directory.
     */
    String getSharedLibDirectory();

    /**
     * Retrieves the directory where the configuration files are located from
     * the specific container.
     * 
     * @return Returns a String representation of the full path to the
     *         directory.
     */
    String getConfDirectory();

}