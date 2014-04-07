/**
 * Project: cargo-itest Created on: 14 sep 2010 File:
 * AbstractDefaultTestPart.java Package: nl.tranquilizedquality.itest
 * 
 * Copyright (c) 2010 Tranquilized Quality www.tq-quality.nl All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of Tranquilized
 * Quality ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with Tranquilized Quality.
 */
package nl.tranquilizedquality.itest;

import static junit.framework.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestSuite;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

/**
 * AbstractDefaultTestPart is a base class that can be used in a
 * {@link TestSuite} that starts up the container utility. This will only starts
 * up his own application context with SQL scripts that need to be executed.
 * Starting up of the container utility will be delegated to the
 * {@link TestSuite}.
 * 
 * @author Salomo Petrus
 * @since 14 sep 2010
 * 
 */
public abstract class AbstractDefaultTestPart extends AbstractTransactionalJUnit4SpringContextTests {

	/** Logger for this class */
	private static final Log log = LogFactory.getLog(AbstractDefaultTestPart.class);

	/** List of SQL scripts that will be executed before every test. */
	protected static List<String> SQL_SCRIPTS;

	/** List of SQL scripts that will be executed after every test. */
	protected static List<String> SQL_CLEAN_UP_SCRIPTS;

	/**
	 * The host to test.
	 */
	protected static String host = "localhost:8890";

	/**
	 * Loads the application context with the ability to load SQL scripts that
	 * need to be executed.
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
		/*
		 * The application server need to be locally started only if the host is
		 * localhost
		 */
		if (StringUtils.contains(host, "localhost") || StringUtils.contains(host, "127.0.0.")) {
			if (log.isInfoEnabled()) {
				log.info("Starting up the application context...");
			}
			try {
				/*
				 * Start up application context.
				 */
				final ConfigurableApplicationContext context = loadContext(new String[] { "itest-sql-script-context.xml" });

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
				final String msg = "Failed to start up the application context! - "
						+ e.getMessage();
				if (log.isErrorEnabled()) {
					log.error(msg);
				}
				fail(msg);
			}
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
