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
package nl.tranquilizedquality.itest.glassfish;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This is a simple example how you could create an integration test using the
 * Glassfish application server.
 * 
 * As you can see you need to do some tweeking since the Glassfish application
 * server has specific distributions for different operating systems.
 * 
 * @author Vincenzo Vitale (vita)
 * @since 20 May 2009
 * 
 */
public class TestAppGlassfishNoDbDeploymentTest {
	/** Logger for this class */
	private static final Log log = LogFactory.getLog(TestAppGlassfishNoDbDeploymentTest.class);

	// /** The container utility for starting up a container. */
	// protected static ContainerUtil CONTAINER_UTIL;

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

	@BeforeClass
	public static void runOnce() throws Exception {
		// The application server need to be locally started only if the
		// host is localhost
		// if (StringUtils.contains(host, "localhost") ||
		// StringUtils.contains(host, "127.0.0.")) {
		// if (log.isInfoEnabled()) {
		// log.info("Starting up the container utility...");
		// }
		//
		// /*
		// * Glassfish configures thousands of configuration files during the
		// * installation process, where the installation directory is
		// * referred. Two different zipped application servers are uploaded
		// * in the googlecode homepage and than the two files are referred in
		// * the different test configurations.
		// */
		// final String operatingSystem = System.getProperty("os.name");
		// String testConfigurationFile = "";
		//
		// if (operatingSystem != null && operatingSystem.startsWith("Windows"))
		// {
		// testConfigurationFile = "glassfish-windows-itest-context.xml";
		// }
		// else {
		// testConfigurationFile = "glassfish-linux-itest-context.xml";
		// }
		//
		// ConfigurableApplicationContext context = loadContext(new String[] {
		// testConfigurationFile, "common-itest-context.xml" });
		//
		// CONTAINER_UTIL = (ContainerUtil) context.getBean("containerUtil");
		// CONTAINER_UTIL.start();
		//
		// }
	}

	@AfterClass
	public static void stop() {
		// if (CONTAINER_UTIL != null) {
		// if (log.isInfoEnabled()) {
		// log.info("Stopping the container utility...");
		// }
		// CONTAINER_UTIL.stop();
		// }
	}

	@Test
	public void testHelloWorld() throws Exception {
		// final WebClient webClient = new WebClient();
		// webClient.setJavaScriptEnabled(false);
		//
		// // Get the first page
		// final HtmlPage index = (HtmlPage) webClient.getPage("http://" + host
		// + ":"
		// + CONTAINER_UTIL.getContainerPort() + "/test-app/");
		//
		// assertNotNull(index);
		// assertTrue(StringUtils.contains(index.asText(), "hello INDEX"));
	}
}
