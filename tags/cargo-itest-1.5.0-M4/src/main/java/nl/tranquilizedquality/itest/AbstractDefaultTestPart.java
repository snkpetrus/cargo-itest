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

import junit.framework.TestSuite;
import nl.tranquilizedquality.itest.configuration.DatasourceConfiguration;
import nl.tranquilizedquality.itest.configuration.SQLScriptsConfiguration;
import nl.tranquilizedquality.itest.domain.SQLScripts;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DatasourceConfiguration.class, SQLScriptsConfiguration.class })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public abstract class AbstractDefaultTestPart extends AbstractTransactionalJUnit4SpringContextTests {

    /** Logger for this class */
    private static final Log LOGGER = LogFactory.getLog(AbstractDefaultTestPart.class);

    /**
     * SQL scripts bean that contains lists of SQL scripts that should be
     * executed before and after a test.
     */
    @Autowired(required = false)
    private SQLScripts sqlScripts;

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
     * Executes the SQL scripts if they are defined.
     */
    @Before
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
