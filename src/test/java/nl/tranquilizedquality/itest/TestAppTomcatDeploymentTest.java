/**
 * Project: cargo-itest Created on: 30 jan 2009 File:
 * TestAppTomcatDeploymentTest.java Package: nl.tranquilizedquality.itest
 * 
 * Copyright (c) 2009 Tranquilized Quality www.tq-quality.nl All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of Tranquilized
 * Quality ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with Tranquilized Quality.
 */
package nl.tranquilizedquality.itest;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import nl.tranquilizedquality.itest.cargo.ContainerUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;
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
 * @author Salomo Petrus
 * 
 */
@RunWith(JUnit4ClassRunner.class)
public class TestAppTomcatDeploymentTest {
	/** Logger for this class */
	private static final Log log = LogFactory.getLog(TestAppTomcatDeploymentTest.class);

	/** The container utility for starting up a container. */
	protected static ContainerUtil CONTAINER_UTIL;

	/**
	 * The host to test.
	 */
	protected static String host = "localhost:8890";

	/**
	 * Loads the application context of the container utility.
	 * 
	 * @param locations
	 *            A string array containing all the files that need to be loaded
	 *            in the application context.
	 * @return Returns the application context.
	 */
	protected static ConfigurableApplicationContext loadContext(String[] locations) {
		return new ClassPathXmlApplicationContext(locations);
	}

	@BeforeClass
	public static void runOnce() throws Exception {
		// The application server need to be locally started only if the
		// host is localhost
		if (StringUtils.contains(host, "localhost") || StringUtils.contains(host, "127.0.0.")) {
			if (log.isInfoEnabled()) {
				log.info("Starting up the container utility...");
			}

			ConfigurableApplicationContext context = loadContext(new String[] {
					"tomcat-itest-context.xml", "common-itest-context.xml" });
			CONTAINER_UTIL = (ContainerUtil) context.getBean("containerUtil");
			CONTAINER_UTIL.start();
		}
	}

	@AfterClass
	public static void stop() throws Exception {
		if (StringUtils.contains(host, "localhost") || StringUtils.contains(host, "127.0.0.")) {
			if (log.isInfoEnabled()) {
				log.info("Stopping the container utility...");
			}

			CONTAINER_UTIL.stop();
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
