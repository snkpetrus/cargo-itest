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

/**
 * This is a simple example of how the {@link AbstractJBossContainerUtil} could
 * be used. This test tests a simple test application called 'test-app'
 * configuring application specific log4j.xml and jndi.properties.
 *
 * As you can see you don't have to do much to get it to work. You only need to
 * do the following steps:
 * <ol>
 * <li>Extend from {@link AbstractJBossContainerUtil}.</li>
 * <li>Create a context file called <i>itest-context.xml</i> in the root of the
 * classpath.</li>
 * <li>Configure the container utility in the <i>itest-context.xml</i>. <b>Make
 * sure you give your bean the name 'containerUtil'.</b></li>
 * <li>Create your own JBoss configuration ZIP file by using the test-app.zip as
 * template since this one already contains certain variables that are used by
 * the {@link AbstractJBossContainerUtil} and put it on a server so the
 * container utility can download it.</li>
 * </ol>
 *
 * After these steps you container utility should be setup correctly and all is
 * left is to create a unit test that uses the container.
 *
 * @author Salomo Petrus (sape)
 * @since 11 dec 2008
 *
 */
public class AppJbossContainerUtil extends AbstractJBossContainerUtil {
    /** Logger for this class */
    private static final Log log = LogFactory.getLog(AppJbossContainerUtil.class);

    @Override
    protected void setupConfiguration() {

        if (log.isInfoEnabled()) {
            log.info("No custom configuration!");
        }

    }

}
