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

import static junit.framework.Assert.fail;
import nl.tranquilizedquality.itest.cargo.ContainerUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.beans.BeansException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
    private static final Log log =
            LogFactory.getLog(AbstractDefaultNoDbDeploymentTest.class);

    /** The container utility for starting up a container. */
    protected static ContainerUtil CONTAINER_UTIL;

    /**
     * The safety cameras host to test.
     */
    protected static String host = "localhost:8890";

    /**
     * The name of the context file for the itest beans.
     */
    protected static String ITEST_CONTEXT_FILENAME = "itest-context.xml";

    /**
     * Loads the application context of the container utility.
     * 
     * @param locations A string array containing all the files that need to be
     *        loaded in the application context.
     * @return Returns the application context.
     */
    protected static ConfigurableApplicationContext loadContext(
            String[] locations) {
        return new ClassPathXmlApplicationContext(locations);
    }

    @BeforeClass
    public static void runOnce() throws Exception {
        // The application server need to be locally started only if the
        // host is localhost
        if (StringUtils.contains(host, "localhost")
                || StringUtils.contains(host, "127.0.0.")) {
            if (log.isInfoEnabled()) {
                log.info("Starting up the container utility...");
            }

            try {
                final ConfigurableApplicationContext context =
                        loadContext(new String[] { ITEST_CONTEXT_FILENAME,
                                "common-itest-context.xml" });

                CONTAINER_UTIL =
                        (ContainerUtil) context.getBean("containerUtil");
                CONTAINER_UTIL.start();
            } catch (BeansException e) {
                final String msg = "Failed to start up the container utility! - " + e.getMessage();
                if (log.isErrorEnabled()) {
                    log.error(msg);
                }
                fail(msg);
            }
        }
    }

    @AfterClass
    public static void stop() {
        if (CONTAINER_UTIL != null) {
            if (log.isInfoEnabled()) {
                log.info("Stopping the container utility...");
            }
            CONTAINER_UTIL.stop();
        }
    }

}
