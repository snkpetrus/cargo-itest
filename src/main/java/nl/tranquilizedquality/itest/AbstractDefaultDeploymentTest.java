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

import java.util.ArrayList;
import java.util.List;

import nl.tranquilizedquality.itest.cargo.ContainerUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

/**
 * This is the base class of a simple integration test. Extending from this
 * class will safe you some work and gets you up and running pretty quick.
 * 
 * @author Salomo Petrus (sape)
 * @since 11 dec 2008
 * 
 */
public abstract class AbstractDefaultDeploymentTest extends
		AbstractTransactionalJUnit4SpringContextTests {
	/** Logger for this class */
	private static final Log log = LogFactory.getLog(AbstractDefaultDeploymentTest.class);

	/** The container utility for starting up a container. */
	protected static ContainerUtil CONTAINER_UTIL;

	/** List of SQL scripts that will be executed before every test. */
	protected static List<String> SQL_SCRIPTS;

	/** List of SQL scripts that will be executed after every test. */
	protected static List<String> SQL_CLEAN_UP_SCRIPTS;

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
	protected static ConfigurableApplicationContext loadContext(final String[] locations) {
		return new ClassPathXmlApplicationContext(locations);
	}

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void runOnce() throws Exception {
		// The application server need to be locally started only if the
		// host is localhost
		if (StringUtils.contains(host, "localhost") || StringUtils.contains(host, "127.0.0.")) {
			if (log.isInfoEnabled()) {
				log.info("Starting up the container utility...");
			}
			try {
				/*
				 * Start up application context.
				 */
				final ConfigurableApplicationContext context = loadContext(new String[] {
						"itest-context.xml", "common-itest-context.xml" });
				CONTAINER_UTIL = (ContainerUtil) context.getBean("containerUtil");
				CONTAINER_UTIL.start();

				/*
				 * Retrieve available SQL scripts.
				 */
				if (log.isInfoEnabled()) {
					log.info("Retrieving available SQL scripts...");
				}

				try {
					SQL_SCRIPTS = (List<String>) context.getBean("sqlScripts");
				}
				catch (final NoSuchBeanDefinitionException e) {
					SQL_SCRIPTS = new ArrayList<String>(0);
				}

				/*
				 * Retrieve available SQL clean up scripts.
				 */
				try {
					SQL_CLEAN_UP_SCRIPTS = (List<String>) context.getBean("sqlCleanUpScripts");
				}
				catch (final NoSuchBeanDefinitionException e) {
					SQL_CLEAN_UP_SCRIPTS = new ArrayList<String>(0);
				}

				final int numberOfScripts = SQL_SCRIPTS.size() + SQL_CLEAN_UP_SCRIPTS.size();
				if (log.isInfoEnabled()) {
					log.info(numberOfScripts + " SQL scripts retrieved...");
				}
			}
			catch (final BeansException e) {
				final String msg = "Failed to start up the container utility! - " + e.getMessage();
				if (log.isErrorEnabled()) {
					log.error(msg);
				}
				fail(msg);
			}
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

	@BeforeTransaction
	public void executeSQLScripts() throws Exception {
		for (final String script : SQL_SCRIPTS) {
			if (log.isInfoEnabled()) {
				log.info("Executing script: " + script);
			}
			executeSqlScript(script, false);
		}
	}

	@AfterTransaction
	public void executeSQLCleanUpScripts() throws Exception {
		for (final String script : SQL_CLEAN_UP_SCRIPTS) {
			if (log.isInfoEnabled()) {
				log.info("Executing clean up script: " + script);
			}
			executeSqlScript(script, false);
		}
	}

}
