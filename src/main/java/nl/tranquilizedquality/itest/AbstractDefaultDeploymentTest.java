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
package nl.tranquilizedquality.itest;

import nl.tranquilizedquality.itest.cargo.ContainerUtil;
import nl.tranquilizedquality.itest.domain.SQLScripts;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/**
 * This is the base class of a simple integration test. Extending from this
 * class will safe you some work and gets you up and running pretty quick.
 *
 * @author Salomo Petrus (sape)
 * @since 11 dec 2008
 *
 */
public abstract class AbstractDefaultDeploymentTest extends AbstractTransactionalJUnit4SpringContextTests {

    /** Logger for this class */
    private static final Log LOGGER = LogFactory.getLog(AbstractDefaultDeploymentTest.class);

    /** The container utility for starting up a container. */
    protected static ContainerUtil CONTAINER_UTIL;

    /**
     * SQL scripts bean that contains lists of SQL scripts that should be
     * executed before and after a test.
     */
    @Autowired(required = false)
    private SQLScripts sqlScripts;

    /**
     * The spring application context where the container utility will be loaded
     * in.
     */
    protected static ConfigurableApplicationContext CONTEXT;

    /**
     * The host to test.
     */
    protected static String host = "localhost:8890";

    /**
     * Determines if the tests are being run on localhost or not.
     *
     * @return Returns true if the test are running on localhost otherwise it
     *         returns false.
     */
    public static boolean isRunningOnLocalHost() {
        return StringUtils.contains(host, "localhost") || StringUtils.contains(host, "127.0.0.");
    }

    /**
     * Stops the container utility..
     */
    @AfterClass
    public static void stop() {
        if (isRunningOnLocalHost()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Stopping the container utility...");
            }

            CONTAINER_UTIL.stop();
        }

        if (CONTEXT != null) {
            CONTEXT.close();
        }
    }

    /**
     * Executes the SQL scripts if they are defined.
     */
    @Before
    public void executeSQLScripts() {

        if (this.sqlScripts != null) {
            for (final String script : this.sqlScripts.getSetupScripts()) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Executing script: " + script);
                }
                executeSqlScript(script, false);
            }
        }
    }

    /**
     * Executes the SQL clean up scripts if they are defined.
     */
    @After
    public void executeSQLCleanUpScripts() {

        if (this.sqlScripts != null) {
            for (final String script : this.sqlScripts.getCleanUpScripts()) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Executing clean up script: " + script);
                }
                executeSqlScript(script, false);
            }
        }
    }

}
