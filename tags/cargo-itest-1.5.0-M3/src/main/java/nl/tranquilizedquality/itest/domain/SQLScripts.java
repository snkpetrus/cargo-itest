/**
 * <pre>
 * Project: cargo-itest Created on: 26 nov. 2014 File: fSQLScripts.java
 * Package: nl.tranquilizedquality.itest.domain
 *
 * Copyright (c) 2014 Tranquilized Quality www.tr-quality.com All rights
 * reserved.
 *
 * This software is the confidential and proprietary information of Dizizid
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Tranquilized Quality.
 * </pre>
 */
package nl.tranquilizedquality.itest.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of SQL scripts that can be executed for a test.
 *
 * @author Salomo Petrus (salomo.petrus@tr-quality.com)
 * @since 26 nov. 2014
 *
 */
public class SQLScripts {

    /**
     * A list containing names of SQL script files on the classpath that are
     * used to setup the test data.
     */
    private final List<String> setupScripts;

    /**
     * A list containing names of SQL script files on the classpath that are
     * used to clean up the test data.
     */
    private final List<String> cleanUpScripts;

    /**
     * Constructor taking a builder.
     *
     * @param builder
     *            The builder where the vaules will be copied from.
     */
    private SQLScripts(final SQLScriptsBuilder builder) {
        this.setupScripts = builder.setupScripts;
        this.cleanUpScripts = builder.cleanUpScripts;
    }

    /**
     * @return the setupScripts
     */
    public List<String> getSetupScripts() {
        return setupScripts;
    }

    /**
     * @return the cleanUpScripts
     */
    public List<String> getCleanUpScripts() {
        return cleanUpScripts;
    }

    /**
     * Builder that can build {@link SQLScripts} beans.
     *
     * @author Salomo Petrus (salomo.petrus@tr-quality.com)
     * @since 26 nov. 2014
     *
     */
    public static class SQLScriptsBuilder {

        /** SQL scripts that can be used to setup the test data. */
        private List<String> setupScripts;

        /** SQL scripts that can be used to clean up the test data. */
        private List<String> cleanUpScripts;

        /**
         * Sets the setup scripts.
         *
         * @param setupScripts
         *            The scripts to use.
         * @return Returns the builder.
         */
        public SQLScriptsBuilder withSetupScripts(final List<String> setupScripts) {
            this.setupScripts = new ArrayList<String>(setupScripts);
            return this;
        }

        /**
         * Sets the clean up scripts.
         *
         * @param cleanUpScripts
         *            The scripts to use.
         * @return Returns the builder.
         */
        public SQLScriptsBuilder withCleanUpScripts(final List<String> cleanUpScripts) {
            this.cleanUpScripts = new ArrayList<String>(cleanUpScripts);
            return this;
        }

        /**
         * Builds the SQL scripts.
         *
         * @return Returns {@link SQLScripts} bean.
         */
        public SQLScripts build() {
            return new SQLScripts(this);
        }
    }

}
