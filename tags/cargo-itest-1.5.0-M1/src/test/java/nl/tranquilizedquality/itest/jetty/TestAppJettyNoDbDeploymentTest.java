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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import nl.tranquilizedquality.itest.AbstractDefaultDeploymentTest;
import nl.tranquilizedquality.itest.cargo.ContainerUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
public class TestAppJettyNoDbDeploymentTest {
	/** Logger for this class */
	private static final Log log = LogFactory.getLog(TestAppJettyNoDbDeploymentTest.class);

	/** The container utility for starting up a container. */
	protected static ContainerUtil CONTAINER_UTIL;

	/**
	 * The safety cameras host to test.
	 */
	protected static String host = "localhost";

	/**
	 * Loads the application context of the container utility.
	 * 
	 * @param locations
	 *            A string array containing all the files that need to be loaded
	 *            in the application context.
	 * @return Returns the application context.
	 */
	protected static ConfigurableApplicationContext loadContext(final String[] locations) {
		return new ClassPathXmlApplicationContext(locations);
	}

	// @BeforeClass
	public static void runOnce() throws Exception {
		// The application server need to be locally started only if the
		// host is localhost
		if (StringUtils.contains(host, "localhost") || StringUtils.contains(host, "127.0.0.")) {
			if (log.isInfoEnabled()) {
				log.info("Starting up the container utility...");
			}

			final ConfigurableApplicationContext context = loadContext(new String[] { "jetty-itest-context.xml", "common-itest-context.xml" });
			CONTAINER_UTIL = (ContainerUtil) context.getBean("containerUtil");
			CONTAINER_UTIL.start();
		}
	}

	// @AfterClass
	public static void stop() {
		if (CONTAINER_UTIL != null) {
			if (log.isInfoEnabled()) {
				log.info("Stopping the container utility...");
			}
			CONTAINER_UTIL.stop();
		}
	}

	/**
	 * FIXME: Not working anymore for some reason.
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
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