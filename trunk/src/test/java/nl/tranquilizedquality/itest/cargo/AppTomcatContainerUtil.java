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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * This is a simple example of how the {@link AbstractTomcatContainerUtil} could
 * be used. This test tests a simple test application called 'test-app'.
 *
 * As you can see you don't have to do much to get it to work. You only need to
 * do the following steps:
 * <ol>
 * <li>Extend from {@link AbstractTomcatContainerUtil}.</li>
 * <li>Create a context file called <i>itest-context.xml</i> in the root of the
 * classpath.</li>
 * <li>Configure the container utility in the <i>itest-context.xml</i>. <b>Make
 * sure you give your bean the name 'containerUtil'.</b></li>
 * <li>Create your own Tomcat ZIP file replacing the port number with the system
 * property ${cargo.server.port}</li>
 * </ol>
 *
 * After these steps you container utility should be setup correctly and all is
 * left is to create a unit test that uses the container.
 *
 * @author Salomo Petrus
 *
 */
public class AppTomcatContainerUtil extends AbstractTomcatContainerUtil {

    /** Logger for this class */
    private static final Log log = LogFactory.getLog(AppTomcatContainerUtil.class);

    /** The path where all configuration resource files are */
    private String configResourcesPath;

    @Override
    protected void setupConfiguration() {
        if (log.isInfoEnabled()) {
            log.info("No configuration to do in " + configResourcesPath);
        }
    }

    /**
     * @param configResourcesPath
     *            the configResourcesPath to set
     */
    @Override
    @Required
    public void setConfigResourcesPath(final String configResourcesPath) {
        this.configResourcesPath = configResourcesPath;
    }

}
