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

import static org.junit.Assert.fail;
import nl.tranquilizedquality.itest.cargo.ContainerUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.springframework.beans.BeansException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * This is the base class of a simple integration test. Extending from this
 * class will safe you some work and gets you up and running pretty quick. This
 * default test is mainly used for applications that don't use any database at
 * all.
 *
 * @author Salomo Petrus (sape)
 * @since 13 feb 2009
 *
 */
public abstract class AbstractDefaultNoDbDeploymentTest {

    /** Logger for this class */
    private static final Log LOGGER = LogFactory.getLog(AbstractDefaultNoDbDeploymentTest.class);

    /** The container utility for starting up a container. */
    protected static ContainerUtil CONTAINER_UTIL;

    /**
     * The safety cameras host to test.
     */
    protected static String host = "localhost:8890";

    /**
     * Array with all configuration classes that should be used for starting up
     * the application context.
     */
    protected static Class<?>[] CONFIGURATION_CLASSES;

    /**
     * The spring application context where the container utility will be loaded
     * in.
     */
    protected static ConfigurableApplicationContext CONTEXT;

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
     * Starts up the container utility. This needs to be called in a static
     * method that is annotated with @BeforeClass so the container will be
     * started only once.
     */
    public static void startupContainer() {

        // The application server need to be locally started only if the
        // host is localhost
        if (isRunningOnLocalHost()) {

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Starting up the container utility...");
            }

            try {
                CONTEXT = new AnnotationConfigApplicationContext(CONFIGURATION_CLASSES);

                CONTAINER_UTIL = (ContainerUtil) CONTEXT.getBean("containerUtil");
                CONTAINER_UTIL.start();
            } catch (final BeansException e) {

                final String msg = "Failed to start up the container utility! - " + e.getMessage();
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error(msg, e);
                }
                fail(msg);
            }
        }
    }

    /**
     * Stops the container utility and closed the application context.
     */
    @AfterClass
    public static void stop() {
        if (CONTAINER_UTIL != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Stopping the container utility...");
            }
            CONTAINER_UTIL.stop();
        }

        if (CONTEXT != null) {
            CONTEXT.close();
        }
    }

}
