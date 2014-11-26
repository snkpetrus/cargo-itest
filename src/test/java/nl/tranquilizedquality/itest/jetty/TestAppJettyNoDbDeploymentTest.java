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
package nl.tranquilizedquality.itest.jetty;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import nl.tranquilizedquality.itest.AbstractDefaultDeploymentTest;
import nl.tranquilizedquality.itest.AbstractDefaultNoDbDeploymentTest;
import nl.tranquilizedquality.itest.jetty.configuration.JettyConfiguration;

import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This is a simple example how you could create an integration test without the
 * {@link AbstractDefaultDeploymentTest}.
 *
 * As you can see there is not much to it.
 *
 * @author Salomo Petrus (sape)
 * @since 24 apr 2009
 *
 */
public class TestAppJettyNoDbDeploymentTest extends AbstractDefaultNoDbDeploymentTest {

    /**
     * The safety cameras host to test.
     */
    protected static String host = "localhost";

    @BeforeClass
    public static void runOnce() throws Exception {

        CONFIGURATION_CLASSES = new Class<?>[] {JettyConfiguration.class };
        startupContainer();
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testHelloWorld() throws Exception {
        final WebClient webClient = new WebClient();
        webClient.setJavaScriptEnabled(false);

        final String url = "http://" + host + ":" + CONTAINER_UTIL.getContainerPort() + "/test-app/";

        // Get the first page
        final HtmlPage index = (HtmlPage) webClient.getPage(url);

        assertNotNull(index);
        assertTrue(StringUtils.contains(index.asText(), "hello INDEX"));
    }

}
