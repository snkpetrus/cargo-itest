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
package nl.tranquilizedquality.itest.tomcat;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import nl.tranquilizedquality.itest.AbstractDefaultDeploymentTest;
import nl.tranquilizedquality.itest.AbstractDefaultHibernateDeploymentTest;
import nl.tranquilizedquality.itest.cargo.ContainerUtil;
import nl.tranquilizedquality.itest.tomcat.configuration.TomcatContainerUtilConfiguration;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This is a simple example how you could create an integration test without the
 * {@link AbstractDefaultDeploymentTest}.
 *
 * As you can see there is not much to it.
 *
 * @author Salomo Petrus
 *
 */
public class TestAppTomcatDeploymentTest extends AbstractDefaultHibernateDeploymentTest {

    /** Logger for this class */
    private static final Log LOGGER = LogFactory.getLog(TestAppTomcatDeploymentTest.class);

    @BeforeClass
    public static void runOnce() throws Exception {

        // The application server need to be locally started only if the
        // host is localhost
        if (isRunningOnLocalHost()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Starting up the container utility...");
            }

            CONTEXT = new AnnotationConfigApplicationContext(TomcatContainerUtilConfiguration.class);
            CONTAINER_UTIL = (ContainerUtil) CONTEXT.getBean("containerUtil");
            CONTAINER_UTIL.start();
        }
    }

    @Test
    public void testHelloWorld() throws Exception {
        final WebClient webClient = new WebClient();
        webClient.setJavaScriptEnabled(false);

        // Get the first page
        final HtmlPage index = (HtmlPage) webClient.getPage("http://" + host + "/test-app/");

        assertNotNull(index);
        assertTrue(StringUtils.contains(index.asText(), "hello INDEX"));
    }

}
